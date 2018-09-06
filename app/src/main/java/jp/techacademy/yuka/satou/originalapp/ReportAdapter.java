package jp.techacademy.yuka.satou.originalapp;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ReportAdapter extends BaseAdapter {
    private LayoutInflater mLayoutInflater = null;
    private List<Report> mReportList;

    public ReportAdapter(Context context) {
        mLayoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void setReportList(List<Report> reportList) {
        mReportList = reportList;
    }

    @Override
    public int getCount() {
        return mReportList.size();
    }

    @Override
    public Object getItem(int position) {
        return mReportList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return mReportList.get(position).getId();
    }



    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = mLayoutInflater.inflate(android.R.layout.simple_list_item_2, null);
        }

        TextView textView1 = (TextView) convertView.findViewById(android.R.id.text1);
        TextView textView2 = (TextView) convertView.findViewById(android.R.id.text2);

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.JAPANESE);
        Date date = mReportList.get(position).getDate();
        textView1.setText(simpleDateFormat.format(date));

        textView2.setText(mReportList.get(position).getStart()+ "　〜　" + mReportList.get(position).getEnd());

        return convertView;
    }
}