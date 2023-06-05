package com.android.homemadeEATS.views.fragment.common

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.android.homemadeEATS.R
import com.android.homemadeEATS.databinding.FragmentMoreBinding
import com.android.homemadeEATS.viewmodel.MoreViewModel
import com.android.homemadeEATS.views.activity.common.LoginActivity
import com.android.homemadeEATS.views.activity.common.PastOrdersActivity
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.fragment_more.*
import java.io.IOException


class MoreFragment : BaseFragment() {
    private var sharedPref: SharedPreferences? = null
    lateinit var activity: Context

    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.activity = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(MoreViewModel::class.java)
        val binding = DataBindingUtil.inflate<FragmentMoreBinding>(
            inflater,
            R.layout.fragment_more,
            container,
            false
        )
        binding.apply {
            viewModel = this@MoreFragment.viewModel as MoreViewModel
            lifecycleOwner = viewLifecycleOwner
        }
        sharedPref = activity.getSharedPreferences(
            getString(R.string.pref_file_key), Context.MODE_PRIVATE
        )
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        logout.setOnClickListener {
            //clear are cached data
            with(sharedPref?.edit()) {
                this?.putString(
                    getString(R.string.pref_user_token),
                    ""
                )
                this?.apply()
            }
            disableFCM()
            viewModel.clearAllData()

            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

            startActivity(intent)
            requireActivity().finish()
        }

        viewProfile.setOnClickListener {
            val viewProfileFragment = UserProfileFragment()
            viewProfileFragment.show(childFragmentManager, "")
        }

        viewPastOrders.setOnClickListener {
            val intent = Intent(activity, PastOrdersActivity::class.java)
            startActivity(intent)
        }
    }

    private fun disableFCM() {
        // Disable auto init
        FirebaseMessaging.getInstance().isAutoInitEnabled = false
        Thread {
            try {
                // Remove InstanceID initiate to unsubscribe all topic
                // TODO: May be a better way to use FirebaseMessaging.getInstance().unsubscribeFromTopic()
                FirebaseInstanceId.getInstance().deleteInstanceId()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }.start()
    }
}