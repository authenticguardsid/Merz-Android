package com.realmattersid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class FakeCodeActivity extends AppCompatActivity implements View.OnClickListener {
    private static Button buttonReport;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fake_code);
        buttonReport = (Button) findViewById(R.id.buttonReport);
        Toast.makeText(getApplicationContext(), "Code :  " + getIntent().getStringExtra("code"), Toast.LENGTH_SHORT).show();
        setListeners();
    }

    private void setListeners() {
        buttonReport.setOnClickListener(this);
    }

    public void reportFake() {
        final Intent ToMasterActivity = new Intent(this, FakeReportActivity.class);
        startActivity(ToMasterActivity);
        finish();
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonReport:
                reportFake();
                break;
        }
    }
}