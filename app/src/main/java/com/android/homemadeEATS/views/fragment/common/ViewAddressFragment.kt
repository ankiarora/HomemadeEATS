package com.android.homemadeEATS.views.fragment.common

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.homemadeEATS.R
import com.android.homemadeEATS.databinding.FragmentViewLocationBinding
import com.android.homemadeEATS.model.common.Address
import com.android.homemadeEATS.repository.common.UserProfileRepository
import com.android.homemadeEATS.viewmodel.common.AddressViewModel
import com.android.homemadeEATS.views.activity.common.AddressActivity
import com.android.homemadeEATS.views.activity.common.ViewAddressActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.android.synthetic.main.fragment_view_location.*

class ViewAddressFragment(val showBottomAddressButtons: Boolean, val selectedAddress: Address?) :
    BaseFragment(),
    OnMapReadyCallback {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(AddressViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentViewLocationBinding>(
            inflater,
            R.layout.fragment_view_location,
            container,
            false
        ).apply {
            viewModel = this@ViewAddressFragment.viewModel as AddressViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val supportMapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment?
        supportMapFragment?.getMapAsync(this)
        if (showBottomAddressButtons) {
            llBottomAddressButtons.visibility = View.VISIBLE
            btnContinue.visibility = View.GONE
        } else {
            llBottomAddressButtons.visibility = View.GONE
            btnContinue.visibility = View.VISIBLE
        }
        setAddressTextViews()
        btnAddAddress.setOnClickListener {
            val intent = Intent(requireActivity(), AddressActivity::class.java)
            intent.putExtra("userType", UserProfileRepository.userProfile?.userType)
            startActivity(intent)
        }

        btnSelectAddress.setOnClickListener {
            (requireActivity() as ViewAddressActivity).addFragment(SelectAddressFragment())
        }
    }

    private fun setAddressTextViews() {
        tvSaveAs.setText(selectedAddress?.saveAs)
        tvAddressValue.setText(selectedAddress?.address)
        tvCityValue.setText(selectedAddress?.city)
        tvStateValue.setText(selectedAddress?.state)
        tvPincodeValue.setText(selectedAddress?.pincode)
        tvLandmarkValue.setText(selectedAddress?.landmark)
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        val latitude = selectedAddress?.latitude
        val longitude = selectedAddress?.longitude
        val latLng = LatLng(latitude!!.toDouble(), longitude!!.toDouble())
        val markerOptions = MarkerOptions().position(latLng).title("I am here!")
        googleMap?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        googleMap?.addMarker(markerOptions)
    }
}