package com.realmattersid;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FakeReportActivity extends AppCompatActivity implements View.OnClickListener  {
    private static final String URL_SERVER_FORM = "http://control.zodapos.com/core/api/report.class.php?action=report";
    private FirebaseAuth mAuth;
    private static EditText edit_naddress, edit_ncity, edit_nclinic,edit_nreason;
    private static Button btn_sendfake, btn_fakeCancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_fake_report);
        mAuth = FirebaseAuth.getInstance();
        edit_nclinic = (EditText) findViewById(R.id.edit_nclinic);
        edit_naddress = (EditText) findViewById(R.id.edit_naddress);
        edit_ncity = (EditText) findViewById(R.id.edit_ncity);
        edit_nreason = (EditText) findViewById(R.id.edit_nreason);
        btn_sendfake = (Button) findViewById(R.id.btn_sendfake);
        btn_fakeCancel = (Button) findViewById(R.id.btn_fakeCancel);

        setListeners();
    }

    private void setListeners() {
        btn_sendfake.setOnClickListener(this);
        btn_fakeCancel.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_sendfake:
                checkValidation();
                break;
            case R.id.btn_fakeCancel:
                cancelReport();
                break;
        }
    }

    private void cancelReport() {
        startActivity(new Intent(this, MasterActivity.class));
        finish();
    }

    private void checkValidation() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String personEmail = currentUser.getEmail();
        final String getNclinic = edit_nclinic.getText().toString();
        final String getAddress = edit_naddress.getText().toString();
        final String getCity = edit_ncity.getText().toString();
        final String getReason = edit_nreason.getText().toString();

        if (getNclinic.equals("") || getNclinic.length() == 0
                || getAddress.equals("") || getAddress.length() == 0
                || getCity.equals("") || getCity.length() == 0
                || getReason.equals("") || getReason.length() == 0){
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
        } else {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, "http://control.zodapos.com/core/api/report.class.php?action=report&email="+personEmail, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("Response", response);
                }
            }, new Response.ErrorListener(){
                @Override
                public void onErrorResponse(VolleyError error) {

                }
            }
            ){
                @Override
                protected Map<String, String> getParams(){
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("nclinic", getNclinic);
                    params.put("address",getAddress);
                    params.put("ncity",getCity);
                    params.put("nreason",getReason);
                    return params;
                }
            };
            queue.add(postRequest);
            Toast.makeText(this, "Send report success", Toast.LENGTH_SHORT).show();
            Intent returnIntent = new Intent(this, MasterActivity.class);
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }

    }
}