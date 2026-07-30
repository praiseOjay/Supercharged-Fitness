package com.superChargedFitness.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.superChargedFitness.R
import com.superChargedFitness.pojo.PWorkOutCategory
import com.superChargedFitness.utils.ConstantString

/**
 * ViewModel for HomeActivity.
 * Builds the static workout category list that was previously constructed
 * inline in HomeActivity.setupHomeData().
 */
class HomeViewModel : ViewModel() {

    private val _workoutCategories = MutableLiveData<List<PWorkOutCategory>>()
    val workoutCategories: LiveData<List<PWorkOutCategory>> = _workoutCategories

    init {
        _workoutCategories.value = buildCategoryList()
    }

    private fun buildCategoryList(): List<PWorkOutCategory> {
        val list = ArrayList<PWorkOutCategory>()

        // 7 X 4 Challenge header
        list.add(category(ConstantString.main, "7 X 4 Challenge", "", 0, 0, 0, ""))

        // Full Body / Lower Body
        list.add(category(ConstantString.full_body, "Full Body", "7 X 4 Challenge", 0, 0, R.drawable.full_body, ConstantString.tbl_full_body_workouts_list))
        list.add(category(ConstantString.full_body, "Lower Body", "7 X 4 Challenge", 0, 0, R.drawable.lower_body, ConstantString.tbl_lower_body_list))

        // Chest
        list.add(category(ConstantString.main, "Chest", "", 0, 0, 0, ""))
        list.add(category(ConstantString.biginner, "Chest", "Beginners", R.color.color_beginner, 0, R.drawable.chest_beginner, ConstantString.tbl_chest_beginner))
        list.add(category(ConstantString.intermediate, "Chest", "Intermediate", R.color.color_intermediate, 0, R.drawable.chest_intermediate, ConstantString.tbl_chest_intermediate))
        list.add(category(ConstantString.advance, "Chest", "Advanced", R.color.color_advance, 0, R.drawable.chest_advanced, ConstantString.tbl_chest_advanced))

        // Abs
        list.add(category(ConstantString.main, "Abs", "", 0, 0, 0, ""))
        list.add(category(ConstantString.biginner, "Abs", "Beginner", R.color.color_beginner, 0, R.drawable.abs_beginner, ConstantString.tbl_abs_beginner))
        list.add(category(ConstantString.intermediate, "Abs", "Intermediate", R.color.color_intermediate, 0, R.drawable.abs_intermediate, ConstantString.tbl_abs_intermediate))
        list.add(category(ConstantString.advance, "Abs", "Advanced", R.color.color_advance, 0, R.drawable.abs_advanced, ConstantString.tbl_abs_advanced))

        // Arm
        list.add(category(ConstantString.main, "Arm", "", 0, 0, 0, ""))
        list.add(category(ConstantString.biginner, "Arm", "Beginner", R.color.color_beginner, 0, R.drawable.arm_beginner, ConstantString.tbl_arm_beginner))
        list.add(category(ConstantString.intermediate, "Arm", "Intermediate", R.color.color_intermediate, 0, R.drawable.abs_intermediate, ConstantString.tbl_arm_intermediate))
        list.add(category(ConstantString.advance, "Arm", "Advanced", R.color.color_advance, 0, R.drawable.abs_advanced, ConstantString.tbl_arm_advanced))

        // Shoulder & Back
        list.add(category(ConstantString.main, "Shoulder & Back", "", 0, 0, 0, ""))
        list.add(category(ConstantString.biginner, "Shoulder & Back", "Beginner", R.color.color_beginner, 0, R.drawable.shoulder_beginner, ConstantString.tbl_shoulder_back_beginner))
        list.add(category(ConstantString.intermediate, "Shoulder & Back", "Intermediate", R.color.color_intermediate, 0, R.drawable.shoulder_intermediate, ConstantString.tbl_shoulder_back_intermediate))
        list.add(category(ConstantString.advance, "Shoulder & Back", "Advanced", R.color.color_advance, 0, R.drawable.shoulder_advanced, ConstantString.tbl_shoulder_back_advanced))

        // Leg
        list.add(category(ConstantString.main, "Leg", "", 0, 0, 0, ""))
        list.add(category(ConstantString.biginner, "Leg", "Beginner", R.color.color_beginner, 0, R.drawable.leg_beginner, ConstantString.tbl_leg_beginner))
        list.add(category(ConstantString.intermediate, "Leg", "Intermediate", R.color.color_intermediate, 0, R.drawable.leg_intermediate, ConstantString.tbl_leg_intermediate))
        list.add(category(ConstantString.advance, "Leg", "Advanced", R.color.color_advance, 0, R.drawable.leg_advanced, ConstantString.tbl_leg_advanced))

        return list
    }

    private fun category(
        level: String, name: String, sub: String,
        bg: Int, typeImg: Int, img: Int, table: String
    ): PWorkOutCategory {
        val cat = PWorkOutCategory()
        cat.catDefficultyLevel = level
        cat.catName = name
        cat.catSubCategory = sub
        cat.catDetailsBg = bg
        cat.catTypeImage = typeImg
        cat.catImage = img
        cat.catTableName = table
        return cat
    }
}
