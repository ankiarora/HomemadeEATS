package com.android.homemadeEATS.views.fragment.common

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Address
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat.checkSelfPermission
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.homemadeEATS.R
import com.android.homemadeEATS.databinding.FragmentSetLocationBinding
import com.android.homemadeEATS.viewmodel.common.AddressViewModel
import com.android.homemadeEATS.views.activity.common.AddressActivity
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_set_location.*
import java.io.IOException
import java.util.*


class SetLocationFragment : BaseFragment(), OnMapReadyCallback {

    private lateinit var currentLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private val permissionCode = 101
    private lateinit var activity: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activity = context

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        viewModel = ViewModelProvider(this).get(AddressViewModel::class.java)

        val binding = DataBindingUtil.inflate<FragmentSetLocationBinding>(
            inflater,
            R.layout.fragment_set_location,
            container,
            false
        ).apply {
            viewModel = this@SetLocationFragment.viewModel as AddressViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())
        fetchLocation()
        handleButtonClickListeners()

    }

    private fun handleButtonClickListeners() {
        btnSaveLocation.setOnClickListener {
            (requireActivity() as AddressActivity).addFragment(AddressFragment())
        }
    }


    private fun fetchLocation() {
        if (checkSelfPermission(
                activity, Manifest.permission.ACCESS_FINE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED && checkSelfPermission(
                activity, Manifest.permission.ACCESS_COARSE_LOCATION
            ) !=
            PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), permissionCode
            )
            return
        }
        val task = fusedLocationProviderClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                Toast.makeText(
                    activity, currentLocation.latitude.toString() + "" +
                            currentLocation.longitude, Toast.LENGTH_SHORT
                ).show()
                val supportMapFragment =
                    childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                supportMapFragment?.getMapAsync(this)
                getLocationName(currentLocation.latitude, currentLocation.longitude)
            } else {
                val mLocationRequest = LocationRequest.create()
                mLocationRequest.interval = 60000
                mLocationRequest.fastestInterval = 5000
                mLocationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                val mLocationCallback: LocationCallback = object : LocationCallback() {
                    override fun onLocationResult(locationResult: LocationResult) {
                        for (location in locationResult.locations) {
                            if (location != null) {
                                currentLocation = location
                                val supportMapFragment =
                                    (activity as AddressActivity).supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
                                supportMapFragment?.getMapAsync(this@SetLocationFragment)
                                getLocationName(currentLocation.latitude, currentLocation.longitude)
                            }
                        }
                    }
                }
                LocationServices.getFusedLocationProviderClient(context)
                    .requestLocationUpdates(mLocationRequest, mLocationCallback, null)
            }
        }
    }

    private fun getLocationName(latitude: Double, longitude: Double) {
        val geocoder = Geocoder(activity, Locale.getDefault())
        try {
            val addresses = geocoder.getFromLocation(latitude, longitude, 1)
            val obj: Address = addresses[0]
            AddressViewModel.mapsSelectedAddress.value = obj.getAddressLine(0)
            AddressViewModel.latitude.value = obj.latitude.toString()
            AddressViewModel.longitude.value = obj.longitude.toString()
            AddressViewModel.city.value = obj.subAdminArea?.toString()
            AddressViewModel.state.value = obj.adminArea?.toString()
            AddressViewModel.pincode.value = obj.postalCode?.toString()
            AddressViewModel.streetOrBuildingName.value = obj.subLocality?.toString()
        } catch (exception: IOException) {
            Toast.makeText(activity, exception.message, Toast.LENGTH_SHORT).show();
        }
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val latLng = LatLng(currentLocation.latitude, currentLocation.longitude)
        val markerOptions = MarkerOptions().position(latLng).title("I am here!")
        markerOptions.draggable(true)
        googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        googleMap?.addMarker(markerOptions)
        googleMap?.setOnMarkerDragListener(dragListener)
    }

    private val dragListener = object : GoogleMap.OnMarkerDragListener {
        override fun onMarkerDragStart(p0: Marker?) {
            //do nothing
        }

        override fun onMarkerDrag(p0: Marker?) {
            //do nothing
        }

        override fun onMarkerDragEnd(marker: Marker?) {
            Toast.makeText(
                activity, marker?.position?.latitude.toString() + "" +
                        marker?.position?.longitude, Toast.LENGTH_SHORT
            ).show()
            marker?.position?.latitude?.let {
                marker.position?.longitude?.let { it1 ->
                    getLocationName(
                        it,
                        it1
                    )
                }
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            permissionCode -> if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {
                fetchLocation()
            }
        }
    }

}