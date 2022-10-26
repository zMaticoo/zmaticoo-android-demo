package com.maticoo.demo

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.maticoo.sdk.InitConfiguration
import com.maticoo.sdk.ad.banner.BannerAd
import com.maticoo.sdk.ad.banner.BannerAdListener
import com.maticoo.sdk.ad.interact.InteractAd
import com.maticoo.sdk.ad.interact.InteractAdListener
import com.maticoo.sdk.ad.utils.error.Error
import com.maticoo.sdk.ad.video.RewardedVideoAd
import com.maticoo.sdk.ad.video.RewardedVideoListener
import com.maticoo.sdk.core.InitCallback
import com.maticoo.sdk.core.MaticooAds
import com.maticoo.sdk.utils.error.InternalError


class MainActivity : AppCompatActivity() {

    companion object {
        private const val TAG = "MaticooTest"
    }

    private val btnLoadAndShowBanner: Button by lazy { findViewById(R.id.btn_load_and_show_banner) }
    private val btnLoadReward: Button by lazy { findViewById(R.id.btn_load_reward) }
    private val btnShowReward: Button by lazy { findViewById(R.id.btn_show_reward) }
    private val btnLoadAndShowInteract: Button by lazy { findViewById(R.id.btn_load_and_show_interact) }

    private val tvAdInfo: TextView by lazy { findViewById(R.id.tv_info) }

    private val layoutBannerContainer: FrameLayout by lazy { findViewById(R.id.layout_banner_container) }
    private val layoutInteractContainer: FrameLayout by lazy { findViewById(R.id.layout_interact_container) }

    private var bannerAd: BannerAd? = null
    private var interactAd: InteractAd? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initSdk()

        btnLoadAndShowBanner.setOnClickListener {
            loadAndShowBanner()
        }

        btnLoadReward.setOnClickListener {
            loadReward()
        }

        btnShowReward.setOnClickListener {
            showReward()
        }

        btnLoadAndShowInteract.setOnClickListener {
            loadAndShowInteract()
        }
    }

    private fun initSdk() {
        showAdInfo("initSdk, start")

        btnLoadAndShowBanner.isEnabled = false
        btnLoadReward.isEnabled = false
        btnShowReward.isEnabled = false
        btnLoadAndShowInteract.isEnabled = false
        val configuration = InitConfiguration.Builder()
            .appKey(Constant.APP_KEY)
            .logEnable(true)
            .build()
        MaticooAds.init(this, configuration, object : InitCallback {
            override fun onSuccess() {
                showAdInfo("initSdk, onSuccess")
                btnLoadAndShowBanner.isEnabled = true
                btnLoadReward.isEnabled = true
                btnLoadAndShowInteract.isEnabled = true
            }

            override fun onError(result: InternalError) {
                showAdInfo("initSdk, onError: error = $result")
            }
        })
    }

    private fun loadAndShowBanner() {

        bannerAd = BannerAd(this, Constant.BANNER_ID)
        bannerAd?.setAdListener(object : BannerAdListener {
            override fun onBannerAdReady(placementId: String, view: View) {
                showAdInfo("bannerAd, onBannerAdReady: placementId = $placementId")

                layoutBannerContainer.removeAllViews()
                val layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                )
                layoutBannerContainer.addView(view, layoutParams)
            }

            override fun onBannerAdFailed(placementId: String, error: Error) {
                showAdInfo("bannerAd, onBannerAdFailed: placementId = $placementId, error = $error")
            }

            override fun onBannerAdClicked(placementId: String) {
                showAdInfo("bannerAd, onBannerAdClicked: placementId = $placementId")
            }

            override fun onBannerAdShowFailed(placementId: String, error: Error) {
                showAdInfo("bannerAd, onBannerAdShowFailed: placementId = $placementId, error = $error")
            }

        })

        showAdInfo("bannerAd, load banner ad started")
        bannerAd?.loadAd()
    }

    private fun loadReward() {
        btnShowReward.isEnabled = false
        RewardedVideoAd.setAdListener(Constant.REWARD_ID, object : SimpleRewardedVideoListener() {
            override fun onRewardedVideoAdLoadSuccess(placementId: String?) {
                btnShowReward.isEnabled = true
                showAdInfo("rewardAd, onRewardedVideoAdLoadSuccess: placementId = $placementId")
            }

            override fun onRewardedVideoAdLoadFailed(placementId: String?, error: Error?) {
                showAdInfo("rewardAd, onRewardedVideoAdLoadFailed: placementId = $placementId, error = $error")
                btnShowReward.isEnabled = false
            }
        })
        showAdInfo("bannerAd, load reward ad started")
        RewardedVideoAd.loadAd(Constant.REWARD_ID)
    }

    private fun showReward() {
        if (!RewardedVideoAd.isReady(Constant.REWARD_ID)) {
            showAdInfo("rewardAd, showReward, Ad is not ready, return")
            return
        }

        RewardedVideoAd.setAdListener(Constant.REWARD_ID, object : SimpleRewardedVideoListener() {
            override fun onRewardedVideoAdShowed(placementId: String?) {
                showAdInfo("rewardAd, onRewardedVideoAdShowed: placementId = $placementId")
            }

            override fun onRewardedVideoAdShowFailed(placementId: String?, error: Error?) {
                showAdInfo("rewardAd, onRewardedVideoAdShowFailed: placementId = $placementId, error = $error")
            }

            override fun onRewardedVideoAdStarted(placementId: String?) {
                showAdInfo("rewardAd, onRewardedVideoAdStarted: placementId = $placementId")
            }

            override fun onRewardedVideoAdCompleted(placementId: String?) {
                showAdInfo("rewardAd, onRewardedVideoAdCompleted: placementId = $placementId")
            }

            override fun onRewardedVideoAdRewarded(placementId: String?) {
                showAdInfo("rewardAd, onRewardedVideoAdRewarded: placementId = $placementId")
            }

            override fun onRewardedVideoAdClosed(placementId: String?) {
                showAdInfo("rewardAd, onRewardedVideoAdClosed: placementId = $placementId")
                btnShowReward.isEnabled = false
            }

            override fun onRewardedVideoAdClicked(placementId: String?) {
                showAdInfo("rewardAd, onRewardedVideoAdClicked: placementId = $placementId")
            }
        })
        RewardedVideoAd.showAd(Constant.REWARD_ID)
    }

    private fun loadAndShowInteract() {
        interactAd = InteractAd(this, Constant.INTERACT_ID)
        interactAd?.setAdListener(object : InteractAdListener {
            override fun onInteractAdReady(placementId: String, view: View) {
                // Invoked when InteractAd Ad are available.
                showAdInfo("interactAd, onInteractAdReady: placementId = $placementId")

                try {
                    if (null != view.parent) {
                        (view.parent as ViewGroup).removeView(view)
                    }
                    layoutInteractContainer.removeAllViews()
                    val layoutParams = FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                    )
                    layoutInteractContainer.addView(view, layoutParams)
                } catch (e: Exception) {
                    showAdInfo("interactAd, onInteractAdReady, Exception = $e")
                }
            }

            override fun onInteractAdFailed(placementId: String, error: Error) {
                showAdInfo("interactAd, onInteractAdFailed: placementId = $placementId, error = ${error}")
            }
            override fun onInteractAdEntranceShowed(placementId: String) {
                showAdInfo("interactAd, onInteractAdEntranceShowed: placementId = $placementId")
            }
            override fun onInteractAdEntranceShowFailed(placementId: String, error: Error) {
                showAdInfo("interactAd, onInteractAdEntranceShowFailed: placementId = $placementId, error = ${error}")
            }
            override fun onInteractAdEntranceClick(placementId: String) {
                showAdInfo("interactAd, onInteractAdEntranceClick: placementId = $placementId")
            }
            override fun onInteractAdFullScreenOpened(placementId: String) {
                showAdInfo("interactAd, onInteractAdFullScreenOpened: placementId = $placementId")
            }
            override fun onInteractAdFullScreenOpenFailed(placementId: String, error: Error) {
                showAdInfo("interactAd, onInteractAdFullScreenOpenFailed: placementId = $placementId")
            }
            override fun onInteractAdFullScreenClose(placementId: String) {
                showAdInfo("interactAd, onInteractAdFullScreenClose: placementId = $placementId")
            }
        })

        interactAd?.loadAd()
    }

    private fun showAdInfo(info: String) {
        tvAdInfo.text = info
        Log.d(TAG, "showAdInfo: info = $info")
    }

    override fun onDestroy() {
        super.onDestroy()
        bannerAd?.destroy()
        interactAd?.destroy()
    }
}

private open class SimpleRewardedVideoListener : RewardedVideoListener {
    override fun onRewardedVideoAdLoadSuccess(placementId: String?) {
    }

    override fun onRewardedVideoAdLoadFailed(placementId: String?, error: Error?) {
    }

    override fun onRewardedVideoAdShowed(placementId: String?) {
    }

    override fun onRewardedVideoAdShowFailed(placementId: String?, error: Error?) {
    }

    override fun onRewardedVideoAdStarted(placementId: String?) {
    }

    override fun onRewardedVideoAdCompleted(placementId: String?) {
    }

    override fun onRewardedVideoAdRewarded(placementId: String?) {
    }

    override fun onRewardedVideoAdClicked(placementId: String?) {
    }

    override fun onRewardedVideoAdClosed(placementId: String?) {
    }

}