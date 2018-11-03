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
import com.realmattersid.model.DataNews;
//import com.smarteist.autoimageslider.SliderLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class FragmentHome extends Fragment {
    //SliderLayout sliderLayout;
    Toolbar toolbar;

    private NewsAdapter newsAdapter;
    private RecyclerView recyclerView;
    private GridLayoutManager layoutmanager;
    private ArrayList<DataNews> dataNews;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View inflat = inflater.inflate(R.layout.fragment_home,container,false);
        setHasOptionsMenu(true);
        toolbar = (Toolbar) inflat.findViewById(R.id.ToolbarHome);
        ((AppCompatActivity)getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle(null);

        recyclerView = (RecyclerView) inflat.findViewById(R.id.RVHome);
        recyclerView.setHasFixedSize(true);
        layoutmanager = new GridLayoutManager(getActivity(),1);
        layoutmanager.setOrientation(GridLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutmanager);

        dataNews = new ArrayList<DataNews>();
        new_episode_data();


        newsAdapter =new NewsAdapter(getActivity(),dataNews);
        recyclerView.setAdapter(newsAdapter);
        newsAdapter.notifyDataSetChanged();
        return inflat;
    }

    public void new_episode_data(){
        JsonArrayRequest arrayRequest = new JsonArrayRequest("http://control.zodapos.com/core/api/news.class.php?action=news", new com.android.volley.Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                if (response.length() > 0) {
                    for (int i = 0; i < response.length(); i++) {
                        try {
                            JSONObject data = response.getJSONObject(i);
                            DataNews item = new DataNews();
                            item.setId(data.getInt("id"));
                            item.setTitle_news(data.getString("title"));
                            item.setImage_news(data.getString("imageg"));
                            item.setUrl_news(data.getString("urlnews"));
                            dataNews.add(item);
                            newsAdapter.notifyDataSetChanged();
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
