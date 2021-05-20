package bangkit.daya.app.ar

import android.Manifest
import android.app.ActivityManager
import android.content.Context
import android.content.pm.PackageManager
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.getSystemService
import androidx.navigation.fragment.findNavController
import bangkit.daya.R
import bangkit.daya.base.ApplicationArFragment
import bangkit.daya.databinding.FragmentArFeatureBinding
import bangkit.daya.model.Place
import bangkit.daya.model.getPositionVector
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.ar.sceneform.AnchorNode
import org.koin.android.viewmodel.ext.android.viewModel

class ArFeatureFragment : Fragment(), SensorEventListener {

    private lateinit var binding: FragmentArFeatureBinding
    private val arFeatureViewModel: ArFeatureViewModel by viewModel()

    private lateinit var arFragment: ApplicationArFragment
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private lateinit var sensorManager: SensorManager
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading = FloatArray(3)
    private val rotationMatrix = FloatArray(9)
    private val orientationAngles = FloatArray(3)

    private var anchorNode: AnchorNode? = null
    private var markers: MutableList<Marker> = emptyList<Marker>().toMutableList()
    private var places: List<Place>? = null
    private var currentLocation: Location? = null
    private var map: GoogleMap? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        if (!isSupportedDevice()) {
            return null
        }
        binding = FragmentArFeatureBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initProperty()
        setupLocation()
        setupAr()
    }

    override fun onResume() {
        super.onResume()
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.also { sensor ->
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { sensor ->
            sensorManager.registerListener(
                this,
                sensor,
                SensorManager.SENSOR_DELAY_NORMAL
            )
        }
    }

    override fun onPause() {
        super.onPause()
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) {
            return
        }
        if (event.sensor.type == Sensor.TYPE_ACCELEROMETER) {
            System.arraycopy(event.values, 0, accelerometerReading, 0, accelerometerReading.size)
        } else if (event.sensor.type == Sensor.TYPE_MAGNETIC_FIELD) {
            System.arraycopy(event.values, 0, magnetometerReading, 0, magnetometerReading.size)
        }

        // Update rotation matrix, which is needed to update orientation angles.
        SensorManager.getRotationMatrix(
            rotationMatrix,
            null,
            accelerometerReading,
            magnetometerReading
        )
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun initProperty() {
        arFragment = childFragmentManager.findFragmentById(R.id.ar_fragment) as ApplicationArFragment

        sensorManager = requireActivity().getSystemService()!!
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    private fun isSupportedDevice(): Boolean {
        val activityManager = requireActivity().getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val openGlVersionString = activityManager.deviceConfigurationInfo.glEsVersion
        if (openGlVersionString.toDouble() < 3.0) {
            Toast.makeText(requireContext(), "Sceneform requires OpenGL ES 3.0 or later", Toast.LENGTH_LONG)
                .show()
            findNavController().navigateUp()
            return false
        }
        return true
    }

    private fun setupLocation() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requireActivity().requestPermissions(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION
                    , Manifest.permission.ACCESS_COARSE_LOCATION), LOCATION_REQUEST_CODE)
            return
        }
        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
            currentLocation = location
            arFeatureViewModel.getNearbyPlaces(location).observe(viewLifecycleOwner) { places ->
                this@ArFeatureFragment.places = places
            }
        }.addOnFailureListener {
            Log.e(TAG, "Could not get location")
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == LOCATION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                setupLocation()
            } else {
                findNavController().navigateUp()
                Toast.makeText(requireContext(), "Permission denied to read your External storage", Toast.LENGTH_SHORT).show();
            }
            return
        }
    }

    private fun setupAr() {
        arFragment.setOnTapArPlaneListener { hitResult, _, _ ->
            val anchor = hitResult.createAnchor()
            anchorNode = AnchorNode(anchor)
            anchorNode?.setParent(arFragment.arSceneView.scene)
            addPlaces(anchorNode!!)
        }
    }

    private fun addPlaces(anchorNode: AnchorNode) {
        val currentLocation = currentLocation
        if (currentLocation == null) {
            Log.w(TAG, "Location has not been determined yet")
            return
        }

        val places = places
        if (places == null) {
            Log.w(TAG, "No places to put")
            return
        }

        for (place in places) {
            // Add the place in AR
            val placeNode = PlaceNode(requireContext(), place)
            placeNode.setParent(anchorNode)
            placeNode.localPosition = place.getPositionVector(orientationAngles[0], currentLocation.latLng)
            placeNode.setOnTapListener { _, _ ->
                showInfoWindow(place)
            }

            // Add the place in maps
            map?.let {
                val marker = it.addMarker(
                    MarkerOptions()
                        .position(place.geometry.location.latLng)
                        .title(place.name)
                )
                marker?.let { mark ->
                    mark.tag = place
                    markers.add(mark)
                }
            }
        }
    }

    private fun showInfoWindow(place: Place) {
        val matchingPlaceNode = anchorNode?.children?.filterIsInstance<PlaceNode>()?.first {
            val otherPlace = (it).place ?: return@first false
            return@first otherPlace == place
        }
        matchingPlaceNode?.showInfoWindow()

//        // Show as marker
//        val matchingMarker = markers.firstOrNull {
//            val placeTag = (it.tag as? Place) ?: return@firstOrNull false
//            return@firstOrNull placeTag == place
//        }
//        matchingMarker?.showInfoWindow()
    }

    companion object {
        private const val TAG = "<RESULT>"
        private const val LOCATION_REQUEST_CODE = 101
    }
}

val Location.latLng: LatLng
    get() = LatLng(this.latitude, this.longitude)