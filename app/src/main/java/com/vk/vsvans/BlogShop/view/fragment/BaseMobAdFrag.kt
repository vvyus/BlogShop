package com.vk.vsvans.BlogShop.view.fragment

import android.app.Activity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.vk.vsvans.BlogShop.interfaces.InterAdmobClose
import com.vk.vsvans.BlogShop.R
open class BaseMobAdFrag:Fragment(), InterAdmobClose {
    lateinit var adView:AdView
    var inerAd: InterstitialAd? = null

    val TAG="MyLog"
//ca-app-pub-3940256099942544/1033173712
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAds()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loadInterAd()
    }

    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onPause() {
        super.onPause()
        adView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        adView.destroy()
    }

    private fun initAds(){
        MobileAds.initialize(activity as Activity)
        val adRequest=AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun loadInterAd(){
        val adRequest=AdRequest.Builder().build()
        InterstitialAd.load(context as Activity,getString(R.string.ad_inter_Id),adRequest,
            object : InterstitialAdLoadCallback() {

                override fun onAdLoaded(ad: InterstitialAd) {
                    Log.d(TAG, "Ad was loaded.")
                    inerAd = ad
                }
            }
        )
    }

    fun showInterAd() {
        if (inerAd != null) {
            inerAd?.fullScreenContentCallback=object : FullScreenContentCallback(){
                override fun onAdDismissedFullScreenContent() {
                    //super.onAdDismissedFullScreenContent()
                    onClose()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    //super.onAdFailedToShowFullScreenContent(p0)
                    // если ошибка то закрываем
                    onClose()
                }
            }
            inerAd?.show(activity as Activity)
        }else {
            onClose()
        }
    }

    override fun onClose() {}
}