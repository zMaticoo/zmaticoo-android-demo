package com.yunce.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.maticoo.sdk.InitConfiguration;
import com.maticoo.sdk.ad.banner.BannerAd;
import com.maticoo.sdk.ad.banner.BannerAdListener;
import com.maticoo.sdk.ad.banner.BannerAdOptions;
import com.maticoo.sdk.ad.interstitial.InterstitialAd;
import com.maticoo.sdk.ad.interstitial.InterstitialAdListener;
import com.maticoo.sdk.ad.video.RewardedVideoAd;
import com.maticoo.sdk.ad.video.RewardedVideoListener;
import com.maticoo.sdk.core.InitCallback;
import com.maticoo.sdk.core.MaticooAds;
import com.zmaticoo.sdk.ads.rewardads.MaticooRewardInfo;
import com.zmaticoo.sdk.base.common.MaticooIds;
import com.zmaticoo.sdk.base.common.logging.ZmaticooLog;
import com.zmaticoo.sdk.flow.model.ComponentError;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // If you want to test your own zMaticoo SDK key, change the value here and update the package name in the build.gradle
    private static final String YOUR_SDK_KEY = "a612f6f97402f33d844f7926016c69d14b3a5ffa9afb6f1e8979b61bdcc5f7b2";
    // If you want to test your own banner, change the value here
    private static final String BANNER_AD_UNIT_ID = "1004139147";
    private static final String INTERSTITIAL_AD_UNIT_ID = "1004002404";
    private static final String REWARD_AD_UNIT_ID = "1004115396";

    private LoadingAdPop loadingPopup;
    private BannerAd bannerAd;
    private FrameLayout bannerContainer;
    private TextView bannerStatus;

    private Button btnInterstitialShow;
    private TextView interstitialStatus;

    private Button btnRewardShow;
    private TextView rewardStatus;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        findViewById(R.id.button_setting).setOnClickListener(this);
        bannerContainer = findViewById(R.id.banner_container);
        bannerStatus = findViewById(R.id.bannerStatus);
        findViewById(R.id.button_init).setOnClickListener(this);
        findViewById(R.id.button_setting).setOnClickListener(this);
        findViewById(R.id.loadBanner).setOnClickListener(this);
        findViewById(R.id.loadInterstitial).setOnClickListener(this);
        findViewById(R.id.loadReward).setOnClickListener(this);

        interstitialStatus = findViewById(R.id.interstitialStatus);
        btnInterstitialShow = findViewById(R.id.interstitialShow);
        btnInterstitialShow.setOnClickListener(this);
        findViewById(R.id.interstitialShow).setOnClickListener(this);

        rewardStatus = findViewById(R.id.rewardStatus);
        btnRewardShow = findViewById(R.id.rewardShow);
        btnRewardShow.setOnClickListener(this);
        findViewById(R.id.rewardShow).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if (view == null) {
            return;
        }
        if (view.getId() == R.id.button_init) {
            initSDK();
        } else if (view.getId() == R.id.loadBanner) {
            loadBanner();
        } else if (view.getId() == R.id.loadInterstitial) {
            loadInterstitial();
        } else if (view.getId() == R.id.interstitialShow) {
            showInterstitial();
        } else if (view.getId() == R.id.loadReward) {
            loadReward();
        } else if (view.getId() == R.id.rewardShow) {
            showReward();
        } else if (view.getId() == R.id.button_setting) {
            startActivity(new Intent(this, SettingActivity.class));
        }
    }


    public void initSDK() {
        InitConfiguration configuration = new InitConfiguration.Builder()
                .appKey(YOUR_SDK_KEY)
                .logLevel(ZmaticooLog.LogLevel.DEVELOP)
                .build();

        MaticooAds.init(this, configuration, new InitCallback() {
            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "SDK Init Success", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(ComponentError result) {
                Toast.makeText(MainActivity.this, "SDK Init Error: " + result.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void loadBanner() {
        if (bannerAd == null) {
            BannerAdOptions adOptions = new BannerAdOptions.
                    Builder(BANNER_AD_UNIT_ID)
                    .setCanCloseAd(true)
                    .build();
            bannerAd = new BannerAd(this, adOptions);
            bannerAd.setAdListener(new BannerAdListener() {
                @Override
                public void onBannerAdReady(MaticooIds maticooIds, View view) {
                    super.onBannerAdReady(maticooIds, view);
                    handler.post(() -> {

                        bannerStatus.setText("load success");
                        bannerContainer.setVisibility(View.VISIBLE);
                    });
                }

                @Override
                public void onBannerAdFailed(MaticooIds maticooIds, ComponentError componentError) {
                    super.onBannerAdFailed(maticooIds, componentError);
                    handler.post(() -> bannerStatus.setText("load failed " + componentError.toString()));
                }

                @Override
                public void onBannerAdClicked(MaticooIds maticooIds) {
                    super.onBannerAdClicked(maticooIds);
                }

                @Override
                public void onBannerAdShow(MaticooIds maticooIds) {
                    super.onBannerAdShow(maticooIds);
                }

                @Override
                public void onBannerAdShowFailed(MaticooIds maticooIds, ComponentError componentError) {
                    super.onBannerAdShowFailed(maticooIds, componentError);
                }

                @Override
                public void onBannerAdClosed(MaticooIds maticooIds) {
                    super.onBannerAdClosed(maticooIds);
                }

                @Override
                public void onBannerAdLeaveApp(MaticooIds maticooIds) {
                    super.onBannerAdLeaveApp(maticooIds);
                }
            });
        }
        bannerContainer.removeAllViews();
        bannerContainer.addView(bannerAd);
        bannerStatus.setText("loading...");
        bannerAd.loadAd();
    }

    private void loadInterstitial() {
        showLoadingPopup();
        InterstitialAd.setAdListener(INTERSTITIAL_AD_UNIT_ID, new InterstitialAdListener() {
            @Override
            public void onAdLoadSuccess(MaticooIds maticooIds) {
                handler.post(() -> {
                    hideLoadingPopup();
                    btnInterstitialShow.setEnabled(true);
                });
            }

            @Override
            public void onAdLoadFailed(MaticooIds maticooIds, ComponentError componentError) {
                handler.post(() -> {
                    hideLoadingPopup();
                    interstitialStatus.setText("load failed " + componentError.toString());
                });
            }

            @Override
            public void onAdDisplayed(MaticooIds maticooIds) {
            }

            @Override
            public void onAdDisplayFailed(MaticooIds maticooIds, ComponentError componentError) {
            }

            @Override
            public void onAdClicked(MaticooIds maticooIds) {
            }

            @Override
            public void onAdClosed(MaticooIds maticooIds) {
            }

            @Override
            public void onAdStart(MaticooIds maticooIds) {
            }

            @Override
            public void onAdSkip(MaticooIds maticooIds) {
            }

            @Override
            public void onEndCardShow(MaticooIds maticooIds) {
            }
        });
        InterstitialAd.loadAd(INTERSTITIAL_AD_UNIT_ID);
    }

    private void showInterstitial() {
        InterstitialAd.showAd(INTERSTITIAL_AD_UNIT_ID);
    }

    private void loadReward() {
        showLoadingPopup();
        RewardedVideoAd.setAdListener(REWARD_AD_UNIT_ID, new RewardedVideoListener() {
            @Override
            public void onRewardedVideoAdLoadSuccess(MaticooIds maticooIds) {
                handler.post(() -> {
                    hideLoadingPopup();
                    btnRewardShow.setEnabled(true);
                });
            }

            @Override
            public void onRewardedVideoAdLoadFailed(MaticooIds maticooIds, ComponentError componentError) {
                handler.post(() -> {
                    hideLoadingPopup();
                    rewardStatus.setText("load failed " + componentError.toString());
                });
            }

            @Override
            public void onRewardedVideoAdShowed(MaticooIds maticooIds) {
            }

            @Override
            public void onRewardedVideoAdShowFailed(MaticooIds maticooIds, ComponentError componentError) {
            }

            @Override
            public void onRewardedVideoAdStarted(MaticooIds maticooIds) {
            }

            @Override
            public void onRewardedVideoAdCompleted(MaticooIds maticooIds) {
            }

            @Override
            public void onRewardedVideoAdRewarded(MaticooIds maticooIds, MaticooRewardInfo maticooRewardInfo) {
            }

            @Override
            public void onRewardedVideoAdClicked(MaticooIds maticooIds) {
            }

            @Override
            public void onRewardedVideoAdClosed(MaticooIds maticooIds) {
            }

            @Override
            public void onRewardedAdSkip(MaticooIds maticooIds) {
            }

            @Override
            public void onRewardedEndCardShow(MaticooIds maticooIds) {
            }
        });
        RewardedVideoAd.loadAd(REWARD_AD_UNIT_ID);
    }

    private void showReward() {
        RewardedVideoAd.showAd(REWARD_AD_UNIT_ID);
    }

    private void showLoadingPopup() {
        loadingPopup = new LoadingAdPop(this);
        loadingPopup.showAdPop(getWindow().getDecorView());
    }

    private void hideLoadingPopup() {
        if (loadingPopup != null) {
            loadingPopup.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        if (bannerAd != null) {
            bannerAd.destroy();
        }
        InterstitialAd.destroy(INTERSTITIAL_AD_UNIT_ID);
        RewardedVideoAd.destroy(REWARD_AD_UNIT_ID);
    }
}
