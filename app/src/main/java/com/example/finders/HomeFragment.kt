package com.example.finders

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.Fragment
import com.example.finders.databinding.FragmentHomeBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.Circle
import org.osmdroid.api.IMapController
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polygon
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {

    private val LOCATION_REQUEST_CODE = 0
    private var listObservations: List<ObservationResponse>? = null
    private val TAG = "HomeFragment"
    private  lateinit var mapView: MapView
    private lateinit var mapController: IMapController
    private lateinit var locationOverlay: MyLocationNewOverlay
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var navigationLine: Polyline? = null

    private val binding: FragmentHomeBinding by lazy {
        FragmentHomeBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = binding.root

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        managePermissions()
        setUpMap()
        var distance = 0;
            distance = Utility.distance
        if(distance >=0&&Utility.unitOfMeasurement!=null){
        Toast.makeText(requireContext(),"distance changed to "+distance+" unit of measurement is "+Utility.unitOfMeasurement,Toast.LENGTH_SHORT).show()}
        GetObservationData()
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }

    private fun GetObservationData() {
        val apiService = EBirdService()
        val call: Call<List<ObservationResponse>> = apiService.getObservations()
        call.enqueue(object : Callback<List<ObservationResponse>?> {
            override fun onResponse(
                call: Call<List<ObservationResponse>?>,
                response: Response<List<ObservationResponse>?>
            ) {

                Log.d(TAG, "onResponse: Entering")
                if (response.isSuccessful && response.body() != null) {
                    listObservations = response.body();

                    listObservations?.let { observations ->
                        for (observation in observations) {
                            Log.d(TAG, "onResponse: " + observation.sciName)
                            Log.d(TAG, "onResponse: " + observation.speciesCode)

                            addBirdSightingMarkerOne(observation,getUserLocation())
                        }
                    }
                } else {
                    Log.d(TAG, "onResponse: Unsuccessful")
                }
            }

            override fun onFailure(call: Call<List<ObservationResponse>?>, t: Throwable) {
                Log.d(TAG, "onFailure: ${t.message}")
            }
        })
    }

    private fun setUpMap() {
        Configuration.getInstance().load(this.requireContext(),
            PreferenceManager.getDefaultSharedPreferences(this.requireContext()))

        mapView = binding.MapViewOne
        mapController = mapView.controller

        mapView.setMultiTouchControls(true)

        //init the start point

        val startPoint = GeoPoint(-29.8587, 31.0218)
        mapController.setCenter(startPoint)
        mapController.setZoom(6.0)

        val locationProvider = GpsMyLocationProvider(this.requireContext())

        locationOverlay = MyLocationNewOverlay(locationProvider, mapView)
        locationOverlay.enableMyLocation()
        mapView.overlays.add(locationOverlay)


      }

    private fun isLocationPermissionGranted(): Boolean{
        val finelocation = ActivityCompat.checkSelfPermission(this.requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        val courselocation = ActivityCompat.checkSelfPermission(this.requireContext(),
            Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED

        return finelocation && courselocation
    }

    private fun managePermissions() {
        val requestPermissions = mutableListOf<String>()
        if (!isLocationPermissionGranted()) {


            requestPermissions.add(Manifest.permission.ACCESS_FINE_LOCATION)
            requestPermissions.add(Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        if (requestPermissions.isNotEmpty()) {
            requestPermissions(requestPermissions.toTypedArray(), LOCATION_REQUEST_CODE)


        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == LOCATION_REQUEST_CODE){
            if(grantResults.isNotEmpty()){
                for(result in grantResults){
                    Toast.makeText(this.requireContext(), "Permissions Granted", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun addBirdSightingMarkerOne(sighting: ObservationResponse, userLocation: GeoPoint) {
        val latitude = sighting.lat
        val longitude = sighting.lng

        val birdLocation = GeoPoint(latitude, longitude)


        // Calculate the distance between the bird sighting and the user's location
         var distanceInKm:Double = 0.0;

        if (Utility.unitOfMeasurement == "Miles"){
            distanceInKm = userLocation.distanceToAsDouble(birdLocation)/ 1609.34
       }
        if(Utility.unitOfMeasurement =="Km"){
            distanceInKm = userLocation.distanceToAsDouble(birdLocation) / 1000

        }


        // Check if the bird sighting is within the 100-kilometer radius
        if (distanceInKm <= Utility.distance) {
            // Add marker
            val marker = Marker(binding.MapViewOne)
            marker.position = birdLocation
            marker.icon = ResourcesCompat.getDrawable(resources, R.drawable.baseline_location_on_24, null)

            val speciesName = sighting.comName
            val scientificName = sighting.sciName
            val observationCount = sighting.howMany

            // Add a snippet to the marker to display additional information
            marker.snippet = "Species: $speciesName\nScientific Name: $scientificName\nObservation Count: $observationCount"

            marker.setOnMarkerClickListener { marker, mapView ->
                val geoPoint = birdLocation
                mapController.animateTo(geoPoint)
                val currentLocation = locationOverlay.myLocation
                currentLocation?.let {
                    drawNavigationLine(it, geoPoint)
                }
                drawNavigationLine(currentLocation, geoPoint)


                marker.showInfoWindow()

                true
            }

            mapView.overlays.add(marker)

        }
    }



    private fun drawNavigationLine(startPoint: GeoPoint, endPoint: GeoPoint) {
        if (navigationLine != null) {
            mapView.overlays.remove(navigationLine)
        }

        val waypoints = ArrayList<GeoPoint>()
        waypoints.add(startPoint)
        waypoints.add(endPoint)

        navigationLine = Polyline()
        navigationLine?.setPoints(waypoints)
        navigationLine?.width = 5f
        navigationLine?.color = Color.BLUE


        mapView.overlays.add(navigationLine)
    }

    fun getUserLocation(): GeoPoint {
        return locationOverlay.myLocation
    }


}
