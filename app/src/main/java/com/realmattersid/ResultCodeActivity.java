package com.realmattersid;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.realmattersid.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResultCodeActivity extends AppCompatActivity implements View.OnClickListener {
    private static int TIME_OUT = 2000;
    private static final String URL_SERVER_REG = "http://control.zodapos.com/core/api/account_data.class.php?action=addclaim";
    public String PARTNER = "";
    public String GCODE = "";
    private static TextView result_code,result_name, result_address, result_phone,result_web;
    private static Button buttonReport;
    View progressOverlay;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_code);
        mAuth = FirebaseAuth.getInstance();
        progressOverlay = findViewById(R.id.progress_overlay);
        result_code = (TextView) findViewById(R.id.dtext2content);
        result_name = (TextView) findViewById(R.id.dtext3content);
        result_address = (TextView) findViewById(R.id.dtext4content);
        result_phone = (TextView) findViewById(R.id.dtext5content);
        result_web = (TextView) findViewById(R.id.dtext6content);
        buttonReport = (Button) findViewById(R.id.buttonReport);
        set_profile_company(getIntent().getStringExtra("key"));
        setListeners();
    }

    public void set_profile_company(String gcode){
        JsonArrayRequest arrayRequest = new JsonArrayRequest("http://control.zodapos.com/core/api/scanner.class.php?action=valid&code="+gcode, new com.android.volley.Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject data = response.getJSONObject(i);
                            GCODE = data.getString("gcode");
                            PARTNER = data.getString("partner");
                            result_code.setText(data.getString("gcode"));
                            result_name.setText(data.getString("title"));
                            result_address.setText(data.getString("address"));
                            result_phone.setText(data.getString("phone"));
                            result_web.setText(data.getString("website"));
                        } catch (JSONException e) {

                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });
        Volley.newRequestQueue(this).add(arrayRequest);
    }

    private void setListeners() {
        buttonReport.setOnClickListener(this);
    }

    public void calimRewards() {
        final Utils utils = new Utils(this);
        utils.animateView(progressOverlay, View.VISIBLE, 0.4f, 200);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String personEmail = currentUser.getEmail();
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
                params.put("email", personEmail);
                params.put("partner", PARTNER);
                params.put("code",GCODE);
                return params;
            }
        };
        queue.add(postRequest);
        Toast.makeText(this, "Your Claim is Saved", Toast.LENGTH_SHORT).show();
        final Intent MasterActivityDone = new Intent(this, MasterActivity.class);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                utils.animateView(progressOverlay, View.GONE, 0, 200);
                startActivity(MasterActivityDone);
                finish();
            }
        }, TIME_OUT);
    }

    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.buttonReport:
                calimRewards();
                break;
        }
    }
}
