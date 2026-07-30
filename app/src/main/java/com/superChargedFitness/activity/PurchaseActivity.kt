package com.superChargedFitness.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import com.superChargedFitness.R
import com.superChargedFitness.billing.BillingManager
import com.superChargedFitness.databinding.ActivityPurchaseBinding
import com.superChargedFitness.viewmodel.PurchaseViewModel


class PurchaseActivity : BaseActivity() {

    private val TAG = "PurchaseActivity"
    private lateinit var binding: ActivityPurchaseBinding
    private val viewModel: PurchaseViewModel by viewModels()
    private var billingManager: BillingManager? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPurchaseBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupBilling()
        initAction()
    }

    private fun setupBilling() {
        billingManager = BillingManager(this, object : BillingManager.BillingEventListener {
            override fun onBillingConnected() {
                Log.d(TAG, "Billing connected")
                viewModel.setConnected()
                // Check if user already has an active subscription
                billingManager?.querySubscriptionStatus()
            }

            override fun onBillingDisconnected() {
                Log.w(TAG, "Billing disconnected")
            }

            override fun onPurchaseComplete(productId: String) {
                Log.d(TAG, "Purchase complete: $productId")
                viewModel.setPurchaseStatus(true)
                alert("Thank you for subscribing! Now you can enjoy our app!")
                startActivity(Intent(this@PurchaseActivity, HomeActivity::class.java))
                finish()
            }

            override fun onPurchaseAlreadyOwned() {
                viewModel.setPurchaseStatus(true)
                alert("Item already purchased.")
            }

            override fun onPurchaseCancelled() {
                Log.d(TAG, "Purchase cancelled by user")
            }

            override fun onBillingError(message: String) {
                Log.e(TAG, "Billing error: $message")
                viewModel.setError(message)
                complain(message)
            }

            override fun onSubscriptionStatusChecked(isSubscribed: Boolean) {
                viewModel.setPurchaseStatus(isSubscribed)
                if (isSubscribed) {
                    Log.d(TAG, "User has active subscription")
                }
            }
        })
        billingManager?.connect()
    }

    private fun initAction() {
        binding.btnPurchasedWeekly.setOnClickListener {
            billingManager?.launchSubscriptionPurchase(this, BillingManager.SKU_WEEKLY_SUB)
        }

        binding.btnPurchasedMonthly.setOnClickListener {
            billingManager?.launchSubscriptionPurchase(this, BillingManager.SKU_YEARLY_SUB)
        }

        binding.txtPrivacy.setOnClickListener {
            com.superChargedFitness.utils.Utils.openWebsite(this@PurchaseActivity,"https://sites.google.com/view/workout-privacy-policy/home")
        }

        binding.txtTerms.setOnClickListener {
            com.superChargedFitness.utils.Utils.openWebsite(this@PurchaseActivity,"https://sites.google.com/view/workout-terms-of-use/home")
        }
    }

    internal fun complain(message: String) {
        Log.e(TAG, "**** Billing Error: $message")
        alert("Error: $message")
    }

    internal fun alert(message: String) {
        val bld = android.app.AlertDialog.Builder(this)
        bld.setMessage(message)
        bld.setNeutralButton("OK", null)
        Log.d(TAG, "Showing alert dialog: $message")
        bld.create().show()
    }

    override fun onDestroy() {
        super.onDestroy()
        billingManager?.disconnect()
    }
}
