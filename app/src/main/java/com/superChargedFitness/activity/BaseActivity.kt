package com.superChargedFitness.activity

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.content.res.Resources
import android.graphics.Point
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.*
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import com.superChargedFitness.R
import com.superChargedFitness.databinding.ActivityBaseBinding
import com.superChargedFitness.interfaces.CallbackListener
import com.superChargedFitness.interfaces.ConfirmDialogCallBack
import java.util.*

open class BaseActivity : AppCompatActivity(), AdapterView.OnItemClickListener,
    ConfirmDialogCallBack {

    private lateinit var baseBinding: ActivityBaseBinding
    lateinit var drawerLayout: DrawerLayout
    private lateinit var context: Context
    private lateinit var listOfMenuItem: ListView
    private lateinit var arrDrawerItem: ArrayList<String>
    private lateinit var arrDrawerImg: ArrayList<Int>
    lateinit var menuAdapter: BaseActivity.MenuAdapter

    override fun setContentView(layoutResID: Int) {
        baseBinding = ActivityBaseBinding.inflate(layoutInflater)
        drawerLayout = baseBinding.root
        val activityContainer = baseBinding.activityContent
        LayoutInflater.from(this).inflate(layoutResID, activityContainer, true)
        super.setContentView(drawerLayout)
        initializeBase()
    }

    override fun setContentView(view: View?) {
        baseBinding = ActivityBaseBinding.inflate(layoutInflater)
        drawerLayout = baseBinding.root
        val activityContainer = baseBinding.activityContent
        activityContainer.addView(view)
        super.setContentView(drawerLayout)
        initializeBase()
    }

    private fun initializeBase() {
        context = this
        listOfMenuItem = baseBinding.listOfMenuItem

        setCommunicationListAdapter()
        baseBinding.txtExit.setOnClickListener {
            drawerLayout.closeDrawer(GravityCompat.START)
            confirmationDialog(this, this, "", getString(R.string.exit_confirmation))
        }

        baseBinding.llBase.post(Runnable {
            val resources: Resources = resources
            val width = TypedValue.applyDimension(
                    TypedValue.COMPLEX_UNIT_DIP,
                    280f,
                    resources.getDisplayMetrics()
            )
            val params =
                    baseBinding.llBase.layoutParams
            params.width = width.toInt()
            baseBinding.llBase.layoutParams = params
        })
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
        drawerLayout.closeDrawer(GravityCompat.START)
        when (arrDrawerItem[position]) {
            "Contact Us" -> contactUs()
            "Rate Us" -> rateUs()
            "Share App" -> shareAppLink()
            "Home" -> drawerLayout.closeDrawer(GravityCompat.START)
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
        val link = "https://play.google.com/store/apps/details?id=${packageName}"
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
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            val windowMetrics = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).currentWindowMetrics
            val insets = windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.navigationBars())
            insets.bottom > 0
        } else {
            val appUsableScreenSize = Point()
            val realScreenSize = Point()
            @Suppress("DEPRECATION")
            val defaultDisplay = (getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
            @Suppress("DEPRECATION")
            defaultDisplay.getSize(appUsableScreenSize)
            @Suppress("DEPRECATION")
            defaultDisplay.getRealSize(realScreenSize)
            appUsableScreenSize.y < realScreenSize.y
        }
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
        val connectivityManager = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val network = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
            return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
        } else {
            @Suppress("DEPRECATION")
            val networkInfo = connectivityManager.activeNetworkInfo
            return networkInfo != null && networkInfo.isConnected
        }
    }

    private fun isOnline(): Boolean {
        return isNetworkConnected()
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
