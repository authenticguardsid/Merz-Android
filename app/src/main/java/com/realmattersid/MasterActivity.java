package com.realmattersid;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.realmattersid.fragment.FragmentAccount;
import com.realmattersid.fragment.FragmentClinic;
import com.realmattersid.fragment.FragmentNotif;
import com.realmattersid.fragment.FragmentScanner;
import com.realmattersid.fragment.FragmentSliding;
import com.realmattersid.utils.BottomNavigationViewHelper;

import com.realmattersid.fragment.FragmentHome;

import java.util.HashMap;
import java.util.Map;

public class MasterActivity extends AppCompatActivity {

    private static final String URL_SERVER_REG = "http://control.zodapos.com/core/api/account_data.class.php?action=rtoken";
    private static final String TAG = "MasterActivity";

    private FirebaseAuth mAuth;

    private BottomNavigationView.OnNavigationItemSelectedListener navListener = new BottomNavigationView.OnNavigationItemSelectedListener() {
        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment selectedFragment = null;
            switch (item.getItemId()) {
                case R.id.fragment_home:
                    selectedFragment = new FragmentHome();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                    break;
                case R.id.navigation_clinic:
                    selectedFragment = new FragmentClinic();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                    break;
                case R.id.navigation_scanner:
                    selectedFragment = new FragmentScanner();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                    break;
                case R.id.navigation_notifications:
                    selectedFragment = new FragmentNotif();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                    break;
                case R.id.navigation_account:
                    selectedFragment = new FragmentAccount();
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,selectedFragment).commit();
                    break;
            }
            return true;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_master);
        mAuth = FirebaseAuth.getInstance();
        BottomNavigationView bottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(bottomNavigationView);
        bottomNavigationView.setOnNavigationItemSelectedListener(navListener);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new FragmentHome()).commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        final String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        sendRegistrationToServer(refreshedToken);
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void sendRegistrationToServer(String token) {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String personEmail = currentUser.getEmail();
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }
                        final String token = task.getResult().getToken();
                        RequestQueue queue = Volley.newRequestQueue(MasterActivity.this);
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
                                params.put("refreshtoken", token);
                                return params;
                            }
                        };
                        queue.add(postRequest);
                    }
                });
    }
}