package org.androidtown.newtiggle.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import org.androidtown.newtiggle.R;
import org.json.JSONObject;

/**
 * Created by 조현정 on 2017-07-14.
 */

public class StatisticsActivity extends AppCompatActivity {

    private NumberPicker yearPicker;
    private NumberPicker monthPicker;
    private Button barBtn;
    private Button pieBtn;
    private int selectDay;

    private JSONObject jsonObject = new JSONObject(); // for temp
    private static final int BAR = 0;
    private static final int PIE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        //2015~2017까지 연도 picker만들기
        yearPicker = (NumberPicker)findViewById(R.id.selectYear);
        yearPicker.setMinValue(2014);
        yearPicker.setMaxValue(2017);
        yearPicker.setWrapSelectorWheel(false);
        yearPicker.setValue(2017);

        monthPicker = (NumberPicker)findViewById(R.id.selectMonth);
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        monthPicker.setWrapSelectorWheel(false);
        monthPicker.setOnLongPressUpdateInterval(100);
        monthPicker.setValue(10);

    }


    public void onBackButtonClicked(View v) {
        Toast.makeText(getApplicationContext(), "돌아가기 버튼이 눌렸어요", Toast.LENGTH_LONG).show();
        finish();
    }


}
