package com.mio.music.custom;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class MiniProgress extends View {
    public MiniProgress(Context context, AttributeSet attrs) {
        super(context, attrs);

        paint = new Paint();
    }

    private int leftColor = Color.parseColor("#eb1c27"),
            rightColor = Color.parseColor("#2e2b2f");
    private Paint paint;
    private float progress;

    public void setProgress(float progress) {
        if (this.progress != progress) {
            this.progress = progress;
            invalidate();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(leftColor);
        canvas.drawRect(0, 0, getWidth() * progress, getHeight(), paint);
        paint.setColor(rightColor);
        canvas.drawRect(getWidth() * progress, 0, getHeight(), getWidth(), paint);
    }
}
