package com.superChargedFitness.adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.TextView

import com.superChargedFitness.R
import com.superChargedFitness.interfaces.Drawer_call
import com.superChargedFitness.utils.ConstantString

import java.util.ArrayList

class DrawerAdapter(private val mContext: Context) : BaseAdapter() {

    private var arrItemName: ArrayList<String>? = null
    private var arrItemIcon: ArrayList<Int>? = null
    private val drawerCall: Drawer_call = mContext as Drawer_call

    init {
//        drawerCall = mContext as Drawer_call
        setDrawerContent()
    }

    override fun getCount(): Int {
        return arrItemIcon!!.size
    }

    override fun getItem(position: Int): Any? {
        return null
    }

    override fun getItemId(position: Int): Long {
        return 0
    }

    override fun getView(position: Int, convertView1: View?, parent: ViewGroup): View {
        var convertView = convertView1
        val holder: viewHolder
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.drawer_row, parent, false)

            holder = viewHolder(convertView!!)
            convertView.tag = holder
        } else {
            holder = convertView.tag as viewHolder
        }

        holder.txtName.text = arrItemName!![position]
        holder.imgIcon.setImageResource(arrItemIcon!![position])

        convertView.setOnClickListener { setDrawer_call(arrItemName!![position]) }

        return convertView
    }

    private inner class viewHolder internal constructor(convertView: View) {
        internal var txtName: TextView
        internal var imgIcon: ImageView

        init {
            imgIcon = convertView.findViewById(R.id.imgIcon)
            txtName = convertView.findViewById(R.id.txtName)

        }

    }

    /* Todo drawer call and actions */
    private fun setDrawer_call(strCallName: String) {
        drawerCall.closeDrawer()
        if (strCallName == ConstantString.Drawer_Rate) {
            openUrl()
        }

    }

    private fun openUrl() {
        val appPackageName = mContext.packageName // getPackageName() from Context or Activity object
        try {
            mContext.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$appPackageName")))
        } catch (anfe: android.content.ActivityNotFoundException) {
            mContext.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=$appPackageName")))
        }

    }

    /* Todo set drawer values */
    private fun setDrawerContent() {
        arrItemName = ArrayList()
        arrItemName!!.add(ConstantString.Drawer_Home)
        arrItemName!!.add(ConstantString.Drawer_Rate)
        arrItemName!!.add(ConstantString.Drawer_Exit)

        arrItemIcon = ArrayList()
//        arrItemIcon!!.add(R.drawable.ic_menu_home)
//        arrItemIcon!!.add(R.drawable.ic_menu_app_rate)
//        arrItemIcon!!.add(R.drawable.ic_menu_exit)

    }

}
