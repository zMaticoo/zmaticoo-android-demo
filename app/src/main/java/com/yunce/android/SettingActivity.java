package com.yunce.android;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

import com.maticoo.sdk.core.MaticooAds;

public class SettingActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        SwitchCompat switchGdpr = findViewById(R.id.switchGdpr);

        switchGdpr.setChecked(MaticooAds.isGDPRConsent());
        switchGdpr.setOnCheckedChangeListener((compoundButton, isChecked) -> {
//            MaticooAds.setConsentStatus(this, isChecked ? 1 : 0);
        });

        SwitchCompat switchDoNotStatus = findViewById(R.id.switchDoNotStatus);
        switchDoNotStatus.setChecked(MaticooAds.isDoNotTrackStatus());
//        switchDoNotStatus.setOnCheckedChangeListener((compoundButton, isChecked) -> MaticooAds.setDoNotTrackStatus(this, isChecked ? CommonConstants.DNT_ON_VALUE : CommonConstants.DNT_OFF_VALUE));

        SwitchCompat switchCoppaStatus = findViewById(R.id.switchCoppaStatus);
//        switchCoppaStatus.setChecked(SDKAuthorityController.getInstance().getCoppa() == CommonConstants.COPPA_ON_VALUE);
//        switchCoppaStatus.setOnCheckedChangeListener((compoundButton, isChecked) -> {
////            MaticooAds.setCoppa(this, isChecked ? CommonConstants.COPPA_ON_VALUE : CommonConstants.COPPA_OFF_VALUE);
//        });
    }
}
