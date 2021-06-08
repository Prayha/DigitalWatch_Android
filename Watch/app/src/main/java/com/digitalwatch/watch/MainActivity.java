package com.digitalwatch.watch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.android.billingclient.api.BillingClient;
import com.android.billingclient.api.BillingClientStateListener;
import com.android.billingclient.api.BillingFlowParams;
import com.android.billingclient.api.BillingResult;
import com.android.billingclient.api.ConsumeParams;
import com.android.billingclient.api.ConsumeResponseListener;
import com.android.billingclient.api.Purchase;
import com.android.billingclient.api.PurchasesUpdatedListener;
import com.android.billingclient.api.SkuDetails;
import com.android.billingclient.api.SkuDetailsParams;
import com.android.billingclient.api.SkuDetailsResponseListener;
import com.digitalwatch.watch.Util.Status;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.Task;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.google.android.play.core.install.model.AppUpdateType.IMMEDIATE;

/* -------------------------------------------------------------------------------------------------
 *
 * 앱이 실행이 되면 SQLite에 저장되어 있는 Setting 값을 가져온후 적용 시키고 (없으면 생성)
 * Runnable , Handler 를 이용하여 오늘의 시간 을 구한뒤 TextView 에 각각 적용 시킨다.
 *
 * -------------------------------------------------------------------------------------------------
 */

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private static final int UPDATE_REQUEST_CODE = 777;

    private TextView[] textViews;
    private int[][] colorRGB;

    AppUpdateManager appUpdateManager;

    private BillingClient billingClient;
    private List<SkuDetails> skuDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        appUpdateManager = AppUpdateManagerFactory.create(getApplicationContext());

        // App 업데이트 체크
        AppUpdateCheck();

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

        // 인앱 결제 세팅
        skuDetails = new ArrayList<>();

        // BillingClient 초기화
        billingClient = BillingClient.newBuilder(this)
                .setListener(purchasesUpdatedListener)
                .enablePendingPurchases()
                .build();

        // Google Play 연결 설정
        billingClient.startConnection(new BillingClientStateListener() {
            @Override
            public void onBillingSetupFinished(BillingResult billingResult) {
                if (billingResult.getResponseCode() ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.e(TAG, "onBillingSetupFinished");

                    // Google Play 구입 가능한 제품 표시
                    List<String> skuList = new ArrayList<>();
                    skuList.add("donation_0001"); // 등록한 인앱상품 ID
                    skuList.add("donation_0002");

                    SkuDetailsParams.Builder params = SkuDetailsParams.newBuilder();
                    params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP);
                    billingClient.querySkuDetailsAsync(params.build(),
                            new SkuDetailsResponseListener() {
                                @Override
                                public void onSkuDetailsResponse(BillingResult billingResult, List<SkuDetails> skuDetailsList) {
                                    Log.e(TAG, "onSkuDetailsResponse");

                                    if (skuDetailsList == null) {
                                        Log.e(TAG, "skuDetailsList is NULL");
                                        return;
                                    }

                                    if (skuDetailsList.size() == 0) {
                                        Log.e(TAG, "skuDetailsList is size 0");
                                        return;
                                    }

                                    // Process the result.
                                    skuDetails.addAll(skuDetailsList);
                                }
                            });
                }
            }
            @Override
            public void onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        });

        // Donation Button 클릭시 인앱 결제
        findViewById(R.id.donation_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (skuDetails.size() == 0) {
                    return;
                }

                BillingFlowParams billingFlowParams = BillingFlowParams.newBuilder()
                        .setSkuDetails(skuDetails.get(0))
                        .build();

                billingClient.launchBillingFlow(MainActivity.this, billingFlowParams).getResponseCode();
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

        appUpdateManager
                .getAppUpdateInfo()
                .addOnSuccessListener(
                        appUpdateInfo -> {
                            if (appUpdateInfo.updateAvailability() == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS) {
                                // If an in-app update is already running, resume the update.
                                try {
                                    appUpdateManager.startUpdateFlowForResult(
                                            appUpdateInfo,
                                            IMMEDIATE,
                                            this,
                                            UPDATE_REQUEST_CODE);
                                } catch (IntentSender.SendIntentException e) {
                                    e.printStackTrace();
                                    }
                            }
                        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == UPDATE_REQUEST_CODE) {
            switch (requestCode) {
                case RESULT_CANCELED :
                    finish();
                    break;
                default:
                    break;
            }
        }
    }

    private void AppUpdateCheck() {
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();

        appUpdateInfoTask.addOnSuccessListener(appUpdateInfo -> {
            if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && appUpdateInfo.isUpdateTypeAllowed(IMMEDIATE)) {
                // Request the update.
                try {
                    appUpdateManager.startUpdateFlowForResult(
                            appUpdateInfo,
                            IMMEDIATE,
                            this,
                            UPDATE_REQUEST_CODE);
                } catch (IntentSender.SendIntentException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    private PurchasesUpdatedListener purchasesUpdatedListener = new PurchasesUpdatedListener() {
        @Override
        public void onPurchasesUpdated(BillingResult billingResult, List<Purchase> purchases) {
            // To be implemented in a later section.
            Log.e(TAG, "onPurchasesUpdated");

            if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK
                    && purchases != null) {
                for (Purchase purchase : purchases) {
                    handlePurchase(purchase);
                }
            } else if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.USER_CANCELED) {
                // Handle an error caused by a user cancelling the purchase flow.
            } else {
                // Handle any other error codes.
            }
        }
    };

    void handlePurchase(Purchase purchase) {
        ConsumeParams consumeParams =
                ConsumeParams.newBuilder()
                        .setPurchaseToken(purchase.getPurchaseToken())
                        .build();

        ConsumeResponseListener listener = new ConsumeResponseListener() {
            @Override
            public void onConsumeResponse(BillingResult billingResult, String purchaseToken) {
                if (billingResult.getResponseCode() == BillingClient.BillingResponseCode.OK) {
                    // Handle the success of the consume operation.
                    Log.e(TAG, "onConsumeResponse");
                    Toast.makeText(MainActivity.this, "감사합니다.", Toast.LENGTH_LONG).show();
                }
            }
        };

        billingClient.consumeAsync(consumeParams, listener);
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