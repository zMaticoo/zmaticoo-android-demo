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
        switchGdpr.setOnCheckedChangeListener((compoundButton, isChecked) -> MaticooAds.setGDPRConsent(isChecked));
    }
}
