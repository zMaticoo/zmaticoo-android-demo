package com.ironsource.adapters.custom.maticoo;

import android.app.Activity;

import com.ironsource.mediationsdk.adunit.adapter.BaseRewardedVideo;
import com.ironsource.mediationsdk.adunit.adapter.internal.listener.AdapterAdInteractionListener;
import com.ironsource.mediationsdk.adunit.adapter.listener.RewardedVideoAdListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.maticoo.sdk.ad.utils.error.Error;
import com.maticoo.sdk.ad.video.RewardedVideoAd;
import com.maticoo.sdk.ad.video.RewardedVideoListener;
import com.maticoo.sdk.utils.log.AdLog;

public class MaticooCustomRewardedVideo extends BaseRewardedVideo implements RewardedVideoListener {

    private RewardedVideoAdListener irLoadListener;
    private RewardedVideoAdListener irShowListener;

    public MaticooCustomRewardedVideo(NetworkSettings networkSettings) {
        super(networkSettings);

    }

    @Override
    public void loadAd(AdData adData, Activity activity, Object listener) {
        String placementId = adData.getString(Constant.PLACEMENT_ID);
        AdLog.getSingleton().LogD("[IronSource] loadAd, placementId = " + placementId);
        if (listener != null && (listener instanceof RewardedVideoAdListener)) {
            irLoadListener = (RewardedVideoAdListener) listener;
            RewardedVideoAd.setAdListener(placementId, this);
        }
        RewardedVideoAd.loadAd(placementId);
    }

    @Override
    public void showAd(AdData adData, AdapterAdInteractionListener listener) {
        String placementId = adData.getString(Constant.PLACEMENT_ID);
        AdLog.getSingleton().LogD(this.hashCode() + "[IronSource] showAd, placementId = " + placementId);
        if (listener != null && (listener instanceof RewardedVideoAdListener)) {
            irShowListener = (RewardedVideoAdListener) listener;
            RewardedVideoAd.setAdListener(placementId, this);
        }
        RewardedVideoAd.showAd(placementId);
    }

    @Override
    public boolean isAdAvailable(AdData adData) {
        String placementId = adData.getString(Constant.PLACEMENT_ID);
        boolean isReady = RewardedVideoAd.isReady(placementId);
        AdLog.getSingleton().LogD("[IronSource] isAdAvailable, placementId = " + placementId + "  isReady = " + isReady);
        tryLoadRewardAd(placementId);
        return isReady;
    }

    @Override
    public void onRewardedVideoAdLoadSuccess(String placementId) {
        AdLog.getSingleton().LogD("[IronSource] onRewardedVideoAdLoadSuccess, placementId = " + placementId + "  irLoadListener = " + irLoadListener);
        if (irLoadListener != null) {
            irLoadListener.onAdLoadSuccess();
        }
    }

    @Override
    public void onRewardedVideoAdLoadFailed(String placementId, Error error) {
        AdLog.getSingleton().LogD("[IronSource] onRewardedVideoAdLoadFailed, placementId = " + placementId + "  irLoadListener = " + irLoadListener);
        if (irLoadListener != null) {
            irLoadListener.onAdLoadFailed(AdUtils.getErrorType(error), error.getCode(), error.getMessage());
        }
    }

    @Override
    public void onRewardedVideoAdShowed(String placementId) {
        AdLog.getSingleton().LogD(this.hashCode() + "[IronSource] onRewardedVideoAdShowed, placementId = " + placementId + "  irShowListener = " + irShowListener);
        if (irShowListener != null) {
            irShowListener.onAdShowSuccess();
            irShowListener.onAdOpened();
        }
    }

    @Override
    public void onRewardedVideoAdShowFailed(String placementId, Error error) {
        AdLog.getSingleton().LogD("[IronSource] onRewardedVideoAdShowFailed, placementId = " + placementId + "  irShowListener = " + irShowListener);
        if (irShowListener != null) {
            irShowListener.onAdShowFailed(error.getCode(), error.getMessage());
        }
    }

    @Override
    public void onRewardedVideoAdStarted(String placementId) {
        AdLog.getSingleton().LogD("[IronSource] onRewardedVideoAdStarted, placementId = " + placementId + "  irShowListener = " + irShowListener);
        if (irShowListener != null) {
            irShowListener.onAdStarted();
        }
    }

    @Override
    public void onRewardedVideoAdCompleted(String placementId) {
        AdLog.getSingleton().LogD("[IronSource] onRewardedVideoAdCompleted, placementId = " + placementId + "  irShowListener = " + irShowListener);
        if (irShowListener != null) {
            irShowListener.onAdEnded();
        }
    }

    @Override
    public void onRewardedVideoAdRewarded(String placementId) {
        AdLog.getSingleton().LogD("[IronSource] onRewardedVideoAdRewarded, placementId = " + placementId + "  irShowListener = " + irShowListener);
        if (irShowListener != null) {
            irShowListener.onAdRewarded();
        }
    }

    @Override
    public void onRewardedVideoAdClicked(String placementId) {
        AdLog.getSingleton().LogD("[IronSource] onRewardedVideoAdClicked, placementId = " + placementId + "  irShowListener = " + irShowListener);
        if (irShowListener != null) {
            irShowListener.onAdClicked();
        }
    }

    @Override
    public void onRewardedVideoAdClosed(String placementId) {
        AdLog.getSingleton().LogD("[IronSource] onRewardedVideoAdClosed, placementId = " + placementId + "  irShowListener = " + irShowListener);
        if (irShowListener != null) {
            irShowListener.onAdClosed();
        }

        tryLoadRewardAd(placementId);
    }

    private void tryLoadRewardAd(String placementId) {
        boolean isReady = RewardedVideoAd.isReady(placementId);
        AdLog.getSingleton().LogD("[IronSource] tryLoadRewardAd, placementId = " + placementId + "  isReady = " + isReady);
        if (!isReady) {
            RewardedVideoAd.loadAd(placementId);
        }
    }
}