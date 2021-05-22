package bangkit.daya.app.ar

import android.os.AsyncTask
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.navigation.fragment.findNavController
import bangkit.daya.R
import bangkit.daya.databinding.FragmentArFeatureBinding
import bangkit.daya.model.Geolocation
import bangkit.daya.model.Place
import bangkit.daya.util.AugmentedRealityLocationUtils
import bangkit.daya.util.AugmentedRealityLocationUtils.INITIAL_MARKER_SCALE_MODIFIER
import bangkit.daya.util.AugmentedRealityLocationUtils.INVALID_MARKER_SCALE_MODIFIER
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
import java.lang.ref.WeakReference
import java.util.concurrent.CompletableFuture

class ArFeatureFragment : Fragment() {

    private lateinit var binding: FragmentArFeatureBinding
    private val arFeatureViewModel: ArFeatureViewModel by viewModel()

    private var arCoreInstallRequested = false

    // Our ARCore-Location scene
    private var locationScene: LocationScene? = null

    private var arHandler = Handler(Looper.getMainLooper())

    lateinit var loadingDialog: AlertDialog

    private val resumeArElementsTask = Runnable {
        locationScene?.resume()
        binding.arSceneView.resume()
    }

    private var userGeolocation = Geolocation.EMPTY_GEOLOCATION

    private var venuesSet: MutableSet<Place> = mutableSetOf()
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
                venuesSet.clear()
                venuesSet.addAll(places)
                areAllMarkersLoaded = false
                locationScene!!.clearMarkers()
                renderVenues()
            }
        }
    }

    private fun setupLoadingDialog() {
        val alertDialogBuilder = AlertDialog.Builder(requireContext())
        val dialogHintMainView =
            LayoutInflater.from(requireContext()).inflate(R.layout.loading_dialog, null) as LinearLayout
        alertDialogBuilder.setView(dialogHintMainView)
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
            LocationAsyncTask(WeakReference(this@ArFeatureFragment)).execute(locationScene!!)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, results: IntArray) {
        if (!PermissionUtils.hasLocationAndCameraPermissions(requireActivity())) {
            Toast.makeText(
                requireContext(), R.string.camera_and_location_permission_request, Toast.LENGTH_LONG
            )
                .show()
            if (!PermissionUtils.shouldShowRequestPermissionRationale(requireActivity())) {
                // Permission denied with checking "Do not ask again".
                PermissionUtils.launchPermissionSettings(requireActivity())
            }
            findNavController().navigateUp()
        }
    }

    private fun fetchVenues(deviceLatitude: Double, deviceLongitude: Double) {
        loadingDialog.dismiss()
        userGeolocation = Geolocation(deviceLatitude.toString(), deviceLongitude.toString())
        arFeatureViewModel.getNearbyPlaces(deviceLatitude, deviceLongitude)
    }

    private fun renderVenues() {
        setupAndRenderVenuesMarkers()
        updateVenuesMarkers()
    }

    private fun setupAndRenderVenuesMarkers() {
        venuesSet.forEach { venue ->
            val completableFutureViewRenderable = ViewRenderable.builder()
                .setView(requireContext(), R.layout.location_layout_renderable)
                .build()
            CompletableFuture.anyOf(completableFutureViewRenderable)
                .handle<Any> { _, throwable ->
                    if (throwable != null) {
                        return@handle null
                    }
                    try {
                        val venueMarker = LocationMarker(
                            venue.geometry.location.lng,
                            venue.geometry.location.lat,
                            setVenueNode(venue, completableFutureViewRenderable)
                        )
                        arHandler.postDelayed({
                            attachMarkerToScene(
                                venueMarker,
                                completableFutureViewRenderable.get().view
                            )
                            if (venuesSet.indexOf(venue) == venuesSet.size - 1) {
                                areAllMarkersLoaded = true
                            }
                        }, 200)

                    } catch (ex: Exception) {
                        Toast.makeText(requireContext(), "Error: ${ex.message}", Toast.LENGTH_SHORT).show()
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
                val pinContainer = layoutRendarable.findViewById<ConstraintLayout>(R.id.pinContainer)
                pinContainer.visibility = View.VISIBLE
            }
        }
    }

    private fun computeNewScaleModifierBasedOnDistance(locationMarker: LocationMarker, distance: Int) {
        val scaleModifier = AugmentedRealityLocationUtils.getScaleModifierBasedOnRealDistance(distance)
        return if (scaleModifier == INVALID_MARKER_SCALE_MODIFIER) {
            detachMarker(locationMarker)
        } else {
            locationMarker.scaleModifier = scaleModifier
        }
    }

    private fun detachMarker(locationMarker: LocationMarker) {
        locationMarker.anchorNode?.anchor?.detach()
        locationMarker.anchorNode?.isEnabled = false
        locationMarker.anchorNode = null
    }

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

    private fun setVenueNode(venue: Place, completableFuture: CompletableFuture<ViewRenderable>): Node {
        val node = Node()
        node.renderable = completableFuture.get()

        val nodeLayout = completableFuture.get().view
        val venueName = nodeLayout.findViewById<TextView>(R.id.name)
        val markerLayoutContainer = nodeLayout.findViewById<ConstraintLayout>(R.id.pinContainer)
        venueName.text = venue.name
        markerLayoutContainer.visibility = View.GONE
        nodeLayout.setOnClickListener{ Toast.makeText(requireContext(), "Clicked: ${venue.name}", Toast.LENGTH_SHORT).show() }
        return node
    }

    class LocationAsyncTask(private val activityWeakReference: WeakReference<ArFeatureFragment>) :
        AsyncTask<LocationScene, Void, List<Double>>() {

        override fun onPreExecute() {
            super.onPreExecute()
            activityWeakReference.get()!!.loadingDialog.show()
        }

        override fun doInBackground(vararg p0: LocationScene): List<Double> {
            var deviceLatitude: Double?
            var deviceLongitude: Double?
            do {
                deviceLatitude = p0[0].deviceLocation?.currentBestLocation?.latitude
                deviceLongitude = p0[0].deviceLocation?.currentBestLocation?.longitude
            } while (deviceLatitude == null || deviceLongitude == null)
            return listOf(deviceLatitude, deviceLongitude)
        }

        override fun onPostExecute(geolocation: List<Double>) {
            activityWeakReference.get()!!.fetchVenues(deviceLatitude = geolocation[0], deviceLongitude = geolocation[1])
            super.onPostExecute(geolocation)
        }
    }
}