package com.superChargedFitness.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.superChargedFitness.data.repository.PurchaseRepository

/**
 * Sealed class representing the possible UI states for the purchase screen.
 */
sealed class PurchaseUiState {
    object Loading : PurchaseUiState()
    object Connected : PurchaseUiState()
    object Purchased : PurchaseUiState()
    object NotPurchased : PurchaseUiState()
    data class Error(val message: String) : PurchaseUiState()
}

/**
 * ViewModel for PurchaseActivity.
 * Manages billing connection state and purchase flow results.
 */
class PurchaseViewModel(application: Application) : AndroidViewModel(application) {

    private val purchaseRepository = PurchaseRepository(application)

    private val _purchaseState = MutableLiveData<PurchaseUiState>(PurchaseUiState.Loading)
    val purchaseState: LiveData<PurchaseUiState> = _purchaseState

    fun isPurchased(): Boolean = purchaseRepository.isPurchased()

    fun setPurchaseStatus(purchased: Boolean) {
        purchaseRepository.setPurchaseStatus(purchased)
        _purchaseState.value = if (purchased) PurchaseUiState.Purchased else PurchaseUiState.NotPurchased
    }

    fun setConnected() {
        _purchaseState.value = PurchaseUiState.Connected
    }

    fun setError(message: String) {
        _purchaseState.value = PurchaseUiState.Error(message)
    }
}
