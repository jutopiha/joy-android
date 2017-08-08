package org.androidtown.newtiggle.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import org.androidtown.newtiggle.R;
import org.androidtown.newtiggle.object.Income;

import java.util.ArrayList;

import static org.androidtown.newtiggle.R.layout.income_list_item;

/**
 * Created by 조현정 on 2017-06-16.
 */

public class IncomeListViewAdapter extends BaseAdapter {

    //getResources 메소드를 사용하기 위한 변수 선언
    //private Context context;

    //Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<Income> incomeList = new ArrayList<Income>();
    //private Drawable iconDrwable = contextCompat.getDrawa

    //ExpenseListViewAdapter의 생성자
    public IncomeListViewAdapter() {


    }

    //Adapter에 사용되는 데이터의 개수를 리턴.
    @Override
    public int getCount() {
        return incomeList.size();
    }

    /*
    public Drawable getIcon(String category) {

    }*/

    //position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        //layout을 inflate라여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(income_list_item, parent, false);
        }

        //화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        //ImageView iconImageView = (ImageView)convertView.findViewById(R.id.incomeCategoryImage);
        TextView moneyTextView = (TextView) convertView.findViewById(R.id.textIncomeMoney);
        TextView cateAndMemoTextView = (TextView) convertView.findViewById(R.id.textIncomeCateAndMemo);

        //Date Set(incomeList_item)에서 position에 위치한 데이터 참조 획득
        Income income = incomeList.get(position);

        //아이템 내 각 위젯에 데이터 변경
        //iconImageView.setImageDrawable(income.getIcon());
        moneyTextView.setText(String.valueOf(income.getmMoney()));
        cateAndMemoTextView.setText("["+income.getmCategory()+"]"+"   "+income.getmMemo());

        return convertView;
    }

    //지정한 위치(position)에 있는 데이터와 관계된 아이템의 ID를 리턴
    @Override
    public long getItemId(int position) {
        return position;
    }

    //지정한 위치(position)에 있는 데이터 리턴
    @Override
    public Object getItem(int position) {
        return incomeList.get(position);
    }

    //아이템 데이터 추가를 위한 함수, Array통째로 넘겨준다.
    public void addItem(ArrayList<Income> addIncome) {
        incomeList = addIncome;
    }

}
