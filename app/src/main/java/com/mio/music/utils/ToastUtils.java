package com.mio.music.utils;

import android.content.Context;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;

public class ToastUtils {
    public static void showShortToast(@NotNull Context context, @NotNull String content) {
        showToast(context, content, Toast.LENGTH_SHORT);
    }

    public static void showLongToast(@NotNull Context context, @NotNull String content) {
        showToast(context, content, Toast.LENGTH_LONG);
    }

    public static void showToast(@NotNull Context context, @NotNull String content, int duration) {
        Toast.makeText(context, content, duration).show();
    }
}
