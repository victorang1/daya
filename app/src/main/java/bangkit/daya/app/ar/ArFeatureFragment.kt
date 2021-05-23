package bangkit.daya.app.ar

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import bangkit.daya.R
import bangkit.daya.databinding.FragmentArFeatureBinding
import bangkit.daya.databinding.LoadingDialogBinding
import bangkit.daya.model.Geolocation
import bangkit.daya.model.Place
import bangkit.daya.util.AugmentedRealityLocationUtils
import bangkit.daya.util.AugmentedRealityLocationUtils.INITIAL_MARKER_SCALE_MODIFIER
import bangkit.daya.util.AugmentedRealityLocationUtils.setupSession
import bangkit.daya.util.PermissionUtils
import com.google.ar.core.TrackingState
import com.google.ar.core.exceptions.CameraNotAvailableException
import com.google.ar.core.exceptions.UnavailableException
import com.google.ar.sceneform.Node
import com.google.ar.sceneform.rendering.ViewRenderable
import org.koin.android.viewmodel.ext.android.viewModel
import uk.co.appoly.arcorelocation.LocationMarker
import uk.co.appoly.arcorelocation.LocationScene
import java.util.concurrent.CompletableFuture

class ArFeatureFragment : Fragment() {

    private lateinit var binding: FragmentArFeatureBinding
    private lateinit var loadingBinding: LoadingDialogBinding
    private val arFeatureViewModel: ArFeatureViewModel by viewModel()

    private var arCoreInstallRequested = false

    private var locationScene: LocationScene? = null

    private var arHandler = Handler(Looper.getMainLooper())

    lateinit var loadingDialog: AlertDialog

    private val resumeArElementsTask = Runnable {
        locationScene?.resume()
        binding.arSceneView.resume()
    }

    private var userGeolocation = Geolocation.EMPTY_GEOLOCATION

    private var placesSet: MutableSet<Place> = mutableSetOf()
    private var areAllMarkersLoaded = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentArFeatureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupLoadingDialog()
        setObserver()
    }

    override fun onResume() {
        super.onResume()
        checkAndRequestPermissions()
    }

    override fun onPause() {
        super.onPause()
        binding.arSceneView.session?.let {
            locationScene?.pause()
            binding.arSceneView.pause()
        }
    }

    private fun setObserver() {
        arFeatureViewModel.places.observe(viewLifecycleOwner) { places ->
            if (!places.isNullOrEmpty()) {
                placesSet.clear()
                placesSet.addAll(places)
                areAllMarkersLoaded = false
                locationScene!!.clearMarkers()
                renderVenues()
            }
        }

        arFeatureViewModel.loading.observe(viewLifecycleOwner) { loading ->
            if (loading.isLoading) {
                loadingBinding.loadingMessage = loading.message
                loadingDialog.show()
            } else if (::loadingDialog.isInitialized && !loading.isLoading) {
                loadingDialog.dismiss()
            }
        }

        arFeatureViewModel.geolocation.observe(viewLifecycleOwner) { geolocation ->
            userGeolocation = geolocation
        }
    }

    private fun setupLoadingDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        loadingBinding = LoadingDialogBinding.inflate(layoutInflater)
        loadingBinding.loadingMessage = ""
        alertDialogBuilder.setView(loadingBinding.root)
        loadingDialog = alertDialogBuilder.create()
        loadingDialog.setCanceledOnTouchOutside(false)
    }

    private fun checkAndRequestPermissions() {
        if (!PermissionUtils.hasLocationAndCameraPermissions(requireActivity())) {
            PermissionUtils.requestCameraAndLocationPermissions(requireActivity())
        } else {
            this.setupSession()
        }
    }

    private fun setupSession() {
        if (binding.arSceneView.session == null) {
            try {
                val session = setupSession(requireActivity(), arCoreInstallRequested)
                if (session == null) {
                    arCoreInstallRequested = true
                    return
                } else {
                    binding.arSceneView.setupSession(session)
                }
            } catch (e: UnavailableException) {
                AugmentedRealityLocationUtils.handleSessionException(requireActivity(), e)
            }
        }

        if (locationScene == null) {
            locationScene = LocationScene(requireActivity(), binding.arSceneView)
            locationScene!!.setMinimalRefreshing(true)
            locationScene!!.setOffsetOverlapping(true)
            locationScene!!.setRemoveOverlapping(true)
            locationScene!!.anchorRefreshInterval = 2000
        }

        try {
            resumeArElementsTask.run()
        } catch (e: CameraNotAvailableException) {
            Toast.makeText(requireContext(), "Unable to get camera", Toast.LENGTH_LONG).show()
            findNavController().navigateUp()
            return
        }

        if (userGeolocation == Geolocation.EMPTY_GEOLOCATION) {
            arFeatureViewModel.prepareLocationService(
                locationScene!!,
                listOf(getString(R.string.loading_location_message), getString(R.string.loading_fetching_message)),)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        results: IntArray
    ) {
        if (!PermissionUtils.hasLocationAndCameraPermissions(requireActivity())) {
            Toast.makeText(
                requireContext(), R.string.camera_and_location_permission_request, Toast.LENGTH_LONG
            ).show()
            if (!PermissionUtils.shouldShowRequestPermissionRationale(requireActivity())) {
                PermissionUtils.launchPermissionSettings(requireActivity())
            }
            findNavController().navigateUp()
        }
    }

    private fun renderVenues() {
        setupAndRenderVenuesMarkers()
        updateVenuesMarkers()
    }

    private fun setupAndRenderVenuesMarkers() {
        placesSet.forEach { place ->
            val completableFutureViewRenderable = ViewRenderable.builder()
                .setView(requireContext(), R.layout.location_layout_renderable)
                .build()
            CompletableFuture.anyOf(completableFutureViewRenderable)
                .handle<Any> { _, throwable ->
                    if (throwable != null) {
                        return@handle null
                    }
                    try {
                        val placeMarker = LocationMarker(
                            place.geometry.location.lng,
                            place.geometry.location.lat,
                            setVenueNode(place, completableFutureViewRenderable)
                        )
                        arHandler.postDelayed({
                            attachMarkerToScene(
                                placeMarker,
                                completableFutureViewRenderable.get().view
                            )
                            if (placesSet.indexOf(place) == placesSet.size - 1) {
                                areAllMarkersLoaded = true
                            }
                        }, 200)

                    } catch (ex: Exception) {
                        Toast.makeText(requireContext(), "Error: ${ex.message}", Toast.LENGTH_SHORT)
                            .show()
                    }
                    null
                }
        }
    }

    private fun attachMarkerToScene(
        locationMarker: LocationMarker,
        layoutRendarable: View
    ) {
        resumeArElementsTask.run {
            locationMarker.scalingMode = LocationMarker.ScalingMode.FIXED_SIZE_ON_SCREEN
            locationMarker.scaleModifier = INITIAL_MARKER_SCALE_MODIFIER

            locationScene?.mLocationMarkers?.add(locationMarker)
            locationMarker.anchorNode?.isEnabled = true

            arHandler.post {
                locationScene?.refreshAnchors()
                val pinContainer =
                    layoutRendarable.findViewById<ConstraintLayout>(R.id.pinContainer)
                pinContainer.visibility = View.VISIBLE
            }
        }
    }

//    private fun detachMarker(locationMarker: LocationMarker) {
//        locationMarker.anchorNode?.anchor?.detach()
//        locationMarker.anchorNode?.isEnabled = false
//        locationMarker.anchorNode = null
//    }

    private fun updateVenuesMarkers() {
        binding.arSceneView.scene.addOnUpdateListener()
        {
            if (!areAllMarkersLoaded) {
                return@addOnUpdateListener
            }

            locationScene?.mLocationMarkers?.forEach { locationMarker ->
                locationMarker.height =
                    AugmentedRealityLocationUtils.generateRandomHeightBasedOnDistance(
                        locationMarker?.anchorNode?.distance ?: 0
                    )
            }


            val frame = binding.arSceneView.arFrame ?: return@addOnUpdateListener
            if (frame.camera.trackingState != TrackingState.TRACKING) {
                return@addOnUpdateListener
            }
            locationScene!!.processFrame(frame)
        }
    }

    private fun setVenueNode(
        place: Place,
        completableFuture: CompletableFuture<ViewRenderable>
    ): Node {
        val node = Node()
        node.renderable = completableFuture.get()

        val nodeLayout = completableFuture.get().view
        val placeName = nodeLayout.findViewById<TextView>(R.id.name)
        val markerLayoutContainer = nodeLayout.findViewById<ConstraintLayout>(R.id.pinContainer)
        placeName.text = place.name
        markerLayoutContainer.visibility = View.GONE
        nodeLayout.setOnClickListener {
            Toast.makeText(
                requireContext(),
                "Clicked: ${place.name}",
                Toast.LENGTH_SHORT
            ).show()
        }
        return node
    }
}