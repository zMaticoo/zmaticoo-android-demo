package com.ironsource.adapters.custom.maticoo;

import android.content.Context;
import android.text.TextUtils;

import com.ironsource.mediationsdk.adunit.adapter.utility.AdData;
import com.ironsource.mediationsdk.adunit.adapter.utility.AdapterErrorType;
import com.maticoo.sdk.ad.utils.error.Error;
import com.maticoo.sdk.ad.utils.error.ErrorCode;
import com.maticoo.sdk.utils.SdkUtil;

public class AdUtils {

    public static AdapterErrorType getErrorType(Error error) {
        if (error.getCode() == ErrorCode.CODE_LOAD_NO_FILL) {
            return AdapterErrorType.ADAPTER_ERROR_TYPE_NO_FILL;
        } else {
            return AdapterErrorType.ADAPTER_ERROR_TYPE_INTERNAL;
        }
    }

    public static String getAppKey(Context context, AdData adData) {
        String appKey = adData.getString(Constant.APP_KEY);
        if (TextUtils.isEmpty(appKey)) {
            appKey = SdkUtil.retrieveAppKeyFromMetadata(context);
        }
        return appKey;
    }
}
