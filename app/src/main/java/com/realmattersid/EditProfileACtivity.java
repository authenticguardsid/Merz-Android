package com.realmattersid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditProfileACtivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static EditText edt_fullname, edt_email, edt_phone;
    private static Button btnSave;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        mAuth = FirebaseAuth.getInstance();
        edt_fullname = (EditText) findViewById(R.id.edt_fullname);
        edt_email = (EditText) findViewById(R.id.edt_email);
        edt_phone = (EditText) findViewById(R.id.edt_phone);
        btnSave = (Button) findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });
        set_profile_form();
    }

    public void set_profile_form(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String personEmail = currentUser.getEmail();
        JsonArrayRequest arrayRequest = new JsonArrayRequest("http://control.zodapos.com/core/api/account_data.class.php?action=profile_view&email="+personEmail, new com.android.volley.Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject data = response.getJSONObject(i);
                            edt_fullname.setText(data.getString("fname"));
                            edt_email.setText(data.getString("email"));
                            edt_phone.setText(data.getString("phone"));
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

    private void checkValidation() {
        final String getFullname = edt_fullname.getText().toString();
        final String getEmailId = edt_email.getText().toString();
        final String getPhone = edt_phone.getText().toString();
        Pattern p = Pattern.compile(Config.regEx);
        Matcher m = p.matcher(getEmailId);
        if (getEmailId.equals("") || getEmailId.length() == 0 || getFullname.equals("") || getFullname.length() == 0
                || getPhone.equals("") || getPhone.length() == 0) {
            Toast.makeText(this, "Please input.", Toast.LENGTH_SHORT).show();
        }
        else if (!m.find()) {
            Toast.makeText(this, "Your Email Id is Invalid.", Toast.LENGTH_SHORT).show();
        } else {
            RequestQueue queue = Volley.newRequestQueue(this);
            StringRequest postRequest = new StringRequest(Request.Method.POST, "http://control.zodapos.com/core/api/account_data.class.php?action=updated&email="+getEmailId, new Response.Listener<String>() {
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
                    params.put("fullname",getFullname);
                    params.put("email", getEmailId);
                    params.put("phone",getPhone);
                    return params;
                }
            };
            queue.add(postRequest);
            Toast.makeText(this, "Update Success", Toast.LENGTH_SHORT).show();
            Intent returnIntent = new Intent();
            setResult(RESULT_CANCELED, returnIntent);
            finish();
        }
    }
}