package com.yunce.android;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.zmaticoo.sdk.ads.nativead.AdChoicesView;
import com.zmaticoo.sdk.ads.nativead.MediaView;
import com.zmaticoo.sdk.ads.nativead.NativeAd;
import com.zmaticoo.sdk.ads.nativead.NativeAdListener;
import com.zmaticoo.sdk.ads.nativead.NativeAdLoader;
import com.zmaticoo.sdk.ads.nativead.NativeAdOptions;
import com.zmaticoo.sdk.ads.nativead.VideoController;
import com.zmaticoo.sdk.ads.nativead.VideoLifecycleCallbacks;
import com.zmaticoo.sdk.ads.nativead.VideoOptions;
import com.zmaticoo.sdk.base.common.MaticooIds;
import com.zmaticoo.sdk.flow.model.ComponentError;

import java.util.ArrayList;
import java.util.List;

public class NativeListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_AD = 1;

    private static final int AD_EVERY_N_NORMAL_ITEMS = 9;

    private static final String NATIVE_AD_UNIT_ID = "1004442676";

    private final Handler mainHandler;
    private final ArrayList<NativeAdViewHolder> adHolders = new ArrayList<>();

    // Only counts the number of "normal" items, not including native ad items.
    private int normalItemCount = 54;

    public NativeListAdapter(Handler mainHandler) {
        this.mainHandler = mainHandler;
    }

    private int getGroups() {
        return normalItemCount / AD_EVERY_N_NORMAL_ITEMS;
    }

    @Override
    public int getItemCount() {
        int groups = getGroups();
        int remaining = normalItemCount % AD_EVERY_N_NORMAL_ITEMS;
        return groups * (AD_EVERY_N_NORMAL_ITEMS + 1) + remaining;
    }

    public void setNormalItemCount(int newCount) {
        if (newCount < 0) {
            newCount = 0;
        }
        normalItemCount = newCount;
    }

    @Override
    public int getItemViewType(int position) {
        int adEveryCycle = AD_EVERY_N_NORMAL_ITEMS + 1; // N normal items + 1 ad item
        int groups = getGroups();
        int groupsItems = groups * adEveryCycle;
        if (position >= groupsItems) {
            return VIEW_TYPE_NORMAL;
        }
        int within = position % adEveryCycle;
        return within == AD_EVERY_N_NORMAL_ITEMS ? VIEW_TYPE_AD : VIEW_TYPE_NORMAL;
    }

    private int getNormalIndexForPosition(int position) {
        int adEveryCycle = AD_EVERY_N_NORMAL_ITEMS + 1;
        int groups = getGroups();
        int groupsItems = groups * adEveryCycle;
        if (position < groupsItems) {
            int cycleIndex = position / adEveryCycle;
            int within = position % adEveryCycle;
            // within is guaranteed to be 0..N-1 because ad positions are filtered out.
            return cycleIndex * AD_EVERY_N_NORMAL_ITEMS + within;
        }
        // Tail: only normal items, no ad.
        return groups * AD_EVERY_N_NORMAL_ITEMS + (position - groupsItems);
    }

    private int getAdSlotIndexForPosition(int position) {
        // For each cycle of (N normal + 1 ad), the ad is at the last position, so slot index = cycle index.
        int adEveryCycle = AD_EVERY_N_NORMAL_ITEMS + 1;
        return position / adEveryCycle;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if (viewType == VIEW_TYPE_AD) {
            View itemView = inflater.inflate(R.layout.item_native_list_ad, parent, false);
            NativeAdViewHolder holder = new NativeAdViewHolder(itemView, mainHandler);
            adHolders.add(holder);
            return holder;
        }
        View itemView = inflater.inflate(R.layout.item_native_list_normal, parent, false);
        return new NormalViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof NormalViewHolder) {
            int normalIndex = getNormalIndexForPosition(position);
            ((NormalViewHolder) holder).tvNormalIndex.setText("item-" + (normalIndex + 1));
            return;
        }
        if (holder instanceof NativeAdViewHolder) {
            int adSlotIndex = getAdSlotIndexForPosition(position);
            ((NativeAdViewHolder) holder).loadNativeAd(adSlotIndex);
        }
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        if (holder instanceof NativeAdViewHolder) {
            ((NativeAdViewHolder) holder).cleanup();
        }
        super.onViewRecycled(holder);
    }

    public void release() {
        for (NativeAdViewHolder holder : adHolders) {
            holder.cleanup();
        }
        adHolders.clear();
    }

    private static class NormalViewHolder extends RecyclerView.ViewHolder {
        private final TextView tvNormalIndex;

        NormalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNormalIndex = itemView.findViewById(R.id.tv_normal_index);
        }
    }

    private static class NativeAdViewHolder extends RecyclerView.ViewHolder {
        private final Handler handler;
        private final FrameLayout nativeAdContainer;
        private final TextView tvLoading;

        private NativeAd nativeAd;
        private NativeAdLoader nativeLoader;
        private int requestToken = 0;

        NativeAdViewHolder(@NonNull View itemView, @NonNull Handler handler) {
            super(itemView);
            this.handler = handler;
            this.nativeAdContainer = itemView.findViewById(R.id.native_ad_container);
            this.tvLoading = itemView.findViewById(R.id.tv_ad_loading);
        }

        void cleanup() {
            // Increase token so late callbacks won't bind into a recycled holder.
            requestToken++;
            if (nativeAd != null) {
                nativeAd.destroy();
                nativeAd = null;
            }
            if (nativeLoader != null) {
                nativeLoader.destroy();
                nativeLoader = null;
            }
            nativeAdContainer.removeAllViews();
            nativeAdContainer.setVisibility(View.GONE);
            tvLoading.setVisibility(View.VISIBLE);
        }

        void loadNativeAd(int adSlotIndex) {
            cleanup();

            nativeAdContainer.removeAllViews();
            nativeAdContainer.setVisibility(View.GONE);
            tvLoading.setText("Loading native ad...");
            tvLoading.setVisibility(View.VISIBLE);

            final int token = ++requestToken;
            Context context = itemView.getContext();

            NativeAdOptions adOptions = new NativeAdOptions.Builder()
                    .setVideoOptions(new VideoOptions.Builder()
                            .setStartMuted(false)
                            .build())
                    .build();

            nativeLoader = new NativeAdLoader.Builder(NATIVE_AD_UNIT_ID)
                    .withNativeAdOptions(adOptions)
                    .withAdListener(new NativeAdListener() {
                        @Override
                        public void onAdLoaded(MaticooIds adId, @NonNull NativeAd ad) {
                            super.onAdLoaded(adId, ad);
                            final NativeAd loadedAd = ad;
                            handler.post(() -> {
                                if (token != requestToken) {
                                    // Holder has been recycled / re-requested.
                                    loadedAd.destroy();
                                    return;
                                }
                                nativeAd = loadedAd;
                                tvLoading.setVisibility(View.GONE);
                                bindNativeAd(context, loadedAd);
                            });
                        }

                        @Override
                        public void onAdFailedToLoad(MaticooIds adId, @NonNull ComponentError error) {
                            super.onAdFailedToLoad(adId, error);
                            handler.post(() -> {
                                if (token != requestToken) {
                                    return;
                                }
                                tvLoading.setText("Native ad load failed"+error.toString());
                                nativeLoader = null;
                            });
                        }
                    })
                    .build();

            nativeLoader.loadAd();
        }

        private void bindNativeAd(Context context, NativeAd ad) {
            if (ad == null || nativeAdContainer == null) {
                return;
            }

            // Video lifecycle callbacks (same as MainActivity).
            if (ad.getMediaContent() != null && ad.getMediaContent().hasVideoContent()) {
                VideoController controller = ad.getMediaContent().getVideoController();
                controller.setVideoLifecycleCallbacks(new VideoLifecycleCallbacks() {
                    @Override
                    public void onVideoStart() {
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

            // Inflate with null root to avoid issues with <merge/> or LayoutParams compatibility.
            ViewGroup nativeAdView = (ViewGroup) LayoutInflater.from(context)
                    .inflate(R.layout.layout_maticoo_native, null, false);

            nativeAdContainer.removeAllViews();
            nativeAdContainer.setVisibility(View.VISIBLE);
            nativeAdContainer.addView(
                    nativeAdView,
                    new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
            );

            // Clickable views
            List<View> clickableViews = new ArrayList<>();

            ImageView ivIcon = nativeAdView.findViewById(R.id.iv_ad_icon);
            if (ad.getIcon() != null) {
                ivIcon.setImageDrawable(ad.getIcon().getDrawable());
            }
            clickableViews.add(ivIcon);

            TextView tvHeadline = nativeAdView.findViewById(R.id.tv_ad_headline);
            if (!TextUtils.isEmpty(ad.getHeadline())) {
                tvHeadline.setText(ad.getHeadline());
            }
            clickableViews.add(tvHeadline);

            TextView tvBody = nativeAdView.findViewById(R.id.tv_ad_body);
            if (!TextUtils.isEmpty(ad.getBody())) {
                tvBody.setText(ad.getBody());
            }
            clickableViews.add(tvBody);

            TextView tvSponsored = nativeAdView.findViewById(R.id.tv_ad_sponsored);
            if (!TextUtils.isEmpty(ad.getAdvertiser())) {
                tvSponsored.setText(ad.getAdvertiser());
            }

            Button btnCallToAction = nativeAdView.findViewById(R.id.btn_ad_action);
            if (!TextUtils.isEmpty(ad.getCallToAction())) {
                btnCallToAction.setText(ad.getCallToAction());
            }
            clickableViews.add(btnCallToAction);

            clickableViews.add(tvSponsored);

            MediaView mediaView = nativeAdView.findViewById(R.id.view_media);
            AdChoicesView adChoicesView = nativeAdView.findViewById(R.id.ad_choices_view);
            adChoicesView.setNativeAd(ad);

            // Bind views
            ad.bindViews(nativeAdView, mediaView, clickableViews);
        }
    }
}

