package com.superChargedFitness.interfaces

interface AdsCallback {
    fun adLoadingFailed()
    fun adClose()
    fun startNextScreen()
    fun onLoaded()
}