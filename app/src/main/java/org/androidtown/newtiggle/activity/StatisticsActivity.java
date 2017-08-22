package org.androidtown.newtiggle.activity;

import android.graphics.Color;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.NumberPicker;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;

import org.androidtown.newtiggle.R;
import org.androidtown.newtiggle.object.MonthlyStat;
import org.androidtown.newtiggle.object.Statistics;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * Created by 조현정 on 2017-07-14.
 */

/* 막대그래프 추가 현정 2017-08-14*/

public class StatisticsActivity extends AppCompatActivity {

    private NumberPicker yearPicker;
    private NumberPicker monthPicker;
    private int selectDay;
    private MonthlyStat tempMonthlyStat;
    private Statistics temp;

    //파이차트 관련
    private PieChart mChart;
    private ArrayList<Integer> incomeExpenseMoneyData = new ArrayList<Integer>();
    private ArrayList<String> incomeExpenseLabel = new ArrayList<String>();
    public static final int[] VORDIPLOM_COLORS = {
            Color.rgb(192, 255, 140), Color.rgb(255, 247, 140)};

    //레이더차트 관련


    /*현재시간 저장하기*/
    long now = System.currentTimeMillis(); //현재시간을 msec로 구함.
    Date date = new Date(now);  // 현재 시간을 date변수에 저장
    SimpleDateFormat sdfNow = new SimpleDateFormat("yyyyMM");   //시간 나타낼 포맷 정함
    String formatDate = sdfNow.format(date);    //nowDate변수에 값을 저장



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        selectDay = Integer.parseInt(formatDate);
        Log.d("dayday :", formatDate);
        sendObject();
        Log.d("sendObject끝나고나서","oncreate야");
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

        mChart = (PieChart)findViewById(R.id.idPieChart);

        mChart.setDescription("월별 수입지출비교");
        mChart.setRotationEnabled(true);
        mChart.setHoleRadius(25f);
        mChart.setTransparentCircleAlpha(0);
        mChart.setCenterText("8월");
        mChart.setCenterTextSize(10);

        addDataSet();


    }

    private void sendObject(){
        Log.d("sendOjbect","들어왔다.");
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        GetMonthlyStat request = new GetMonthlyStat();
        request.run();
        //showChart();
    }

    public String postData(){
        Log.d("postData","들어왔다.");
        String msg = MainActivity.urlString+"/statistic/monthly";

        InputStream inputStream = null;
        BufferedReader rd = null;
        StringBuilder result = new StringBuilder();

        StringBuilder requestUrl = new StringBuilder(msg);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("uid", MainActivity.currentUserId));
        nvps.add(new BasicNameValuePair("date", Integer.toString(selectDay)));
        String querystring = URLEncodedUtils.format(nvps, "utf-8");

        requestUrl.append("?");
        requestUrl.append(querystring);

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(requestUrl.toString());
        Log.d("msg is :", requestUrl.toString());

        try {

            //answer객체 서버로 전송하고 survey객체 받아오는 과정

            HttpResponse httpResponse = httpClient.execute(httpGet);

            Log.v("******server", "send msg successed");

            inputStream = httpResponse.getEntity().getContent();
            rd = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }
            Log.v("Main::bring success", "result:" + result.toString());
           showMonthlyStat(result.toString());

        } catch (IOException e) {
            e.printStackTrace();
            Log.v("******server", "send msg failed");
        }



        if (result != null) {
            return result.toString();
        } else {
            return null;
        }

    }

    private class GetMonthlyStat extends Thread{
        @Override
        public void run(){
            postData();
        }
    }

    public void showMonthlyStat(String jsonString){
        Log.d("showMonthlyStat","들어왔다.");

        try {
            JSONObject stringToJson = new JSONObject(jsonString);   //서버에서 string으로 받은 결과를 json객체로 바꿈
            //pieChart data채우기
            incomeExpenseMoneyData.clear();
            incomeExpenseLabel.clear();
            incomeExpenseMoneyData.add(stringToJson.getInt("income"));
            incomeExpenseMoneyData.add(stringToJson.getInt("expense"));
            incomeExpenseLabel.add("수입");
            incomeExpenseLabel.add("지출");


            //RadarChart data채우기
            Iterator<?> keys = stringToJson.keys();


            while(keys.hasNext()){
                String key = (String) keys.next();
                Log.d("fieldddd :", stringToJson.get(key).toString());  //label 따로 저장해야함

            }


        }
        catch (JSONException e) {
        }

    }


    private void addDataSet(){
        Log.d("msg: ", "addDataSet started");
        ArrayList<PieEntry> yEntrys = new ArrayList<>();
        ArrayList<String> xEntrys = new ArrayList<>();

        for(int i = 0; i < incomeExpenseLabel.size(); i++){
            yEntrys.add(new PieEntry(incomeExpenseMoneyData.get(i) , i));
        }

        for(int i = 1; i < incomeExpenseMoneyData.size(); i++){
            xEntrys.add(incomeExpenseLabel.get(i));
        }

        //create the data set
        PieDataSet pieDataSet = new PieDataSet(yEntrys, "월통계");
        pieDataSet.setSliceSpace(2);
        pieDataSet.setValueTextSize(12);

        //add colors to dataset
        ArrayList<Integer> colors = new ArrayList<>();
        colors.add(Color.rgb(192, 255, 140));
        colors.add(Color.rgb(255, 247, 140));

        pieDataSet.setColors(colors);

        //add legend to chart
        Legend legend = mChart.getLegend();
        legend.setFormSize(10f);
        legend.setForm(Legend.LegendForm.CIRCLE);
        legend.setPosition(Legend.LegendPosition.BELOW_CHART_CENTER);
        legend.setTextSize(12f);
        legend.setTextColor(Color.BLACK);
        legend.setXEntrySpace(5f);
        legend.setYEntrySpace(5f);

        legend.setCustom(VORDIPLOM_COLORS, new String[]{"수입","지출"});


        //create pie data object
        ArrayList<String>xVals = new ArrayList<String>();
        xVals.add("수입");
        xVals.add("지출");
        PieData pieData = new PieData(pieDataSet);
        mChart.setData(pieData);
        mChart.invalidate();
    }

    public void onBackButtonClicked(View v) {
        Toast.makeText(getApplicationContext(), "돌아가기 버튼이 눌렸어요", Toast.LENGTH_LONG).show();
        finish();
    }

    public void monthlyStatClicked (View v) {
        int year = yearPicker.getValue();
        int month = monthPicker.getValue();
        selectDay = year*100 + month;
        sendObject();

        mChart.setRotationEnabled(true);
        //mChart.setUsePercentValues(true);
        //mChart.setHoleColor(Color.BLUE);
        //mChart.setCenterTextColor(Color.BLACK);
        mChart.setHoleRadius(25f);
        mChart.setTransparentCircleAlpha(0);
        mChart.setCenterText(month+"월");
        mChart.setCenterTextSize(10);
        //mChart.setDrawEntryLabels(true);
        //mChart.setEntryLabelTextSize(20);

        addDataSet();

        Toast.makeText(getApplicationContext(), "선택날짜:"+selectDay, Toast.LENGTH_SHORT).show();
    }
}
