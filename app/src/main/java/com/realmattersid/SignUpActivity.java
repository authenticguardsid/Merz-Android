package com.realmattersid;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.realmattersid.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener  {
    private static final String URL_SERVER_REG = "http://control.zodapos.com/core/api/signup.class.php?action=registerform";
    private static EditText fullName, emailId, mobileNumber, password, confirmPassword;
    private static TextView login;
    private static Button signUpButton;
    private static CheckBox terms_conditions;
    private FirebaseAuth auth;
    private ProgressDialog pDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_signup);
        auth = FirebaseAuth.getInstance();
        fullName = (EditText) findViewById(R.id.signup_fullname);
        emailId = (EditText) findViewById(R.id.signup_email);
        mobileNumber = (EditText) findViewById(R.id.signup_phone);
        password = (EditText) findViewById(R.id.signup_pwd);
        confirmPassword = (EditText) findViewById(R.id.signup_cpwd);
        signUpButton = (Button) findViewById(R.id.signUpBtn);
        login = (TextView) findViewById(R.id.already_user);
        terms_conditions = (CheckBox) findViewById(R.id.terms_conditions);

        XmlResourceParser xrp = getResources().getXml(R.drawable.text_selector);
        try {
            ColorStateList csl = ColorStateList.createFromXml(getResources(),
                    xrp);

            login.setTextColor(csl);
            terms_conditions.setTextColor(csl);
        } catch (Exception e) {
        }
        setListeners();
    }

    private void setListeners() {
        signUpButton.setOnClickListener(this);
        login.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signUpBtn:
                checkValidation();
                break;
            case R.id.already_user:
                signipScreen();
                break;
        }
    }

    private void signipScreen() {
        startActivity(new Intent(this, SignInActivity.class));
        finish();
    }

    private void checkValidation() {
        displayLoader();
        final Utils utils = new Utils(this);
        final String getFullName = fullName.getText().toString();
        final String getEmailId = emailId.getText().toString();
        final String getMobileNumber = mobileNumber.getText().toString();
        final String getPassword = password.getText().toString();
        String getConfirmPassword = confirmPassword.getText().toString();


        Pattern p = Pattern.compile(Config.regEx);
        Matcher m = p.matcher(getEmailId);

        if (getFullName.equals("") || getFullName.length() == 0
                || getEmailId.equals("") || getEmailId.length() == 0
                || getMobileNumber.equals("") || getMobileNumber.length() == 0
                || getPassword.equals("") || getPassword.length() == 0
                || getConfirmPassword.equals("")
                || getConfirmPassword.length() == 0) {
            Toast.makeText(this, "All fields are required.", Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
        }
        else if (!m.find()){
            Toast.makeText(this, "Your Email Id is Invalid.", Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
        }
        else if (!getConfirmPassword.equals(getPassword)) {
            Toast.makeText(this, "Both password doesn't match.", Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
        }
        else if (!terms_conditions.isChecked()){
            Toast.makeText(this, "Please select Terms and Conditions.", Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
        } else {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, URL_SERVER_REG, new Response.Listener<String>() {
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
                    params.put("fname", getFullName);
                    params.put("phone",getMobileNumber);
                    params.put("email",getEmailId);
                    params.put("password",getPassword);
                    return params;
                }
            };
            queue.add(postRequest);
            auth.createUserWithEmailAndPassword(getEmailId, getPassword)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pDialog.dismiss();
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignUpActivity.this, "SignUP Failed.",Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(SignUpActivity.this, "SignUP Success.",Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                                finish();
                            }
                        }
                    });
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
