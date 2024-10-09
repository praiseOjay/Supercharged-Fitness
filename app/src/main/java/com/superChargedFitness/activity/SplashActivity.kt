package com.superChargedFitness.activity

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.android.vending.billing.IInAppBillingService
import com.superChargedFitness.R
import com.superChargedFitness.interfaces.AdsCallback
import com.superChargedFitness.utils.ConstantString
import org.json.JSONObject

class SplashActivity : AppCompatActivity(), AdsCallback {

    internal var bindService: Boolean = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
//        supportActionBar!!.hide()

        val serviceIntent = Intent("com.android.vending.billing.InAppBillingService.BIND")
        serviceIntent.setPackage("com.android.vending")

        try {
            bindService = bindService(serviceIntent, mServiceConn1, Context.BIND_AUTO_CREATE)
        } catch (e: Exception) {
            e.printStackTrace()
        }

//        successCall()
        startNextActivity(2000)
//        handler.postDelayed(myRunnable, 10000);
    }

    fun successCall() {
            if (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.SPLASH_SCREEN_COUNT, 1) == 1) {
                com.superChargedFitness.utils.Utils.setPref(this, ConstantString.SPLASH_SCREEN_COUNT, 2)
                Log.e("TAG", "successCall::::count:::IFFFF  "+ com.superChargedFitness.utils.Utils.getPref(this, ConstantString.SPLASH_SCREEN_COUNT, 0) )
                Handler(Looper.getMainLooper()).postDelayed({
                    isLoaded = true
                    finish()
                    startActivity(Intent(this, HomeActivity::class.java))
                }, 1500)
            } else {
                Log.e("TAG", "successCall::::count:::ELSEEE  "+ com.superChargedFitness.utils.Utils.getPref(this, ConstantString.SPLASH_SCREEN_COUNT, 0) )
                //checkAd()
            }

    }

    private fun checkAd() {
        if (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
            if (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.AD_TYPE_FB_GOOGLE, "") == ConstantString.AD_GOOGLE) {
                //CommonConstantAd.googlebeforloadAd(this@SplashActivity)
                //Log.e("TAG", "checkAd:Google::::  " )
            } else if (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.AD_TYPE_FB_GOOGLE, "") == ConstantString.AD_FACEBOOK) {
                //CommonConstantAd.facebookbeforeloadFullAd(this@SplashActivity)
                //Log.e("TAG", "checkAd:Facebook:::: " )
            }

            if (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
                Handler().postDelayed({
                    when (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.AD_TYPE_FB_GOOGLE, "")) {
                        //ConstantString.AD_GOOGLE -> {
                           // CommonConstantAd.showInterstitialAdsGoogle(this,this@SplashActivity)
                       // }
                       // ConstantString.AD_FACEBOOK -> {
                            //CommonConstantAd.showInterstitialAdsFacebook(this@SplashActivity)
                       // }
                        else -> {
                            startNextActivity(0)
                        }
                    }
                    com.superChargedFitness.utils.Utils.setPref(this, ConstantString.SPLASH_SCREEN_COUNT, 1)
                }, 3000)
            }else{
                startNextActivity(0)
            }


            Log.e("TAG", "checkAd:IFFFFF:::: " + com.superChargedFitness.utils.Utils.getPref(this, ConstantString.STATUS_ENABLE_DISABLE, ""))
        } else {
            com.superChargedFitness.utils.Utils.setPref(this, ConstantString.SPLASH_SCREEN_COUNT, 1)
            Log.e("TAG", "checkAd:ELSE:::: " + com.superChargedFitness.utils.Utils.getPref(this, ConstantString.STATUS_ENABLE_DISABLE, ""))
            startNextActivity(0)
        }

    }

    private fun startNextActivity(time:Long) {

        Handler(Looper.getMainLooper()).postDelayed({
            /* Create an Intent that will start the Menu-Activity. */
            isLoaded = true
            finish()
            startActivity(Intent(this, HomeActivity::class.java))
        }, time)

       /* try {
            Thread {
                kotlin.run {
                    synchronized(this) {
                        Thread.sleep(time)
                        runOnUiThread {
                            isLoaded = true
                            finish()
                            startActivity(Intent(this, HomeActivity::class.java))
                        }
                    }
                }
            }.start()
        } catch (e: Exception) {
            e.printStackTrace()
        }*/
    }

    //    ServiceConnection mServiceConn;
    internal var mService: IInAppBillingService? = null
    internal var mServiceConn1: ServiceConnection = object : ServiceConnection {

        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mService = IInAppBillingService.Stub.asInterface(service)
            try {
                val ownedItems = mService!!.getPurchases(3, packageName, "subs", null)
                val response = ownedItems.getInt("RESPONSE_CODE")

                //JSONArray arr = new JSONArray(ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST"));
                val arrHashProduct = ArrayList<HashMap<String, String>>()
                try {
                    val purchaseDataList = ownedItems.getStringArrayList("INAPP_PURCHASE_DATA_LIST")
                    if (purchaseDataList != null && purchaseDataList.size > 0) {
                        for (i in purchaseDataList.indices) {
                            val purchaseData = purchaseDataList[i]
                            val jo = JSONObject(purchaseData)
                            val skuId = jo.getString("productId")
                            val purchaseState: Int
                            if (jo.getString("purchaseState").isNotEmpty()) {
                                purchaseState = Integer.parseInt(jo.getString("purchaseState"))
                            } else {
                                purchaseState = -1
                            }
                            // InApp.addPurchasedItem(rootContext, skuId, purchaseState);
                            val valueMap = HashMap<String, String>()
                            valueMap["productId"] = skuId
                            valueMap["isProductPurchased"] = "" + if (purchaseState == 0) "true" else "false"
                            arrHashProduct.add(valueMap)
                            if (purchaseState == 0) {
                                com.superChargedFitness.utils.Utils.setPref(this@SplashActivity, ConstantString.pref_Key_purchase_status, true)
                                return
                            } else {
                                com.superChargedFitness.utils.Utils.setPref(this@SplashActivity, ConstantString.pref_Key_purchase_status, false)
                            }
                        }
                    } else {
//                        checkPurchaseStatus()
                        com.superChargedFitness.utils.Utils.setPref(this@SplashActivity, ConstantString.pref_Key_purchase_status, false)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            mService = null
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(myRunnable)
        try {
            if (mService != null) {
                unbindService(mServiceConn1)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun adLoadingFailed() {
        startNextActivity(0)
    }

    override fun adClose() {
        startNextActivity(0)
    }

    override fun startNextScreen() {
        startNextActivity(0)
    }
    private var isLoaded = false
    override fun onLoaded() {
        isLoaded = true
    }

    private val myRunnable = Runnable {
        Log.e("TAG", "myRunnable::::::  $isLoaded" )
        if (!isLoaded){
            startNextActivity(0)
        }
    }
    private val handler = Handler()

    override fun onStop() {
        Log.e("TAG", "onStop:Handler:::::: " )
        handler.removeCallbacks(myRunnable)
        super.onStop()
    }

}
