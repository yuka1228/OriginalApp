package jp.techacademy.yuka.satou.originalapp;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmResults;
import io.realm.Sort;

public class MainActivity extends AppCompatActivity {
    public final static String EXTRA_REPORT = "jp.techacademy.yuka.satou.originalapp";

    private Realm mRealm;
    private RealmChangeListener mRealmListener = new RealmChangeListener() {
        @Override
        public void onChange(Object element) {
            reloadListView();
        }
    };
    private ListView mListView;
    private ReportAdapter mReportAdapter;
    private Report mReport;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            Intent intent = new Intent(getApplicationContext(), SettingActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button startButton = (Button) findViewById(R.id.startButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReport();
            }
        });

        Button endButton = (Button) findViewById(R.id.endButton);
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReport2();
            }
        });

        Button offButton = (Button) findViewById(R.id.offButton);
        offButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addReport3();
            }
        });

        setTitle("出退勤管理");

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String contents = "#年月日, 出社, 退社¥n";
//                for (Report report: ReportAdapter.) {
//                    contents += report.getDate() + "," + report.getStart() + "," + report.getEnd() + "¥n";
//                }

                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_SENDTO);

                intent.setType("text/plain");
                intent.setData(Uri.parse("mailto:" + "sato-y@zenrin-tokai.co.jp"));
                intent.putExtra(Intent.EXTRA_SUBJECT, "出退勤時間");
                intent.putExtra(Intent.EXTRA_TEXT, contents);
                startActivity(Intent.createChooser(intent, null));

            }
        });

        //Realmの設定
        mRealm = Realm.getDefaultInstance();
        mRealm.addChangeListener(mRealmListener);

        // ListViewの設定
        mReportAdapter = new ReportAdapter(MainActivity.this);
        mListView = (ListView) findViewById(R.id.listView1);

        // ListViewをタップしたときの処理
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // 入力・編集する画面に遷移させる
                Report report = (Report) parent.getAdapter().getItem(position);

                Intent intent = new Intent(MainActivity.this, InputActivity.class);
                intent.putExtra(EXTRA_REPORT, report.getId());

                startActivity(intent);
            }
        });

        // ListViewを長押ししたときの処理
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                // タスクを削除する
                final Report report = (Report) parent.getAdapter().getItem(position);

                // ダイアログを表示する
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

                builder.setTitle("削除");
                builder.setMessage("削除しますか");
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        RealmResults<Report> results = mRealm.where(Report.class).equalTo("id", report.getId()).findAll();

                        mRealm.beginTransaction();
                        results.deleteAllFromRealm();
                        mRealm.commitTransaction();

                        reloadListView();
                    }
                });
                builder.setNegativeButton("CANCEL", null);

                AlertDialog dialog = builder.create();
                dialog.show();

                return true;
            }
        });

        reloadListView();
    }

    private void reloadListView() {

        // Realmデータベースから、「全てのデータを取得して新しい日時順に並べた結果」を取得
        RealmResults<Report> reportRealmResults = mRealm.where(Report.class).findAll().sort("date", Sort.DESCENDING);
        // 上記の結果を、ReportList としてセットする
        mReportAdapter.setReportList(mRealm.copyFromRealm(reportRealmResults));
        // ReportのListView用のアダプタに渡す
        mListView.setAdapter(mReportAdapter);
        // 表示を更新するために、アダプターにデータが変更されたことを知らせる
        mReportAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mRealm.close();
    }

    private void addReport() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Report> reportRealmResults = realm.where(Report.class).findAll();
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        String todaystr = sdf.format(today);
        mReport = null;
        for (Report report:reportRealmResults) {
            String reportstr = sdf.format(report.getDate());
            if (reportstr.equals(todaystr)) {
                mReport = report;
            }
        }

        if (mReport == null) {
            // 新規作成の場合
            mReport = new Report();


            int identifier;
            if (reportRealmResults.max("id") != null) {
                identifier = reportRealmResults.max("id").intValue() + 1;
            } else {
                identifier = 0;
            }
            mReport.setId(identifier);
            mReport.setDate(new Date());
        }

        mReport.setStart(sdf2.format(today));
        if (mReport.getEnd() == null) {
            mReport.setEnd("--:--");
        }

        realm.copyToRealmOrUpdate(mReport);
        realm.commitTransaction();

        realm.close();

    }

    private void addReport2() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Report> reportRealmResults = realm.where(Report.class).findAll();
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        String todaystr = sdf.format(today);
        mReport = null;
        for (Report report:reportRealmResults) {
            String reportstr = sdf.format(report.getDate());
            if (reportstr.equals(todaystr)) {
                mReport = report;
            }
        }

        if (mReport == null) {
            // 新規作成の場合
            mReport = new Report();


            int identifier;
            if (reportRealmResults.max("id") != null) {
                identifier = reportRealmResults.max("id").intValue() + 1;
            } else {
                identifier = 0;
            }
            mReport.setId(identifier);
            mReport.setDate(new Date());
        }

        mReport.setEnd(sdf2.format(today));
        if (mReport.getStart() == null) {
            mReport.setStart("--:--");
        }

        realm.copyToRealmOrUpdate(mReport);
        realm.commitTransaction();

        realm.close();

    }

    private void addReport3() {
        Realm realm = Realm.getDefaultInstance();
        realm.beginTransaction();
        RealmResults<Report> reportRealmResults = realm.where(Report.class).findAll();
        Date today = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd");
        //SimpleDateFormat sdf2 = new SimpleDateFormat("HH:mm");
        String todaystr = sdf.format(today);
        mReport = null;
        for (Report report:reportRealmResults) {
            String reportstr = sdf.format(report.getDate());
            if (reportstr.equals(todaystr)) {
                mReport = report;
            }
        }

        if (mReport == null) {
            // 新規作成の場合
            mReport = new Report();

            int identifier;
            if (reportRealmResults.max("id") != null) {
                identifier = reportRealmResults.max("id").intValue() + 1;
            } else {
                identifier = 0;
            }
            mReport.setId(identifier);
            mReport.setDate(new Date());
        }

        mReport.setStart("--:--");
        mReport.setEnd("--:--");

        realm.copyToRealmOrUpdate(mReport);
        realm.commitTransaction();

        realm.close();

    }


}
