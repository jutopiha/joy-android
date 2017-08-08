package org.androidtown.newtiggle.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.androidtown.newtiggle.R;
import org.androidtown.newtiggle.object.Income;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.androidtown.newtiggle.R.layout.activity_income_show_detail;

/**
 * Created by 조현정 on 2017-06-17.
 */

public class ShowIncomeDetailActivity extends AppCompatActivity{

    private Income selectIncome;
    private TextView mIncomeDay, mIncomeTime, mIncomeCategory, mIncomeMoney, mIncomeMemo;
    private int position;


    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(activity_income_show_detail);

        mIncomeDay = (TextView)findViewById(R.id.incomeDayValue);
        mIncomeTime = (TextView)findViewById(R.id.incomeTimeValue);
        mIncomeCategory = (TextView)findViewById(R.id.incomeCategoryValue);
        mIncomeMoney = (TextView)findViewById(R.id.incomeMoneyValue);
        mIncomeMemo = (TextView)findViewById(R.id.incomeMemoValue);

        //ShowActivity에서 보낸 intent(Income 정보를 갖고 있음)을 받아서 TextView에 set
        Intent intent = getIntent();
        selectIncome =(Income)intent.getSerializableExtra("selectIncome");

        //position = (int)intent.getIntExtra("positionNumber",1); 오류나면 여기 체크해봐

        mIncomeDay.setText(makeDateForm(selectIncome.getmDate()));
        Log.d("********",makeDateForm(selectIncome.getmDate()));
        mIncomeTime.setText(makeTimeForm(selectIncome.getmTime()));
        mIncomeCategory.setText(selectIncome.getmCategory());
        mIncomeMoney.setText(String.valueOf(selectIncome.getmMoney()));
        mIncomeMemo.setText(selectIncome.getmMemo());

    }


    public void onBackButtonClicked(View v) {
        Toast.makeText(getApplicationContext(), "돌아가기 버튼이 눌렸어요", Toast.LENGTH_LONG).show();
        Intent intent = new Intent();
        setResult(RESULT_CANCELED);
        finish();
    }

    public void onDeleteButtonClicked(View v){
        Toast.makeText(getApplicationContext(), "삭제버튼 눌림", Toast.LENGTH_LONG).show();
        deleteIncomeObject();

        Intent intent = new Intent();
        setResult(RESULT_OK,intent);
        finish();
    }

    //해당Income삭제
    private void deleteIncomeObject(){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        DeleteIncomeThread request = new DeleteIncomeThread();
        request.run();
    }

    private class DeleteIncomeThread extends Thread{
        @Override
        public void run(){

            String screens;
            DeleteIncome request = new DeleteIncome();
            screens = request.deleteData();
        }
    }

    private class DeleteIncome extends AsyncTask<String, String, String>{

        String msg = MainActivity.urlString+"/delete/income";

        @Override
        protected String doInBackground(String... strings){
            deleteData();
            return null;
        }

        @Override
        protected void onPostExecute(String s){
            Log.v("Client::", "send delete request");
        }

        public String deleteData(){
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
            //HttpPost httpPost = new HttpPost(requestUrl.toString());
            HttpDelete httpDelete = new HttpDelete((requestUrl.toString()));
            Log.d("msg is :", requestUrl.toString());

            try{
                HttpResponse httpResponse = httpClient.execute(httpDelete);
                Log.v("******server", "send msg successed");

                inputStream = httpResponse.getEntity().getContent();
                rd = new BufferedReader(new InputStreamReader(inputStream));
                String line;
                while ((line = rd.readLine()) != null) {
                    result.append(line);
                }
                Log.v("Main::bring success", "result:" + result.toString());
            }

            catch(IOException e){
                e.printStackTrace();
                Log.v("******server", "send msg failed");
            }

            if(result != null){
                return result.toString();
            }
            else{
                return null;
            }
        }
    }

    public void onModifyButtonClicked(View v){
        Intent intent = new Intent(ShowIncomeDetailActivity.this, ModifyIncomeActivity.class);
        intent.putExtra("selectIncome", selectIncome);
        startActivity(intent);

        Intent nintent = new Intent();
        setResult(RESULT_CANCELED,nintent);//고침
        finish();
    }

    public String makeDateForm(int date) {
        int dateOfYear, dateOfMonth, dateOfday;
        dateOfYear = date/10000;
        dateOfMonth = (date-dateOfYear*10000)/100;
        dateOfday = date-dateOfYear*10000-dateOfMonth*100;

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
}

