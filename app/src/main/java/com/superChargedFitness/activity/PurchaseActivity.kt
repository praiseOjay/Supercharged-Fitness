package com.superChargedFitness.activity

//import kotlinx.android.synthetic.main.activity_purchase.*
import android.app.Activity
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.os.RemoteException
import android.text.TextUtils
import android.util.Log
import com.android.vending.billing.IInAppBillingService
import com.superChargedFitness.R
import com.superChargedFitness.utils.ConstantString
import kotlinx.android.synthetic.main.activity_purchase.*
import org.json.JSONObject


class PurchaseActivity : BaseActivity(), com.superChargedFitness.inapp.IabBroadcastReceiver.IabBroadcastListener {

    // Todo inapp purchase declaration
    // The helper object
    lateinit var mHelper: com.superChargedFitness.inapp.IabHelper
    // Provides purchase notification while this app is running
    lateinit var mBroadcastReceiver: com.superChargedFitness.inapp.IabBroadcastReceiver
    private val TAG = "Purchase Activity"
    private val base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAgWJ9ITNn/97053Cs9F9gDZRn2J2EYeMASttOKDg/uM7nd1n/PVYEaLK3cT53k4n7qi6kOBiisLDjMEY206FFi8kHAnU4IOzEeSuBR/l/RkcKZsM+kF9DzsGnbxWz6NaaX8R2ja05LJj9mYLbXOIlXjAybHpkaEsTp3zP77c7RhTP0c48BXQ0UVdoX6QJk5b7V4+a5Pr9sJ5RalduD0kvHo4WJr75/Dp/Z7E1/3ITbbRKx5JM2ZUGEYQBWIynaxa4BwYYWdXHDci30X3BfKqaRBr/EK2VQOIJDXONNM3+37zixFU9kCFRGId8zLnlpuH80rFO761XPfGYmn/I8pEWjwIDAQAB"

    // static final String SKU_PREMIUM = "monthly_sub";
    internal val RC_REQUEST = 10001

    internal var bindService: Boolean = false

    internal val SKU_WEEKLY_SUB = "weekly_sub"
    //    internal val SKU_MONTHLY_SUB = "monthly"
    internal val SKU_YEAR_SUB = "yearly"

    internal var mInfiniteGasSku = ""
    // Will the subscription auto-renew?
    internal var mAutoRenewEnabled = false

    internal var mSubscribedToInfiniteGas = false

    internal var mSelectedSubscriptionPeriod = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_purchase)

        loadPurchaseData()

        val serviceIntent = Intent("com.android.vending.billing.InAppBillingService.BIND")
        serviceIntent.setPackage("com.android.vending")

        try {
            bindService = bindService(serviceIntent, mServiceConn1, Context.BIND_AUTO_CREATE)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        initAction()

    }

    private fun initAction() {

        btnPurchasedWeekly.setOnClickListener {
            val payload = ""

            mSelectedSubscriptionPeriod = SKU_WEEKLY_SUB

            var oldSkus: MutableList<String>? = null
            if (!TextUtils.isEmpty(mInfiniteGasSku) && mInfiniteGasSku != mSelectedSubscriptionPeriod) {
                // The user currently has a valid subscription, any purchase action is going to
                // replace that subscription
                oldSkus = ArrayList()
                oldSkus!!.add(mInfiniteGasSku)
            }

            Log.d(TAG, "Launching purchase flow for Sync subscription.")
            if (mHelper.mAsyncInProgress) {
                mHelper.flagEndAsync()
            }

            try {
                mHelper.launchPurchaseFlow(this, mSelectedSubscriptionPeriod, com.superChargedFitness.inapp.IabHelper.ITEM_TYPE_SUBS, oldSkus, RC_REQUEST, mPurchaseFinishedListener, payload)
            } catch (e: com.superChargedFitness.inapp.IabHelper.IabAsyncInProgressException) {
                complain("Error launching purchase flow. Another async operation in progress.")
            }
        }

        btnPurchasedMonthly.setOnClickListener {
            val payload = ""

            mSelectedSubscriptionPeriod = SKU_YEAR_SUB

            var oldSkus: MutableList<String>? = null
            if (!TextUtils.isEmpty(mInfiniteGasSku) && mInfiniteGasSku != mSelectedSubscriptionPeriod) {
                // The user currently has a valid subscription, any purchase action is going to
                // replace that subscription
                oldSkus = ArrayList()
                oldSkus!!.add(mInfiniteGasSku)
            }
//            val oldSkus: List<String>
//            oldSkus = ArrayList()
//            oldSkus.add(mInfiniteGasSku)

            Log.d(TAG, "Launching purchase flow for Sync subscription.")
            if (mHelper.mAsyncInProgress) {
                mHelper.flagEndAsync()
            }

            try {
                mHelper.launchPurchaseFlow(this, mSelectedSubscriptionPeriod, com.superChargedFitness.inapp.IabHelper.ITEM_TYPE_SUBS, oldSkus, RC_REQUEST, mPurchaseFinishedListener, payload)
            } catch (e: com.superChargedFitness.inapp.IabHelper.IabAsyncInProgressException) {
                complain("Error launching purchase flow. Another async operation in progress.")
            }
        }

        txtPrivacy.setOnClickListener {
            com.superChargedFitness.utils.Utils.openWebsite(this@PurchaseActivity,"https://sites.google.com/view/workout-privacy-policy/home")
        }

        txtTerms.setOnClickListener {
            com.superChargedFitness.utils.Utils.openWebsite(this@PurchaseActivity,"https://sites.google.com/view/workout-terms-of-use/home")
        }
    }

    // Callback for when a purchase is finished
    internal var mPurchaseFinishedListener: com.superChargedFitness.inapp.IabHelper.OnIabPurchaseFinishedListener = object : com.superChargedFitness.inapp.IabHelper.OnIabPurchaseFinishedListener {
        override fun onIabPurchaseFinished(result: com.superChargedFitness.inapp.IabResult, purchase: com.superChargedFitness.inapp.Purchase) {
            Log.d(TAG, "Purchase finished: $result, purchase: $purchase")

//              if we were disposed of in the meantime, quit.
//            if (mHelper == null) {
//                return
//            }

            if (result.response == 7) {
                alert("Item already purchased.")
                return
            } else if (result.isFailure) {
                complain("Error purchasing: $result")
                return
            }
            if (!verifyDeveloperPayload(purchase)) {
                complain("Error purchasing. Authenticity verification failed.")
                return
            }

            Log.d(TAG, "Subscription successful.")
            com.superChargedFitness.utils.Utils.setPref(this@PurchaseActivity, ConstantString.pref_Key_purchase_status, true)
            if (purchase.sku.equals(SKU_WEEKLY_SUB)) {
                // bought the infinite gas subscription
                Log.d(TAG, "Subscription purchased.")
                alert("Thank you for Subscribing Now you enjoy our app!")

                mSubscribedToInfiniteGas = true
                mAutoRenewEnabled = purchase.isAutoRenewing
                mInfiniteGasSku = purchase.sku
                startActivity(Intent(this@PurchaseActivity, HomeActivity::class.java))
                finish()
            } else if (purchase.sku.equals(SKU_YEAR_SUB)) {
                // bought the infinite gas subscription
                Log.d(TAG, "Subscription purchased.")
                alert("Thank you for Subscribing Now you enjoy our app!")

                mSubscribedToInfiniteGas = true
                mAutoRenewEnabled = purchase.isAutoRenewing
                mInfiniteGasSku = purchase.sku
                startActivity(Intent(this@PurchaseActivity, HomeActivity::class.java))
                finish()
            }
        }

    }

    // Todo Here define inapp purchase methods and data
    private fun loadPurchaseData() {
        Log.d(TAG, "Creating IAB helper.")
        mHelper = com.superChargedFitness.inapp.IabHelper(this, base64EncodedPublicKey)

        // enable debug logging (for a production application, you should set this to false).
        mHelper.enableDebugLogging(true)

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        Log.d(TAG, "Starting setup.")
        mHelper.startSetup(object : com.superChargedFitness.inapp.IabHelper.OnIabSetupFinishedListener {
            override fun onIabSetupFinished(result: com.superChargedFitness.inapp.IabResult) {
                Log.d(TAG, "Setup finished.")

                if (!result.isSuccess()) {
                    // Oh noes, there was a problem.
                    complain("Problem setting up in-app billing: $result")
                    return
                }

                // Have we been disposed of in the meantime? If so, quit.
                //if (mHelper == null) return

                // Important: Dynamically register for broadcast messages about updated purchases.
                // We register the receiver here instead of as a <receiver> in the Manifest
                // because we always call getPurchases() at startup, so therefore we can ignore
                // any broadcasts sent while the app isn't running.
                // Note: registering this listener in an Activity is a bad idea, but is done here
                // because this is a SAMPLE. Regardless, the receiver must be registered after
                // IabHelper is setup, but before first call to getPurchases().
                mBroadcastReceiver =
                    com.superChargedFitness.inapp.IabBroadcastReceiver(this@PurchaseActivity)
                val broadcastFilter = IntentFilter(com.superChargedFitness.inapp.IabBroadcastReceiver.ACTION)
                registerReceiver(mBroadcastReceiver, broadcastFilter)

                // IAB is fully set up. Now, let's get an inventory of stuff we own.
                Log.d(TAG, "Setup successful. Querying inventory.")
                try {
                    mHelper.queryInventoryAsync(mGotInventoryListener)
                } catch (e: com.superChargedFitness.inapp.IabHelper.IabAsyncInProgressException) {
                    complain("Error querying inventory. Another async operation in progress.")
                }
            }
        })
    }

    // Listener that's called when we finish querying the items and subscriptions we own
    internal var mGotInventoryListener: com.superChargedFitness.inapp.IabHelper.QueryInventoryFinishedListener = object : com.superChargedFitness.inapp.IabHelper.QueryInventoryFinishedListener {
        override fun onQueryInventoryFinished(result: com.superChargedFitness.inapp.IabResult, inventory: com.superChargedFitness.inapp.Inventory) {
            Log.d(TAG, "Query inventory finished.")

            // Have we been disposed of in the meantime? If so, quit.
//            if (mHelper == null) return

            // Is it a failure?
            if (result.isFailure) {
                complain("Failed to query inventory: $result")
                return
            }

            Log.d(TAG, "Query inventory was successful.")

            /*
             * Check for items we own. Notice that for each purchase, we check
             * the developer payload to see if it's correct! See
             * verifyDeveloperPayload().
             */

            // Do we have the premium upgrade?
            //            Purchase premiumPurchase = inventory.getPurchase(SKU_PREMIUM);
            //            mIsPremium = (premiumPurchase != null && verifyDeveloperPayload(premiumPurchase));
            //            Log.d(TAG, "User is " + (mIsPremium ? "PREMIUM" : "NOT PREMIUM"));

            val WeeklySub = inventory.getPurchase(SKU_WEEKLY_SUB)
            val MonthlySub = inventory.getPurchase(SKU_YEAR_SUB)
            val YearlySub = inventory.getPurchase(SKU_YEAR_SUB)

            if (WeeklySub != null && WeeklySub.isAutoRenewing()) {
                mInfiniteGasSku = SKU_YEAR_SUB
                mAutoRenewEnabled = true
            } else if (YearlySub != null && YearlySub.isAutoRenewing()) {
                mInfiniteGasSku = SKU_YEAR_SUB
                mAutoRenewEnabled = true
            } else {
                mInfiniteGasSku = ""
                mAutoRenewEnabled = false
            }

            Log.d(TAG, "Initial inventory query finished; enabling main UI.")
        }
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
                            if (jo.getString("purchaseState").length > 0) {
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
                                com.superChargedFitness.utils.Utils.setPref(this@PurchaseActivity, ConstantString.pref_Key_purchase_status, true)
                                return
                            } else {
                                com.superChargedFitness.utils.Utils.setPref(this@PurchaseActivity, ConstantString.pref_Key_purchase_status, false)
                            }
                        }
                    } else {
//                        checkPurchaseStatus()
                        com.superChargedFitness.utils.Utils.setPref(this@PurchaseActivity, ConstantString.pref_Key_purchase_status, false)
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

    internal fun verifyDeveloperPayload(p: com.superChargedFitness.inapp.Purchase): Boolean {
        val payload = p.getDeveloperPayload()

        /*
         * TODO: verify that the developer payload of the purchase is correct. It will be
         * the same one that you sent when initiating the purchase.
         *
         * WARNING: Locally generating a random string when starting a purchase and
         * verifying it here might seem like a good approach, but this will fail in the
         * case where the user purchases an item on one device and then uses your app on
         * a different device, because on the other device you will not have access to the
         * random string you originally generated.
         *
         * So a good developer payload has these characteristics:
         *
         * 1. If two different users purchase an item, the payload is different between them,
         *    so that one user's purchase can't be replayed to another user.
         *
         * 2. The payload must be such that you can verify it even when the app wasn't the
         *    one who initiated the purchase flow (so that items purchased by the user on
         *    one device work on other devices owned by the user).
         *
         * Using your own server to store and verify developer payloads across app
         * installations is recommended.
         */

        return true
    }

    internal fun complain(message: String) {
        Log.e(TAG, "**** TrivialDrive Error: $message")
        alert("Error: $message")
    }

    internal fun alert(message: String) {
        val bld = android.app.AlertDialog.Builder(this)
        bld.setMessage(message)
        bld.setNeutralButton("OK", null)
        Log.d(TAG, "Showing alert dialog: $message")
        bld.create().show()
    }

    override fun receivedBroadcast() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            if (mService != null) {
                unbindService(mServiceConn1)
            }

            if (mHelper != null) {
                mHelper.dispose()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        unregisterReceiver(mBroadcastReceiver)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        Log.d(TAG, "onActivityResult($requestCode,$resultCode,$data")

        // Pass on the activity result to the helper for handling
        if (requestCode == RC_REQUEST && resultCode == Activity.RESULT_OK) {
            if (!mHelper.handleActivityResult(requestCode, resultCode, data)) {
//            if(resultCode == Activity.RESULT_OK){
//                startActivity(Intent(this@PurchaseActivity,HomeActivity::class.java))
//            }
                // not handled, so handle it ourselves (here's where you'd
                // perform any handling of activity results not related to in-app
                // billing...
                super.onActivityResult(requestCode, resultCode, data)
            } else {
                Log.d(TAG, "onActivityResult handled by IABUtil.")
            }
        }
    }

}
