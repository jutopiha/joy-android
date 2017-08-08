package org.androidtown.newtiggle.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookAuthorizationException;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.gson.Gson;

import org.androidtown.newtiggle.R;
import org.androidtown.newtiggle.object.FacebookUser;
import org.androidtown.newtiggle.object.User;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by Juha on 2017-06-17.
 */
public class SignInActivity extends AppCompatActivity implements View.OnClickListener {

    RelativeLayout facebookSigninButton;
    CallbackManager mFacebookCallbackManager;

    private String mFacebookId;
    private User user = new User();

    GraphRequest request;   //get info
    JSONObject result;      //loaded data

    StringTokenizer itr;
    boolean isInitial = true;
    boolean isLoginBtnPressed = false;
    boolean isSignIn = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_sign_in);

        facebookSigninButton = (RelativeLayout) findViewById(R.id.sign_in_facebook_button);
        facebookSigninButton.setOnClickListener(this);

        setCustomActionBar();       // ActionBar 숨기기

        TextView tvLogin = (TextView) findViewById(R.id.tvLogin);   // 로그인하기 버튼
        tvLogin.setText("페이스북으로 로그인하기");
        Typeface typeFace = Typeface.createFromAsset(getAssets(), "SD Gothic Bold.ttf");  //asset > fonts 폴더 내 폰트파일 적용
        tvLogin.setTypeface(typeFace);

        if(AccessToken.getCurrentAccessToken() != null) {
            // 로그인이 되어 있는 경우, 로그아웃 한다.
            LoginManager.getInstance().logOut();
        }




    }

    public void onClick(View v) {
        //facebook sdk initialize
        FacebookSdk.sdkInitialize(getApplicationContext());

        // 디버그 모드 임시!!!!!!!!!!!!~!!~!
        /*
        if (BuildConfig.DEBUG) {
            FacebookSdk.setIsDebugEnabled(true);
            FacebookSdk.addLoggingBehavior(LoggingBehavior.INCLUDE_ACCESS_TOKENS);
        }
        */

        // Initialize Facebook Login button
        mFacebookCallbackManager = CallbackManager.Factory.create();



        //use customizing facebook login button
        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "user_birthday"));

        LoginManager.getInstance().registerCallback(mFacebookCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Toast.makeText(getApplicationContext(), R.string.login_success, Toast.LENGTH_SHORT).show();

                //onCompete를 진행하는 과정 '중간'에서 onAuthStateChanged 리스너가 권한이 바뀌었음을 알고 goMain을 호출해버림
                //그래서 boolean값으로 순서를 정해줌
                isLoginBtnPressed = true;
                //goMain();
                Log.d("***getAccessToken()", String.valueOf(loginResult.getAccessToken()));
                Log.d("***.getToken()", String.valueOf(loginResult.getAccessToken().getToken()));
                Log.d("***.getUserId()", String.valueOf(loginResult.getAccessToken().getUserId()));
                Log.d("***.getPermissions()", String.valueOf(loginResult.getAccessToken().getPermissions()));
                mFacebookId = loginResult.getAccessToken().getUserId();


                GraphRequest request = GraphRequest.newMeRequest(
                        AccessToken.getCurrentAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(
                                    JSONObject object,
                                    GraphResponse response) {
                                // Application code
                                try {
                                    Log.d("^^^^^USER INFORMATION1", String.valueOf(object.getInt("id")));
                                    Log.d("^^^^^USER INFORMATION2", String.valueOf(object.getString("name")));
                                    Log.d("^^^^^USER INFORMATION3", String.valueOf(object.getString("birthday")));
                                    Log.d("^^^^^USER INFORMATION4", String.valueOf(object.getString("gender")));
                                    result = object;
                                    readUsers() ;
                                }
                                catch (JSONException e){
                                }
                            }
                        });
                Bundle parameters = new Bundle();
                parameters.putString("fields","id,name,birthday,gender");
                request.setParameters(parameters);
                request.executeAsync();

                //handleFacebookAccessToken(loginResult.getAccessToken());

            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(), R.string.login_cancel, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(), R.string.login_error, Toast.LENGTH_SHORT).show();
                Log.d("signin", error.toString());
                if (error instanceof FacebookAuthorizationException) {
                    if (AccessToken.getCurrentAccessToken() != null) {
                        LoginManager.getInstance().logOut();
                    }
                }
            }
        });

    }
/*
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
    }
*/

    protected void onActivityResult (int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        mFacebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        /*
        Log.v("****현조", "bye");
        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {  //수입내역 삭제
                    Log.d("수입삭제","수입삭제");
                    user.setMainBank(data.getStringExtra("mainBank"));
                }



                break;
        }
        */
    }

    public void saveData() {

        Gson gson = new Gson();
        FacebookUser facebookUser = gson.fromJson(result.toString(), FacebookUser.class);
        Log.d("****FACEBOOK USER!!!", facebookUser.toString());

        int birth = 0;

        //facebookUser = gson.fromJson(result.toString(), FacebookUser.class);
        // set values
        user.setUserId(mFacebookId);
        user.setName(facebookUser.getName());

        if (facebookUser.getBirthday() != null) {

            itr = new StringTokenizer(facebookUser.getBirthday(), "/");
            while (itr.hasMoreTokens()) {
                birth = Integer.parseInt(itr.nextToken());
            }
            user.setBirth(birth);
        } else {
            user.setBirth(birth);
        }

        if (facebookUser.getGender() != null) {
            user.setGender(facebookUser.getGender());
        } else {
            user.setGender("none");
        }

        user.setProfilePicture("none");
        user.setMainBank("none");
        /*
        Intent intent = new Intent(getApplicationContext(), UserInfoActivity.class);
        Log.v("****이주하", "hihi");
        startActivityForResult(intent,0);
        Log.v("****이주하", "bye");
*/
        user.setOnAutoParse(false);
        user.setOnAutoAlarm(false);
        Log.d("User:::::", user.toString());

        // 이제 이 User Class를 서버로 보내서 DB에 저장해야 함.
        isSignIn = true;
        sendObject();


        //최초 로그인 시 SignActivity->MainActivity
        goMain();

    }


    public void readUsers() {
        // SQL Database로부터 이미 연동이 되어 있는 User인지 확인.
        sendObject();

        if(isInitial){ // 동일한 아이디가 없어
            saveData();
        } else{ // 동일한 아이디가 있어
            goMain();
        }

    }

    public void goMain(){
        Intent mainIntent = new Intent (SignInActivity.this, MainActivity.class);
        startActivity(mainIntent);
        finish();
    }

    private boolean setCustomActionBar(){
        ActionBar actionBar = getSupportActionBar();
        //기존 액션바 숨기기
        actionBar.hide();

        return true;
    }

    // GET User
    private void sendObject(){
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        SignInActivity.SaveNewShow request = new SignInActivity.SaveNewShow();
        request.run();
    }

    private class SaveNewShow extends Thread
    {
        @Override
        public void run() {
            //create network connecting thread
            String resultStr;
            if (!isSignIn) {
                // // SQL Database로부터 이미 연동이 되어 있는 User인지 확인.
                resultStr = getUser();

                if(!resultStr.equals("[]"))  { //동일한 아이디가 존재하면
                    Log.v("Main::bring success", "이미 우리 회원이야.");
                    isInitial = false;
                }else{ //동일한 아이디가 없으면
                    Log.v("Main::bring success", "새로운 회원이야. 회원가입 시켜.");
                    isInitial = true;
                }

            } else {
                postUser();
            }
        }
    }

    public String getUser() {
        String msg = MainActivity.urlString + "/get/userinfo";

        InputStream inputStream = null;
        BufferedReader rd = null;
        StringBuilder getResult = new StringBuilder();

        StringBuilder requestUrl = new StringBuilder(msg);

        List<NameValuePair> nvps = new ArrayList<NameValuePair>();
        nvps.add(new BasicNameValuePair("uid", mFacebookId));
        String querystring = URLEncodedUtils.format(nvps, "utf-8");

        requestUrl.append("?");
        requestUrl.append(querystring);

        HttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(requestUrl.toString());
        Log.d("msg is :", requestUrl.toString());

        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);

            Log.v("******server", "send msg successed");

            inputStream = httpResponse.getEntity().getContent();
            rd = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = rd.readLine()) != null) {
                getResult.append(line);
            }
            Log.v("Main::bring success", "getResult:" + getResult.toString());
            //showDealingList(getResult.toString());

        } catch (IOException e) {
            e.printStackTrace();
            Log.v("******server", "send msg failed");
        }

        if (getResult != null) {
            return getResult.toString();
        } else {
            Log.v("******server", "getResult is null");
            return null;
        }
    }

    public String postUser() {
        String msg = MainActivity.urlString + "/post/signin";

        InputStream inputStream = null;
        BufferedReader rd = null;
        StringBuilder getResult = new StringBuilder();

        StringBuilder requestUrl = new StringBuilder(msg);

        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(requestUrl.toString());
        Log.d("msg is :", requestUrl.toString());

        try {
            Gson gson = new Gson();
            //JSONObject json = new JSONObject();
            String json="";
            json = gson.toJson(user);

            // loglog
            Log.v("^^^^^json", json);

            StringEntity stringEntity=new StringEntity(json, "utf-8");
            httpPost.setEntity(stringEntity);

            HttpResponse httpResponse = httpClient.execute(httpPost);
            Log.v("******server", "send msg successed");

            inputStream = httpResponse.getEntity().getContent();
            rd = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = rd.readLine()) != null) {
                getResult.append(line);
            }
            Log.v("Main::bring success", "getResult:" + getResult.toString());

        } catch (IOException e) {
            e.printStackTrace();
            Log.v("******server", "send msg failed");
        }

        if (getResult != null) {
            return getResult.toString();
        } else {
            Log.v("******server", "getResult is null");
            return null;
        }

    }

}