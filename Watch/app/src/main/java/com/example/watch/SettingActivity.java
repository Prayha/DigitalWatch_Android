package com.example.watch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity {
    private final static String TAG = "SettingActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        findViewById(R.id.subButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TextView textView = findViewById(R.id.subtextView);

                Log.e(TAG, "1 = " + textView.getTextSize());
                // TextView 는 setText 를 할때 DP로 계산해서 넣어줘야한다.
                textView.setTextSize(textView.getTextSize() / getDensity());
                Log.e(TAG, "2 = " + textView.getTextSize());
            }
        });
    }

    private float getDensity() {
        return getResources().getDisplayMetrics().density;
    }
}