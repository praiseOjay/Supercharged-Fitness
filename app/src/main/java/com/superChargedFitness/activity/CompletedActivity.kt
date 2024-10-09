package com.superChargedFitness.activity

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.FileProvider
import com.superChargedFitness.R
import com.superChargedFitness.pojo.PWorkOutDetails
import com.superChargedFitness.utils.ConstantString
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException

open class CompletedActivity : BaseActivity() {

    lateinit var context: Context
    lateinit var pWorkoutList: ArrayList<PWorkOutDetails>
    var tablename: String = ""
    var workoutId: String = ""
    private var bitmap: Bitmap? = null
    lateinit var txtLevelNo: TextView
    lateinit var txtTotalNoOfLevel: TextView
    lateinit var txtDurationTime: TextView
    lateinit var rltLevelComplete: LinearLayout
    internal var uri: Uri? = null
    lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed)
        window.statusBarColor = resources.getColor(R.color.colorAccent)
        context = this
        rltLevelComplete = findViewById(R.id.rltLevelComplete)
        txtDurationTime = findViewById(R.id.txtDurationTime)
        txtTotalNoOfLevel = findViewById(R.id.txtTotalNoOfLevel)
        getSetIntent()
        setProgressDialog()

        /*if (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.AD_TYPE_FB_GOOGLE, "") == ConstantString.AD_GOOGLE &&
            com.superChargedFitness.utils.Utils.getPref(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
            CommonConstantAd.loadNativeAd(this, nativeAdDetail)
        }*/

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

    fun onClickShare(view: View) {
        if (com.superChargedFitness.utils.Utils.checkPermission(context)) {
            com.superChargedFitness.utils.Utils.initFullAdd(context)
            getBitmap()
        }
    }

    fun onClickBack(view: View) {
        saveData()
        finish()
    }

    private fun getSetIntent() {
        val intent = intent
//        txtLevelNo.text = "" + Constant.LEVEL_NO
        pWorkoutList = intent.getSerializableExtra(ConstantString.workout_list) as ArrayList<PWorkOutDetails>
        tablename = intent.getStringExtra(ConstantString.table_name_from_workactivity)!!
        workoutId = intent.getStringExtra(ConstantString.workout_id_from_workactivity)!!
        txtTotalNoOfLevel.text = pWorkoutList.size.toString()
        txtDurationTime.text = intent.getStringExtra("Duration")
    }

    private fun getBitmap() {
        val handler = Handler()
        handler.postDelayed({
            progressDialog.show()
            //rltLevelComplete.setBackgroundColor(Color.BLACK);
            rltLevelComplete.isDrawingCacheEnabled = true
            rltLevelComplete.buildDrawingCache(true)
            if (bitmap != null) {
                bitmap!!.recycle()
                bitmap = null
            }
            bitmap = null
            rltLevelComplete.invalidate()
            bitmap = Bitmap.createBitmap(rltLevelComplete.drawingCache)
            progressDialog.dismiss()
            //rltLevelComplete.setBackgroundColor(Color.TRANSPARENT);
            saveImage()
        }, 0)
    }

    private fun saveImage() {
        val folderName: File
        val fileName: File
        folderName = com.superChargedFitness.utils.Utils.createPackageDir(context, context.resources.getString(R.string.app_name))
        var name = System.currentTimeMillis().toString() + ".jpg"
        fileName = File(folderName.absolutePath + File.separator + name)
        uri = FileProvider.getUriForFile(context, "$packageName.provider", fileName)
        var outputStream: FileOutputStream? = null
        try {
            outputStream = FileOutputStream(fileName)
            bitmap!!.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
            outputStream.close()
            shareImage()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    private fun shareImage() {
        val share = Intent()
        share.action = Intent.ACTION_SEND
        share.type = "image/*"
        val link = "https://play.google.com/store/apps/details?id=" + context.packageName
        share.putExtra(Intent.EXTRA_TEXT, link)
        share.putExtra(Intent.EXTRA_TITLE, link)
        share.putExtra(Intent.EXTRA_STREAM, uri)
        context.startActivity(share)
    }

    fun onClickTryAgain(view: View) {
        saveData()
        val intent1 = intent
        val intent = Intent(context, WorkoutActivity::class.java)
        intent.putExtra(ConstantString.workout_list, intent1.getSerializableExtra(ConstantString.workout_list))
        startActivity(intent)
        finish()
    }

    private fun setProgressDialog() {
        progressDialog = ProgressDialog(context)
        progressDialog.setMessage("Please Wait")
        progressDialog.setCancelable(false)
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
    }

    fun saveData(){
        com.superChargedFitness.utils.Utils.setPref(this, ConstantString.PREF_LAST_UN_COMPLETE_DAY + "_" + tablename + "_" + workoutId, 0)
    }

    override fun onBackPressed() {
        saveData()
        super.onBackPressed()
    }

    override fun onDestroy() {
        saveData()
        super.onDestroy()
    }

    override fun onStop() {
        saveData()
        super.onStop()
    }
}
