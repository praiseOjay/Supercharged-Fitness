package com.superChargedFitness.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.recyclerview.widget.LinearLayoutManager
import com.superChargedFitness.R
import com.superChargedFitness.adapter.WorkoutListAdapter
import com.superChargedFitness.database.DataHelper
import com.superChargedFitness.interfaces.AdsCallback
import com.superChargedFitness.pojo.PWorkOutCategory
import com.superChargedFitness.pojo.PWorkOutDetails
import com.superChargedFitness.utils.ConstantString
import kotlinx.android.synthetic.main.activity_workout_list.*

class WorkoutListActivity : BaseActivity(), AdsCallback {

    private lateinit var mContext: Context
    private lateinit var pWorkOutCategory: PWorkOutCategory
    private lateinit var workOutDetailData: ArrayList<PWorkOutDetails>
    var adClickCount: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        setContentView(R.layout.activity_workout_list)

        Log.e("TAG", "onCreate::::Getnavigationsize:::  "+getNavigationSize(this) )
        val param = llMain.layoutParams as FrameLayout.LayoutParams
        param.setMargins(0, 0, 0, getNavigationSize(this))
        llMain.layoutParams = param

        if (Build.VERSION.SDK_INT >= 23) {
            Log.e("TAG", "onCreate::::223 "+ Build.VERSION.SDK_INT)
            val w: Window = window
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        mContext = this

        pWorkOutCategory = intent.getSerializableExtra(ConstantString.key_workout_category_item) as PWorkOutCategory

        defaultSetup()
        initAction()

       /* if (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.AD_TYPE_FB_GOOGLE, "") == ConstantString.AD_GOOGLE &&
                com.superChargedFitness.utils.Utils.getPref(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
            CommonConstantAd.loadBannerGoogleAd(this, llAdView, ConstantString.GOOGLE_BANNER_TYPE_AD)
//            llAdViewFacebook.visibility = View.GONE
//            llAdView.visibility = View.VISIBLE
        } else if (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.AD_TYPE_FB_GOOGLE, "") == ConstantString.AD_FACEBOOK
                &&
                com.superChargedFitness.utils.Utils.getPref(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
//            llAdViewFacebook.visibility = View.VISIBLE
//            llAdView.visibility = View.GONE
            CommonConstantAd.loadFacebookBannerAd(this, llAdViewFacebook)
        } else {
            llAdView.visibility = View.GONE
            llAdViewFacebook.visibility = View.GONE
        }*/

    }



    /* Todo common methods */
    private fun defaultSetup() {
        txtWorkoutListCategoryName.text = pWorkOutCategory.catName
        txtWorkoutListCategoryDetails.text = pWorkOutCategory.catSubCategory
        imgToolbarBack.setImageResource(pWorkOutCategory.catImage)

        when {
            ConstantString.biginner == pWorkOutCategory.catDefficultyLevel -> imgWorkoutDificultyImage.setImageResource(R.drawable.ic_beginner_level)
            ConstantString.intermediate == pWorkOutCategory.catDefficultyLevel -> imgWorkoutDificultyImage.setImageResource(R.drawable.ic_intermediate_level)
            ConstantString.advance == pWorkOutCategory.catDefficultyLevel -> imgWorkoutDificultyImage.setImageResource(R.drawable.ic_advanced_level)
            else -> imgWorkoutDificultyImage.visibility = View.GONE
        }

        val data = DataHelper(mContext)
        workOutDetailData = data.getWorkOutDetails(pWorkOutCategory.catTableName)

        rcyWorkoutList.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)
        //rcyWorkoutList.addItemDecoration(Utils.SimpleDividerItemDecoration(this))

        val workoutListAdapter = WorkoutListAdapter(mContext, workOutDetailData)
        rcyWorkoutList.adapter = workoutListAdapter

    }

    private fun initAction() {
        imgWorkOutListBack.setOnClickListener {
            finish()
        }
        btnStartWorkout.setOnClickListener {
            if (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.START_BTN_COUNT, 1) == 1) {
                if (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
                    when (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.AD_TYPE_FB_GOOGLE, "")) {
                        //ConstantString.AD_GOOGLE -> {
                            //CommonConstantAd.showInterstitialAdsGoogle(this,this)
                        //}
                        //ConstantString.AD_FACEBOOK -> {
                            //CommonConstantAd.showInterstitialAdsFacebook(this)
                        //}
                        else -> {
                            startExerciseActivity()
                        }
                    }
                    com.superChargedFitness.utils.Utils.setPref(this, ConstantString.START_BTN_COUNT, 0)
                } else {
                    startExerciseActivity()
                }
            } else {
                if (adClickCount == 1) {
                    com.superChargedFitness.utils.Utils.setPref(this, ConstantString.START_BTN_COUNT, 1)
                }
                startExerciseActivity()
            }
        }
    }

    private fun startExerciseActivity(){
        val intent = Intent(mContext, WorkoutActivity::class.java)
        intent.putExtra(ConstantString.workout_list, workOutDetailData)
        intent.putExtra(ConstantString.work_table_name, pWorkOutCategory.catTableName)
        startActivity(intent)
    }

    override fun adLoadingFailed() {
        startExerciseActivity()
    }

    override fun adClose() {
        startExerciseActivity()
    }

    override fun startNextScreen() {
        startExerciseActivity()
    }

    override fun onLoaded() {

    }

}
