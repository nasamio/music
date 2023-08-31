package com.mio.music.custom;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.viewpager.widget.ViewPager;

import java.util.Map;

import lombok.NonNull;

public class CusVp extends ViewPager {
    private static final String TAG = "CusVp";

    private @NonNull View verticalHandleView;
    private static final float MIN_DELTA = 50;
    private float startX;
    private float startY;

    private int direction = -1; // -1 无效 0 自己处理 横向 1 纵向
    private MotionEvent actionDown;


    public CusVp(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

/*    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.d("TAG", "onTouchEvent: " + direction + ",action : " + event.getAction());

        int copyDir = direction;

        if (event.getAction() == MotionEvent.ACTION_UP) {
            direction = -1;
            startX = 0;
            startY = 0;
        }

        switch (copyDir) {
            case 0:
                return super.onTouchEvent(event); // ViewPager处理横向滑动
            case 1:
                // todo 补一个action down
                return verticalHandleView.onTouchEvent(event);
            case -1:
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = event.getX();
                        startY = event.getY();
                    case MotionEvent.ACTION_MOVE:
                        float deltaX = event.getX() - startX;
                        float deltaY = event.getY() - startY;

                        Log.d(TAG, "onTouchEvent: dx : " + deltaX + ",dy : " + deltaY);
                        if (Math.abs(deltaX) > Math.abs(deltaY) *//*&& Math.abs(deltaX) >= MIN_DELTA*//*) {
                            direction = 0;
                        } else if (Math.abs(deltaY) > Math.abs(deltaX) *//*&& Math.abs(deltaY) >= MIN_DELTA*//*) {
                            direction = 1;
                        }
                        break;
                }
        }
        return true;
    }*/

    public void setVerticalHandle(@NonNull View view) {
        this.verticalHandleView = view;

        verticalHandleView.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                    case MotionEvent.ACTION_CANCEL:
                        direction = -1;
                        startX = 0;
                        startY = 0;
                        break;
                }
                return false;
            }
        });
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {


        return super.dispatchTouchEvent(ev);
    }
}
