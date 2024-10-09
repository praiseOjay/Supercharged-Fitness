package com.superChargedFitness.adapter

import android.content.Context
import android.content.Intent
import android.util.Log

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import com.google.android.ads.nativetemplates.TemplateView

import com.superChargedFitness.R
import com.superChargedFitness.activity.WorkoutListActivity
import com.superChargedFitness.pojo.PWorkOutCategory
import com.superChargedFitness.utils.CommonConstantAd
import com.superChargedFitness.utils.ConstantString
import java.io.Serializable

class WorkoutCategoryAdapter(internal val mContext: Context, internal val arrWorkoutCategoryData: ArrayList<PWorkOutCategory>) : RecyclerView.Adapter<WorkoutCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, i: Int): ViewHolder {
        val convertView = LayoutInflater.from(mContext).inflate(R.layout.workout_row, parent, false)
        return ViewHolder(convertView)
    }

    override fun onBindViewHolder(holder: ViewHolder, pos: Int) {
        val item = getItem(pos)

        holder.imgWorkoutDeificulty.visibility = View.VISIBLE

        if(item.catDefficultyLevel.equals(ConstantString.biginner)){
            holder.imgWorkoutDeificulty.setImageResource(R.drawable.ic_beginner_level)
        } else if(item.catDefficultyLevel.equals(ConstantString.intermediate)){
            holder.imgWorkoutDeificulty.setImageResource(R.drawable.ic_intermediate_level)
        }else if(item.catDefficultyLevel.equals(ConstantString.advance)){
            holder.imgWorkoutDeificulty.setImageResource(R.drawable.ic_advanced_level)
        } else{
            holder.imgWorkoutDeificulty.visibility = View.GONE
        }

        if(item.catDefficultyLevel == ConstantString.main){
            holder.rltWorkOutTitle.visibility = View.VISIBLE
//            holder.nativeAd.visibility = View.GONE
            holder.RltWorkOutDetails.visibility = View.GONE
            holder.txtWorkoutTitle.text = item.catName
        }/*else if(item.catDefficultyLevel == ConstantString.nativeAd){
//            holder.nativeAd.visibility = View.VISIBLE
            holder.rltWorkOutTitle.visibility = View.GONE
            holder.RltWorkOutDetails.visibility = View.GONE
            CommonConstantAd.loadNativeAd(mContext,holder.nativeAd)
        }*/ else{
//            holder.nativeAd.visibility = View.GONE
            holder.rltWorkOutTitle.visibility = View.GONE
            holder.RltWorkOutDetails.visibility = View.VISIBLE
            holder.txtWorkoutCategoryTitle.text = item.catName
            holder.txtWorkoutDetails.text = item.catSubCategory
            Log.e("TAG", "onBindViewHolder:::item:::  "+item.catName+"  "+item.catSubCategory+"  "+item.catDetailsBg )
            if(item.catDetailsBg.equals(0)) {
                holder.txtWorkoutDetails.background = null
            } else{
                if (item.catSubCategory == ConstantString.beginnerColor || item.catSubCategory == ConstantString.beginnerColor2){
                    holder.txtWorkoutDetails.setBackgroundResource(R.drawable.bg_beginner_color)
                }else if (item.catSubCategory == ConstantString.intermediateColor){
                    holder.txtWorkoutDetails.setBackgroundResource(R.drawable.bg_intermediate_color)
                }else if (item.catSubCategory == ConstantString.advanceColor){
                    holder.txtWorkoutDetails.setBackgroundResource(R.drawable.bg_advance_color)
                }
//                holder.txtWorkoutDetails.setBackgroundResource(item.catDetailsBg)
            }
            if(!item.equals(0)){
                holder.imgWorkoutRow.setImageResource(item.catImage)
            }
        }
    }

    fun getItem(pos: Int): PWorkOutCategory {
        val item: PWorkOutCategory = arrWorkoutCategoryData[pos]
        return item
    }

    override fun getItemCount(): Int {
        return arrWorkoutCategoryData.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),View.OnClickListener {
        val rltWorkOutTitle: RelativeLayout = itemView.findViewById(R.id.rltWorkOutTitle)
        val RltWorkOutDetails: CardView = itemView.findViewById(R.id.RltWorkOutDetails)

        val txtWorkoutTitle: TextView = itemView.findViewById(R.id.txtWorkoutTitle)
        val txtWorkoutDetails: TextView = itemView.findViewById(R.id.txtWorkoutDetails)
        val txtWorkoutCategoryTitle: TextView = itemView.findViewById(R.id.txtWorkoutCategoryTitle)

        val imgWorkoutRow: ImageView = itemView.findViewById(R.id.imgWorkoutRow)
        val imgWorkoutDeificulty: ImageView = itemView.findViewById(R.id.imgWorkoutDeificulty)
//        val nativeAd: TemplateView = itemView.findViewById(R.id.nativeAd)

        init {
            itemView.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val item = getItem(position)
            if(!item.catDefficultyLevel.equals(ConstantString.main)) {
                val intent = Intent(mContext, WorkoutListActivity::class.java)
                intent.putExtra(ConstantString.key_workout_category_item, item as Serializable)
                mContext.startActivity(intent)
            }
        }

    }
}
