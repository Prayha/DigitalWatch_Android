package com.example.watch.Custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class CustomView extends ImageView {
    private static final String TAG = "CusotmView";

    private Context context;

    private Rect rectangle = new Rect();


    public CustomView(Context context) {
        super(context);
        this.context = context;
    }

    public CustomView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        Log.e(TAG, "width = " + getWidth());
        Log.e(TAG, "height = " + getHeight());
        Log.e(TAG, "Destiny = " + context.getResources().getDisplayMetrics().density);

        float dp = getWidth() / getDensity();
        Log.e(TAG, "dp = " + dp);

        rectangle.left = 0;
        rectangle.right = getWidth();
        rectangle.top = 0;
        rectangle.bottom = getHeight();

        int num = 50;

        int getX = getWidth();
        int getNumX = getX / num + 1;

        Paint backgroundColor = new Paint();

        for (int i = 0 ; i < num ; i++) {
            rectangle.left = i * getNumX;
            rectangle.right = (i * getNumX) + getNumX;

            backgroundColor.setColor(Color.rgb(255, i * 5 , i * 5));
            canvas.drawRect(rectangle, backgroundColor);
        }
    }

    private float getDensity() {
        return context.getResources().getDisplayMetrics().density;
    }

    private float getScaledDensity() {
        return context.getResources().getDisplayMetrics().scaledDensity;
    }

}
