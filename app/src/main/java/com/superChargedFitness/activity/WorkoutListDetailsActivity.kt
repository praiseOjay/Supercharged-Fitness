package com.superChargedFitness.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.bumptech.glide.Glide
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdView
import com.superChargedFitness.R
import com.superChargedFitness.database.DataHelper
import com.superChargedFitness.pojo.PWorkOutDetails
import com.superChargedFitness.utils.ConstantString
import kotlinx.android.synthetic.main.activity_workout_list_details.*

class WorkoutListDetailsActivity : BaseActivity() {

    lateinit var workOutCategoryData: ArrayList<PWorkOutDetails>
    lateinit var mContext: Context
    private var currentPos: Int = 0
    private var typeOfControl:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout_list_details)
        mContext = this

        window.statusBarColor = resources.getColor(R.color.colorGrayTrans)
        workOutCategoryData = intent.getSerializableExtra(ConstantString.key_workout_list_array) as ArrayList<PWorkOutDetails>
        currentPos = intent.getIntExtra(ConstantString.key_workout_list_pos, 0)
        typeOfControl = intent.getStringExtra(ConstantString.key_workout_details_type) as String

        defaultSetup()

        initAction()

        /*if (Utils.getPref(this, ConstantString.AD_TYPE_FB_GOOGLE, "") == ConstantString.AD_GOOGLE &&
                Utils.getPref(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
            CommonConstantAd.loadBannerGoogleAd(this, llAdView, ConstantString.GOOGLE_BANNER_TYPE_AD)
            llAdViewFacebook.visibility = View.GONE
            llAdView.visibility = View.VISIBLE
        } else if (Utils.getPref(this, ConstantString.AD_TYPE_FB_GOOGLE, "") == ConstantString.AD_FACEBOOK
                &&
                Utils.getPref(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
            llAdViewFacebook.visibility = View.VISIBLE
            llAdView.visibility = View.GONE
            CommonConstantAd.loadFacebookBannerAd(this, llAdViewFacebook)
        } else {
            llAdView.visibility = View.GONE
            llAdViewFacebook.visibility = View.GONE
        }*/

    }



    /* Todo Common settings methods */
    private fun defaultSetup() {
        val doWorkOutPgrAdpt = DoWorkoutPagerAdapter()
        viewPagerWorkoutDetails.adapter = doWorkOutPgrAdpt
        viewPagerWorkoutDetails.currentItem = currentPos
        imgbtnDone.text = (1 + currentPos).toString().plus(" / ").plus(workOutCategoryData.size)

        if(typeOfControl != ConstantString.val_is_workout_list_activity){
            rltBottomControl.visibility = View.GONE
        }

    }

    private fun initAction() {
        imgbtnBack.setOnClickListener {
            onBackPressed()
            //overridePendingTransition(R.anim.none, R.anim.slide_down)
        }
        imgbtnNext.setOnClickListener {
            if (viewPagerWorkoutDetails.currentItem < workOutCategoryData.size)
                viewPagerWorkoutDetails.currentItem = viewPagerWorkoutDetails.currentItem + 1
        }
        imgbtnPrev.setOnClickListener {
            if (viewPagerWorkoutDetails.currentItem > 0)
                viewPagerWorkoutDetails.currentItem = viewPagerWorkoutDetails.currentItem - 1
        }
        imgbtnVideo.setOnClickListener {
            try {
                val dbHelper = DataHelper(mContext)
                val strVideoLink = dbHelper.getVideoLink(com.superChargedFitness.utils.Utils.ReplaceSpacialCharacters(workOutCategoryData[viewPagerWorkoutDetails.currentItem].title))
                if(strVideoLink != "") {
                    val str = "https://www.youtube.com/watch?v=$strVideoLink"
                    openYoutube(str)
                } else{
                    Toast.makeText(this,getString(R.string.error_video_not_exist),Toast.LENGTH_SHORT).show()
                }
            } catch (e: ActivityNotFoundException){
                e.printStackTrace()
                Toast.makeText(this,"Youtube player not available on this device",Toast.LENGTH_LONG).show()
            }
//            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + strVideoLink)))
        }
        viewPagerWorkoutDetails.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onPageScrollStateChanged(p0: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onPageSelected(pos: Int) {
                imgbtnDone.text = (1 + pos).toString().plus(" / ").plus(workOutCategoryData.size)
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.none, R.anim.slide_down)
    }

    /* Todo adapter */
    inner class DoWorkoutPagerAdapter : PagerAdapter() {

        override fun isViewFromObject(convertView: View, anyObject: Any): Boolean {
            return convertView === anyObject as RelativeLayout
        }

        override fun getCount(): Int {
            return workOutCategoryData.size
        }

        private fun getItem(pos: Int): PWorkOutDetails {
            return workOutCategoryData[pos]
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val item: PWorkOutDetails = getItem(position)
            val itemView = LayoutInflater.from(mContext).inflate(R.layout.workout_details_row, container, false)
            val txtWorkoutTitle: TextView = itemView.findViewById(R.id.txtWorkoutTitle)
            val txtWorkoutDetails: TextView = itemView.findViewById(R.id.txtWorkoutDetails)
            val viewfliperWorkout: ViewFlipper = itemView.findViewById(R.id.imgWorkoutDemo)
            val adView: AdView = itemView.findViewById(R.id.adView)
            val nativeAd: TemplateView = itemView.findViewById(R.id.nativeAdDetail)

            com.superChargedFitness.utils.Utils.initAdd(mContext,adView)

            txtWorkoutTitle.text = item.title
            txtWorkoutDetails.text = item.descriptions.replace("\\n", System.getProperty("line.separator")).replace("\\r", "")

            viewfliperWorkout.removeAllViews()

            val listImg:ArrayList<String> = com.superChargedFitness.utils.Utils.getAssetItems(mContext,
                com.superChargedFitness.utils.Utils.ReplaceSpacialCharacters(item.title))
            for (i in 0 until listImg.size) {
                val imgview = ImageView(mContext)
//                Glide.with(mContext).load("//android_asset/burpee/".plus(i.toString()).plus(".png")).into(imgview)
                Glide.with(mContext).load(listImg.get(i)).into(imgview)

                val layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT)
                layoutParams.gravity = Gravity.START
                imgview.layoutParams = layoutParams

                // imgview.setLayoutParams(LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT))
                viewfliperWorkout.addView(imgview)
            }

            viewfliperWorkout.isAutoStart = true
            viewfliperWorkout.setFlipInterval(mContext.resources.getInteger(R.integer.viewfliper_animation))
            viewfliperWorkout.startFlipping()

            /*if (com.superChargedFitness.utils.Utils.getPref(mContext, ConstantString.AD_TYPE_FB_GOOGLE, "") == ConstantString.AD_GOOGLE &&
                com.superChargedFitness.utils.Utils.getPref(mContext, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
                CommonConstantAd.loadNativeAd(mContext,nativeAd)
            }*/

            container.addView(itemView)
            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as RelativeLayout)
        }
    }

}
