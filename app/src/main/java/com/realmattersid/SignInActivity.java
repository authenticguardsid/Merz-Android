package com.realmattersid;


import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.content.res.XmlResourceParser;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.realmattersid.utils.Utils;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SignInActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private static EditText emailid, password;
    private static Button loginButton;
    private static TextView forgotPassword, signUp;
    private static CheckBox show_hide_password;
    private static LinearLayout loginLayout;
    private static Animation shakeAnimation;
    private static SignInButton signInButton;
    private GoogleSignInApi mGoogleSignInClient;
    private GoogleApiClient mGoogleApiClient;
    private ProgressDialog pDialog;

    FirebaseAuth.AuthStateListener mAuthListener;

    private static final int RC_SIGN_IN = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= 21) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }
        setContentView(R.layout.activity_signin);
        mAuth = FirebaseAuth.getInstance();

        emailid = (EditText) findViewById(R.id.login_emailid);
        password = (EditText) findViewById(R.id.login_password);
        loginButton = (Button) findViewById(R.id.loginBtn);
        forgotPassword = (TextView) findViewById(R.id.forgot_password);
        signUp = (TextView) findViewById(R.id.createAccount);
        show_hide_password = (CheckBox) findViewById(R.id.show_hide_password);
        loginLayout = (LinearLayout) findViewById(R.id.login_layout);
        shakeAnimation = AnimationUtils.loadAnimation(this,R.anim.shake);
        signInButton = (SignInButton) findViewById(R.id.sign_in_button);
        mGoogleSignInClient = Auth.GoogleSignInApi;

        mAuthListener = new FirebaseAuth.AuthStateListener(){
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    if(firebaseAuth.getCurrentUser().isEmailVerified()){
                        HashMap<String,Object> data=new HashMap<>();
                        data.put("userId",firebaseAuth.getCurrentUser().getUid());
                        data.put("email",firebaseAuth.getCurrentUser().getEmail());
                        data.put("name",firebaseAuth.getCurrentUser().getDisplayName());
                        data.put("photo",firebaseAuth.getCurrentUser().getPhotoUrl().toString());
                        sendToHome();
                    }else{
                        Toast.makeText(SignInActivity.this, "not allowed email isn't verified ", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        };

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext())
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(SignInActivity.this, "You Got An Error", Toast.LENGTH_SHORT).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API,gso)
                .build();

        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn();
            }
        });

        show_hide_password.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton button, boolean isChecked) {
                if (isChecked) {
                    show_hide_password.setText(R.string.hide_pwd);
                    password.setInputType(InputType.TYPE_CLASS_TEXT);
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    show_hide_password.setText(R.string.show_pwd);
                    password.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        forgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SForgotActivity.class));
                finish();
            }
        });

        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(SignInActivity.this, SignUpActivity.class));
                finish();
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkValidation();
            }
        });

    }

    private void sendToHome(){
        Intent intent = new Intent(SignInActivity.this, MasterActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onStop() {super.onStop();}

    private void checkValidation() {
        displayLoader();
        final Utils utils = new Utils(this);
        String getEmailId = emailid.getText().toString();
        String getPassword = password.getText().toString();
        Pattern p = Pattern.compile(Config.regEx);
        Matcher m = p.matcher(getEmailId);
        if (getEmailId.equals("") || getEmailId.length() == 0 || getPassword.equals("") || getPassword.length() == 0) {
            loginLayout.startAnimation(shakeAnimation);
            Toast.makeText(this, "Enter both credentials.", Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
        } else if (!m.find()){
            Toast.makeText(this, "Your Email Id is Invalid.", Toast.LENGTH_SHORT).show();
            pDialog.dismiss();
        } else {
            mAuth.signInWithEmailAndPassword(getEmailId, getPassword)
                    .addOnCompleteListener(SignInActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            pDialog.dismiss();
                            if (!task.isSuccessful()) {
                                Toast.makeText(SignInActivity.this, "Your email account is not registered.", Toast.LENGTH_SHORT).show();
                            } else {
                                Intent intent = new Intent(SignInActivity.this, MasterActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
        }

    }

    private void signIn(){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {

            }
        }

    }



    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        displayLoader();
        pDialog.setTitle("Masuk, Mohon Tunggu...");
        pDialog.show();
        //Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SignInActivity.this, "Login Berhasil", Toast.LENGTH_SHORT).show();
                            pDialog.dismiss();
                            finish();
                        } else {
                            //Log.w(TAG, "signInWithCredential:failure", task.getException());
                            pDialog.dismiss();
                            Toast.makeText(SignInActivity.this, "Failed ", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }

    private void displayLoader() {
        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Account Verification...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();
    }
}