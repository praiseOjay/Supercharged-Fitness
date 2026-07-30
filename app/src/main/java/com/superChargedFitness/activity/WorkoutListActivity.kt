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
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.superChargedFitness.R
import com.superChargedFitness.adapter.WorkoutListAdapter
import com.superChargedFitness.databinding.ActivityWorkoutListBinding
import com.superChargedFitness.interfaces.AdsCallback
import com.superChargedFitness.pojo.PWorkOutCategory
import com.superChargedFitness.pojo.PWorkOutDetails
import com.superChargedFitness.utils.ConstantString
import com.superChargedFitness.viewmodel.WorkoutListViewModel

class WorkoutListActivity : BaseActivity(), AdsCallback {

    private lateinit var mContext: Context
    private lateinit var pWorkOutCategory: PWorkOutCategory
    private lateinit var workOutDetailData: ArrayList<PWorkOutDetails>
    var adClickCount: Int = 1
    private lateinit var binding: ActivityWorkoutListBinding
    private val viewModel: WorkoutListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        Log.e("TAG", "onCreate::::Getnavigationsize:::  "+getNavigationSize(this) )
        val param = binding.llMain.layoutParams as FrameLayout.LayoutParams
        param.setMargins(0, 0, 0, getNavigationSize(this))
        binding.llMain.layoutParams = param

        if (Build.VERSION.SDK_INT >= 23) {
            Log.e("TAG", "onCreate::::223 "+ Build.VERSION.SDK_INT)
            val w: Window = window
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        }

        mContext = this

        pWorkOutCategory = intent.getSerializableExtra(ConstantString.key_workout_category_item) as PWorkOutCategory

        defaultSetup()
        initAction()
    }



    /* Todo common methods */
    private fun defaultSetup() {
        binding.txtWorkoutListCategoryName.text = pWorkOutCategory.catName
        binding.txtWorkoutListCategoryDetails.text = pWorkOutCategory.catSubCategory
        binding.imgToolbarBack.setImageResource(pWorkOutCategory.catImage)

        when {
            ConstantString.biginner == pWorkOutCategory.catDefficultyLevel -> binding.imgWorkoutDificultyImage.setImageResource(R.drawable.ic_beginner_level)
            ConstantString.intermediate == pWorkOutCategory.catDefficultyLevel -> binding.imgWorkoutDificultyImage.setImageResource(R.drawable.ic_intermediate_level)
            ConstantString.advance == pWorkOutCategory.catDefficultyLevel -> binding.imgWorkoutDificultyImage.setImageResource(R.drawable.ic_advanced_level)
            else -> binding.imgWorkoutDificultyImage.visibility = View.GONE
        }

        binding.rcyWorkoutList.layoutManager = LinearLayoutManager(mContext, LinearLayoutManager.VERTICAL, false)

        // Observe workout details from ViewModel (loaded via Room)
        viewModel.workoutDetails.observe(this) { details ->
            workOutDetailData = details
            val workoutListAdapter = WorkoutListAdapter(mContext, workOutDetailData)
            binding.rcyWorkoutList.adapter = workoutListAdapter
        }

        // Load data via ViewModel
        viewModel.loadWorkoutDetails(pWorkOutCategory.catTableName)
    }

    private fun initAction() {
        binding.imgWorkOutListBack.setOnClickListener {
            finish()
        }
        binding.btnStartWorkout.setOnClickListener {
            if (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.START_BTN_COUNT, 1) == 1) {
                if (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
                    when (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.AD_TYPE_FB_GOOGLE, "")) {
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
        Log.d("WorkoutListActivity", "startExerciseActivity called")
        if (::workOutDetailData.isInitialized) {
            Log.d("WorkoutListActivity", "Starting WorkoutActivity with ${workOutDetailData.size} workouts")
            val intent = Intent(mContext, WorkoutActivity::class.java)
            intent.putExtra(ConstantString.workout_list, workOutDetailData)
            intent.putExtra(ConstantString.work_table_name, pWorkOutCategory.catTableName)
            startActivity(intent)
        } else {
            Log.e("WorkoutListActivity", "workOutDetailData not initialized!")
        }
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
