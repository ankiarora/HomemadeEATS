package com.android.homemadeEATS.views.fragment.customer

import android.content.Context
import android.content.DialogInterface
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.homemadeEATS.R
import com.android.homemadeEATS.adapters.CustomerMealsAdapter
import com.android.homemadeEATS.databinding.CustomerAllOrderFragmentBinding
import com.android.homemadeEATS.model.customer.CustomerMeal
import com.android.homemadeEATS.repository.customer.CustomerMealsRepository
import com.android.homemadeEATS.viewmodel.customer.CustomerMealsViewModel
import com.android.homemadeEATS.views.activity.customer.CustomerNavActivity
import com.android.homemadeEATS.views.fragment.common.BaseFragment
import com.android.homemadeEATS.views.fragment.common.ImageDisplayFragment
import kotlinx.android.synthetic.main.customer_all_order_fragment.*

class CustomerAllOrdersFragment : BaseFragment() {
    private var mealsList: MutableList<CustomerMeal> = ArrayList()
    private lateinit var adapter: CustomerMealsAdapter
    private var skip: Int = 0
    private var totalItems: Int = 0
    private var isScrolling: Boolean = false
    private var cartItemMealArray: ArrayList<CustomerMeal> = ArrayList()
    lateinit var activity: Context


    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activity = context
    }

    private lateinit var viewManager: LinearLayoutManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(CustomerMealsViewModel::class.java)
        val binding = DataBindingUtil.inflate<CustomerAllOrderFragmentBinding>(
            inflater,
            R.layout.customer_all_order_fragment,
            container,
            false
        ).apply {
            lifecycleOwner = viewLifecycleOwner
            viewModel = this@CustomerAllOrdersFragment.viewModel as CustomerMealsViewModel
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = CustomerMealsAdapter(obj)
        place_orders_list.adapter = adapter

        viewManager = LinearLayoutManager(activity)
        place_orders_list.apply { layoutManager = viewManager }

        recyclerViewListener()
        subscribeUi(adapter, skip)
        cartItemMealArray = CustomerMealsRepository.cartItems
        adapter.submitList(mealsList)
        handleSearchView()
        showBottomCartSheet()
    }

    val obj = object : CustomerMealsAdapter.OnItemClickListener {
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
            (cartItemMealArray[0].cookAddress._id != item.cookAddress._id) -> {
                showCartFullDialog(item)
            }
        }
        total_amount_of_items.text = item.cookFirstName.plus(" " + item.cookLastName)
        (viewModel as CustomerMealsViewModel).setCurrentSetCustomerMealsList(
            adapter.currentList,
            cartItemMealArray
        )
        showBottomCartSheet()
    }

    private fun showBottomCartSheet() {
        var totalMeals = 0
        for (customerMeal in CustomerMealsRepository.cartItems) {
            totalMeals += customerMeal.totalCartItems
        }
        if (totalMeals > 0)
            total_amount_of_items.text =
                CustomerMealsRepository.cartItems[0].cookFirstName.plus(" " + CustomerMealsRepository.cartItems[0].cookLastName)
        total_amount_of_meals.text = "Number of meals: $totalMeals"
        if (totalMeals == 0)
            bottom_cart_layout.visibility = View.GONE
        else
            bottom_cart_layout.visibility = View.VISIBLE
        bottom_cart_layout.setOnClickListener {
            (context as CustomerNavActivity).setCurrentFragment(CustomerCartFragment())
        }
    }

    private fun showCartFullDialog(item: CustomerMeal) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Cart Items")
        builder.setMessage("Clear your cart before adding a meal from different cook.")
            .setPositiveButton("Continue") { _: DialogInterface, _: Int ->
                for (cartItem in cartItemMealArray) {
                    cartItem.totalCartItems = 0
                }
                adapter.notifyDataSetChanged()
                cartItemMealArray.clear()
                updateMealCartItems(item)
            }
            .setNegativeButton("Cancel") { _: DialogInterface, _: Int ->
                item.totalCartItems = 0
                adapter.notifyDataSetChanged()
            }
        builder.setCancelable(false)
        builder.show()
    }

    private fun handleSearchView() {
        searchGoBtn.setOnClickListener {
            skip = 0
            mealsList.clear()
            adapter.notifyDataSetChanged()
            subscribeUi(adapter, skip)
        }
    }

    override fun onResume() {
        super.onResume()
        mealsList.clear()
    }

    private fun subscribeUi(adapter: CustomerMealsAdapter, skip: Int) {
        showProgressBar()
        (viewModel as CustomerMealsViewModel).getAllMeals(skip, searchItem.text.toString())
            .observe(
                viewLifecycleOwner,
                { activeData ->
                    hideProgressBar()
                    if (activeData.error!!.isNotEmpty())
                        Toast.makeText(context, activeData.error, Toast.LENGTH_LONG).show()
                    else {
                        loadMoreLayout.visibility = View.GONE
                        val meals: List<CustomerMeal> =
                            compareMealsWithLocallySavedMeals(activeData.meals)
                        mealsList.addAll(meals)
                        adapter.notifyDataSetChanged()
                    }
                })
    }

    private fun compareMealsWithLocallySavedMeals(meals: List<CustomerMeal>?): List<CustomerMeal> {
        val savedMealsMap: HashMap<String, Int> = HashMap()
        for (meal in cartItemMealArray) {
            savedMealsMap[meal._id] = meal.totalCartItems
        }

        for (meal in meals!!) {
            if (savedMealsMap.containsKey(meal._id) && savedMealsMap[meal._id] != meal.totalCartItems) {
                meal.totalCartItems = savedMealsMap[meal._id]!!
            }
        }
        return meals
    }

    private fun recyclerViewListener() {
        place_orders_list.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL) {
                    isScrolling = true
                }
            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                totalItems = viewManager.itemCount
                if (isScrolling && viewManager.findLastCompletelyVisibleItemPosition() == totalItems - 1) {
                    isScrolling = false
                    skip += 5
                    loadMoreLayout.visibility = View.VISIBLE
                    subscribeUi(adapter, skip)
                }
            }
        })
    }

}