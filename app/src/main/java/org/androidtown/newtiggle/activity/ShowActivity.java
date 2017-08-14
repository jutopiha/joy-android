package org.androidtown.newtiggle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.androidtown.newtiggle.adapter.ExpenseListViewAdapter;
import org.androidtown.newtiggle.adapter.IncomeListViewAdapter;
import org.androidtown.newtiggle.R;
import org.androidtown.newtiggle.object.Expense;
import org.androidtown.newtiggle.object.Income;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.androidtown.newtiggle.R.layout.activity_show;

/**
 * Created by Juha on 2017-05-18.
 */

public class ShowActivity extends AppCompatActivity implements View.OnClickListener  {

    private Button mObjectBtn;

    private URL url;
    private static String strUrl, strCookie, result;

    private JSONObject jsonObject = new JSONObject();
    private int selectDay;
    private int sYear, sMonth, sDay;

    final static int ACT_DELETE = 1;
    private int positionNumber;
    private int allIncome;
    private int allExpense;
    private TextView mAllIncome, mAllExpense;
    //private SimpleAdapter adapter = null;
    //private ListView showJSON = null;

    IncomeListViewAdapter incomeAdapter= new IncomeListViewAdapter();
    ExpenseListViewAdapter expenseAdapter = new ExpenseListViewAdapter();

    ArrayList<Expense> expenseArrayList = new ArrayList<Expense>(); //JSONObject에서 expense 부분만을 저장하기 위한 arrayList
    ArrayList<Income> incomeArrayList = new ArrayList<Income>();    //JSONObject에서 income 부분만을 저장하기 위한 arrayList

    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd", java.util.Locale.getDefault());
    Date date = new Date();
    int currentDate = Integer.parseInt(dateFormat.format(date));

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(activity_show);


        //Calendar 인스턴스 생성
        CalendarView calendar = (CalendarView)findViewById(R.id.calendar);


        //리스너 등록
        calendar.setOnDateChangeListener(new CalendarView.OnDateChangeListener(){


            //선택할 때마다 selectDay 변수에 선택된 날짜를 int형으로 저장한다.
            @Override
            public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth){
                selectDay = year*10000+(month+1)*100+dayOfMonth;
                sYear = year;
                sMonth = month+1;
                sDay = dayOfMonth;
            }
        });

        //selectDay = sYear*10000+(sMonth+1)*100+sDay;
        selectDay = currentDate;


        bindView();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.

    }

    @Override
    public void onClick(View v) {

        switch(v.getId()){
            case R.id.objectBtn:
                allIncome = 0;
                allExpense = 0;
                sendObject();
                break;

        }
    }



    //view를 bind해줌
    private void bindView() {
        mObjectBtn = (Button) findViewById(R.id.objectBtn);
        mAllIncome = (TextView)findViewById(R.id.allIncomeTv);
        mAllExpense = (TextView)findViewById(R.id.allExpenseTv);
        mObjectBtn.setOnClickListener(this);
    }



    private void sendObject(){

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        SaveNewShow request = new SaveNewShow();
        request.run();
    }

    public void onBackButtonClicked(View v) {
        Toast.makeText(getApplicationContext(), "돌아가기 버튼이 눌렸어요", Toast.LENGTH_LONG).show();
        finish();
    }

    private class SaveNewShow extends Thread
    {
        @Override
        public void run() {

            postData("ljh", selectDay);

        }
    }

    public String postData(String uid, int data) {

        String msg = MainActivity.urlString + "/get/dealing";


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
            showDealingList(result.toString());

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


    public void showDealingList(String jsonString) {  //json객체 string으로 받아 파싱하여 income/expense object를 생성한 뒤, 리스트뷰로 만들어서 show함

        //listView를 위한
        ListView incomeListView;
        ListView expenseListView;

        //해당하는 날짜의 값들만을 JSONObject에서 골라내어 저장함.
        try {
            JSONObject stringToJson = new JSONObject(jsonString);   //서버에서 string으로 받은 결과를 json객체로 바꿈

            JSONArray incomeArr =  stringToJson.getJSONArray("incomeList"); //incomeList JSONArray를 파싱.
            JSONArray expenseArr = stringToJson.getJSONArray("expenseList");//expenseList JSONArray를 파싱.

            incomeArrayList.clear();
            expenseArrayList.clear();
            for(int i=0; i<incomeArr.length();i++)
            {
                //jsonArray에서 jsonObject를 뽑아냄
                JSONObject currentIncomeJson = incomeArr.getJSONObject(i);

                //뽑아낸 jsonObject를 arrayList에 추가해 준다.
                //각각의 income list를 Income class의 object를 생성하여 저장함.
                Income newIncome = new Income(currentIncomeJson);
                allIncome += newIncome.getmMoney();
                incomeArrayList.add(newIncome);
            }

            for(int j=0 ; j<expenseArr.length();j++)
            {
                JSONObject currentExpenseJson = expenseArr.getJSONObject(j);

                Expense newExpense = new Expense(currentExpenseJson);
                allExpense += newExpense.getmMoney();
                expenseArrayList.add(newExpense);
            }
        }
        catch (JSONException e) {
        }

        //리스트뷰 참조 및 adapter달기
        incomeListView = (ListView)findViewById(R.id.incomeList);
        incomeListView.setAdapter(incomeAdapter);
        //income의 리스트가 눌렸을때 리스너를 생성하고 달아줌
        IncomeListViewClickListener incomeListViewClickListener = new IncomeListViewClickListener();
        incomeListView.setOnItemClickListener(incomeListViewClickListener);

        expenseListView = (ListView)findViewById(R.id.expenseList);
        expenseListView.setAdapter(expenseAdapter);

        //expense의 리스트가 눌렸을때 리스너를 생성하고 달아줌
        ExpenseListViewClickListener expenseListViewClickListener = new ExpenseListViewClickListener();
        expenseListView.setOnItemClickListener(expenseListViewClickListener);

        incomeAdapter.addItem(incomeArrayList);     //jsonObject를 파싱하여 만든 income들의 list를 incomeArrayList로 어뎁터에 추가함.
        expenseAdapter.addItem(expenseArrayList);

        mAllIncome.setText("총 수입:   "+String.valueOf(allIncome)+"원");
        mAllExpense.setText("총 지출:   "+String.valueOf(allExpense)+"원");

    }

    private class IncomeListViewClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView parent, View v, int position, long id) {

            Intent intent = new Intent(ShowActivity.this, ShowIncomeDetailActivity.class);
            intent.putExtra("selectIncome", incomeArrayList.get(position));
            positionNumber = position;
            startActivityForResult(intent, ACT_DELETE);
        }
    }
    private class ExpenseListViewClickListener implements AdapterView.OnItemClickListener {
        public void onItemClick(AdapterView parent, View v, int position, long id) {

            Intent intent = new Intent(ShowActivity.this, ShowExpenseDetailActivity.class);
            Log.d("*********보내기전", String.valueOf(expenseArrayList.get(position).getmMoney()));
            intent.putExtra("selectExpense", expenseArrayList.get(position));
            positionNumber = position;
            startActivityForResult(intent, ACT_DELETE);
        }
    }

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case ACT_DELETE:
                if (resultCode == RESULT_OK) {  //수입내역 삭제
                    allIncome = 0;
                    allExpense = 0;
                    Log.d("수입삭제","수입삭제");
                   // incomeArrayList.remove(positionNumber);
                   // incomeAdapter.notifyDataSetChanged();
                    sendObject();
                }

                else if(resultCode == RESULT_FIRST_USER){   //지출내역 삭제
                    allIncome = 0;
                    allExpense = 0;
                    Log.d("지출삭제","지출삭제");
                    //expenseArrayList.remove(positionNumber);
                   // expenseAdapter.notifyDataSetChanged();
                    sendObject();
                }

                else if(resultCode == RESULT_CANCELED){ //상세내역조회에서 뒤로가기를 누르거나 수정했을때
                    allIncome = 0;
                    allExpense = 0;
                    Log.d("뒤로가기수정","뒤로가기수정");
                    sendObject();
                }
                else {
                    Log.d("***************", "여기야");
                    sendObject();
                }

                break;
        }
    }
}
