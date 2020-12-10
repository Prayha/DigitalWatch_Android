package com.example.watch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextView day = findViewById(R.id.day_TextView);
        TextView day2 = findViewById(R.id.day2_TextView);
        TextView time = findViewById(R.id.time_TextView);
        TextView ampm = findViewById(R.id.ampm_TextView);
        TextView second = findViewById(R.id.second_TextView);

        TextView[] textViews = new TextView[5];
        textViews[0] = day;
        textViews[1] = day2;
        textViews[2] = time;
        textViews[3] = ampm;
        textViews[4] = second;

        TimerHandler handler = new TimerHandler(textViews);
        TimerRunner runner = new TimerRunner(handler);
        Thread thread = new Thread(runner);
        thread.start();
    }


}

// TextView 를 배열로 받아서 날짜 출력
class TimerHandler extends Handler {
    private static final String TAG = "TimerHandler";

    TextView[] textViews;

    public TimerHandler(TextView[] textViews) {
        this.textViews = textViews;

    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        Calendar cal = Calendar.getInstance() ;

        int year = cal.get(Calendar.YEAR);
        int month = cal.get(Calendar.MONTH) + 1;
        int date = cal.get(Calendar.DATE);
        int week = cal.get(Calendar.DAY_OF_WEEK);
        int hour = cal.get(Calendar.HOUR);
        int hour24 = cal.get(Calendar.HOUR_OF_DAY);
        int second = cal.get(Calendar.SECOND);

        String str = year + "년 " + month + "월 " + date + "일";
        textViews[0].setText(str);
        str = WeekDistinction(week);
        textViews[1].setText(str);
        SimpleDateFormat sdf = new SimpleDateFormat("mm");
        str = hour + " : " + sdf.format(cal.getTime());
        textViews[2].setText(str);
        str = AmPmDistinction(hour24);
        textViews[3].setText(str);
        sdf = new SimpleDateFormat("ss");
        str = ": " + sdf.format(cal.getTime());
        textViews[4].setText(str);
    }

    private String WeekDistinction (int num) {
        String week = "";

        switch (num) {
            case 1 :
                week = "일요일";
                break;
            case 2 :
                week = "월요일";
                break;
            case 3 :
                week = "화요일";
                break;
            case 4 :
                week = "수요일";
                break;
            case 5 :
                week = "목요일";
                break;
            case 6 :
                week = "금요일";
                break;
            default:
                week = "토요일";
                break;
        }

        return week;
    }

    private String AmPmDistinction(int num) {
        if (num - 12 > 0) {
            return "오후";
        } else {
            return "오전";
        }
    }
}

// 1초마다 Handler 에 Message 를 보내어 Handler 실행
class TimerRunner implements Runnable {
    private static final String TAG = "TimerRunner";

    TimerHandler handler;

    public TimerRunner(TimerHandler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        while (true) {
            try {
                Thread.sleep(1000);
            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.sendEmptyMessage(0);
        }
    }
}