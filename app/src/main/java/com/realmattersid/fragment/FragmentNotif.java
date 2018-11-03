package com.realmattersid.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.realmattersid.R;
import com.realmattersid.adapter.NewsAdapter;
import com.realmattersid.adapter.NotifAdapter;
import com.realmattersid.model.DataNews;
import com.realmattersid.model.DataNotif;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentNotif extends Fragment {
    Toolbar toolbar;
    private NotifAdapter notifAdapter;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutmanager;
    private ArrayList<DataNotif> dataNotif;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View inflat = inflater.inflate(R.layout.fragment_notif,container,false);
        setHasOptionsMenu(true);
        toolbar = (Toolbar) inflat.findViewById(R.id.ToolbarNotif);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(null);;
        recyclerView = (RecyclerView) inflat.findViewById(R.id.RVNotif);
        recyclerView.setHasFixedSize(true);
        layoutmanager = new GridLayoutManager(getActivity(),1);
        layoutmanager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutmanager);

        dataNotif = new ArrayList<DataNotif>();
        new_episode_data();


        notifAdapter =new NotifAdapter(getActivity(),dataNotif);
        recyclerView.setAdapter(notifAdapter);
        notifAdapter.notifyDataSetChanged();
        return inflat;
    }

    public void new_episode_data(){
        JsonArrayRequest arrayRequest = new JsonArrayRequest("http://control.zodapos.com/core/api/notif.class.php?action=view", new com.android.volley.Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject data = response.getJSONObject(i);
                            DataNotif item = new DataNotif();
                            item.setId(data.getInt("id"));
                            item.setKey(data.getString("key"));
                            item.setTitle_notif(data.getString("title"));
                            item.setSummary_notif(data.getString("summary"));
                            item.setDate_notif(data.getString("date"));
                            item.setImage_notif(data.getString("image"));
                            dataNotif.add(item);
                            notifAdapter.notifyDataSetChanged();
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