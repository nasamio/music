package com.mio.music.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.viewpager.widget.ViewPager;

import org.jetbrains.annotations.Nullable;

import lombok.NonNull;

public class CustomViewPager extends ViewPager {
    private float startX;

    public CustomViewPager(@NonNull Context context) {
        super(context);
    }

    public CustomViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startX = ev.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                float deltaX = Math.abs(ev.getX() - startX);
                // 如果横向位移大于某个阈值，才拦截事件
                if (deltaX > 50) {
                    return true; // 拦截事件，不让子 View 处理
                }
                break;
        }
        return super.onInterceptTouchEvent(ev);
    }
}
