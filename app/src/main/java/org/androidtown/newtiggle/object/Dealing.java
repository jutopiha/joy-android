package org.androidtown.newtiggle.object;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by 이주하 on 2017-06-19.
 */

public class Dealing implements Serializable {
    String mOriginalJson;

    String mUserId;
    int mDate;
    int mTime;
    int mMoney;
    String mMemo;
    String mCategory;

    public String getmOriginalJson() {
        return mOriginalJson;
    }

    public void setmOriginalJson(String mOriginalJson) {
        this.mOriginalJson = mOriginalJson;
    }




    public String getmUserId() {
        return mUserId;
    }

    public void setmUserId(String mUserId) {
        this.mUserId = mUserId;
    }

    public int getmDate() {
        return mDate;
    }

    public void setmDate(int mDate) {
        this.mDate = mDate;
    }

    public int getmTime() {
        return mTime;
    }

    public void setmTime(int mTime) {
        this.mTime = mTime;
    }

    public int getmMoney() {
        return mMoney;
    }

    public void setmMoney(int mMoney) {
        this.mMoney = mMoney;
    }

    public String getmMemo() {
        return mMemo;
    }

    public void setmMemo(String mMemo) {
        this.mMemo = mMemo;
    }

    public String getmCategory() {
        return mCategory;
    }

    public void setmCategory(String mCategory) {
        this.mCategory = mCategory;
    }

}
