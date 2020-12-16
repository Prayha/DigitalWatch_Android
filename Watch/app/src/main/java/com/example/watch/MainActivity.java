package com.example.watch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.watch.Util.Status;

import java.text.SimpleDateFormat;
import java.util.Calendar;

/* -------------------------------------------------------------------------------------------------
 *
 * 앱이 실행이 되면 SQLite에 저장되어 있는 Setting 값을 가져온후 적용 시키고 (없으면 생성)
 * Runnable , Handler 를 이용하여 오늘의 시간 을 구한뒤 TextView 에 각각 적용 시킨다.
 *
 * -------------------------------------------------------------------------------------------------
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private TextView[] textViews;
    private int[][] colorRGB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 화면을 켜진상태로 유지 https://developer.android.com/training/scheduling/wakelock?hl=ko
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // Color Setting
        colorRGB = Status.setColor();

        // TextView
        TextView day = findViewById(R.id.day_TextView);
        TextView day2 = findViewById(R.id.day2_TextView);
        TextView time = findViewById(R.id.time_TextView);
        TextView ampm = findViewById(R.id.ampm_TextView);
        TextView second = findViewById(R.id.second_TextView);

        // TextView 배열
        textViews = new TextView[5];
        textViews[0] = day;
        textViews[1] = day2;
        textViews[2] = time;
        textViews[3] = ampm;
        textViews[4] = second;

        // Setting Button 클릭시 Activity 이동
        ImageButton settingButton = findViewById(R.id.setting_ImageButton);
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        // Timer 돌릴 Thread 실행
        TimerHandler handler = new TimerHandler(textViews);
        TimerRunner runner = new TimerRunner(handler);
        Thread thread = new Thread(runner);
        thread.start();
    }

    // SQLite 에 Setting 가져와서 적용시키기
    @Override
    protected void onResume() {
        super.onResume();

        // SQLite DB 데이터 가져오기
        SettingSQLiteQuery sqLiteQuery = new SettingSQLiteQuery(MainActivity.this);

        // Data 가 없으면
        if (sqLiteQuery.totalCount() == 0) {
            // 생성
            sqLiteQuery.settingInsert(12, 100);
        } else {
            // 가져오기
            SettingDTO data = sqLiteQuery.settingSelect();

            // TextView 에서 Time 부분 (ex: 12:00) 시간 부분만 살짝 더 크게 TextSize 조절
            for (int i = 0 ; i < textViews.length ; i++) {
                int textSize = data.getTextSize();

                switch (i) {
                    case 2 : // time TextView
                        textViews[i].setTextSize((textSize + 20) / getDensity());
                        break;
                    default:
                        textViews[i].setTextSize(textSize / getDensity());
                        break;
                }

                textViews[i].setTextColor(Color.rgb(colorRGB[data.getColor()][0], colorRGB[data.getColor()][1], colorRGB[data.getColor()][2]));
            }
        }
    }

    // Px to Dp
    private float getDensity() {
        return getResources().getDisplayMetrics().density;
    }
}

// TextView 를 배열로 받아서 날짜 출력
class TimerHandler extends Handler {
    private static final String TAG = "TimerHandler";

    // 사용할 TextViews (MainActivity 에서 가져온 TextView)
    TextView[] textViews;

    public TimerHandler(TextView[] textViews) {
        this.textViews = textViews;
    }

    @Override
    public void handleMessage(@NonNull Message msg) {
        // 시간 가져오기
        Calendar cal = Calendar.getInstance() ;

        int year = cal.get(Calendar.YEAR); // 년도
        int month = cal.get(Calendar.MONTH) + 1; // 월 (월은 0부터 시작이기에 + 1)
        int date = cal.get(Calendar.DATE); // 일
        int week = cal.get(Calendar.DAY_OF_WEEK); // 요일 (ex: 1 -> 일요일, 2 -> 월요일 등)
        int hour = cal.get(Calendar.HOUR); // 시간 (12시 기준)
        int hour24 = cal.get(Calendar.HOUR_OF_DAY); // 시간 (24시 기준)
        int second = cal.get(Calendar.SECOND); // 초

        String str = year + "년 " + month + "월 " + date + "일";
        textViews[0].setText(str);
        str = WeekDistinction(week);
        textViews[1].setText(str);
        SimpleDateFormat sdf = new SimpleDateFormat("mm"); // 분 을 두자릿수로 나타내기 위해 사용
        str = hour12(hour) + " : " + sdf.format(cal.getTime());
        textViews[2].setText(str);
        str = AmPmDistinction(hour24);
        textViews[3].setText(str);
        sdf = new SimpleDateFormat("ss"); // 초 를 두자릿수로 나타내기 위해 사용
        str = ": " + sdf.format(cal.getTime());
        textViews[4].setText(str);
    }

    private int hour12(int num) {
        return num == 0 ? 12 : num;
    }

    // Calendar.DAY_OF_WEEK 의 내용을 받아서 String 으로 변환
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

    // Calendar.HOUR_OF_DAY 24시간을 받아서 오후 오전 String 변환
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
                Thread.sleep(1000); // 1초 쉼
            } catch (Exception e) {
                e.printStackTrace();
            }

            handler.sendEmptyMessage(0); // 아무런 내용이없는 Message 보내기
        }
    }
}