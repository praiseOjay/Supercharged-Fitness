package com.superChargedFitness.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.Uri
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.superChargedFitness.R
import com.superChargedFitness.interfaces.CallbackListener
import com.superChargedFitness.interfaces.ConfirmDialogCallBack
import kotlinx.android.synthetic.main.activity_base.*
import java.util.*

open class BaseActivity : AppCompatActivity(), AdapterView.OnItemClickListener,
    ConfirmDialogCallBack {

    lateinit var drawerLayout: DrawerLayout
    private lateinit var context: Context
    private lateinit var listOfMenuItem: ListView
    private lateinit var arrDrawerItem: ArrayList<String>
    private lateinit var arrDrawerImg: ArrayList<Int>
    lateinit var menuAdapter: BaseActivity.MenuAdapter

    override fun setContentView(layoutResID: Int) {
        drawerLayout = LayoutInflater.from(this).inflate(R.layout.activity_base, null) as DrawerLayout
        val activityContainer = drawerLayout.findViewById(R.id.activity_content) as FrameLayout
        LayoutInflater.from(this).inflate(layoutResID, activityContainer, true)
        super.setContentView(drawerLayout)
        context = this
        listOfMenuItem = findViewById(R.id.listOfMenuItem)

        setCommunicationListAdapter()
        txtExit.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            confirmationDialog(this, this, "", getString(R.string.exit_confirmation))
        }

        llBase.post(Runnable {
            val resources: Resources = resources
            val width = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    280f,
                    resources.getDisplayMetrics()
            )
            val params =
                    llBase.getLayoutParams()
            params.width = width.toInt()
            llBase.setLayoutParams(params)
        })

        /*if (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.AD_TYPE_FB_GOOGLE,"") == ConstantString.AD_GOOGLE
                && com.superChargedFitness.utils.Utils.getPref(this,ConstantString.STATUS_ENABLE_DISABLE,"") == ConstantString.ENABLE) {
            CommonConstantAd.googlebeforloadAd(this)
        } else if (com.superChargedFitness.utils.Utils.getPref(this,ConstantString.AD_TYPE_FB_GOOGLE,"") == ConstantString.AD_FACEBOOK
                && com.superChargedFitness.utils.Utils.getPref(this,ConstantString.STATUS_ENABLE_DISABLE,"") == ConstantString.ENABLE) {
            CommonConstantAd.facebookbeforeloadFullAd(this)
        }*/
    }

    private fun setCommunicationListAdapter() {

        arrDrawerItem = ArrayList()
        arrDrawerItem.add("Home")
        arrDrawerItem.add("Contact Us")
        arrDrawerItem.add("Share App")
        arrDrawerItem.add("Rate Us")


        arrDrawerImg = ArrayList()
        arrDrawerImg.add(R.drawable.round_home_white_24)
        arrDrawerImg.add(R.drawable.round_perm_contact_calendar_white_24)
        arrDrawerImg.add(R.drawable.round_share_white_24)
        arrDrawerImg.add(R.drawable.round_star_white_24)


        listOfMenuItem.onItemClickListener = this
        menuAdapter = MenuAdapter()
        listOfMenuItem.adapter = menuAdapter
        setListViewHeightBasedOnItems(listOfMenuItem)

    }

    inner class MenuAdapter : BaseAdapter() {

        override fun getView(p0: Int, p1: View?, p2: ViewGroup?): View {
            val menuView = LayoutInflater.from(context).inflate(R.layout.cell_of_drawer_item, null)
            val imgItem = menuView.findViewById(R.id.imgItem) as ImageView
            val txtItem = menuView.findViewById(R.id.txtItem) as TextView
            imgItem.setImageResource(arrDrawerImg[p0])
            txtItem.text = arrDrawerItem[p0]
            return menuView
        }

        override fun getItem(p0: Int): Any {
            return p0
        }

        override fun getItemId(p0: Int): Long {
            return p0.toLong()
        }

        override fun getCount(): Int {
            return arrDrawerItem.size
        }
    }



    @SuppressLint("WrongConstant")
    override fun onItemClick(parent: AdapterView<*>, view: View, position: Int, id: Long) {
        drawerLayout.closeDrawer(Gravity.START)
        when {
            arrDrawerItem[position] === "Contact Us" -> contactUs()
            arrDrawerItem[position] === "Rate Us" -> rateUs()
            arrDrawerItem[position] === "Share App" -> shareAppLink()
            arrDrawerItem[position] === "Home" -> drawerLayout.closeDrawer(Gravity.START)
        }
    }

    private fun moreApp() {
        val uri = Uri.parse("https://play.google.com/store/apps/developer?id=Ninety+Nine+Apps")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        startActivity(intent)
    }

    private fun rateUs() {
        val appPackageName = packageName
        try {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store")))
        } catch (e: ActivityNotFoundException) {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }
    }

    private fun shareAppLink() {
        val shareIntent = Intent()
        shareIntent.action = Intent.ACTION_SEND
        val link = "https://play.google.com/store/apps/details?id=${"packageName"}"
        shareIntent.putExtra(Intent.EXTRA_TEXT, link)
        shareIntent.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name)+" - Android")
        shareIntent.type = "text/plain"
        startActivity(Intent.createChooser(shareIntent, resources.getString(R.string.app_name)+" - Android"))
    }

    private fun contactUs() {
        try {
            val sendIntentGmail = Intent(Intent.ACTION_SEND)
            sendIntentGmail.type = "plain/text"
            sendIntentGmail.setPackage("com.google.android.gm")
            sendIntentGmail.putExtra(Intent.EXTRA_EMAIL, arrayOf("OjerinolaPraise@gmail.com"))
            if (resources.getString(R.string.app_name) != null)
                sendIntentGmail.putExtra(Intent.EXTRA_SUBJECT, resources.getString(R.string.app_name)+" - Android")
            startActivity(sendIntentGmail)
        }  catch (e: ActivityNotFoundException){
            e.printStackTrace()
            Toast.makeText(this,"Gmail app not found on this device", Toast.LENGTH_LONG).show()
        }
    }

    fun setListViewHeightBasedOnItems(listView: ListView): Boolean {
        val listAdapter = listView.adapter
        if (listAdapter != null) {
            val numberOfItems = listAdapter.count
            // Get total height of all items.
            var totalItemsHeight = 0
            for (itemPos in 0 until numberOfItems) {
                val item = listAdapter.getView(itemPos, null, listView)
                item.measure(0, 0)
                totalItemsHeight += item.measuredHeight
            }
            // Get total height of all item dividers.
            val totalDividersHeight = listView.dividerHeight * (numberOfItems - 1)
            // Set list height.
            val params = listView.layoutParams
            params.height = totalItemsHeight + totalDividersHeight
            listView.layoutParams = params
            listView.requestLayout()
            return true
        } else {
            return false
        }
    }


    fun getNavigationSize(context: Context): Int {
        val resources = context.resources
        val resourceId = resources.getIdentifier("navigation_bar_height", "dimen", "android")
        Log.e(
                "TAG",
                "getNavigationSize:IFF:::  ${resources.getDimensionPixelSize(resourceId)}      ${isSoftNavigationBarAvailable()} "
        )
        return if (isSoftNavigationBarAvailable()) {
            if (resourceId > 0) {
                resources.getDimensionPixelSize(resourceId)
            } else {
                0
            }
        } else {
            0
        }
    }

    private fun Context.isSoftNavigationBarAvailable(): Boolean {
        val navBarInteractionModeId = resources.getIdentifier(
                "config_navBarInteractionMode",
                "integer",
                "android"
        )
        if (navBarInteractionModeId > 0 && resources.getInteger(navBarInteractionModeId) > 0) {
            // nav gesture is enabled in the settings
            return false
        }
        val appUsableScreenSize = Point()
        val realScreenSize = Point()
        val defaultDisplay = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        defaultDisplay.getSize(appUsableScreenSize)
        defaultDisplay.getRealSize(realScreenSize)
        return appUsableScreenSize.y < realScreenSize.y
    }

    fun confirmationDialog(
            content: Context,
            confirmCallBack: ConfirmDialogCallBack,
            strTitle: String,
            strMsg: String
    ): Boolean {

        val builder1 = AlertDialog.Builder(content)
        builder1.setTitle(strTitle)
        builder1.setMessage(strMsg)
        builder1.setCancelable(true)

        builder1.setPositiveButton("Yes") { dialog, _ ->
            dialog.cancel()
            confirmCallBack.Okay()
        }

        builder1.setNegativeButton("No") { dialog, _ ->
            dialog.cancel()
            confirmCallBack.cancel()
        }

        val alert11 = builder1.create()
        alert11.show()

        return false
    }

    override fun Okay() {
        val homeIntent = Intent(Intent.ACTION_MAIN)
        homeIntent.addCategory(Intent.CATEGORY_HOME)
        homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
        startActivity(homeIntent)
        finishAffinity()
    }

    override fun cancel() {

    }

    fun openYoutube(strVideoLink: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(strVideoLink))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        intent.setPackage("com.google.android.youtube");
        startActivity(intent)
    }

    fun isNetworkConnected(): Boolean {
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        return cm.activeNetworkInfo != null && cm.activeNetworkInfo!!.isConnected
    }
    private fun isOnline(): Boolean {
        var outcome = false
        try {
            val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val networkInfo = cm.allNetworkInfo
            for (tempNetworkInfo in networkInfo) {
                if (tempNetworkInfo.isConnected) {
                    outcome = true
                    break
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return outcome
    }



    fun openInternetDialog(callbackListener: CallbackListener, isSplash: Boolean) {
        if (!isOnline()) {
            val builder = AlertDialog.Builder(this)
            builder.setTitle("No internet Connection")
            builder.setCancelable(false)
            builder.setMessage("Please turn on internet connection to continue")
            builder.setNegativeButton("Retry") { dialog, _ ->

                if (!isSplash) {
                    openInternetDialog(callbackListener, false)
                }
                dialog!!.dismiss()
                callbackListener.onRetry()

            }
            builder.setPositiveButton("Close") { dialog, _ ->
                dialog!!.dismiss()
                val homeIntent = Intent(Intent.ACTION_MAIN)
                homeIntent.addCategory(Intent.CATEGORY_HOME)
                homeIntent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                startActivity(homeIntent)
                finishAffinity()
            }
            val alertDialog = builder.create()
            alertDialog.show()
        }
    }

    fun adDimLightProgressDialog(context: Context): Dialog {

        val builder = AlertDialog.Builder(context)
        builder.setCancelable(false)
        builder.setView(R.layout.anim_ad_progress)

        val alDialog = builder.create()
//        alDialog.show()

        alDialog.window!!.setBackgroundDrawableResource(android.R.color.transparent)

        return alDialog
    }
}
