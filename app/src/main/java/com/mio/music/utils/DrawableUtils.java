package com.mio.music.utils;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import java.util.Random;

import static android.graphics.drawable.GradientDrawable.Orientation.*;

public class DrawableUtils {
    public static Drawable getRandomDrawable(int type) {
        return getRandomGradientDrawable(type);
    }

    private static Drawable getRandomColorDrawable() {
        Random random = new Random();
        int i = random.nextInt(2);
        switch (i) {
            case 1:
                return new ColorDrawable(Color.parseColor("#FF5733"));
            case 2:
                return new ColorDrawable(Color.parseColor("#00DBDE"));
            default:
                return new ColorDrawable(Color.parseColor("#85FFBD"));
        }
    }

    private static Drawable getRandomGradientDrawable(int type) {
        return new GradientDrawable(getRandomOri(),
                getRandomGradientColorArray(type));
    }

    private static GradientDrawable.Orientation getRandomOri() {
        Random random = new Random();
        int i = random.nextInt(8);
        switch (i) {
            case 0:
                return TOP_BOTTOM;
            case 1:
                return TR_BL;
            case 2:
            default:
                return RIGHT_LEFT;
            case 3:
                return BR_TL;
            case 4:
                return BOTTOM_TOP;
            case 5:
                return BL_TR;
            case 6:
                return LEFT_RIGHT;
            case 7:
                return TL_BR;
        }
    }

    private static int[] getRandomGradientColorArray(int type) {
        switch (type) {
            case 0:
                return new int[]{
                        Color.parseColor("#0093E9"),
                        Color.parseColor("#80D0C7"),
                };
            case 1:
                return new int[]{
                        Color.parseColor("#F6D242"),
                        Color.parseColor("#FF52E5"),
                };
            case 2:
                return new int[]{
                        Color.parseColor("#FAB2FF"),
                        Color.parseColor("#1904E5"),
                };
            case 3:
                return new int[]{
                        Color.parseColor("#FF96F9"),
                        Color.parseColor("#C32BAC"),
                };
            case 4:
                return new int[]{
                        Color.parseColor("#FD6E6A"),
                        Color.parseColor("#FFC600"),
                };
            case 5:
                return new int[]{
                        Color.parseColor("#43CBFF"),
                        Color.parseColor("#9708CC"),
                };
            case 6:
                return new int[]{
                        Color.parseColor("#ABDCFF"),
                        Color.parseColor("#0396FF"),
                };
            case 7:
                return new int[]{
                        Color.parseColor("#E2B0FF"),
                        Color.parseColor("#9F44D3"),
                };
            case 8:
                return new int[]{
                        Color.parseColor("#F6CEEC"),
                        Color.parseColor("#D939CD"),
                };
            default:
                return new int[]{
                        Color.parseColor("#8EC5FC"),
                        Color.parseColor("#E0C3FC"),
                };
        }
    }
}
