package com.realmattersid.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.realmattersid.EditProfileACtivity;
import com.realmattersid.MainActivity;
import com.realmattersid.MenuProfileActivity;
import com.realmattersid.R;
import com.realmattersid.SignInActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class FragmentAccount extends Fragment {
    private View rootView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInApi mGoogleSignInClient2;
    private GoogleApiClient mGoogleApiClient;
    private static Button logoutBtn;

    private static TextView textEditProfile,textFname, textEmail, textPhone, textGPScity;
    private static RelativeLayout pmenu1,pmenu2,pmenu3,pmenu4,pmenu5;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
        mGoogleSignInClient2 = Auth.GoogleSignInApi;
        mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        rootView = inflater.inflate(R.layout.fragment_account,container,false);
        textEditProfile = (TextView) rootView.findViewById(R.id.textEditProfile);
        textFname = (TextView) rootView.findViewById(R.id.textFname);
        textEmail = (TextView) rootView.findViewById(R.id.textEmail);
        textPhone = (TextView) rootView.findViewById(R.id.textPhone);
        textGPScity = (TextView) rootView.findViewById(R.id.textGPScity);
        textEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), EditProfileACtivity.class);
                startActivity(intent);
            }
        });
        pmenu1 = (RelativeLayout) rootView.findViewById(R.id.pmenu1);
        pmenu1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MenuProfileActivity.class);
                intent.putExtra("mkind", "help");
                startActivity(intent);
            }
        });
        pmenu2 = (RelativeLayout) rootView.findViewById(R.id.pmenu2);
        pmenu3 = (RelativeLayout) rootView.findViewById(R.id.pmenu3);
        pmenu3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MenuProfileActivity.class);
                intent.putExtra("mkind", "termofservice");
                startActivity(intent);
            }
        });
        pmenu4 = (RelativeLayout) rootView.findViewById(R.id.pmenu4);
        pmenu4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), MenuProfileActivity.class);
                intent.putExtra("mkind", "privacy");
                startActivity(intent);
            }
        });
        pmenu5 = (RelativeLayout) rootView.findViewById(R.id.pmenu5);
        pmenu5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=com.realmattersid")));
            }
        });

        logoutBtn = (Button) rootView.findViewById(R.id.logoutBtn);
        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
                Intent intent = new Intent(getActivity(), SignInActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        });
        get_profile_view();
        return rootView;
    }

    public void onResume() {
        super.onResume();
        get_profile_view();
    }

    public void logout() {
        mGoogleSignInClient.signOut().addOnCompleteListener(getActivity(),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        FirebaseAuth.getInstance().signOut();
                        LoginManager.getInstance().logOut();
                        Toast.makeText(getContext(), "Logged Out", Toast.LENGTH_LONG).show();
                    }
                });
        mGoogleSignInClient2.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
            @Override
            public void onResult(@NonNull Status status) {
                FirebaseAuth.getInstance().signOut();
                Intent intent = new Intent(getContext(), SignInActivity.class);
                startActivity(intent);
                getActivity().finish();
                Toast.makeText(getContext(), "Logged Out", Toast.LENGTH_LONG).show();
            }
        });
    }

    public void get_profile_view(){
        FirebaseUser currentUser = mAuth.getCurrentUser();
        final String personEmail = currentUser.getEmail();
        JsonArrayRequest arrayRequest = new JsonArrayRequest("http://control.zodapos.com/core/api/account_data.class.php?action=profile_view&email="+personEmail, new com.android.volley.Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject data = response.getJSONObject(i);
                            textFname.setText(data.getString("fname"));
                            textEmail.setText(data.getString("email"));
                            textPhone.setText(data.getString("phone"));
                            textGPScity.setText(data.getString("city"));
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
        Volley.newRequestQueue(getActivity()).add(arrayRequest);
    }
}
