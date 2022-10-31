package com.ironsource.adapters.custom.maticoo;

import android.content.Context;

import com.ironsource.mediationsdk.LoadWhileShowSupportState;
import com.ironsource.mediationsdk.adunit.adapter.BaseAdapter;
import com.ironsource.mediationsdk.adunit.adapter.listener.NetworkInitializationListener;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.model.NetworkSettings;
import com.maticoo.sdk.InitConfiguration;
import com.maticoo.sdk.core.InitCallback;
import com.maticoo.sdk.core.MaticooAds;
import com.maticoo.sdk.utils.error.InternalError;
import com.maticoo.sdk.utils.log.AdLog;

import java.util.concurrent.atomic.AtomicBoolean;

public class MaticooCustomAdapter extends BaseAdapter {

    private static final AtomicBoolean initialized = new AtomicBoolean();

    @Override
    public void init(AdData adData, Context context, NetworkInitializationListener networkInitializationListener) {
        if (initialized.compareAndSet(false, true)) {
            String appKey = AdUtils.getAppKey(context, adData);
            AdLog.getSingleton().LogD("[IronSource]init, appKey = " + appKey);
            InitConfiguration configuration = new InitConfiguration.Builder()
                    .appKey(appKey)
                    .logEnable(true)
                    .build();
            MaticooAds.init(configuration, new InitCallback() {
                @Override
                public void onSuccess() {
                    AdLog.getSingleton().LogD("[IronSource] init Success");
                    networkInitializationListener.onInitSuccess();
                }

                @Override
                public void onError(InternalError result) {
                    AdLog.getSingleton().LogD("[IronSource] init onError, result  = " + result);
                    networkInitializationListener.onInitFailed(result.getErrorCode(), result.getErrorMessage());
                }
            });
        }
    }

    @Override
    public String getNetworkSDKVersion() {
        return MaticooAds.getSDKVersion();
    }

    @Override
    public String getAdapterVersion() {
        return MaticooAds.getSDKVersion();
    }

    @Override
    public LoadWhileShowSupportState getLoadWhileShowSupportedState(NetworkSettings networkSettings) {
        return LoadWhileShowSupportState.LOAD_WHILE_SHOW_BY_INSTANCE;
    }
}
