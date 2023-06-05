package com.android.homemadeEATS.views.fragment.cook

import android.Manifest
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.homemadeEATS.R
import com.android.homemadeEATS.adapters.CookOrdersAdapter
import com.android.homemadeEATS.databinding.FragmentPendingOrdersBinding
import com.android.homemadeEATS.model.cook.CookNewOrder
import com.android.homemadeEATS.model.cook.CookNewOrdersGet
import com.android.homemadeEATS.model.cook.OrderId
import com.android.homemadeEATS.viewmodel.common.OrdersViewModel
import com.android.homemadeEATS.views.activity.common.ExpandOrderActivity
import com.android.homemadeEATS.views.activity.cook.CookNavActivity
import com.android.homemadeEATS.views.fragment.common.BaseFragment
import kotlinx.android.synthetic.main.fragment_pending_orders.*


class CookPendingOrdersFragment : BaseFragment() {
    private val CALL_PHONE = 101
    private lateinit var customerPhoneNumber: String
    private lateinit var adapter: CookOrdersAdapter
    private lateinit var viewManager: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewModel = ViewModelProvider(this).get(OrdersViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentPendingOrdersBinding>(
            inflater,
            R.layout.fragment_pending_orders,
            container,
            false
        ).apply {
            viewModel = this@CookPendingOrdersFragment.viewModel as OrdersViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    private fun subscribeUi() {
        showProgressBar()
        (viewModel as OrdersViewModel).pendingOrders()
            .observe(
                (context as AppCompatActivity?)!!,
                { t: CookNewOrdersGet? ->
                    hideProgressBar()
                    if (t?.newOrdersList!!.isEmpty())
                        llCookPendingOrders.visibility = View.GONE
                    adapter.submitList(t.newOrdersList)
                    no_of_orders.text = t.newOrdersList.size.toString()
                }
            )
    }

    private var obj = object : CookOrdersAdapter.OnItemClickListenerAdaper {
        override fun openMealDetails(mealDetails: CookNewOrder?) {
            val intent = Intent(activity, ExpandOrderActivity::class.java)
            intent.putExtra("mealDetails", mealDetails?.mealDetails as ArrayList<Parcelable>)
            startActivity(intent)
        }

        override fun acceptRejectOrder(item: CookNewOrder?, orderAccepted: Boolean) {
            val orderId = OrderId(item!!.orderId)
            (viewModel as OrdersViewModel).acceptRejectOrder(orderId, orderAccepted)
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

        override fun orderPrepared(orderId: String?) {
            //do nothing
        }

        override fun orderReceived(orderId: String?, otp: String) {
            //do nothing
        }

        override fun callCustomer(customerPhoneNumber: String?) {
            this@CookPendingOrdersFragment.customerPhoneNumber = customerPhoneNumber!!
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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CookOrdersAdapter(context, CookPendingOrdersFragment::class.simpleName, obj)
        pending_orders_list.adapter = adapter
        viewManager = LinearLayoutManager(activity)
        pending_orders_list.apply { layoutManager = viewManager }
        subscribeUi()
    }

}