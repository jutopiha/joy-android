package org.androidtown.newtiggle.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Spinner;

import org.androidtown.newtiggle.R;

/**
 * Created by CE-L-05 on 2017-06-19.
 */

public class UserInfoActivity extends AppCompatActivity implements View.OnClickListener {

    private Spinner userBank;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_info);

    }

    @Override
    public void onClick(View v) {

        switch(v.getId())
        {
            case R.id.sendUserInfoBtn:
                Intent intent = new Intent();
                intent.putExtra("mainBank", userBank.getSelectedItem().toString());
                setResult(RESULT_OK,intent);
                finish();
                break;
        }

    }

    private void bindView(){
        userBank = (Spinner)findViewById(R.id.userBankSp);
    }




}
