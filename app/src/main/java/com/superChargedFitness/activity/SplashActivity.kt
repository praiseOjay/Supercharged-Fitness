package com.superChargedFitness.activity

import android.content.Context
import android.content.Intent
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.superChargedFitness.R
import com.superChargedFitness.billing.BillingManager
import com.superChargedFitness.data.repository.PurchaseRepository
import com.superChargedFitness.interfaces.AdsCallback
import com.superChargedFitness.utils.ConstantString

class SplashActivity : AppCompatActivity(), AdsCallback {

    private var billingManager: BillingManager? = null
    private lateinit var purchaseRepository: PurchaseRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        purchaseRepository = PurchaseRepository(this)

        // Check subscription status via modern BillingClient
        billingManager = BillingManager(this, object : BillingManager.BillingEventListener {
            override fun onBillingConnected() {
                billingManager?.querySubscriptionStatus()
            }

            override fun onBillingDisconnected() {
                Log.w("SplashActivity", "Billing disconnected")
            }

            override fun onPurchaseComplete(productId: String) {}
            override fun onPurchaseAlreadyOwned() {}
            override fun onPurchaseCancelled() {}

            override fun onBillingError(message: String) {
                Log.e("SplashActivity", "Billing error: $message")
            }

            override fun onSubscriptionStatusChecked(isSubscribed: Boolean) {
                purchaseRepository.setPurchaseStatus(isSubscribed)
            }
        })
        billingManager?.connect()

        startNextActivity(2000)
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
            }

    }

    private fun checkAd() {
        if (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {

            if (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE) {
                Handler(Looper.getMainLooper()).postDelayed({
                    when (com.superChargedFitness.utils.Utils.getPref(this, ConstantString.AD_TYPE_FB_GOOGLE, "")) {
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
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(myRunnable)
        billingManager?.disconnect()
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
    private val handler = Handler(Looper.getMainLooper())

    override fun onStop() {
        Log.e("TAG", "onStop:Handler:::::: " )
        handler.removeCallbacks(myRunnable)
        super.onStop()
    }

}
