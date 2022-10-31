package com.maticoo.demo

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.ironsource.mediationsdk.IronSource
import com.ironsource.mediationsdk.logger.IronSourceError
import com.ironsource.mediationsdk.model.Placement
import com.maticoo.sdk.ad.video.RewardedVideoAd


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MaticooTest"

        private const val IRON_SOURCE_APP_KEY = "16f13e745"
    }

    private val btnLoadReward: Button by lazy { findViewById(R.id.btn_load_ad) }
    private val btnShowReward: Button by lazy { findViewById(R.id.btn_show_ad) }
    private val btnIszMaticooReady: Button by lazy { findViewById(R.id.btn_is_maticoo_ready) }

    private val tvAdInfo: TextView by lazy { findViewById(R.id.tv_info) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSdk()

        btnLoadReward.setOnClickListener {
            loadReward()
        }

        btnShowReward.setOnClickListener {
            showReward()
        }

        btnIszMaticooReady.setOnClickListener {

            val isReady = RewardedVideoAd.isReady("1003178826");
            Log.d(TAG, "check zMatcioo ad is ready: $isReady")
            Toast.makeText(this, "check zMatcioo ad is ready: $isReady", Toast.LENGTH_SHORT).show()
        }
    }

    private fun initSdk() {
        Log.d(TAG, "init, start")
        IronSource.setRewardedVideoListener(IronRewardedVideoListener())
        IronSource.init(this, IRON_SOURCE_APP_KEY) {
            Log.d(TAG, "init, onInitializationComplete")
        }
    }

    fun loadReward() {
        Log.d(TAG, "loadAd: start")
        IronSource.loadRewardedVideo()
    }

    fun showReward() {
        Log.d(TAG, "showAd: start")
        IronSource.showRewardedVideo()
    }

    private class IronRewardedVideoListener :
        com.ironsource.mediationsdk.sdk.RewardedVideoListener {
        override fun onRewardedVideoAvailabilityChanged(b: Boolean) {
            Log.d(TAG, "onRewardedVideoAvailabilityChanged: b = $b")
        }

        override fun onRewardedVideoAdOpened() {
            Log.d(TAG, "onRewardedVideoAdOpened")
        }

        override fun onRewardedVideoAdClosed() {
            Log.d(TAG, "onRewardedVideoAdClosed")
        }

        override fun onRewardedVideoAdStarted() {
            Log.d(TAG, "onRewardedVideoAdStarted")
        }

        override fun onRewardedVideoAdEnded() {
            Log.d(TAG, "onRewardedVideoAdEnded")
        }

        override fun onRewardedVideoAdRewarded(placement: Placement) {
            Log.d(TAG, "onRewardedVideoAdRewarded, placement = " + placement.placementName)
        }

        override fun onRewardedVideoAdShowFailed(ironSourceError: IronSourceError) {
            Log.d(TAG, "onRewardedVideoAdShowFailed, error = $ironSourceError")
        }

        override fun onRewardedVideoAdClicked(placement: Placement) {
            Log.d(TAG, "onRewardedVideoAdClicked, placement = " + placement.placementName)
        }
    }

    override fun onResume() {
        super.onResume()
        IronSource.onResume(this)
    }

    override fun onPause() {
        super.onPause()
        IronSource.onPause(this)
    }

    private fun showAdInfo(info: String) {
        tvAdInfo.text = info
        Log.d(TAG, "showAdInfo: info = $info")
    }
}