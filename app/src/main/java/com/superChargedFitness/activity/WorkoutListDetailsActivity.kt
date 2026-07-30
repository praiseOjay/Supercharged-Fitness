package com.superChargedFitness.activity

import android.content.ActivityNotFoundException
import android.content.Context
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.activity.OnBackPressedCallback
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import android.os.Build
import com.bumptech.glide.Glide
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.AdView
import com.superChargedFitness.R
import com.superChargedFitness.databinding.ActivityWorkoutListDetailsBinding
import com.superChargedFitness.pojo.PWorkOutDetails
import com.superChargedFitness.utils.ConstantString

class WorkoutListDetailsActivity : BaseActivity() {

    private val workoutViewModel: com.superChargedFitness.viewmodel.WorkoutViewModel by viewModels()

    lateinit var workOutCategoryData: ArrayList<PWorkOutDetails>
    lateinit var mContext: Context
    private var currentPos: Int = 0
    private var typeOfControl:String = ""
    private lateinit var binding: ActivityWorkoutListDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityWorkoutListDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        mContext = this

        window.statusBarColor = ContextCompat.getColor(this, R.color.colorGrayTrans)
        @Suppress("DEPRECATION", "UNCHECKED_CAST")
        workOutCategoryData = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(ConstantString.key_workout_list_array, ArrayList::class.java) as ArrayList<PWorkOutDetails>
        } else {
            intent.getSerializableExtra(ConstantString.key_workout_list_array) as ArrayList<PWorkOutDetails>
        }
        currentPos = intent.getIntExtra(ConstantString.key_workout_list_pos, 0)
        typeOfControl = intent.getStringExtra(ConstantString.key_workout_details_type) as String

        defaultSetup()

        initAction()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                finish()
                overridePendingTransition(R.anim.none, R.anim.slide_down)
            }
        })
    }



    /* Todo Common settings methods */
    private fun defaultSetup() {
        val doWorkOutPgrAdpt = DoWorkoutPagerAdapter()
        binding.viewPagerWorkoutDetails.adapter = doWorkOutPgrAdpt
        binding.viewPagerWorkoutDetails.currentItem = currentPos
        binding.imgbtnDone.text = (1 + currentPos).toString().plus(" / ").plus(workOutCategoryData.size)

        if(typeOfControl != ConstantString.val_is_workout_list_activity){
            binding.rltBottomControl.visibility = View.GONE
        }

    }

    private fun initAction() {
        binding.imgbtnBack.setOnClickListener {
            onBackPressed()
            //overridePendingTransition(R.anim.none, R.anim.slide_down)
        }
        binding.imgbtnNext.setOnClickListener {
            if (binding.viewPagerWorkoutDetails.currentItem < workOutCategoryData.size)
                binding.viewPagerWorkoutDetails.currentItem = binding.viewPagerWorkoutDetails.currentItem + 1
        }
        binding.imgbtnPrev.setOnClickListener {
            if (binding.viewPagerWorkoutDetails.currentItem > 0)
                binding.viewPagerWorkoutDetails.currentItem = binding.viewPagerWorkoutDetails.currentItem - 1
        }
        // Observe video link results from ViewModel
        workoutViewModel.videoLink.observe(this) { strVideoLink ->
            try {
                if (strVideoLink.isNotEmpty()) {
                    val str = "https://www.youtube.com/watch?v=$strVideoLink"
                    openYoutube(str)
                } else {
                    Toast.makeText(this, getString(R.string.error_video_not_exist), Toast.LENGTH_SHORT).show()
                }
            } catch (e: ActivityNotFoundException) {
                e.printStackTrace()
                Toast.makeText(this, "Youtube player not available on this device", Toast.LENGTH_LONG).show()
            }
        }

        binding.imgbtnVideo.setOnClickListener {
            val title = com.superChargedFitness.utils.Utils.ReplaceSpacialCharacters(workOutCategoryData[binding.viewPagerWorkoutDetails.currentItem].title)
            workoutViewModel.loadVideoLink(title)
        }
        binding.viewPagerWorkoutDetails.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onPageScrollStateChanged(p0: Int) {
                //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onPageSelected(pos: Int) {
                binding.imgbtnDone.text = (1 + pos).toString().plus(" / ").plus(workOutCategoryData.size)
            }
        })
    }

    override fun onBackPressed() {
        @Suppress("DEPRECATION")
        super.onBackPressed()
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
