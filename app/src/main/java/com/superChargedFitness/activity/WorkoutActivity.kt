package com.superChargedFitness.activity

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import androidx.viewpager.widget.ViewPager
import com.ankushgrover.hourglass.Hourglass
import com.bumptech.glide.Glide
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import com.superChargedFitness.R
import com.superChargedFitness.database.DataHelper
import com.superChargedFitness.interfaces.AdsCallback
import com.superChargedFitness.pojo.PWorkOutDetails
import com.superChargedFitness.utils.CommonUtility
import com.superChargedFitness.utils.ConstantString
import kotlinx.android.synthetic.main.activity_workout.*
import java.util.*

class WorkoutActivity : com.superChargedFitness.activity.BaseActivity(), AdsCallback {
    lateinit var pWorkoutList: ArrayList<PWorkOutDetails>
    lateinit var mContext: Context
    private var recycleWorkIndicatorAdapter: RecycleWorkIndicatorAdapter? = null
    var recycleWorkoutTimeIndicatorAdapter: RecycleWorkoutTimeIndicatorAdapter? = null
    var adClickCount: Int = 1
    var timeCountDown = 0
    var timer: Timer? = null
    var flagTimerPause: Boolean = false
    var textToSpeech: TextToSpeech? = null
    var boolSound: Boolean = true
    var tableName: String = ""
    lateinit var txtTimer: TextView
    private var startTime: Long = 0
    private var running = false
    private var currentTime: Long = 0
    private var totalSec = 0
    internal var progress = 0
    private var countDownTimer: Hourglass? = null
    var timeTotal: String = ""
    lateinit var pDialog : Dialog


    override fun onBackPressed() {
//        super.onBackPressed()
        confirmToExitDialog()

    }

    private fun confirmToExitDialog() {
        flagTimerPause = true
        val dialog = Dialog(this)
        dialog.setCancelable(true)
        dialog.setContentView(R.layout.dl_exercise_exit)
        dialog.window!!.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val imgbtnClose = dialog.findViewById(R.id.imgbtnClose) as ImageView
        val btnQuite = dialog.findViewById(R.id.btnQuite) as Button
        val btnContinue = dialog.findViewById(R.id.btnContinue) as Button


        imgbtnClose.setOnClickListener {
            try {
                flagTimerPause = false
                dialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        /*btnQuite.setOnClickListener {
            if (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.EXIT_BTN_COUNT, 1) == 2) {
                if (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.AD_TYPE_FB_GOOGLE, "") == ConstantString.AD_GOOGLE &&
                        com.superChargedFitness.utils.Utils.getPref(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
                    CommonConstantAd.showInterstitialAdsGoogle(mContext,object : AdsCallback {
                        override fun adLoadingFailed() {
                            quiteData(dialog)
                        }

                        override fun adClose() {
                            quiteData(dialog)
                        }

                        override fun startNextScreen() {
                            quiteData(dialog)
                        }
                        override fun onLoaded() {

                        }

                    })
                } else if (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.AD_TYPE_FB_GOOGLE, "") == ConstantString.AD_FACEBOOK &&
                        com.superChargedFitness.utils.Utils.getPref(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
                    CommonConstantAd.showInterstitialAdsFacebook(object : AdsCallback {
                        override fun adLoadingFailed() {
                            quiteData(dialog)
                        }

                        override fun adClose() {
                            quiteData(dialog)
                        }

                        override fun startNextScreen() {
                            quiteData(dialog)
                        }
                        override fun onLoaded() {

                        }

                    })
                } else {
                    quiteData(dialog)
                }
                com.superChargedFitness.utils.Utils.setPref(this, ConstantString.EXIT_BTN_COUNT, 1)
            } else {
                if (adClickCount == 1) {
                    com.superChargedFitness.utils.Utils.setPref(this, ConstantString.EXIT_BTN_COUNT, 2)
                }
                quiteData(dialog)
            }

//            quiteData(dialog)
        }*/

        btnContinue.setOnClickListener {
            try {
                flagTimerPause = false
                dialog.dismiss()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }


        dialog.setOnCancelListener {
            flagTimerPause = false
        }
        dialog.show()
    }

    fun quiteData(dialog: Dialog) {
        try {
            saveData()
            finish()
            dialog.dismiss()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_workout)

        window.statusBarColor = resources.getColor(R.color.colorGrayTrans)
        mContext = this

        try {
            pWorkoutList = intent.getSerializableExtra(ConstantString.workout_list) as ArrayList<PWorkOutDetails>
            tableName = intent.getStringExtra(ConstantString.work_table_name)!!
        } catch (e: Exception) {
            e.printStackTrace()
        }

        pDialog=adDimLightProgressDialog(mContext)

        defaultSetup()

        initAction()

        /*if (com.superChargedFitness.utils.Utils.getPref(this,ConstantString.AD_TYPE_FB_GOOGLE,"") == ConstantString.AD_GOOGLE
                && com.superChargedFitness.utils.Utils.getPref(this,ConstantString.STATUS_ENABLE_DISABLE,"") == ConstantString.ENABLE) {
            CommonConstantAd.googlebeforloadAd(this)
        } else if (com.superChargedFitness.utils.Utils.getPref(this,ConstantString.AD_TYPE_FB_GOOGLE,"") == ConstantString.AD_FACEBOOK
                && com.superChargedFitness.utils.Utils.getPref(this,ConstantString.STATUS_ENABLE_DISABLE,"") == ConstantString.ENABLE) {
            CommonConstantAd.facebookbeforeloadFullAd(this)
        }*/
    }

    /* Todo Common methods */
    private fun defaultSetup() {
        txtTimer = findViewById(R.id.txtTimer)
        start()
        boolSound = com.superChargedFitness.utils.Utils.getPref(mContext, ConstantString.key_workout_sound, true)
        if (boolSound) {
            imgSound.setImageResource(R.drawable.ic_sound_on)
        } else {
            imgSound.setImageResource(R.drawable.ic_sound_off)
        }

        recycleWorkIndicatorAdapter = RecycleWorkIndicatorAdapter()
        val layoutManager = FlexboxLayoutManager()
        layoutManager.flexWrap = FlexWrap.NOWRAP
        rcyWorkoutStatus.layoutManager = layoutManager
        rcyWorkoutStatus.adapter = recycleWorkIndicatorAdapter

        val doWorkOutPgrAdpt = DoWorkoutPagerAdapter()
        viewPagerWorkout.adapter = doWorkOutPgrAdpt
//        viewPagerWorkout.currentItem = 0
        val currentItemPosition = com.superChargedFitness.utils.Utils.getPref(this,ConstantString.PREF_LAST_UN_COMPLETE_DAY+"_"+tableName+"_"+pWorkoutList[0].workout_id,0)
        viewPagerWorkout.currentItem = currentItemPosition
        workoutSetup(0)
    }

    fun start() {
        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                setTime()
                handler.postDelayed(this, 1000)
            }
        }, 1000)
        this.startTime = System.currentTimeMillis()
        this.running = true
    }

    private fun setTime() {
        /*if (getElapsedTimeMin() == 0L) {
            txtTimer.text = "0" + getElapsedTimeMin() + ":" + String.format("%02d", getElapsedTimeSecs())
        } else {
            if (getElapsedTimeMin() + "".length > 1) {
                txtTimer.text = "" + getElapsedTimeMin() + ":" + String.format("%02d", getElapsedTimeSecs())
            } else {
                txtTimer.text = "0" + getElapsedTimeMin() + ":" + String.format("%02d", getElapsedTimeSecs())
            }
        }*/

        try {
            if (!flagTimerPause) {
                totalSec += 1
                txtTimer.text = CommonUtility.secToTime(totalSec)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun pauseWorkOutTime() {
        this.running = false
        currentTime = System.currentTimeMillis() - startTime
    }

    private fun resumeWorkOutTime() {
        this.running = true
        this.startTime = System.currentTimeMillis() - currentTime
    }

    private fun setupCountDown() {
        pauseWorkOutTime()
        progress = 0
        countDownTimer = object : Hourglass(30000, 1000) {
            override fun onTimerTick(timeRemaining: Long) {
            }

            override fun onTimerFinish() {
                resumeWorkOutTime()
            }
        }
        (countDownTimer as Hourglass).startTimer()
    }

    override fun onPause() {
        super.onPause()
        pauseWorkOutTime()
    }

    //Time in seconds
    private fun getElapsedTimeSecs(): Long {
        var elapsed: Long = 0
        if (running) {
            elapsed = (System.currentTimeMillis() - startTime) / 1000 % 60
        }
        return elapsed
    }

    //Time in minutes
    private fun getElapsedTimeMin(): Long {
        var elapsed: Long = 0
        if (running) {
            elapsed = (System.currentTimeMillis() - startTime) / 1000 / 60 % 60
        }
        return elapsed
    }

    private fun initAction() {
        imgBack.setOnClickListener {
            finish()
        }

        imgbtnNext.setOnClickListener {
            workoutCompleted(viewPagerWorkout.currentItem + 1)
//            viewPagerWorkout.currentItem = viewPagerWorkout.currentItem + 1
        }

        imgbtnPrev.setOnClickListener {
            workoutCompleted(viewPagerWorkout.currentItem - 1)
//            viewPagerWorkout.currentItem = viewPagerWorkout.currentItem - 1
        }

        imgbtnDone.setOnClickListener {
            workoutCompleted(viewPagerWorkout.currentItem + 1)
        }

        imgbtnPause.setOnClickListener {
            showWorkoutDetails()
        }

        imgInfo.setOnClickListener {
            showWorkoutDetails()
        }

        imgVideo.setOnClickListener {
            try {
                val dbHelper = DataHelper(mContext)
                val strVideoLink = dbHelper.getVideoLink(com.superChargedFitness.utils.Utils.ReplaceSpacialCharacters(pWorkoutList[viewPagerWorkout.currentItem].title))
                if (strVideoLink != "") {
                    flagTimerPause = true
                    val str = "https://www.youtube.com/watch?v=$strVideoLink"
                    openYoutube(str)

                    //                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=$strVideoLink")))
                } else {
                    Toast.makeText(this, getString(R.string.error_video_not_exist), Toast.LENGTH_SHORT).show()
                }
            } catch (e: ActivityNotFoundException){
                e.printStackTrace()
                Toast.makeText(this,"Youtube player not available on this device",Toast.LENGTH_LONG).show()
            }

//            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/watch?v=" + strVideoLink)))
        }

        imgSound.setOnClickListener {
            boolSound = com.superChargedFitness.utils.Utils.getPref(mContext, ConstantString.key_workout_sound, true)
            if (boolSound) {
                imgSound.setImageResource(R.drawable.ic_sound_off)
                boolSound = false
                com.superChargedFitness.utils.Utils.setPref(mContext, ConstantString.key_workout_sound, boolSound)
            } else {
                imgSound.setImageResource(R.drawable.ic_sound_on)
                boolSound = true
                com.superChargedFitness.utils.Utils.setPref(mContext, ConstantString.key_workout_sound, boolSound)
            }
        }

        viewPagerWorkout.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageScrollStateChanged(p0: Int) {
            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {
            }

            override fun onPageSelected(pos: Int) {
                workoutSetup(pos)
                if (boolSound) {
                    if (textToSpeech != null) {
                        textToSpeech!!.setSpeechRate(1.0f)
                        textToSpeech!!.speak(pWorkoutList[viewPagerWorkout.currentItem].title.toLowerCase().replace("ups", "up's"), TextToSpeech.QUEUE_FLUSH, null);
                    }
                }
            }

        })
    }

    /* Todo show current workout details */
    private fun showWorkoutDetails() {
        flagTimerPause = true
        val intent = Intent(mContext, WorkoutListDetailsActivity::class.java)
        intent.putExtra(ConstantString.key_workout_details_type, ConstantString.val_is_workout)
        intent.putExtra(ConstantString.key_workout_list_array, pWorkoutList)
        intent.putExtra(ConstantString.key_workout_list_pos, viewPagerWorkout.currentItem)
        mContext.startActivity(intent)
        overridePendingTransition(R.anim.slide_up, R.anim.none)
    }

    /* Todo set current workout setup */
    private fun workoutSetup(pos: Int) {
        val pworkDetails: PWorkOutDetails = pWorkoutList[pos]
        if (timer != null) {
            timer?.cancel()
        }

        if (pworkDetails.time_type == ConstantString.workout_type_time) {
            rltStepTypeWorkOut.visibility = View.GONE
            rltTimeTypeWorkOut.visibility = View.VISIBLE
            startWorkoutTimer(pworkDetails.time.substring(pworkDetails.time.indexOf(":") + 1).toInt())
        } else {
            rltStepTypeWorkOut.visibility = View.VISIBLE
            rltTimeTypeWorkOut.visibility = View.GONE
        }
        recycleWorkIndicatorAdapter?.notifyDataSetChanged()
    }

    /* Todo workout completed method */
    private fun workoutCompleted(pos: Int) {
        if (viewPagerWorkout.currentItem == (pWorkoutList.size - 1)) {
            timeTotal = txtTimer.text.toString()
            pDialog.show()

            //if (com.superChargedFitness.utils.Utils.getPref(this,ConstantString.AD_TYPE_FB_GOOGLE,"") == ConstantString.AD_GOOGLE &&
            //        com.superChargedFitness.utils.Utils.getPref(this,ConstantString.STATUS_ENABLE_DISABLE,"") == ConstantString.ENABLE) {
            //    CommonConstantAd.showInterstitialAdsGoogle(this,this)
            //} else if (com.superChargedFitness.utils.Utils.getPref(this,ConstantString.AD_TYPE_FB_GOOGLE,"") == ConstantString.AD_FACEBOOK &&
             //       com.superChargedFitness.utils.Utils.getPref(this,ConstantString.STATUS_ENABLE_DISABLE,"") == ConstantString.ENABLE) {
             //   CommonConstantAd.showInterstitialAdsFacebook(this)
            //}else{
                nextActivityStart()
           //



        } else {
            flagTimerPause = true
            val intent = Intent(mContext, NextPrevDetailsWorkoutActivity::class.java)
            intent.putExtra(ConstantString.key_workout_list_pos, pos)
            intent.putExtra(ConstantString.key_workout_list_array, pWorkoutList)
            intent.putExtra(ConstantString.extra_workout_list_pos, (viewPagerWorkout.currentItem + 1))
            startActivity(intent)
            viewPagerWorkout.currentItem = pos
        }
    }

    private fun nextActivityStart() {
        finish()
        val intent = Intent(mContext, CompletedActivity::class.java)
        intent.putExtra(ConstantString.workout_list, pWorkoutList)
        intent.putExtra("Duration", timeTotal)
        intent.putExtra(ConstantString.workout_id_from_workactivity,pWorkoutList[0].workout_id.toString())
        intent.putExtra(ConstantString.table_name_from_workactivity,tableName)
        startActivity(intent)
        pauseWorkOutTime()
        setupCountDown()
    }

    /* Todo Workout Timing method */
    private fun startWorkoutTimer(totalTime: Int) {
        timeCountDown = 0
        txtTimeCountDown.text = "".plus(timeCountDown.toString()).plus(" / ").plus(totalTime.toString())

        recycleWorkoutTimeIndicatorAdapter = RecycleWorkoutTimeIndicatorAdapter(totalTime)
        val layoutManagerTimer = FlexboxLayoutManager()
        layoutManagerTimer.flexWrap = FlexWrap.NOWRAP
        rcyBottomWorkoutTimeStatus.layoutManager = layoutManagerTimer
        rcyBottomWorkoutTimeStatus.adapter = recycleWorkoutTimeIndicatorAdapter

        val handler = Handler()
        timer = Timer(false)

        val timerTask = object : TimerTask() {
            override fun run() {
                handler.post {
                    if (!flagTimerPause) {
                        timeCountDown++
//                    viewPagerWorkout.setCurrentItem(timeCountDown)
                        txtTimeCountDown.text = "".plus(timeCountDown.toString()).plus(" / ").plus(totalTime.toString())
                        recycleWorkoutTimeIndicatorAdapter?.notifyDataSetChanged()

                        if (timeCountDown.equals(totalTime)) {
                            timer?.cancel()
                            workoutCompleted(viewPagerWorkout.currentItem + 1)
                            //viewPagerWorkout.currentItem = viewPagerWorkout.currentItem + 1
                        }
                    }
                }
            }
        }
        timer?.schedule(timerTask, 1000, 1000)
    }

    /* Todo Override methods */
    override fun onResume() {
        super.onResume()
        resumeWorkOutTime()
        textToSpeech = TextToSpeech(mContext, TextToSpeech.OnInitListener { status ->
            if (status != TextToSpeech.ERROR) {
                textToSpeech!!.language = Locale.US
                if (boolSound) {
                    textToSpeech!!.setSpeechRate(1.0f)
                    textToSpeech!!.speak(pWorkoutList[viewPagerWorkout.currentItem].title.toLowerCase().replace("ups", "up's"), TextToSpeech.QUEUE_FLUSH, null);
                }
            }
        })
        flagTimerPause = false
    }

    override fun onDestroy() {
        super.onDestroy()
        if (textToSpeech != null) {
            textToSpeech!!.stop();
            textToSpeech!!.shutdown();
        }
    }

    /* Todo here define adapter */
    inner class RecycleWorkIndicatorAdapter : RecyclerView.Adapter<RecycleWorkIndicatorAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(mContext).inflate(R.layout.row_of_recycleview, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            /*if (viewPagerWorkout.currentItem > position) {
                holder.viewIndicator.setBackgroundColor(mContext.resources.getColor(R.color.color_theme))
            } else {
                holder.viewIndicator.setBackgroundColor(mContext.resources.getColor(R.color.color_gray))
            }*/

            if (viewPagerWorkout.currentItem > position) {
                holder.viewIndicator.background = ContextCompat.getDrawable(mContext, R.drawable.view_line_theme)
            } else {
                holder.viewIndicator.background = ContextCompat.getDrawable(mContext, R.drawable.view_line_gray)
            }
        }

        override fun getItemCount(): Int {
            return pWorkoutList.size
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            internal var viewIndicator: View = itemView.findViewById(R.id.viewIndicator) as View

//            init {
//                viewIndicator = itemView.findViewById(R.id.viewIndicator) as View
//            }

        }
    }

    inner class RecycleWorkoutTimeIndicatorAdapter(val totalWorkOut: Int) : RecyclerView.Adapter<RecycleWorkoutTimeIndicatorAdapter.ViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(mContext).inflate(R.layout.row_of_recycle_time, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {

            if (timeCountDown > position) {
                holder.viewIndicator.setBackgroundColor(mContext.resources.getColor(R.color.color_theme))
            } else {
                holder.viewIndicator.setBackgroundColor(mContext.resources.getColor(R.color.color_gray))
            }
        }

        override fun getItemCount(): Int {
            return totalWorkOut
        }

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            internal var viewIndicator: View = itemView.findViewById(R.id.viewIndicator) as View
//            init {
//                viewIndicator = itemView.findViewById(R.id.viewIndicator) as View
//            }
        }
    }


    inner class DoWorkoutPagerAdapter : PagerAdapter() {

        override fun isViewFromObject(convertView: View, anyObject: Any): Boolean {
            return convertView === anyObject as RelativeLayout
        }

        override fun getCount(): Int {
            return pWorkoutList.size
        }

        private fun getItem(pos: Int): PWorkOutDetails {
            return pWorkoutList[pos]
        }

        override fun instantiateItem(container: ViewGroup, position: Int): Any {
            val item: PWorkOutDetails = getItem(position)
            val itemView = LayoutInflater.from(mContext).inflate(R.layout.start_workout_row, container, false)
//            val txtWorkoutTime: TextView = itemView.findViewById(R.id.txtWorkoutTime)
            val txtWorkoutTitle: TextView = itemView.findViewById(R.id.txtWorkoutTitle)
            val txtWorkoutDetails: TextView = itemView.findViewById(R.id.txtWorkoutDetails)
            val viewfliperWorkout: ViewFlipper = itemView.findViewById(R.id.viewfliperWorkout)

//            txtWorkoutTime.text = item.time
            if (item.time_type == ConstantString.workout_type_step) {
                txtWorkoutTitle.text = "x ".plus(item.time)
            } else {
                txtWorkoutTitle.text = item.time
            }

            txtWorkoutDetails.text = item.title

            viewfliperWorkout.removeAllViews()
            val listImg: ArrayList<String> = com.superChargedFitness.utils.Utils.getAssetItems(mContext, com.superChargedFitness.utils.Utils.ReplaceSpacialCharacters(item.title))

            for (i in 0 until listImg.size) {
                val imgview = ImageView(mContext)
//                Glide.with(mContext).load("//android_asset/burpee/".plus(i.toString()).plus(".png")).into(imgview)
                Glide.with(mContext).load(listImg.get(i)).into(imgview)
                imgview.layoutParams = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT)

                viewfliperWorkout.addView(imgview)
            }

            viewfliperWorkout.isAutoStart = true
            viewfliperWorkout.flipInterval = mContext.resources.getInteger(R.integer.viewfliper_animation)
            viewfliperWorkout.startFlipping()

            container.addView(itemView)
            return itemView
        }

        override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
            container.removeView(`object` as RelativeLayout)
        }

    }


    fun saveData() {
        com.superChargedFitness.utils.Utils.setPref(this, ConstantString.PREF_LAST_UN_COMPLETE_DAY + "_" + tableName + "_" + pWorkoutList[0].workout_id.toString(),
                viewPagerWorkout.currentItem)
    }

    override fun onStop() {
        saveData()
        flagTimerPause = true
        Log.e("TAG", "onStop::::::::: ")
        super.onStop()
    }

    override fun adLoadingFailed() {
        nextActivityStart()
    }

    override fun adClose() {
        nextActivityStart()
    }

    override fun startNextScreen() {
        nextActivityStart()
    }

    override fun onLoaded() {

    }
}

