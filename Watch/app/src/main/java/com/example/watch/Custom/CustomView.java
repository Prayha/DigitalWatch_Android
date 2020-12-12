package com.example.watch.Custom;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

@SuppressLint("AppCompatCustomView")
public class CustomView extends ImageView {
    private static final String TAG = "CusotmView";

    private Context context;

    private Rect rectangle = new Rect();
    int[] colorRGB = new int[3];


    public CustomView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        this.context = context;

        colorRGB[0] = 255;
        colorRGB[1] = 0;
        colorRGB[2] = 0;
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);

        rectangle.left = 0;
        rectangle.right = getWidth();
        rectangle.top = 0;
        rectangle.bottom = getHeight();

        int num = 21; // Color 보여줄 갯수

        int getNumX = getWidth() / num + 1; // 한칸의 가로길이

        Paint backgroundColor = new Paint();

        for (int i = 0 ; i < num ; i++) {
            rectangle.left = i * getNumX; // 왼쪽 사각형 값 (x1)
            rectangle.right = (i * getNumX) + getNumX; // 오른쪽 사각형 값 (x2)
            int red, green, blue;

            if (i < 11) { // 10개 까지는 흰색으로부터 지정한 Color 전까지
                red = ColorCalculation(255, colorRGB[0]);
                green = ColorCalculation(255, colorRGB[1]);
                blue = ColorCalculation(255, colorRGB[2]);

                // 흰색 (255) 로부터 지정한 색 (ex) 30) 을 뺀 후 10으로 나눈값을 하나식 그린다.
                backgroundColor.setColor(Color.rgb(255 - (red * i), 255 - (green * i), 255 - (blue * i)));
            } else if (i == 11) { // 가운데 Color 는 지정한 Color
                backgroundColor.setColor(Color.rgb(colorRGB[0], colorRGB[1], colorRGB[2]));
            } else { // 남은 10개는 지정한 Color 부터 검정색 까지
                red = ColorCalculation(colorRGB[0], 0);
                green = ColorCalculation(colorRGB[1], 0);
                blue = ColorCalculation(colorRGB[2], 0);

                backgroundColor.setColor(Color.rgb(colorRGB[0] - (red * i), colorRGB[1] - (green * i), colorRGB[2] - (blue * i)));
            }

            canvas.drawRect(rectangle, backgroundColor);
        }


    }

    private float getDensity() {
        return context.getResources().getDisplayMetrics().density;
    }

    private float getScaledDensity() {
        return context.getResources().getDisplayMetrics().scaledDensity;
    }

    private int ColorCalculation (int num1, int num2) {
        return (num1 - num2) / 10;
    }

    private void setColor() {
        int[][] colorRGB= new int[12][3];

        // 빨강색
        colorRGB[0][0] = 255;
        colorRGB[0][1] = 0;
        colorRGB[0][2] = 0;

        // 다홍색
        colorRGB[1][0] = 255;
        colorRGB[1][1] = 94;
        colorRGB[1][2] = 0;

        // 주황색
        colorRGB[2][0] = 255;
        colorRGB[2][1] = 187;
        colorRGB[2][2] = 0;

        // 노랑색
        colorRGB[3][0] = 255;
        colorRGB[3][1] = 228;
        colorRGB[3][2] = 0;

        // 연두색
        colorRGB[4][0] = 171;
        colorRGB[4][1] = 242;
        colorRGB[4][2] = 0;

        // 초록색
        colorRGB[5][0] = 29;
        colorRGB[5][1] = 219;
        colorRGB[5][2] = 22;

        // 하늘색
        colorRGB[6][0] = 0;
        colorRGB[6][1] = 216;
        colorRGB[6][2] = 255;

        // 파랑색
        colorRGB[7][0] = 0;
        colorRGB[7][1] = 84;
        colorRGB[7][2] = 255;

        // 남색
        colorRGB[8][0] = 1;
        colorRGB[8][1] = 0;
        colorRGB[8][2] = 255;

        // 보라색
        colorRGB[9][0] = 95;
        colorRGB[9][1] = 0;
        colorRGB[9][2] = 255;

        // 이상한 핑크색
        colorRGB[10][0] = 255;
        colorRGB[10][1] = 0;
        colorRGB[10][2] = 221;

        // 빨강핑크색
        colorRGB[11][0] = 255;
        colorRGB[11][1] = 0;
        colorRGB[11][2] = 127;

    }
}
