package com.realmattersid;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.realmattersid.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SForgotActivity extends AppCompatActivity implements View.OnClickListener {
    private static final String URL_SERVER_REG = "http://control.zodapos.com/core/api/signin.class.php?action=forgot";
    private static EditText emailId;
    private static TextView submit, back;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_forgot);
        emailId = (EditText) findViewById(R.id.registered_emailid);
        submit = (TextView) findViewById(R.id.forgot_button);
        back = (TextView) findViewById(R.id.backToLoginBtn);

        XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            back.setTextColor(csl);
            submit.setTextColor(csl);

        } catch (Exception e) {
        }
        setListeners();
    }

    // Set Listeners over buttons
    private void setListeners() {
        back.setOnClickListener(this);
        submit.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backToLoginBtn:
                signinScreen();
                break;

            case R.id.forgot_button:
                submitButtonTask();
                break;

        }
    }

    private void signinScreen() {
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }

    private void submitButtonTask() {
        displayLoader();
        final Utils utils = new Utils(this);
        final String getEmailId = emailId.getText().toString();
        Pattern p = Pattern.compile(Config.regEx);
        Matcher m = p.matcher(getEmailId);
        if (getEmailId.equals("") || getEmailId.length() == 0){
            Toast.makeText(this, "Please enter your Email Id.", Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
        } else if (!m.find()){
            Toast.makeText(this, "Your Email Id is Invalid.", Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
        } else {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, URL_SERVER_REG, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    pDialog.dismiss();
                    if (response.equals("successdata")) {
                        Toast.makeText(SForgotActivity.this, "Your password has sent to your email address.",Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SForgotActivity.this, SignInActivity.class));
                        finish();
                    } else {
                        Toast.makeText(SForgotActivity.this, "Your email is not registered.",Toast.LENGTH_SHORT).show();
                    }
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
                    params.put("email",getEmailId);
                    return params;
                }
            };
            queue.add(postRequest);
        }
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Saviing Account...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }
}
