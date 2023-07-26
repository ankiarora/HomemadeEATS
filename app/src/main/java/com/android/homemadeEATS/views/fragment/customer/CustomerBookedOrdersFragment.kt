package com.android.homemadeEATS.views.fragment.customer

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.homemadeEATS.R
import com.android.homemadeEATS.adapters.CustomerBookedMealItemsAdapter
import com.android.homemadeEATS.databinding.FragmentCustomerBookedOrdersBinding
import com.android.homemadeEATS.model.customer.CustomerBookedMeal
import com.android.homemadeEATS.viewmodel.customer.CustomerMealsViewModel
import com.android.homemadeEATS.views.activity.common.ExpandOrderActivity
import com.android.homemadeEATS.views.activity.customer.CustomerNavActivity
import com.android.homemadeEATS.views.fragment.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_customer_booked_orders.*


class CustomerBookedOrdersFragment : BaseFragment() {
    private val CALL_PHONE = 101
    private lateinit var cookPhoneNo: String
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var adapter: CustomerBookedMealItemsAdapter
    private lateinit var activity: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activity = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(CustomerMealsViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentCustomerBookedOrdersBinding>(
            inflater,
            R.layout.fragment_customer_booked_orders,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@CustomerBookedOrdersFragment.viewModel as CustomerMealsViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CustomerBookedMealItemsAdapter(obj)
        customerBookedItems.adapter = adapter
        viewManager = LinearLayoutManager(activity)
        customerBookedItems.apply { layoutManager = viewManager }
        subscribeUi()
    }

    private fun subscribeUi() {
        showProgressBar()
        (viewModel as CustomerMealsViewModel).bookedOrders()
            .observe(
                viewLifecycleOwner
            ) { activeData ->
                hideProgressBar()
                if (activeData.meals!!.isEmpty()) {
                    customerBookedItems.visibility = View.GONE
                } else
                    adapter.submitList(activeData.meals)
            }
    }

    private var obj = object : CustomerBookedMealItemsAdapter.OnItemClickListener {
        override fun openMealDetails(mealDetails: CustomerBookedMeal?) {
            val intent = Intent(activity, ExpandOrderActivity::class.java)
            intent.putExtra("mealDetails", mealDetails?.mealDetails as ArrayList<Parcelable>)
            startActivity(intent)
        }

        override fun refreshCustomerItemList() {
            subscribeUi()
        }

        override fun callCook(cookPhoneNumber: String?) {
            this@CustomerBookedOrdersFragment.cookPhoneNo = cookPhoneNumber!!
            if (ContextCompat.checkSelfPermission(
                    context as CustomerNavActivity,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), CALL_PHONE)
            } else {
                makeACall(cookPhoneNo)
            }
        }
    }

    private fun makeACall(customerPhoneNumber: String?) {
        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$customerPhoneNumber")
        startActivity(callIntent);
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>, grantResults: IntArray
    ) {
        when (requestCode) {
            CALL_PHONE -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    makeACall(this.cookPhoneNo)
                } else {
                    Toast.makeText(context, "Permission not granted", Toast.LENGTH_LONG).show()
                }
                return
            }
            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }
}