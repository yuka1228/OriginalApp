package jp.techacademy.yuka.satou.originalapp;

import java.io.Serializable;
import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class Report extends RealmObject implements Serializable {
    private String start; //出勤時刻
    private String end; //退勤時刻
    private Date date; //日時

    // id をプライマリーキーとして設定
    @PrimaryKey
    private int id;

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
