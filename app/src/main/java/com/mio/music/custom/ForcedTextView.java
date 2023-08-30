package com.mio.music.custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * describe:自定义TextView，实现轮播 免焦点
 */

@SuppressLint("AppCompatCustomView")
public class ForcedTextView extends TextView {


    public ForcedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean isFocused() {
        return true;
    }
}
