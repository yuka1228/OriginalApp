package jp.techacademy.yuka.satou.originalapp;

import android.app.Application;

import io.realm.Realm;

public class ReportApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(this);
    }
}