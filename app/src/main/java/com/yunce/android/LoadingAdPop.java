package com.yunce.android;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.PopupWindow;

public class LoadingAdPop extends PopupWindow {

    private RotateAnimation rotateAnimation;
    private View ivLoading;

    public LoadingAdPop(Context context) {
        super(context);
        View popupView = LayoutInflater.from(context).inflate(R.layout.popup_loading, null);
        setContentView(popupView);
        setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setOutsideTouchable(false);

        rotateAnimation = new RotateAnimation(0f,
                360f,
                Animation.RELATIVE_TO_SELF,
                0.5f,
                Animation.RELATIVE_TO_SELF,
                0.5f);
        rotateAnimation.setDuration(1000);
        rotateAnimation.setRepeatCount(Animation.INFINITE);
        rotateAnimation.setInterpolator(new LinearInterpolator());

        ivLoading = popupView.findViewById(R.id.ivLoading);
        ivLoading.startAnimation(rotateAnimation);
        setOnDismissListener(() -> {
            if (rotateAnimation != null) {
                rotateAnimation.cancel();
            }
            if (ivLoading != null) {
                ivLoading.clearAnimation();
            }
        });
    }

    public void showAdPop(View view) {
        showAtLocation(view, Gravity.CENTER, 0, 0);
    }

}
