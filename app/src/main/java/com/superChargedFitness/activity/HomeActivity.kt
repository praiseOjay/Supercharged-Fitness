package com.superChargedFitness.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.messaging.FirebaseMessaging
import com.superChargedFitness.R
import com.superChargedFitness.adapter.WorkoutCategoryAdapter
import com.superChargedFitness.database.DataHelper
import com.superChargedFitness.interfaces.CallbackListener
import com.superChargedFitness.interfaces.ConfirmDialogCallBack
import com.superChargedFitness.pojo.PWorkOutCategory
import com.superChargedFitness.utils.AppControl
import com.superChargedFitness.utils.ConstantString
import kotlinx.android.synthetic.main.activity_home.*

class HomeActivity : BaseActivity(), View.OnClickListener, CallbackListener {
    private lateinit var mContext: Context

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
        setContentView(R.layout.activity_home)

        mContext = this
        DataHelper(mContext).getReadWriteDB()


        //setDefaultData()
        setupHomeData()


        successCall()


       /* if (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.AD_TYPE_FB_GOOGLE, "") == ConstantString.AD_GOOGLE &&
                com.superChargedFitness.utils.Utils.getPref(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
            CommonConstantAd.loadBannerGoogleAd(this, llAdView, ConstantString.GOOGLE_BANNER_TYPE_AD)
            llAdViewFacebook.visibility = View.GONE
            llAdView.visibility = View.VISIBLE
            loadOpenAppAd()

        } else if (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.AD_TYPE_FB_GOOGLE, "") == ConstantString.AD_FACEBOOK
                &&
                com.superChargedFitness.utils.Utils.getPref(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
            llAdViewFacebook.visibility = View.VISIBLE
            llAdView.visibility = View.GONE
            CommonConstantAd.loadFacebookBannerAd(this, llAdViewFacebook)
        } else {
            llAdView.visibility = View.GONE
            llAdViewFacebook.visibility = View.GONE
        }*/

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
        val arrWorkoutCategoryData: ArrayList<PWorkOutCategory> = ArrayList()

        var workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.main
        workoutCategoryDetails.catName = "7 X 4 Challenge"
        workoutCategoryDetails.catSubCategory = ""
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = 0
        workoutCategoryDetails.catTableName = ""
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.full_body
        workoutCategoryDetails.catName = "Full Body"
        workoutCategoryDetails.catSubCategory = "7 X 4 Challenge"
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.full_body
        workoutCategoryDetails.catTableName = ConstantString.tbl_full_body_workouts_list
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.full_body
        workoutCategoryDetails.catName = "Lower Body"
        workoutCategoryDetails.catSubCategory = "7 X 4 Challenge"
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.lower_body
        workoutCategoryDetails.catTableName = ConstantString.tbl_lower_body_list
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        /*workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.nativeAd
        workoutCategoryDetails.catName = ""
        workoutCategoryDetails.catSubCategory = ""
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = 0
        workoutCategoryDetails.catTableName = ""
        arrWorkoutCategoryData.add(workoutCategoryDetails)*/

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.main
        workoutCategoryDetails.catName = "Chest"
        workoutCategoryDetails.catSubCategory = ""
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = 0
        workoutCategoryDetails.catTableName = ""
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.biginner
        workoutCategoryDetails.catName = "Chest"
        workoutCategoryDetails.catSubCategory = "Beginners"
        workoutCategoryDetails.catDetailsBg = R.color.color_beginner
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.chest_beginner
        workoutCategoryDetails.catTableName = ConstantString.tbl_chest_beginner
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.intermediate
        workoutCategoryDetails.catName = "Chest"
        workoutCategoryDetails.catSubCategory = "Intermediate"
        workoutCategoryDetails.catDetailsBg = R.color.color_intermediate
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.chest_intermediate
        workoutCategoryDetails.catTableName = ConstantString.tbl_chest_intermediate
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.advance
        workoutCategoryDetails.catName = "Chest"
        workoutCategoryDetails.catSubCategory = "Advanced"
        workoutCategoryDetails.catDetailsBg = R.color.color_advance
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.chest_advanced
        workoutCategoryDetails.catTableName = ConstantString.tbl_chest_advanced
        arrWorkoutCategoryData.add(workoutCategoryDetails)

       /* workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.nativeAd
        workoutCategoryDetails.catName = ""
        workoutCategoryDetails.catSubCategory = ""
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = 0
        workoutCategoryDetails.catTableName = ""
        arrWorkoutCategoryData.add(workoutCategoryDetails)*/

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.main
        workoutCategoryDetails.catName = "Abs"
        workoutCategoryDetails.catSubCategory = ""
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = 0
        workoutCategoryDetails.catTableName = ""
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.biginner
        workoutCategoryDetails.catName = "Abs"
        workoutCategoryDetails.catSubCategory = "Beginner"
        workoutCategoryDetails.catDetailsBg = R.color.color_beginner
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.abs_beginner
        workoutCategoryDetails.catTableName = ConstantString.tbl_abs_beginner
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.intermediate
        workoutCategoryDetails.catName = "Abs"
        workoutCategoryDetails.catSubCategory = "Intermediate"
        workoutCategoryDetails.catDetailsBg = R.color.color_intermediate
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.abs_intermediate
        workoutCategoryDetails.catTableName = ConstantString.tbl_abs_intermediate
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.advance
        workoutCategoryDetails.catName = "Abs"
        workoutCategoryDetails.catSubCategory = "Advanced"
        workoutCategoryDetails.catDetailsBg = R.color.color_advance
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.abs_advanced
        workoutCategoryDetails.catTableName = ConstantString.tbl_abs_advanced
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        /*workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.nativeAd
        workoutCategoryDetails.catName = ""
        workoutCategoryDetails.catSubCategory = ""
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = 0
        workoutCategoryDetails.catTableName = ""
        arrWorkoutCategoryData.add(workoutCategoryDetails)*/

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.main
        workoutCategoryDetails.catName = "Arm"
        workoutCategoryDetails.catSubCategory = ""
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = 0
        workoutCategoryDetails.catTableName = ""
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.biginner
        workoutCategoryDetails.catName = "Arm"
        workoutCategoryDetails.catSubCategory = "Beginner"
        workoutCategoryDetails.catDetailsBg = R.color.color_beginner
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.arm_beginner
        workoutCategoryDetails.catTableName = ConstantString.tbl_arm_beginner
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.intermediate
        workoutCategoryDetails.catName = "Arm"
        workoutCategoryDetails.catSubCategory = "Intermediate"
        workoutCategoryDetails.catDetailsBg = R.color.color_intermediate
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.abs_intermediate
        workoutCategoryDetails.catTableName = ConstantString.tbl_arm_intermediate
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.advance
        workoutCategoryDetails.catName = "Arm"
        workoutCategoryDetails.catSubCategory = "Advanced"
        workoutCategoryDetails.catDetailsBg = R.color.color_advance
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.abs_advanced
        workoutCategoryDetails.catTableName = ConstantString.tbl_arm_advanced
        arrWorkoutCategoryData.add(workoutCategoryDetails)


        /*workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.nativeAd
        workoutCategoryDetails.catName = ""
        workoutCategoryDetails.catSubCategory = ""
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = 0
        workoutCategoryDetails.catTableName = ""
        arrWorkoutCategoryData.add(workoutCategoryDetails)*/

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.main
        workoutCategoryDetails.catName = "Shoulder & Back"
        workoutCategoryDetails.catSubCategory = ""
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = 0
        workoutCategoryDetails.catTableName = ""
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.biginner
        workoutCategoryDetails.catName = "Shoulder & Back"
        workoutCategoryDetails.catSubCategory = "Beginner"
        workoutCategoryDetails.catDetailsBg = R.color.color_beginner
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.shoulder_beginner
        workoutCategoryDetails.catTableName = ConstantString.tbl_shoulder_back_beginner
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.intermediate
        workoutCategoryDetails.catName = "Shoulder & Back"
        workoutCategoryDetails.catSubCategory = "Intermediate"
        workoutCategoryDetails.catDetailsBg = R.color.color_intermediate
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.shoulder_intermediate
        workoutCategoryDetails.catTableName = ConstantString.tbl_shoulder_back_intermediate
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.advance
        workoutCategoryDetails.catName = "Shoulder & Back"
        workoutCategoryDetails.catSubCategory = "Advanced"
        workoutCategoryDetails.catDetailsBg = R.color.color_advance
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.shoulder_advanced
        workoutCategoryDetails.catTableName = ConstantString.tbl_shoulder_back_advanced
        arrWorkoutCategoryData.add(workoutCategoryDetails)


       /* workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.nativeAd
        workoutCategoryDetails.catName = ""
        workoutCategoryDetails.catSubCategory = ""
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = 0
        workoutCategoryDetails.catTableName = ""
        arrWorkoutCategoryData.add(workoutCategoryDetails)*/

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.main
        workoutCategoryDetails.catName = "Leg"
        workoutCategoryDetails.catSubCategory = ""
        workoutCategoryDetails.catDetailsBg = 0
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = 0
        workoutCategoryDetails.catTableName = ""
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.biginner
        workoutCategoryDetails.catName = "Leg"
        workoutCategoryDetails.catSubCategory = "Beginner"
        workoutCategoryDetails.catDetailsBg = R.color.color_beginner
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.leg_beginner
        workoutCategoryDetails.catTableName = ConstantString.tbl_leg_beginner
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.intermediate
        workoutCategoryDetails.catName = "Leg"
        workoutCategoryDetails.catSubCategory = "Intermediate"
        workoutCategoryDetails.catDetailsBg = R.color.color_intermediate
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.leg_intermediate
        workoutCategoryDetails.catTableName = ConstantString.tbl_leg_intermediate
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        workoutCategoryDetails = PWorkOutCategory()
        workoutCategoryDetails.catDefficultyLevel = ConstantString.advance
        workoutCategoryDetails.catName = "Leg"
        workoutCategoryDetails.catSubCategory = "Advanced"
        workoutCategoryDetails.catDetailsBg = R.color.color_advance
        workoutCategoryDetails.catTypeImage = 0
        workoutCategoryDetails.catImage = R.drawable.leg_advanced
        workoutCategoryDetails.catTableName = ConstantString.tbl_leg_advanced
        arrWorkoutCategoryData.add(workoutCategoryDetails)

        rcyWorkout.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rcyWorkout.addItemDecoration(com.superChargedFitness.utils.Utils.SimpleDividerItemDecoration(this))

        val workoutAdapter = WorkoutCategoryAdapter(mContext, arrWorkoutCategoryData)
        rcyWorkout.adapter = workoutAdapter

    }


    private fun loadOpenAppAd() {
        (this.application as? AppControl)?.showAdIfAvailable(
            this,
            object : AppControl.OnShowAdCompleteListener {
                override fun onShowAdComplete() {

                }
            })
    }



    private fun successCall() {

        if (isNetworkConnected()) {
            if (ConstantString.ENABLE_DISABLE == ConstantString.ENABLE) {

               /* com.superChargedFitness.utils.Utils.setPref(this@HomeActivity, ConstantString.AD_TYPE_FB_GOOGLE, ConstantString.AD_TYPE_FACEBOOK_GOOGLE)
                com.superChargedFitness.utils.Utils.setPref(this@HomeActivity, ConstantString.FB_BANNER, ConstantString.FB_BANNER_ID)
                com.superChargedFitness.utils.Utils.setPref(this@HomeActivity, ConstantString.FB_INTERSTITIAL, ConstantString.FB_INTERSTITIAL_ID)
                com.superChargedFitness.utils.Utils.setPref(this@HomeActivity, ConstantString.GOOGLE_BANNER, ConstantString.GOOGLE_BANNER_ID)
                com.superChargedFitness.utils.Utils.setPref(this@HomeActivity, ConstantString.GOOGLE_INTERSTITIAL, ConstantString.GOOGLE_INTERSTITIAL_ID)
                com.superChargedFitness.utils.Utils.setPref(this@HomeActivity, ConstantString.GOOGLE_NATIVE, ConstantString.GOOGLE_NATIVE_ID)
                com.superChargedFitness.utils.Utils.setPref(this@HomeActivity, ConstantString.GOOGLE_OPEN_APP, ConstantString.GOOGLE_OPEN_APP_ID)
                com.superChargedFitness.utils.Utils.setPref(this@HomeActivity, ConstantString.STATUS_ENABLE_DISABLE, ConstantString.ENABLE_DISABLE)


                setAppAdId(ConstantString.GOOGLE_ADMOB_APP_ID)*/


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
