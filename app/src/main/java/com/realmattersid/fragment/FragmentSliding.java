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

import com.realmattersid.R;
import com.realmattersid.adapter.NewsAdapter;
import com.realmattersid.model.DataNews;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class FragmentSliding extends Fragment {
    private SlidingUpPanelLayout mLayout;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View inflat = inflater.inflate(R.layout.fragment_sliding,container,false);
        mLayout = (SlidingUpPanelLayout) inflat.findViewById(R.id.sliding_layout);
        return inflat;
    }
}
