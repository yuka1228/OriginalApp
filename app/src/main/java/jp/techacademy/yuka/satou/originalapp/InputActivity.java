package jp.techacademy.yuka.satou.originalapp;

import android.app.TimePickerDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;

import java.util.Calendar;

import io.realm.Realm;
import io.realm.RealmResults;

public class InputActivity extends AppCompatActivity {

    private int mStartHour, mStartMinute, mEndHour, mEndMinute;
    private Button mStartButton, mEndButton;
    private Report mReport;

    private View.OnClickListener mOnStartClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(InputActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            mStartHour = hourOfDay;
                            mStartMinute = minute;
                            String timeString = String.format("%02d", mStartHour) + ":" + String.format("%02d", mStartMinute);
                            mStartButton.setText(timeString);
                        }
                    }, mStartHour, mStartMinute, false);
            timePickerDialog.show();
        }
    };

    private View.OnClickListener mOnEndClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            TimePickerDialog timePickerDialog = new TimePickerDialog(InputActivity.this,
                    new TimePickerDialog.OnTimeSetListener() {
                        @Override
                        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                            mEndHour = hourOfDay;
                            mEndMinute = minute;
                            String timeString = String.format("%02d", mEndHour) + ":" + String.format("%02d", mEndMinute);
                            mEndButton.setText(timeString);
                        }
                    }, mEndHour, mEndMinute, false);
            timePickerDialog.show();
        }
    };

    private View.OnClickListener mOnDoneClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            addReport();
            finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);

        // ActionBarを設定する
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // UI部品の設定
        mStartButton = (Button)findViewById(R.id.start_button);
        mStartButton.setOnClickListener(mOnStartClickListener);
        mEndButton = (Button)findViewById(R.id.end_button);
        mEndButton.setOnClickListener(mOnEndClickListener);
        findViewById(R.id.done_button).setOnClickListener(mOnDoneClickListener);

        // EXTRA_REPORT から Report の id を取得して、 id から Report のインスタンスを取得する
        Intent intent = getIntent();
        int reportId = intent.getIntExtra(MainActivity.EXTRA_REPORT, -1);
        Realm realm = Realm.getDefaultInstance();
        mReport = realm.where(Report.class).equalTo("id", reportId).findFirst();
        realm.close();

        Calendar calendar = Calendar.getInstance();
        if (mReport == null) {
            // 新規作成の場合
            mStartHour = calendar.get(Calendar.HOUR_OF_DAY);
            mStartMinute = calendar.get(Calendar.MINUTE);
            mEndHour = calendar.get(Calendar.HOUR_OF_DAY);
            mEndMinute = calendar.get(Calendar.MINUTE);
        } else {
            // 更新の場合
            String startString = mReport.getStart();
            String endString = mReport.getEnd();
            if (mReport.getStart() == null || mReport.getStart().equals("--:--")) {
                mStartHour = calendar.get(Calendar.HOUR_OF_DAY);
                mStartMinute = calendar.get(Calendar.MINUTE);
                startString = "--:--";
            } else {
                startString = mReport.getStart();
                String[] array = startString.split(":");
                mStartHour = Integer.parseInt(array[0]);
                mStartMinute = Integer.parseInt(array[1]);
            }
            if (mReport.getEnd() == null || mReport.getEnd().equals("--:--")) {
                mEndHour = calendar.get(Calendar.HOUR_OF_DAY);
                mEndMinute = calendar.get(Calendar.MINUTE);
                endString = "--:--";
            } else {
                endString = mReport.getEnd();
                String[] array = endString.split(":");
                mEndHour = Integer.parseInt(array[0]);
                mEndMinute = Integer.parseInt(array[1]);
            }
            mStartButton.setText(startString);
            mEndButton.setText(endString);
        }

    }

    private void addReport() {
        Realm realm = Realm.getDefaultInstance();

        realm.beginTransaction();

        mReport.setStart(mStartButton.getText().toString());
        mReport.setEnd(mEndButton.getText().toString());

        realm.copyToRealmOrUpdate(mReport);
        realm.commitTransaction();

        realm.close();
    }
}
