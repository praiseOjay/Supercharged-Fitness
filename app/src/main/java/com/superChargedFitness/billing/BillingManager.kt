package com.superChargedFitness.billing

import android.app.Activity
import android.content.Context
import android.util.Log
import com.android.billingclient.api.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Modern Google Play Billing wrapper replacing the deprecated AIDL-based IabHelper.
 * Uses BillingClient 7.x API for subscriptions.
 *
 * Lifecycle: call [connect] when Activity starts and [disconnect] when it's destroyed.
 */
class BillingManager(
    private val context: Context,
    private val listener: BillingEventListener
) : PurchasesUpdatedListener {

    companion object {
        private const val TAG = "BillingManager"
        const val SKU_WEEKLY_SUB = "weekly_sub"
        const val SKU_YEARLY_SUB = "yearly"
    }

    interface BillingEventListener {
        fun onBillingConnected()
        fun onBillingDisconnected()
        fun onPurchaseComplete(productId: String)
        fun onPurchaseAlreadyOwned()
        fun onPurchaseCancelled()
        fun onBillingError(message: String)
        fun onSubscriptionStatusChecked(isSubscribed: Boolean)
    }

    private var billingClient: BillingClient = BillingClient.newBuilder(context)
        .setListener(this)
        .enablePendingPurchases()
        .build()

    private val scope = CoroutineScope(Dispatchers.Main)

    // ── Connection ──────────────────────────────────────────────

    fun connect() {
        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(result: BillingResult) {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    Log.d(TAG, "Billing connected successfully")
                    listener.onBillingConnected()
                } else {
                    Log.e(TAG, "Billing setup failed: ${result.debugMessage}")
                    listener.onBillingError("Billing setup failed: ${result.debugMessage}")
                }
            }

            override fun onBillingServiceDisconnected() {
                Log.w(TAG, "Billing service disconnected")
                listener.onBillingDisconnected()
            }
        })
    }

    fun disconnect() {
        billingClient.endConnection()
    }

    // ── Query existing purchases ────────────────────────────────

    fun querySubscriptionStatus() {
        scope.launch {
            val params = QueryPurchasesParams.newBuilder()
                .setProductType(BillingClient.ProductType.SUBS)
                .build()

            billingClient.queryPurchasesAsync(params) { result, purchases ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    val hasActiveSub = purchases.any { purchase ->
                        purchase.purchaseState == Purchase.PurchaseState.PURCHASED &&
                            (purchase.products.contains(SKU_WEEKLY_SUB) ||
                                purchase.products.contains(SKU_YEARLY_SUB))
                    }
                    // Acknowledge any un-acknowledged purchases
                    purchases.filter {
                        it.purchaseState == Purchase.PurchaseState.PURCHASED && !it.isAcknowledged
                    }.forEach { acknowledgePurchase(it) }

                    listener.onSubscriptionStatusChecked(hasActiveSub)
                } else {
                    Log.e(TAG, "Query purchases failed: ${result.debugMessage}")
                    listener.onSubscriptionStatusChecked(false)
                }
            }
        }
    }

    // ── Launch purchase flow ────────────────────────────────────

    fun launchSubscriptionPurchase(activity: Activity, productId: String) {
        scope.launch {
            val productList = listOf(
                QueryProductDetailsParams.Product.newBuilder()
                    .setProductId(productId)
                    .setProductType(BillingClient.ProductType.SUBS)
                    .build()
            )

            val params = QueryProductDetailsParams.newBuilder()
                .setProductList(productList)
                .build()

            billingClient.queryProductDetailsAsync(params) { result, productDetailsList ->
                if (result.responseCode == BillingClient.BillingResponseCode.OK && productDetailsList.isNotEmpty()) {
                    val productDetails = productDetailsList[0]

                    // Get the first offer token for the subscription
                    val offerToken = productDetails.subscriptionOfferDetails
                        ?.firstOrNull()?.offerToken

                    if (offerToken != null) {
                        val flowParams = BillingFlowParams.newBuilder()
                            .setProductDetailsParamsList(
                                listOf(
                                    BillingFlowParams.ProductDetailsParams.newBuilder()
                                        .setProductDetails(productDetails)
                                        .setOfferToken(offerToken)
                                        .build()
                                )
                            )
                            .build()

                        billingClient.launchBillingFlow(activity, flowParams)
                    } else {
                        listener.onBillingError("No subscription offers available")
                    }
                } else {
                    listener.onBillingError("Product not found: ${result.debugMessage}")
                }
            }
        }
    }

    // ── PurchasesUpdatedListener callback ────────────────────────

    override fun onPurchasesUpdated(result: BillingResult, purchases: MutableList<Purchase>?) {
        when (result.responseCode) {
            BillingClient.BillingResponseCode.OK -> {
                purchases?.forEach { purchase ->
                    if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
                        acknowledgePurchase(purchase)
                        val productId = purchase.products.firstOrNull() ?: ""
                        listener.onPurchaseComplete(productId)
                    }
                }
            }
            BillingClient.BillingResponseCode.ITEM_ALREADY_OWNED -> {
                listener.onPurchaseAlreadyOwned()
            }
            BillingClient.BillingResponseCode.USER_CANCELED -> {
                listener.onPurchaseCancelled()
            }
            else -> {
                listener.onBillingError("Purchase error: ${result.debugMessage}")
            }
        }
    }

    // ── Acknowledge ─────────────────────────────────────────────

    private fun acknowledgePurchase(purchase: Purchase) {
        if (!purchase.isAcknowledged) {
            val params = AcknowledgePurchaseParams.newBuilder()
                .setPurchaseToken(purchase.purchaseToken)
                .build()

            scope.launch {
                billingClient.acknowledgePurchase(params) { result ->
                    if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                        Log.d(TAG, "Purchase acknowledged successfully")
                    } else {
                        Log.e(TAG, "Acknowledge failed: ${result.debugMessage}")
                    }
                }
            }
        }
    }
}
