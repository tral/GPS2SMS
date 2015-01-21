package ru.perm.trubnikov.gps2sms;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class BaseActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Определение темы должно быть ДО super.onCreate и setContentView
        setTheme(DBHelper.determineTheme(this));

        super.onCreate(savedInstanceState);

    }
}