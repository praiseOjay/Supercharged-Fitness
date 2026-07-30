package com.superChargedFitness.activity

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.superChargedFitness.R
import com.superChargedFitness.pojo.PWorkOutDetails
import com.superChargedFitness.utils.ConstantString
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.Serializable

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
    private var progressDialog: AlertDialog? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_completed)
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorAccent)
        context = this
        rltLevelComplete = findViewById(R.id.rltLevelComplete)
        txtDurationTime = findViewById(R.id.txtDurationTime)
        txtTotalNoOfLevel = findViewById(R.id.txtTotalNoOfLevel)
        getSetIntent()
        setProgressDialog()

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                saveData()
                finish()
            }
        })
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
        @Suppress("DEPRECATION", "UNCHECKED_CAST")
        pWorkoutList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent.getSerializableExtra(ConstantString.workout_list, ArrayList::class.java) as ArrayList<PWorkOutDetails>
        } else {
            intent.getSerializableExtra(ConstantString.workout_list) as ArrayList<PWorkOutDetails>
        }
        tablename = intent.getStringExtra(ConstantString.table_name_from_workactivity)!!
        workoutId = intent.getStringExtra(ConstantString.workout_id_from_workactivity)!!
        txtTotalNoOfLevel.text = pWorkoutList.size.toString()
        txtDurationTime.text = intent.getStringExtra("Duration")
    }

    private fun getBitmap() {
        val handler = Handler(Looper.getMainLooper())
        handler.post {
            progressDialog?.show()
            if (bitmap != null) {
                bitmap!!.recycle()
                bitmap = null
            }
            
            bitmap = Bitmap.createBitmap(rltLevelComplete.width, rltLevelComplete.height, Bitmap.Config.ARGB_8888)
            val canvas = Canvas(bitmap!!)
            rltLevelComplete.draw(canvas)
            
            progressDialog?.dismiss()
            saveImage()
        }
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
        @Suppress("DEPRECATION")
        val workouts = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent1.getSerializableExtra(ConstantString.workout_list, ArrayList::class.java)
        } else {
            intent1.getSerializableExtra(ConstantString.workout_list)
        }
        intent.putExtra(ConstantString.workout_list, workouts)
        startActivity(intent)
        finish()
    }

    private fun setProgressDialog() {
        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        val progressBar = ProgressBar(context)
        progressBar.setPadding(40, 40, 40, 40)
        builder.setView(progressBar)
        builder.setMessage("Please Wait")
        progressDialog = builder.create()
    }

    fun saveData(){
        com.superChargedFitness.utils.Utils.setPref(this, ConstantString.PREF_LAST_UN_COMPLETE_DAY + "_" + tablename + "_" + workoutId, 0)
        
        // Record the last workout date for streak tracking
        val dateFormat = java.text.SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault())
        val today = dateFormat.format(java.util.Date())
        val prefs = getSharedPreferences("MyPref", Context.MODE_PRIVATE)
        prefs.edit().putString(com.superChargedFitness.worker.StreakTrackingWorker.PREF_LAST_WORKOUT_DATE, today).apply()
    }

    override fun onBackPressed() {
        @Suppress("DEPRECATION")
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
