package org.androidtown.newtiggle.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import org.androidtown.newtiggle.R;
import org.androidtown.newtiggle.object.Income;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by 조현정 on 2017-06-17.
 */

public class ModifyIncomeActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText mDay;
    private EditText mIncomeDay, mIncomeTime, mIncomeMoney, mIncomeMemo;
    private Spinner mIncomeCategory;

    private Income selectIncome;
    private String incomeCat1 = "월급", incomeCat2="용돈", incomeCat3="보너스", incomeCat4 = "기타";

    InputMethodManager imm; //화면 터치시 키보드 내리기 위한

    DatePicker mDate;
    int year,month, day;

    TimePicker mTime;
    int hour, minute;

    private Button mObjectBtn;
    private JSONObject jsonObject = new JSONObject(); // for temp


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_income);


        mDate = (DatePicker) findViewById(R.id.incomeDatePicker); //day 받기 위한
        mTime = (TimePicker) findViewById(R.id.incomeTimePicker);
        mIncomeDay = (EditText) findViewById(R.id.incomeDayEt);
        mIncomeTime = (EditText) findViewById(R.id.incomeTimeEt);
        mIncomeCategory = (Spinner) findViewById(R.id.incomeCategorySp);
        mIncomeMoney = (EditText) findViewById(R.id.incomeMoneyEt);
        mIncomeMemo = (EditText) findViewById(R.id.incomeMemoEt);

        //ShowDetailActivity에서 보낸 intent(Income 정보를 갖고 있음)을 받아서 EditText에 set
        Intent intent = getIntent();
        selectIncome = (Income)intent.getSerializableExtra("selectIncome");


        mIncomeDay.setText(String.valueOf(makeDateForm(selectIncome.getmDate())));
        mIncomeTime.setText(String.valueOf(makeTimeForm(selectIncome.getmTime())));

        Log.d("*************","111111111111");

        if (incomeCat1.equals(selectIncome.getmCategory()))
            mIncomeCategory.setSelection(0);
        else if (incomeCat2.equals(selectIncome.getmCategory()))
            mIncomeCategory.setSelection(1);
        else if (incomeCat3.equals(selectIncome.getmCategory()))
            mIncomeCategory.setSelection(2);
        else
            mIncomeCategory.setSelection(3);

        Log.d("*************","222222222");
        mIncomeMoney.setText(String.valueOf(selectIncome.getmMoney()));
        mIncomeMemo.setText(selectIncome.getmMemo());

        //여기까지 기존의 사용자가 입력했던 값을 불러와 editText로 띄워줌.

        //datePicker가 변할때 editText의 값을 바꿔주기 위한 코드들.

        int tempDate = selectIncome.getmDate();
        //day받기 위한
        mDate.init(mDate.getYear(),mDate.getMonth(),mDate.getDayOfMonth(), new DatePicker.OnDateChangedListener() {

            @Override
            public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                mIncomeDay.setText(String.format("%d-%d-%d", year, monthOfYear + 1, dayOfMonth));
                selectIncome.setmDate(year*10000+(monthOfYear+1)*100+dayOfMonth);
            }
        });


        mTime.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                mIncomeTime.setText(String.format("%d:%d", hourOfDay, minute));
                selectIncome.setmTime((hourOfDay*100+minute));
            }
        });

        bindView();

        //화면 터치시 키보드 내리기
        imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
    }

    @Override
    public void onClick(View v){
        switch(v.getId())
        {
            case R.id.objectBtn:
                sendObject();
                finish();
                break;
        }
    }


    public void onBackButtonClicked(View v) {
        Toast.makeText(getApplicationContext(), "돌아가기 버튼이 눌렸어요", Toast.LENGTH_LONG).show();
        finish();
    }

   /* public void onObjectButtonClicked(View v){
        sendObject();
        finish();
    }*/

    //화면 터치시 키보드 내리기
    public void linearOnClick(View view){
        imm.hideSoftInputFromWindow(mIncomeMoney.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(mIncomeMemo.getWindowToken(), 0);
    }

    public String makeDateForm(int date) {
        int dateOfYear, dateOfMonth, dateOfday;
        dateOfYear = date/10000;
        dateOfMonth = (dateOfYear*10000-date)/100;
        dateOfday = dateOfYear*10000-dateOfMonth*100-date;

        String result = dateOfYear+"-"+dateOfMonth+"-"+dateOfday;
        return result;
    }

    public String makeTimeForm(int time){
        int timeOfHour, timeOfMinute;
        timeOfHour = time/100;
        timeOfMinute = time-timeOfHour*100;

        String result = timeOfHour+":"+timeOfMinute;
        return result;
    }

    private void bindView(){
        mObjectBtn = (Button)findViewById(R.id.objectBtn);

        mObjectBtn.setOnClickListener(this);

    }

    private void sendObject(){

        try{
            int incomeDay = selectIncome.getmDate();
            int incomeTime = selectIncome.getmTime();

            //incomeDay = mDate.getYear()*10000+(mDate.getMonth()+1)*100 + mDate.getDayOfMonth();

            //time만들기
            //if (Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
             //   incomeTime = mTime.getHour() + 100 + mTime.getMinute();
           // } else {
           //     incomeTime = mTime.getCurrentHour() * 100 + mTime.getCurrentMinute();
           // }


            //jsonObject.put("day", mJsonExpenseDayEt.getText().toString());  //day받기 위한
            jsonObject.put("date", incomeDay); //day받기 위한
            jsonObject.put("time", incomeTime); //time받기 위한
            jsonObject.put("category", mIncomeCategory.getSelectedItem().toString()); //category받기 위한
            Log.d("*************",String.valueOf(mIncomeMoney.getText()));
            jsonObject.put("money", mIncomeMoney.getText().toString());
            jsonObject.put("memo", mIncomeMemo.getText().toString());
        } catch(JSONException e){
            e.printStackTrace();
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        SaveNewIncome request = new SaveNewIncome();
        request.run();
    }

    private class SaveNewIncome extends Thread
    {
        @Override
        public void run() {

            String screens;
            //create network connecting thread
            SendNewIncome request = new SendNewIncome();
            screens = request.postData("ljh", jsonObject);

            JSONObject result;
            JSONObject data;

        }
    }

    private class SendNewIncome extends AsyncTask<String, String, String> {

        String msg = MainActivity.urlString + "/put/income";

        @Override
        protected String doInBackground(String... strings) {
            postData("ljh", jsonObject);
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            //super.onPostExecute(s);
            Log.v("Main::", "start send");
        }

        public String postData(String uid, JSONObject data) {
            InputStream inputStream = null;
            BufferedReader rd = null;
            StringBuilder result = new StringBuilder();

            StringBuilder requestUrl = new StringBuilder(msg);

            List<NameValuePair> nvps = new ArrayList<NameValuePair>();
            nvps.add(new BasicNameValuePair("uid", MainActivity.currentUserId));
            nvps.add(new BasicNameValuePair("iid", String.valueOf(selectIncome.getmIncomeId())));
            String querystring = URLEncodedUtils.format(nvps, "utf-8");

            requestUrl.append("?");
            requestUrl.append(querystring);

            HttpClient httpClient = new DefaultHttpClient();
            HttpPut httpPut = new HttpPut(requestUrl.toString());
            Log.d("msg is :", requestUrl.toString());

            try {
                String json="";
                json=data.toString();

                // loglog
                Log.v("^^^^^json", json);

                StringEntity stringEntity=new StringEntity(json, "utf-8");
                httpPut.setEntity(stringEntity);

                //answer객체 서버로 전송하고 survey객체 받아오는 과정

                HttpResponse httpResponse = httpClient.execute(httpPut);
                Log.v("******server", "send msg successed");

                inputStream = httpResponse.getEntity().getContent();
                rd = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                Log.v("Main::bring success", "result:" + result.toString());

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
    }

}

