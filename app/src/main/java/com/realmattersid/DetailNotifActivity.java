package com.realmattersid;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.realmattersid.model.DataNotif;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DetailNotifActivity extends AppCompatActivity{
    private static TextView title_notf, summary_notif, date_notif;
    private static ImageView image_notif;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_notif);
        title_notf = (TextView) findViewById(R.id.title_notf);
        summary_notif = (TextView) findViewById(R.id.summary_notif);
        date_notif = (TextView) findViewById(R.id.date_notif);
        image_notif = (ImageView) findViewById(R.id.image_notif);
        detail_notif(getIntent().getStringExtra("key"));
    }

    public void detail_notif(String getKey){
        JsonArrayRequest arrayRequest = new JsonArrayRequest("http://control.zodapos.com/core/api/notif.class.php?action=detail&key="+getKey, new com.android.volley.Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject data = response.getJSONObject(i);
                            title_notf.setText(data.getString("title"));
                            summary_notif.setText(data.getString("summary"));
                            date_notif.setText(data.getString("date"));
                            Picasso.get().load(data.getString("image")).into(image_notif);
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
}