package com.mio.music.utils;

import android.view.View;
import android.widget.Button;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;

import com.mio.music.Constants;

import lombok.NonNull;

public class PlayStatusUtils {
    public static void bind(@NonNull LifecycleOwner owner, @NonNull View button, int playDrawable, int pauseDrawable) {
        LiveDataBus.get().with(Constants.playStatus, Boolean.class).observe(owner, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean == null) return;
                boolean isPlay = aBoolean;
                Object tag = button.getTag();
                if (tag == null) {
                    // 直接设置
                    bind(button, isPlay ? pauseDrawable : playDrawable);
                    return;
                }
                boolean tagPlay = (boolean) tag;
                if (tagPlay != isPlay) {
                    bind(button, isPlay ? pauseDrawable : playDrawable);
                    button.setTag(isPlay);
                }
            }
        });
    }

    private static void bind(View button, int resId) {
        button.setBackgroundResource(resId);
    }
}
