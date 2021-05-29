package bangkit.daya.app.imagerecognition

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.os.Bundle
import android.util.Log
import android.util.Size
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import bangkit.daya.databinding.FragmentImageRecognitionBinding
import bangkit.daya.ml.PlaceModel
import bangkit.daya.model.ModelResult
import bangkit.daya.model.Recognition
import bangkit.daya.util.YuvToRgbConverter
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.koin.android.viewmodel.ext.android.viewModel
import org.tensorflow.lite.gpu.CompatibilityList
import org.tensorflow.lite.support.image.TensorImage
import org.tensorflow.lite.support.model.Model
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.Executors

typealias RecognitionListener = (recognition: List<Recognition>) -> Unit

class ImageRecognitionFragment : Fragment() {

    private lateinit var binding: FragmentImageRecognitionBinding
    private val recognitionAdapter: RecognitionAdapter by lazy {
        RecognitionAdapter {
            if (it.isNotEmpty() || it != "No object is found") {
                val directions =
                    ImageRecognitionFragmentDirections.actionImageRecognitionFragmentToDetailFragment(it)
                findNavController().navigate(directions)
            }
        }
    }
    private val imageRecognitionViewModel: ImageRecognitionViewModel by viewModel()

    private lateinit var imageAnalyzer: ImageAnalysis
    private val cameraExecutor = Executors.newSingleThreadExecutor()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageRecognitionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.fabRetry.setOnClickListener {
            imageRecognitionViewModel.resetValue()
        }
        setObserver()
        initAdapter()
        if (allPermissionsGranted()) {
            readResultFromFile()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(), REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS
            )
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults:
        IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                readResultFromFile()
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
                findNavController().navigateUp()
            }
        }
    }

    private fun setObserver() {
        imageRecognitionViewModel.showedItem.observe(viewLifecycleOwner) { result ->
            recognitionAdapter.setData(result)
        }

        imageRecognitionViewModel.fabRetryVisibility.observe(viewLifecycleOwner) { visibility ->
            binding.fabRetry.visibility = visibility
        }

        imageRecognitionViewModel.modelResultLabels.observe(viewLifecycleOwner) {
            startCamera()
        }
    }

    private fun initAdapter() {
        binding.rvResult.layoutManager = LinearLayoutManager(requireContext())
        binding.rvResult.adapter = recognitionAdapter
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder()
                .build()

            imageAnalyzer = ImageAnalysis.Builder()
                .setTargetResolution(Size(224, 224))
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysisUseCase: ImageAnalysis ->
                    analysisUseCase.setAnalyzer(
                        cameraExecutor,
                        ImageAnalyzer(requireContext()) { items ->
                            val showedItem = imageRecognitionViewModel.showedItem.value?.label ?: ""
                            if (showedItem == "No object is found" || showedItem == "") {
                                imageRecognitionViewModel.updateData(items)
                            }
                        })
                }

            val cameraSelector = if (cameraProvider.hasCamera(CameraSelector.DEFAULT_BACK_CAMERA))
                CameraSelector.DEFAULT_BACK_CAMERA else CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                cameraProvider.unbindAll()

                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )
                preview.setSurfaceProvider(binding.viewFinder.surfaceProvider)
            } catch (exc: Exception) {
                Log.e("<ERR>", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun readResultFromFile() {
        Observable.create<MutableList<ModelResult>> { subscriber ->
            val reader =
                BufferedReader(InputStreamReader(requireActivity().assets.open("labels.txt")))
            val tempResults = mutableListOf<ModelResult>()
            var label: List<String>
            reader.lineSequence().forEach { resultRead ->
                label = resultRead.split("|")
                if (!label.isNullOrEmpty()) {
                    tempResults.add(ModelResult(label[0], label[1], label[2]))
                }
            }
            reader.close()
            subscriber.onNext(tempResults)
        }
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe{ imageRecognitionViewModel.setModelResult(it) }
    }

    private fun allPermissionsGranted() =
        REQUIRED_PERMISSIONS.all { permission ->
            ContextCompat.checkSelfPermission(
                requireContext(), permission
            ) == PackageManager.PERMISSION_GRANTED
        }

    private class ImageAnalyzer(ctx: Context, private val listener: RecognitionListener) :
        ImageAnalysis.Analyzer {

        private val placeModel: PlaceModel by lazy {

            val compatList = CompatibilityList()

            val options = if (compatList.isDelegateSupportedOnThisDevice) {
                Log.d(TAG, "This device is GPU Compatible ")
                Model.Options.Builder().setDevice(Model.Device.GPU).build()
            } else {
                Log.d(TAG, "This device is GPU Incompatible ")
                Model.Options.Builder().setNumThreads(4).build()
            }

            PlaceModel.newInstance(ctx, options)
        }

        override fun analyze(imageProxy: ImageProxy) {

            val items = mutableListOf<Recognition>()

            val tfImage = TensorImage.fromBitmap(toBitmap(imageProxy))

            val outputs = placeModel.process(tfImage)
                .probabilityAsCategoryList.apply {
                    sortByDescending { it.score }
                }.take(MAX_RESULT_DISPLAY)

            for (output in outputs) {
                items.add(Recognition(output.label, output.score))
            }

            listener(items)

            imageProxy.close()
        }

        private val yuvToRgbConverter = YuvToRgbConverter(ctx)
        private lateinit var bitmapBuffer: Bitmap
        private lateinit var rotationMatrix: Matrix

        @SuppressLint("UnsafeExperimentalUsageError", "UnsafeOptInUsageError")
        private fun toBitmap(imageProxy: ImageProxy): Bitmap? {

            val image = imageProxy.image ?: return null

            if (!::bitmapBuffer.isInitialized) {
                Log.d(TAG, "Initalise toBitmap()")
                rotationMatrix = Matrix()
                rotationMatrix.postRotate(imageProxy.imageInfo.rotationDegrees.toFloat())
                bitmapBuffer = Bitmap.createBitmap(
                    imageProxy.width, imageProxy.height, Bitmap.Config.ARGB_8888
                )
            }

            yuvToRgbConverter.yuvToRgb(image, bitmapBuffer)

            return Bitmap.createBitmap(
                bitmapBuffer,
                0,
                0,
                bitmapBuffer.width,
                bitmapBuffer.height,
                rotationMatrix,
                false
            )
        }

        companion object {
            private const val TAG = "ImageAnalyzer"
        }
    }

    companion object {
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val MAX_RESULT_DISPLAY = 1
    }
}