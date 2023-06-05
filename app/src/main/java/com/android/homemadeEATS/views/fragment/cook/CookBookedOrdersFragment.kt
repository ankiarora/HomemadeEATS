package com.android.homemadeEATS.views.fragment.cook

import android.Manifest
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.homemadeEATS.R
import com.android.homemadeEATS.adapters.CookOrdersAdapter
import com.android.homemadeEATS.databinding.FragmentCookBookedOrdersBinding
import com.android.homemadeEATS.model.cook.CookNewOrder
import com.android.homemadeEATS.model.cook.OrderId
import com.android.homemadeEATS.viewmodel.common.OrdersViewModel
import com.android.homemadeEATS.views.activity.common.ExpandOrderActivity
import com.android.homemadeEATS.views.activity.cook.CookNavActivity
import com.android.homemadeEATS.views.fragment.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_cook_booked_orders.*
import kotlinx.android.synthetic.main.fragment_cook_booked_orders.no_of_orders


class CookBookedOrdersFragment : BaseFragment() {
    private val CALL_PHONE = 101
    private lateinit var customerPhoneNumber: String
    private lateinit var adapter: CookOrdersAdapter
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var activity: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activity = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(OrdersViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentCookBookedOrdersBinding>(
            inflater,
            R.layout.fragment_cook_booked_orders,
            container,
            false
        ).apply {
            viewModel = this@CookBookedOrdersFragment.viewModel as OrdersViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CookOrdersAdapter(context, CookBookedOrdersFragment::class.simpleName, obj)
        booked_orders_list.adapter = adapter
        viewManager = LinearLayoutManager(activity)
        booked_orders_list.apply { layoutManager = viewManager }
        subscribeUi()
    }

    private fun subscribeUi() {
        showProgressBar()
        (viewModel as OrdersViewModel).bookedOrders()
            .observe(
                viewLifecycleOwner,
                { activeData ->
                    if (activeData.newOrdersList!!.isEmpty()) {
                        llCookBookedOrders.visibility = View.GONE
                    }
                    hideProgressBar()
                    adapter.submitList(activeData.newOrdersList)
                    no_of_orders.text = activeData?.newOrdersList.size.toString()
                })
    }

    private var obj = object : CookOrdersAdapter.OnItemClickListenerAdaper {
        override fun openMealDetails(mealDetails: CookNewOrder?) {
            val intent = Intent(activity, ExpandOrderActivity::class.java)
            intent.putExtra("mealDetails", mealDetails?.mealDetails as ArrayList<*>)
            startActivity(intent)
        }

        override fun acceptRejectOrder(item: CookNewOrder?, orderAccepted: Boolean) {
            //do nothing
        }

        override fun orderPrepared(orderId: String?) {
            val order = OrderId(orderId!!)
            (viewModel as OrdersViewModel).orderPrepared(order)
                .observe(
                    viewLifecycleOwner,
                    {
                        it?.message?.let { it1 ->
                            showDialogBox(it1)
                        }
                        if (!it?.error.isNullOrEmpty()) {
                            Toast.makeText(context, it?.error, Toast.LENGTH_LONG).show()
                        }
                    })
        }

        private fun showDialogBox(message: String) {
            val builder: AlertDialog.Builder = AlertDialog.Builder(context)
            builder.setTitle(getString(R.string.great_dialog_title))
            builder.setMessage(message)
            builder.setPositiveButton("OK") { dialogInterface: DialogInterface, i: Int ->
                subscribeUi()
            }
            builder.setCancelable(false);
            builder.show()
        }

        override fun orderReceived(orderId: String?, otp: String) {
            //do nothing
        }

        override fun callCustomer(customerPhoneNumber: String?) {
            this@CookBookedOrdersFragment.customerPhoneNumber = customerPhoneNumber!!
            if (ContextCompat.checkSelfPermission(
                    context as CookNavActivity,
                    Manifest.permission.CALL_PHONE
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissions(arrayOf(Manifest.permission.CALL_PHONE), CALL_PHONE)
            } else {
                makeACall(customerPhoneNumber)
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
                    makeACall(this.customerPhoneNumber)
                } else {
                    Toast.makeText(context, "permission not granted", Toast.LENGTH_LONG).show()
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