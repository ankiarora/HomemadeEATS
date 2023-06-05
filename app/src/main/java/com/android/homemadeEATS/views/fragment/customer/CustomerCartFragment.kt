package com.android.homemadeEATS.views.fragment.customer

import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.homemadeEATS.R
import com.android.homemadeEATS.adapters.CustomerMealsAdapter
import com.android.homemadeEATS.databinding.FragmentCustomerCartBinding
import com.android.homemadeEATS.model.common.MealAmount
import com.android.homemadeEATS.model.customer.CustomerMeal
import com.android.homemadeEATS.model.customer.CustomerOrderMeal
import com.android.homemadeEATS.repository.common.UserProfileRepository
import com.android.homemadeEATS.repository.customer.CustomerMealsRepository
import com.android.homemadeEATS.viewmodel.customer.CustomerMealsViewModel
import com.android.homemadeEATS.views.activity.common.ViewAddressActivity
import com.android.homemadeEATS.views.activity.customer.CustomerNavActivity
import com.android.homemadeEATS.views.fragment.common.BaseFragment
import com.android.homemadeEATS.views.fragment.common.ImageDisplayFragment
import com.razorpay.Checkout
import kotlinx.android.synthetic.main.customer_all_order_fragment.place_orders_list
import kotlinx.android.synthetic.main.fragment_customer_cart.*
import org.json.JSONException
import org.json.JSONObject
import kotlin.collections.ArrayList
import kotlin.math.roundToInt


class CustomerCartFragment : BaseFragment() {
    private lateinit var viewManager: LinearLayoutManager
    private lateinit var adapter: CustomerMealsAdapter
    private lateinit var activity: Context
    private var cartItemMealArray: ArrayList<CustomerMeal> = ArrayList()
    var price = 0

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activity = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(CustomerMealsViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentCustomerCartBinding>(
            inflater,
            R.layout.fragment_customer_cart,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@CustomerCartFragment.viewModel as CustomerMealsViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CustomerMealsAdapter(obj)
        place_orders_list.adapter = adapter
        viewManager = LinearLayoutManager(activity)
        place_orders_list.apply { layoutManager = viewManager }
        cartItemMealArray = CustomerMealsRepository.cartItems
        adapter.submitList(cartItemMealArray)
        updateUI()
        getLocation()
    }


    private fun getLocation() {
        (viewModel as CustomerMealsViewModel).getSelectedLocation().observe(
            viewLifecycleOwner
        ) { result ->
            if (result != null) {
                location.text = result.saveAs + " : " + result.mapsAddress
            }
        }

        (viewModel as CustomerMealsViewModel)._selectedLocation.observe(
            viewLifecycleOwner
        ) {
            val address = it
            llCustomerLocation.setOnClickListener {
                val intent = Intent(requireContext(), ViewAddressActivity::class.java)
                intent.putExtra("Selected Address", address)
                startActivity(intent)
            }
        }
    }

    private fun updateUI() {
        if(cartItemMealArray.isEmpty()){
            llCustomerCartItems.visibility = View.GONE
        }
        price = 0
        for (item in cartItemMealArray) {
            price += item.price!! * item.totalCartItems
        }
        val taxPrice = Integer.parseInt(taxCharges.text.toString())
        val total = price + taxPrice
        foodPrice.text = "Rs. $price"
        totalPrice.text = "Rs. $total"
        if (cartItemMealArray.isEmpty()) {
            summarizedCart.visibility = View.GONE
        }

        customerProceedOrder.setOnClickListener {
            val amount = (total.toFloat() * 100).roundToInt()
            val checkout = Checkout()
            checkout.setKeyID("rzp_test_jwkPvb83GeOE05")
            checkout.setImage(R.drawable.rzp_logo)
            val jsonObject = JSONObject()
            try {
                jsonObject.put("name", "DabbaAdda")
                jsonObject.put("description", "Food payment")
                jsonObject.put("theme.color", "#FF7F50")
                jsonObject.put("currency", "INR")
                jsonObject.put("amount", amount)
                jsonObject.put("prefill.contact", UserProfileRepository.userProfile?.phoneNumber)
                jsonObject.put("prefill.email", UserProfileRepository.userProfile?.email)

                val retryObj = JSONObject()
                retryObj.put("enabled", true)
                retryObj.put("max_count", 4)
                jsonObject.put("retry", retryObj)
//                checkout.open(context as CustomerNavActivity, jsonObject)
                orderItems()
            } catch (e: JSONException) {
                Log.e("", "Error in starting Razorpay Checkout", e);
            }
        }
    }

    fun orderItems() {
        // get customer selected location
        (viewModel as CustomerMealsViewModel)._selectedLocation.observe(
            viewLifecycleOwner
        ) {
            val mealDetail: ArrayList<MealAmount> = ArrayList()
            var cookId = ""
            for (meal in cartItemMealArray) {
                mealDetail.add(MealAmount(meal._id, meal.totalCartItems))
                cookId = meal.cookId.toString()
            }
            val customerOrderItem =
                CustomerOrderMeal(mealDetail, price, 30, 30, cookId, it!!)
            (viewModel as CustomerMealsViewModel).customerOrderItems(customerOrderItem).observe(
                viewLifecycleOwner
            ) { activeData ->
                Toast.makeText(requireContext(), activeData?.message, Toast.LENGTH_LONG)
                    .show()
                showOrderPlacesDialog()
                cartItemMealArray.clear()
                adapter.notifyDataSetChanged()
                CustomerMealsRepository.cartItems.clear()
            }
        }
    }

    private fun showOrderPlacesDialog() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Awesome!")
        builder.setMessage("Order Placed Successfully.")
            .setPositiveButton("Ok") { _: DialogInterface, _: Int ->
                summarizedCart.visibility = View.GONE
                (context as CustomerNavActivity).setCurrentFragment(CustomerAllOrdersFragment())
            }
        builder.setCancelable(false);
        builder.show()
    }

    private val obj = object : CustomerMealsAdapter.OnItemClickListener {
        override fun openImages(imagesList: ArrayList<Uri>) {
            val fragment = ImageDisplayFragment()
            val bundle = Bundle()
            bundle.putSerializable("images", imagesList)
            fragment.arguments = bundle
            fragment.show((context as CustomerNavActivity).supportFragmentManager, "")
        }

        override fun updateCartItems(item: CustomerMeal) {
            updateMealCartItems(item)
        }
    }

    private fun updateMealCartItems(item: CustomerMeal) {
        when {
            (cartItemMealArray.isEmpty()) -> {
                cartItemMealArray.add(item)
            }
            (cartItemMealArray[0].cookId == item.cookId) -> {
                var flag = 0
                for (meal in cartItemMealArray) {
                    if (meal._id == item._id) {
                        flag = 1
                        if (item.totalCartItems > 0)
                            cartItemMealArray[cartItemMealArray.indexOf(meal)] = item
                        else
                            cartItemMealArray.remove(meal)
                        break
                    }
                }
                if (flag == 0)
                    cartItemMealArray.add(item)
            }
        }
        updateUI()
        adapter.notifyDataSetChanged()
        val currentItems = CustomerMealsRepository.currentItemList.toMutableList()
        for (meal in currentItems) {
            if (meal._id == item._id) {
                currentItems[currentItems.indexOf(meal)] = item
                break
            }
        }
        (viewModel as CustomerMealsViewModel).setCurrentSetCustomerMealsList(
            currentItems,
            cartItemMealArray
        )
    }
}