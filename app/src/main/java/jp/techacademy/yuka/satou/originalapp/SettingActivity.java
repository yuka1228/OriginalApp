package jp.techacademy.yuka.satou.originalapp;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener {

    EditText mEditText;
    private final String mailaddress = "mailaddress";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        setTitle("設定");

        Button changeButton = (Button) findViewById(R.id.changeButton);
        changeButton.setOnClickListener(this);

        mEditText = (EditText) findViewById(R.id.editText);
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        String mail = sp.getString(mailaddress, "");
        mEditText.setText(mail);
    }

    @Override
    public void onClick(View v) {
        String mail = mEditText.getText().toString();

        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(mailaddress, mail);
        editor.commit();
        finish();
    }
}
