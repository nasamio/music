package com.mio.music.utils;

import android.content.Context;

public class DensityUtils {

    /**
     * 将 dp 转换为 px
     *
     * @param context 上下文
     * @param dpValue dp 值
     * @return 对应的 px 值
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 将 px 转换为 dp
     *
     * @param context 上下文
     * @param pxValue px 值
     * @return 对应的 dp 值
     */
    public static int px2dp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
