package com.yunce.android;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
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
import com.zmaticoo.sdk.ads.nativead.AdChoicesView;
import com.zmaticoo.sdk.ads.nativead.MediaView;
import com.zmaticoo.sdk.ads.nativead.NativeAd;
import com.zmaticoo.sdk.ads.nativead.NativeAdListener;
import com.zmaticoo.sdk.ads.nativead.NativeAdLoader;
import com.zmaticoo.sdk.ads.nativead.NativeAdOptions;
import com.zmaticoo.sdk.ads.nativead.VideoController;
import com.zmaticoo.sdk.ads.nativead.VideoLifecycleCallbacks;
import com.zmaticoo.sdk.ads.nativead.VideoOptions;
import com.zmaticoo.sdk.ads.rewardads.MaticooRewardInfo;
import com.zmaticoo.sdk.base.common.MaticooIds;
import com.zmaticoo.sdk.base.common.logging.ZmaticooLog;
import com.zmaticoo.sdk.flow.model.ComponentError;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    // If you want to test your own zMaticoo SDK key, change the value here and update the package name in the build.gradle
    private static final String TAG = "MaticooDev";
    private static final String YOUR_SDK_KEY = "a612f6f97402f33d844f7926016c69d14b3a5ffa9afb6f1e8979b61bdcc5f7b2";
    // If you want to test your own banner, change the value here
    private static final String BANNER_AD_UNIT_ID = "1004207889";
    private static final String INTERSTITIAL_AD_UNIT_ID = "1004273195";
    private static final String REWARD_AD_UNIT_ID = "1004273329";
    private static final String NATIVE_AD_UNIT_ID = "1004442727";

    private LoadingAdPop loadingPopup;
    private BannerAd bannerAd;
    private FrameLayout bannerContainer;
    private TextView bannerStatus;

    private Button btnInterstitialShow;
    private TextView interstitialStatus;

    private Button btnRewardShow;
    private TextView rewardStatus;

    // Native
    private NativeAd nativeAd;
    private NativeAdLoader nativeLoader;
    private FrameLayout nativeContainer;
    private TextView nativeStatus;

    private Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
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

        nativeContainer = findViewById(R.id.native_container);
        nativeStatus = findViewById(R.id.nativeStatus);
        findViewById(R.id.loadNative).setOnClickListener(this);
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
        } else if (view.getId() == R.id.loadNative) {
            loadNative();
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

    private void loadNative() {
        // 清理旧广告
        if (nativeContainer != null) {
            nativeContainer.removeAllViews();
            nativeContainer.setVisibility(View.GONE);
        }
        if (nativeAd != null) {
            nativeAd.destroy();
            nativeAd = null;
        }

        nativeStatus.setText("loading...");

        NativeAdOptions adOptions = new NativeAdOptions.Builder()
                .setVideoOptions(new VideoOptions.Builder()
                        .setStartMuted(false)
                        .build()
                )
                .build();

        if (nativeLoader != null) {
            nativeLoader.destroy();
        }

        nativeLoader = new NativeAdLoader.Builder(NATIVE_AD_UNIT_ID)
                .withNativeAdOptions(adOptions)
                .withAdListener(new NativeAdListener() {
                    @Override
                    public void onAdLoaded(MaticooIds adId, @NonNull NativeAd ad) {
                        super.onAdLoaded(adId, ad);
                        handler.post(() -> {
                            nativeStatus.setText("load success");
//                            ZmaticooLog.d(TAG, "Native ad loaded: " + adId);
                            Toast.makeText(MainActivity.this, "Native load success", Toast.LENGTH_SHORT).show();
                            nativeAd = ad;
                            renderNativeAd();
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(MaticooIds adId, @NonNull ComponentError error) {
                        super.onAdFailedToLoad(adId, error);
                        handler.post(() -> {
                            nativeStatus.setText("load failed " + error.toString());
//                            ZmaticooLog.d(TAG, "Native ad load failed: " + error);
                            Toast.makeText(MainActivity.this, "Native load failed", Toast.LENGTH_SHORT).show();
                        });
                    }

                    @Override
                    public void onAdClicked(MaticooIds adId) {
                        super.onAdClicked(adId);
//                        ZmaticooLog.d(TAG, "Native ad clicked: " + adId);
                        Toast.makeText(MainActivity.this, "Native clicked", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onAdDisplayed(MaticooIds adId) {
                        super.onAdDisplayed(adId);
//                        ZmaticooLog.d(TAG, "Native ad displayed: " + adId);
                    }

                    @Override
                    public void onAdDisplayFailed(MaticooIds adId, ComponentError error) {
                        super.onAdDisplayFailed(adId, error);
                        handler.post(() -> {
                            nativeStatus.setText("show failed " + error.toString());
//                            ZmaticooLog.d(TAG, "Native ad display failed: " + error);
                            Toast.makeText(MainActivity.this, "Native show failed", Toast.LENGTH_SHORT).show();
                        });
                    }
                }).build();

        nativeLoader.loadAd();
    }

    private void renderNativeAd() {
        if (nativeAd == null || nativeContainer == null) {
            return;
        }

        // 视频回调
        if (nativeAd.getMediaContent() != null && nativeAd.getMediaContent().hasVideoContent()) {
            VideoController controller = nativeAd.getMediaContent().getVideoController();
            controller.setVideoLifecycleCallbacks(new VideoLifecycleCallbacks() {
                @Override
                public void onVideoStart() {
                    // 视频开始
                }

                @Override
                public void onVideoPlay() {
                }

                @Override
                public void onVideoPause() {
                }

                @Override
                public void onVideoEnd() {
                }

                @Override
                public void onVideoMute(boolean isMuted) {
                }
            });
        }

        ViewGroup nativeAdView = (ViewGroup) LayoutInflater.from(this)
                .inflate(R.layout.layout_maticoo_native, nativeContainer, false);

        nativeContainer.removeAllViews();
        nativeContainer.setVisibility(View.VISIBLE);
        nativeContainer.addView(nativeAdView,
                new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        // 可点击视图
        java.util.List<View> clickableViews = new java.util.ArrayList<>();

        ImageView ivIcon = nativeAdView.findViewById(R.id.iv_ad_icon);
        if (nativeAd.getIcon() != null) {
            ivIcon.setImageDrawable(nativeAd.getIcon().getDrawable());
        }
        clickableViews.add(ivIcon);

        TextView tvHeadline = nativeAdView.findViewById(R.id.tv_ad_headline);
        if (!TextUtils.isEmpty(nativeAd.getHeadline())) {
            tvHeadline.setText(nativeAd.getHeadline());
        }
        clickableViews.add(tvHeadline);

        TextView tvBody = nativeAdView.findViewById(R.id.tv_ad_body);
        if (!TextUtils.isEmpty(nativeAd.getBody())) {
            tvBody.setText(nativeAd.getBody());
        }
        clickableViews.add(tvBody);

        TextView tvAdvertiser = nativeAdView.findViewById(R.id.tv_ad_advertiser);
        if (!TextUtils.isEmpty(nativeAd.getAdvertiser())) {
            tvAdvertiser.setText(nativeAd.getAdvertiser());
        }

        Button btnCallToAction = nativeAdView.findViewById(R.id.btn_ad_action);
        if (!TextUtils.isEmpty(nativeAd.getCallToAction())) {
            btnCallToAction.setText(nativeAd.getCallToAction());
        }
        clickableViews.add(btnCallToAction);

        MediaView mediaView = nativeAdView.findViewById(R.id.view_media);
        AdChoicesView adChoicesView = nativeAdView.findViewById(R.id.ad_choices_view);
        adChoicesView.setNativeAd(nativeAd);

        // 绑定视图
        nativeAd.bindViews(nativeAdView, mediaView, clickableViews);
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
        if (nativeContainer != null) {
            nativeContainer.removeAllViews();
        }
        if (nativeAd != null) {
            nativeAd.destroy();
            nativeAd = null;
        }
        if (nativeLoader != null) {
            nativeLoader.destroy();
            nativeLoader = null;
        }
        InterstitialAd.destroy(INTERSTITIAL_AD_UNIT_ID);
        RewardedVideoAd.destroy(REWARD_AD_UNIT_ID);
    }
}
