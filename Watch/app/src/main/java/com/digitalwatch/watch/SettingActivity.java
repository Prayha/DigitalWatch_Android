package com.digitalwatch.watch;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;

import com.digitalwatch.watch.Adapter.ColorPaletteAdapter;
import com.digitalwatch.watch.Util.Status;

/* -------------------------------------------------------------------------------------------------
 *
 *  디바이스의 화면의 크기를 구한뒤 GridView의 내용을 생성할때의 각 View 의 크기를 유동적으로 움직이기
 *  위하여 Width 와 Height 를 구한뒤 값을 넘긴다.
 *
 *  TextWatcher 를 사용하여 EditText 값이 변할때마다 exampleTextView 안에 적용되게 만들었다.
 *
 * -------------------------------------------------------------------------------------------------
 */

public class SettingActivity extends AppCompatActivity {
    private final static String TAG = "SettingActivity";

    private TextView exampleTextView;
    private int[][] colorRGB;
    // Default 설정
    private int setColor = 12;
    private int size = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        // 키보드 화면 밀기
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);

        colorRGB = Status.setColor();

        // 디바이스 가로, 세로길이 구하기
        DisplayMetrics dm = getApplication().getResources().getDisplayMetrics();
        int width = dm.widthPixels / colorRGB.length;
        int height = dm.heightPixels / colorRGB.length * colorRGB[0].length;

        GridView colorPalette = findViewById(R.id.Color_Palette);
        colorPalette.setNumColumns(colorRGB.length); // GridView 에서 가로로 보여줄 수
        ColorPaletteAdapter adapter = new ColorPaletteAdapter(SettingActivity.this, colorRGB, width, height); // GridView 에서 각 View 의 크기 (width, height)
        colorPalette.setAdapter(adapter);

        colorPalette.setOnItemClickListener(colorPaletteOnItemClickListener);

        // 결과가 보일 TextView
        exampleTextView = findViewById(R.id.example_TextView);

        // EditText에 값이 입력될시 바로바로 적용된다.
        EditText textSize = findViewById(R.id.textsize_EditText);

        // EditText에 TextSize값을 넣을때
        textSize.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                // TextSize 값 받기
                String sizeString = textSize.getText().toString();

                // 숫자가 아니면 오류 넘기기
                try {
                    size = Integer.parseInt(sizeString);

                    exampleTextView.setTextSize(size / getDensity());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        // SQLite Update 버튼, 이벤트
        findViewById(R.id.update_Button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SettingSQLiteQuery query = new SettingSQLiteQuery(SettingActivity.this);

                // SQLite 변경
                query.settingUpdate(setColor, size);
                query.settingCloes();

                finish();
            }
        });
    }

    // GridView 클릭 이벤트
    AdapterView.OnItemClickListener colorPaletteOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            // exampleTextView 에 바로 적용
            exampleTextView.setTextColor(Color.rgb(colorRGB[position][0], colorRGB[position][1], colorRGB[position][2]));

            // 저장할 Color
            setColor = position;
        }
    };

    private float getDensity() {
        return getResources().getDisplayMetrics().density;
    }
}