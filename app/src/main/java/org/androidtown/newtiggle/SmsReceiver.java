package org.androidtown.newtiggle;

/**
 * Created by CE-L-05 on 2017-08-08.
 */


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import org.androidtown.newtiggle.activity.ExpenseActivity;
import org.androidtown.newtiggle.activity.MainActivity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class SmsReceiver extends BroadcastReceiver {
    private Bundle bundle;
    private SmsMessage currentSMS;
    private String message;
    private int money = 0;
    private String memo = "";
    private int date = 0;
    private int time = 0;

    private JSONObject jsonObject = new JSONObject(); // for temp



    @Override
    public void onReceive(Context context, Intent intent) {

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            Log.d("onReceive()","부팅완료");
            //Intent i = new Intent(context, ScreenService.class);
            //context.startService(i);
        }

        if (intent.getAction().equals(Intent.ACTION_SCREEN_ON)) {
            Log.d("onReceive()","스크린 ON");
        }

        if (intent.getAction().equals(Intent.ACTION_SCREEN_OFF)) {
            Log.d("onReceive()","스크린 OFF");
        }

        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Log.d("SMSBroadcastReceiver", "SMS 메시지가 수신되었습니다.");


            //Toast.makeText(context, "문자가 수신되었습니다", Toast.LENGTH_SHORT).show();
            bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdu_Objects = (Object[]) bundle.get("pdus");
                if (pdu_Objects != null) {
                    for (Object aObject : pdu_Objects) {

                        currentSMS = getIncomingMessage(aObject, bundle);
                        String senderNo = currentSMS.getDisplayOriginatingAddress();
                        message = currentSMS.getDisplayMessageBody();
                        Toast.makeText(context, "senderNum: " + senderNo + " :\n message: " + message, Toast.LENGTH_LONG).show();
                        //Log.d("senderNo", senderNo);

                        /* Parse money, memo from message */
                        String[] tokens = message.split("\n");
                        for(int i = 0; i < tokens.length ; i++) {
                            try {
                                if (tokens[i].contains("원") != false) {
                                    String moneyString = tokens[i];
                                    moneyString = moneyString.substring(0, moneyString.indexOf("원"));
                                    if(moneyString.contains("일시불") != false){
                                        moneyString = moneyString.substring(moneyString.indexOf("일시불 ")+4, moneyString.length());
                                        Log.d("일시불~", moneyString);
                                    }
                                    moneyString = moneyString.replaceAll(",", "");

                                    money = Integer.parseInt(moneyString);
                                } else if (tokens[i].contains("KRW") != false) {
                                    String moneyString = tokens[i];
                                    moneyString = moneyString.substring(4, moneyString.indexOf("."));
                                    moneyString = moneyString.replaceAll(",", "");
                                    money = Integer.parseInt(moneyString);

                                } else if (tokens[i].contains("사용") != false) {
                                    memo = tokens[i].substring(0, tokens[i].length() - 3);
                                } else if (tokens[i].contains("[") == false && tokens[i].contains(":") == false
                                        && tokens[i].contains("*") == false && tokens[i].contains("님") == false
                                        && tokens[i].contains("할부") == false && tokens[i].contains("해외") == false) {
                                    memo = tokens[i];
                                }
                            } catch (Exception e) {
                                Log.d("parsing error", tokens[i]);
                            }

                        }

                        /* get date, time */
                        Log.v("hi", "조규원");
                        getSpecialDateFormat();
                        Log.v("hi", "신우환");

                        /* get card company name from sender */
                        getCardName(senderNo);
                        Log.d("message", message);

                        Log.d("money", money+"");
                        Log.d("memo", memo);
                        Log.d("date", date+"");
                        Log.d("time", time+"");

                        /* server에 전송하는 코드 추가 */


                    }
                    this.abortBroadcast();
                    // End of loop
                }
            }
        } // bundle null
    }

    private SmsMessage getIncomingMessage(Object aObject, Bundle bundle) {
        SmsMessage currentSMS;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String format = bundle.getString("format");
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject, format);
        } else {
            currentSMS = SmsMessage.createFromPdu((byte[]) aObject);
        }
        return currentSMS;
    }

    private String getCardName(String sender) {
        String cardName = "";
        Log.d("***", ""+Arrays.asList(CardInfo.cardAddressList).contains(sender));
        int temp = Arrays.asList(CardInfo.cardAddressList).indexOf(sender);
        Log.d("***", ""+CardInfo.cardAddressList[temp+1]);

        return cardName;

    }

    public void getSpecialDateFormat(){
        // 현재 시간을 msec으로 구한다.
        long now = System.currentTimeMillis();
        Log.v("hi", "1");

        // 현재 시간을 저장 한다.
        Date dateObj = new Date(now);
        Log.v("hi", "2");

        SimpleDateFormat curDateFormat = new SimpleDateFormat("yyyyMMdd");
        SimpleDateFormat curTimeFormat = new SimpleDateFormat("HHmm");
        String strCurDate = curDateFormat.format(dateObj);
        String strCurTime = curTimeFormat.format(dateObj);
        date = Integer.parseInt(strCurDate);
        time = Integer.parseInt(strCurTime);
        Log.v("hi", "3");

        Log.d("strCurDate", date+"");
        Log.d("strCurTime", time+"");
    }






    private void sendObject() {

        try {

            jsonObject.put("date", date); //day받기 위한
            jsonObject.put("time", time); //time받기 위한
            jsonObject.put("money", money);
            jsonObject.put("memo", memo);
            jsonObject.put("category", "기타"); //category받기 위한
            jsonObject.put("payMethod", "카드"); //결제수단 받기위한


        } catch (JSONException e) {
            e.printStackTrace();
        }

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        SmsReceiver.SaveNewExpense request = new SmsReceiver.SaveNewExpense();
        request.run();

    }



    private class SaveNewExpense extends Thread
    {
        @Override
        public void run() {

            postData("ljh", jsonObject);

        }
    }

    public String postData(String uid, JSONObject data) {

        String msg = MainActivity.urlString + "/post/expense";

        InputStream inputStream = null;
        BufferedReader rd = null;
        StringBuilder result = new StringBuilder();

        StringBuilder requestUrl = new StringBuilder(msg);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("uid", MainActivity.currentUserId));
        String querystring = URLEncodedUtils.format(nvps, "utf-8");

        requestUrl.append("?");
        requestUrl.append(querystring);

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(requestUrl.toString());
        Log.d("msg is :", requestUrl.toString());

        try {

            String json="";
            json=data.toString();

            // loglog
            Log.v("^^^^^json", json);

            StringEntity stringEntity=new StringEntity(json, "utf-8");
            httpPost.setEntity(stringEntity);

            //answer객체 서버로 전송하고 survey객체 받아오는 과정

            HttpResponse httpResponse = httpClient.execute(httpPost);
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