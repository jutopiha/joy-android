package org.androidtown.newtiggle.object;

import org.json.JSONObject;

/**
 * Created by 조현정 on 2017-07-25.
 */

public class MonthlyStat extends Statistics {

    private JSONObject cateExpense;
    private int monthExpense;

    public JSONObject getCateExpense() {
        return cateExpense;
    }

    public void setCateExpense(JSONObject cateExpense) {
        this.cateExpense = cateExpense;
    }

    public int getMonthExpense() {
        return monthExpense;
    }

    public void setMonthExpense(int monthExpense) {
        this.monthExpense = monthExpense;
    }




}
