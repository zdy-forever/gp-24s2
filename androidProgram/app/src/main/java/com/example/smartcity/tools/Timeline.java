package com.example.smartcity.tools;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * @author : Lanping Hu
 * UID: u7904927
 */

public class Timeline extends View {
    private List<TimeSlot> timeSlots = new ArrayList<>();
    private final Paint paint = new Paint();
    private final int backgroundColor = Color.parseColor("#E6F3FF"); // 浅蓝色背景
     
    public Timeline(Context context) {
        super(context);
        init();
    }

    public Timeline(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 绘制背景
        canvas.drawColor(backgroundColor);

        for (TimeSlot slot : timeSlots) {
            paint.setColor(slot.color);
            float left = slot.startTime * getWidth();
            float right = slot.endTime * getWidth();
            canvas.drawRect(left, 0, right, getHeight(), paint);
        }
    }

    public void setTimeSlots(List<TimeSlot> slots) {
        this.timeSlots = slots;
        invalidate();
    }

    public static class TimeSlot {
        public float startTime; // 0.0 to 1.0
        public float endTime; // 0.0 to 1.0
        public int color;

        public TimeSlot(float startTime, float endTime, int color) {
            this.startTime = startTime;
            this.endTime = endTime;
            this.color = color;
        }
    }
}
