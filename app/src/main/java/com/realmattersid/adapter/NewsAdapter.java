package com.realmattersid.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.realmattersid.NewsActivity;
import com.realmattersid.R;
import com.realmattersid.model.DataNews;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NewsAdapter extends RecyclerView.Adapter<NewsAdapter.ViewHolder> {
    private ArrayList<DataNews> listdata;
    private Context context;


    public NewsAdapter(Context context, ArrayList<DataNews> listdata) {
        this.listdata = listdata;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_news,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.title_news.setText(listdata.get(position).getTitle_news());
        Picasso.get().load(listdata.get(position).getImage_news()).into(holder.image_news);
        holder.image_news.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, NewsActivity.class);
                intent.putExtra("urlnews", listdata.get(position).getUrl_news());
                context.startActivity(intent);
            }
        });
    }
    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView title_news;
        public ImageView image_news;

        public ViewHolder(View itemView) {
            super(itemView);
            title_news = (TextView) itemView.findViewById(R.id.title_news);
            image_news = (ImageView) itemView.findViewById(R.id.image_news);
        }
    }
}
