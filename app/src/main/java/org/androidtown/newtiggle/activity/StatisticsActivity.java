package org.androidtown.newtiggle.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.numetriclabz.numandroidcharts.ChartData;
import com.numetriclabz.numandroidcharts.StackedBarChart;

import org.androidtown.newtiggle.R;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by 조현정 on 2017-07-14.
 */

/* 막대그래프 추가 현정 2017-08-14*/

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

        StackedBarChart stackBarChart = (StackedBarChart) findViewById(R.id.chart);     //bar chart findview
        //ChartData value = new ChartData;

        List<ChartData> value = new ArrayList<>();

        Float[] value1 = {2f,3f,6f,5f };
        Float[] value2 = {3f,5f,7f,9f };

        value.add(new ChartData(value1, "income"));
        value.add(new ChartData(value2, "expense"));

        List<String> h_lables = new ArrayList<>();
        h_lables.add("sun");
        h_lables.add("mon");

        stackBarChart.setLabels(h_lables);
        stackBarChart.setData(value);

        stackBarChart.setHorizontalStckedBar(true);
        stackBarChart.setPercentageStacked(true);

    }


    public void onBackButtonClicked(View v) {
        Toast.makeText(getApplicationContext(), "돌아가기 버튼이 눌렸어요", Toast.LENGTH_LONG).show();
        finish();
    }

    public void sendBarInfo (View v) {
        Toast.makeText(getApplicationContext(), "막대그래프", Toast.LENGTH_SHORT).show();
    }
}
