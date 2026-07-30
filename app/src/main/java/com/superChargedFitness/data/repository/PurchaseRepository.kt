package com.superChargedFitness.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.superChargedFitness.utils.ConstantString

/**
 * Repository for managing purchase/subscription state.
 * Decouples purchase status checks from direct SharedPreferences access
 * that was previously scattered across SplashActivity, PurchaseActivity, etc.
 */
class PurchaseRepository(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("MyPref", Context.MODE_PRIVATE)

    fun isPurchased(): Boolean {
        return prefs.getBoolean(ConstantString.pref_Key_purchase_status, false)
    }

    fun setPurchaseStatus(purchased: Boolean) {
        prefs.edit().putBoolean(ConstantString.pref_Key_purchase_status, purchased).apply()
    }
}
