package ru.perm.trubnikov.gps2sms;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class AnotherMsgActivity extends BaseActivity {

    String phoneNumber;
    // ------------------------------------------------------------------------------------------
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Определение темы должно быть ДО super.onCreate и setContentView
        //setTheme(DBHelper.determineTheme(this));

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_another_msg);

        ShowBackButton();

        Button btn = (Button) findViewById(R.id.button1);
        btn.requestFocus();
        btn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        String sent_sms_text = getIntent().getStringExtra("SENT_SMS_TEXT");
        phoneNumber = getIntent().getStringExtra("PHONE");

        Button btn2 = (Button) findViewById(R.id.button2);
        btn2.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                sendIntent.setData(Uri.parse("sms:" + phoneNumber));
                startActivity(sendIntent);
            }
        });

        TextView st = (TextView) findViewById(R.id.textViewTxt);
        st.setText(sent_sms_text);

    }

    // ------------------------------------------------------------------------------------------

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void ShowBackButton() {
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }


}
