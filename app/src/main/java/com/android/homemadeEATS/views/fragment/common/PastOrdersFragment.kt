package com.android.homemadeEATS.views.fragment.common

import android.content.Intent
import android.os.Bundle
import android.os.Parcelable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.homemadeEATS.R
import com.android.homemadeEATS.adapters.CookOrdersAdapter
import com.android.homemadeEATS.databinding.FragmentPastOrdersBinding
import com.android.homemadeEATS.model.cook.CookNewOrder
import com.android.homemadeEATS.model.cook.CookNewOrdersGet
import com.android.homemadeEATS.viewmodel.common.OrdersViewModel
import com.android.homemadeEATS.views.activity.common.ExpandOrderActivity
import kotlinx.android.synthetic.main.fragment_past_orders.*
import kotlinx.android.synthetic.main.fragment_pending_orders.no_of_orders
import kotlinx.android.synthetic.main.fragment_pending_orders.pending_orders_list

class PastOrdersFragment : BaseFragment() {

    private lateinit var viewManager: LinearLayoutManager
    private lateinit var adapter: CookOrdersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(OrdersViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentPastOrdersBinding>(
            inflater,
            R.layout.fragment_past_orders,
            container,
            false
        ).apply {
            viewModel = this@PastOrdersFragment.viewModel as OrdersViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        return binding.root
    }

    private var obj = object : CookOrdersAdapter.OnItemClickListenerAdaper {
        override fun openMealDetails(mealDetails: CookNewOrder?) {
            val intent = Intent(activity, ExpandOrderActivity::class.java)
            intent.putExtra("mealDetails", mealDetails?.mealDetails as ArrayList<Parcelable>)
            startActivity(intent)
        }

        override fun acceptRejectOrder(item: CookNewOrder?, orderAccepted: Boolean) {
            //not required
        }

        override fun orderPrepared(orderId: String?) {
            //not required
        }

        override fun orderReceived(orderId: String?, otp: String) {
            //not required
        }

        override fun callCustomer(customerPhoneNumber: String?) {
            //not required
        }

    }

    private fun subscribeUi() {
        showProgressBar()
        (viewModel as OrdersViewModel).pastOrders()
            .observe(
                (context as AppCompatActivity?)!!
            ) { t: CookNewOrdersGet? ->
                hideProgressBar()
                if (t?.newOrdersList!!.isEmpty())
                    llPastOrders.visibility = View.GONE
                adapter.submitList(t.newOrdersList)
                no_of_orders.text = t.newOrdersList.size.toString()
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        adapter = CookOrdersAdapter(context, PastOrdersFragment::class.simpleName, obj)
        pending_orders_list.adapter = adapter
        viewManager = LinearLayoutManager(activity)
        pending_orders_list.apply { layoutManager = viewManager }
        subscribeUi()
    }
}