package com.superChargedFitness.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.messaging.FirebaseMessaging
import com.superChargedFitness.R
import com.superChargedFitness.adapter.WorkoutCategoryAdapter
import com.superChargedFitness.databinding.ActivityHomeBinding
import com.superChargedFitness.interfaces.CallbackListener
import com.superChargedFitness.interfaces.ConfirmDialogCallBack
import com.superChargedFitness.utils.ConstantString
import com.superChargedFitness.viewmodel.HomeViewModel

class HomeActivity : BaseActivity(), View.OnClickListener, CallbackListener {
    private lateinit var mContext: Context
    private lateinit var binding: ActivityHomeBinding
    private val viewModel: HomeViewModel by viewModels()

    override fun onBackPressed() {
        confirmationDialog(this, object : ConfirmDialogCallBack {
            override fun Okay() {
                val homeIntent = Intent(Intent.ACTION_MAIN)
                homeIntent.addCategory(Intent.CATEGORY_HOME)
                homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(homeIntent)
                finishAffinity()
            }

            override fun cancel() {

            }

        }, "", getString(R.string.exit_confirmation))
    }

    private fun subScribeToFirebaseTopic() {
        FirebaseMessaging.getInstance().subscribeToTopic("superChargedFitness_topic")
                .addOnCompleteListener { task ->
                    if (!task.isSuccessful) {
                        Log.e("subScribeFirebaseTopic", ": Fail")
                    } else {
                        Log.e("subScribeFirebaseTopic", ": Success")
                    }
                }
    }

    /* Todo Objects*/
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        mContext = this

        setupHomeData()
        successCall()
        subScribeToFirebaseTopic()
    }

    @SuppressLint("WrongConstant")
    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.imgbtnDrawer -> drawerLayout.openDrawer(Gravity.START)
        }
    }


    private fun setupHomeData() {
        binding.rcyWorkout.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        binding.rcyWorkout.addItemDecoration(com.superChargedFitness.utils.Utils.SimpleDividerItemDecoration(this))

        viewModel.workoutCategories.observe(this) { categories ->
            val workoutAdapter = WorkoutCategoryAdapter(mContext, ArrayList(categories))
            binding.rcyWorkout.adapter = workoutAdapter
        }
    }


    private fun loadOpenAppAd() {
        (this.application as? com.superChargedFitness.utils.AppControl)?.showAdIfAvailable(
            this,
            object : com.superChargedFitness.utils.AppControl.OnShowAdCompleteListener {
                override fun onShowAdComplete() {

                }
            })
    }



    private fun successCall() {

        if (isNetworkConnected()) {
            if (ConstantString.ENABLE_DISABLE == ConstantString.ENABLE) {

            } else {
                com.superChargedFitness.utils.Utils.setPref(this@HomeActivity, ConstantString.STATUS_ENABLE_DISABLE, ConstantString.ENABLE_DISABLE)
            }

        } else {
            openInternetDialog(this, true)
        }

    }


    fun setAppAdId(id: String?) {
        try {
            val applicationInfo =
                    packageManager.getApplicationInfo(packageName, PackageManager.GET_META_DATA)
            val bundle = applicationInfo.metaData
            val beforeChangeId = bundle.getString("com.google.android.gms.ads.APPLICATION_ID")
            Log.e("TAG", "setAppAdId:BeforeChange:::::  $beforeChangeId")
            applicationInfo.metaData.putString("com.google.android.gms.ads.APPLICATION_ID", id)
            val AfterChangeId = bundle.getString("com.google.android.gms.ads.APPLICATION_ID")
            Log.e("TAG", "setAppAdId:AfterChange::::  $AfterChangeId")
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NullPointerException) {
            e.printStackTrace()
        }
    }

    override fun onSuccess() {

    }

    override fun onCancel() {

    }

    override fun onRetry() {

    }
}
