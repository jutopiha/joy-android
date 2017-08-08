package org.androidtown.newtiggle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.facebook.AccessToken;

import org.androidtown.newtiggle.R;

public class MainActivity extends AppCompatActivity {

    public static final String urlString = "http://18.220.36.184:9000";
    public static String currentUserId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        if(AccessToken.getCurrentAccessToken() == null) {
            // 로그인이 되어 있지 않은 경우, SignInActivity로 간다
            startActivity(new Intent(this, SignInActivity.class));
            finish();
            return;
        }
        else {
            currentUserId = AccessToken.getCurrentAccessToken().getUserId();
        }


    }


    public void incomeButton(View v) {
        Intent intent = new Intent(getApplicationContext(), IncomeActivity.class);
        startActivity(intent);
    }

    public void expenseButton(View v){
        Intent intent = new Intent(getApplicationContext(), ExpenseActivity.class);
        startActivity(intent);
    }

    public void showButton(View v){
        Intent intent = new Intent(getApplicationContext(), ShowActivity.class);
        startActivity(intent);
    }

    public void staticsButton(View v){
        Intent intent = new Intent(getApplicationContext(), StatisticsActivity.class);
        startActivity(intent);
    }

    public void exitButton(View v){
        //LoginManager.getInstance().logOut();
        //currentUserId = "guest";
        Intent intent = new Intent(getApplicationContext(), SignInActivity.class);
        startActivity(intent);
        finish();
    }
}

