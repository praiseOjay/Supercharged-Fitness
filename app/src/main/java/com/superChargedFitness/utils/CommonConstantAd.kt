package com.superChargedFitness.utils

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.facebook.ads.Ad
import com.facebook.ads.InterstitialAdListener
import com.google.android.ads.nativetemplates.TemplateView
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.superChargedFitness.interfaces.AdsCallback


object CommonConstantAd {



    private fun getAdRequest(): AdRequest {
        return AdRequest.Builder().build()
    }


    fun loadNativeAd(context: Context, templateView: TemplateView,isViewInvisible:Boolean = false) {
        if (com.superChargedFitness.utils.Utils.getPref(context, ConstantString.AD_TYPE_FB_GOOGLE, "") == ConstantString.AD_GOOGLE &&
            com.superChargedFitness.utils.Utils.getPref(context, ConstantString.STATUS_ENABLE_DISABLE, "") == ConstantString.ENABLE
        ) {
            val adLoader: AdLoader =
                AdLoader.Builder(context, com.superChargedFitness.utils.Utils.getPref(context, ConstantString.GOOGLE_NATIVE, ""))
                    .withAdListener(object :AdListener(){
                        override fun onAdFailedToLoad(p0: LoadAdError) {
                            templateView.visibility = View.GONE
                            if (isViewInvisible){
                                templateView.visibility = View.INVISIBLE
                            }
                            Log.e("TAG", "onAdFailedToLoad:::Native Ad==>>>>  "+p0.message.toString() )
                        }

                        override fun onAdLoaded() {
                            super.onAdLoaded()
                            templateView.visibility = View.VISIBLE

                        }
                    })
                    .forNativeAd { nativeAd ->
                        templateView.visibility = View.VISIBLE
                        templateView.setNativeAd(nativeAd)
                    }
                    .build()
            adLoader.loadAd(AdRequest.Builder().build())
        } else {
            templateView.visibility = View.GONE
        }

    }

    var mInterstitialAd: InterstitialAd? = null
    fun googlebeforloadAd(context: Context) {

        try {
            InterstitialAd.load(
                    context, com.superChargedFitness.utils.Utils.getPref(context, ConstantString.GOOGLE_INTERSTITIAL, "")!!, getAdRequest(),
                    object : InterstitialAdLoadCallback() {
                        override fun onAdFailedToLoad(adError: LoadAdError) {
                            Log.e("TAG ERRRR:::: ", adError.message)
                            mInterstitialAd = null
                        }

                        override fun onAdLoaded(interstitialAd: InterstitialAd) {
                            Log.e("TAG", "Ad was loaded.")
                            mInterstitialAd = interstitialAd
                        }
                    }
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    fun showInterstitialAdsGoogle(context: Context,adsCallback: AdsCallback) {

        try {
            if (mInterstitialAd != null) {
                mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback() {
                    override fun onAdDismissedFullScreenContent() {
                        Log.d("TAG", "Ad was dismissed.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        mInterstitialAd = null
                        adsCallback.startNextScreen()

                    }

                    override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                        Log.d("TAG", "Ad failed to show.")
                        // Don't forget to set the ad reference to null so you
                        // don't show the ad a second time.
                        mInterstitialAd = null
                        adsCallback.startNextScreen()
                    }

                    override fun onAdShowedFullScreenContent() {
                        Log.d("TAG", "Ad showed fullscreen content.")
                        // Called when ad is dismissed.
                        adsCallback.onLoaded()
                    }
                }
                mInterstitialAd?.show(context as Activity)
            } else {
                Log.e("TAG", "showInterstitialAdsGoogle:::::NOT LOADED:::::  " )
                adsCallback.startNextScreen()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /*Facebook Full Ad*/
    var interstitialAdFb: com.facebook.ads.InterstitialAd? = null
    private var adsCallbackFb: AdsCallback? = null
    fun facebookbeforeloadFullAd(context: Context) {
        try {
            interstitialAdFb = com.facebook.ads.InterstitialAd(context, com.superChargedFitness.utils.Utils.getPref(context,ConstantString.FB_INTERSTITIAL,""))
            adsCallbackFb = null
            val interstitialAdListener: InterstitialAdListener = object : InterstitialAdListener {
                override fun onInterstitialDisplayed(ad: Ad?) {
                    Log.e("TAG", "Interstitial ad displayed.")

                }

                override fun onInterstitialDismissed(ad: Ad?) {
                    Log.e("TAG", "Interstitial ad dismissed.")
                    adsCallbackFb!!.adClose()
                }

                override fun onError(ad: Ad?, adError: com.facebook.ads.AdError) {
    //                adsCallbackFb!!.adLoadingFailed()
                    Log.e("TAG", "onError:Facebook :::::::::  "+adError.errorMessage+"  "+adError.errorCode )
                }

                override fun onAdLoaded(ad: Ad?) {
                    Log.e("TAG", "Interstitial ad is loaded and ready to be displayed!")
                    // Show the ad
                }

                override fun onAdClicked(ad: Ad?) {
                    Log.e("TAG", "Interstitial ad clicked!")
                }

                override fun onLoggingImpression(ad: Ad?) {
                    Log.e("TAG", "Interstitial ad impression logged!")
                }
            }

            interstitialAdFb!!.loadAd(
                    interstitialAdFb!!.buildLoadAdConfig()
                            .withAdListener(interstitialAdListener)
                            .build()
            )
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }
    fun showInterstitialAdsFacebook(adsCallbackFb: AdsCallback) {
        this.adsCallbackFb = adsCallbackFb
        try {
            if (interstitialAdFb != null) {
                if (interstitialAdFb!!.isAdLoaded) {
                    interstitialAdFb!!.show()
                    adsCallbackFb.onLoaded()
                } else {
                    adsCallbackFb.startNextScreen()
                }
            } else {
                adsCallbackFb.startNextScreen()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun loadFacebookBannerAd(context: Context, banner_container: LinearLayout) {

        Log.e("TAG", "loadFbAdFacebook::::::::::: ")
        var adView: com.facebook.ads.AdView? = null
        adView = com.facebook.ads.AdView(context, com.superChargedFitness.utils.Utils.getPref(context,ConstantString.FB_BANNER,""), com.facebook.ads.AdSize.BANNER_HEIGHT_50)



        banner_container.addView(adView)

        val adListener: com.facebook.ads.AdListener = object : com.facebook.ads.AdListener {
            override fun onError(ad: Ad?, adError: com.facebook.ads.AdError) {
                // Ad error callback
                Log.e("TAG", "onError:Fb:::: ${adError.errorCode}   ${adError.errorMessage}")
                banner_container.visibility = View.GONE
            }

            override fun onAdLoaded(ad: Ad?) {
                // Ad loaded callback
                Log.e("TAG", "onAdLoaded:::::: ")
                banner_container.visibility = View.VISIBLE
            }

            override fun onAdClicked(ad: Ad?) {
                // Ad clicked callback
            }

            override fun onLoggingImpression(ad: Ad?) {
                // Ad impression logged callback
            }


        }

        adView!!.loadAd(adView.buildLoadAdConfig().withAdListener(adListener).build())
    }

    fun loadBannerGoogleAd(context: Context, llAdview: RelativeLayout, type: String) {
        val adViewBottom = AdView(context)
        if (type.equals("GOOGLE_BANNER_TYPE_AD")) {
            //adViewBottom.adSize = AdSize.BANNER
        } else if (type.equals("GOOGLE_RECTANGLE_BANNER_TYPE_AD")) {
            //adViewBottom.adSize = AdSize.MEDIUM_RECTANGLE
        }
        adViewBottom.adUnitId = com.superChargedFitness.utils.Utils.getPref(context,ConstantString.GOOGLE_BANNER,"")
        llAdview.addView(adViewBottom)
        val adRequest = AdRequest.Builder().build()
        adViewBottom.loadAd(adRequest)
        adViewBottom.adListener = object : AdListener() {
            override fun onAdLoaded() {
                super.onAdLoaded()
                adViewBottom.visibility = View.VISIBLE
                llAdview.visibility = View.VISIBLE
            }

            override fun onAdFailedToLoad(p0: LoadAdError) {
                super.onAdFailedToLoad(p0)
                llAdview.visibility = View.GONE
                Log.e("TAG", "onAdFailedToLoad:::Google Ad:::  ${p0.toString()}")
            }
        }
    }



}