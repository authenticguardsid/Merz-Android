package com.realmattersid.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.realmattersid.DetailNotifActivity;
import com.realmattersid.R;
import com.realmattersid.model.DataNotif;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class NotifAdapter extends RecyclerView.Adapter<NotifAdapter.ViewHolder> {
    private ArrayList<DataNotif> listdata;
    private Context context;


    public NotifAdapter(Context context, ArrayList<DataNotif> listdata) {
            this.listdata = listdata;
            this.context = context;
            }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_notification,parent,false);
            return new ViewHolder(itemView);
            }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.title_notif.setText(listdata.get(position).getTitle_notif());
        holder.summary_notif.setText(listdata.get(position).getSummary_notif());
        holder.date_notif.setText(listdata.get(position).getDate_notif());
        Picasso.get().load(listdata.get(position).getImage_notif()).into(holder.image_notif);
        holder.card_list_notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, DetailNotifActivity.class);
                intent.putExtra("key", listdata.get(position).getKey());
                context.startActivity(intent);
            }
        });

            }
    @Override
    public int getItemCount() {
            return listdata.size();
            }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public CardView card_list_notif;
        public TextView date_notif,title_notif,summary_notif;
        public ImageView image_notif;

        public ViewHolder(View itemView) {
            super(itemView);
            card_list_notif = (CardView) itemView.findViewById(R.id.card_list_notif);
            title_notif = (TextView) itemView.findViewById(R.id.title_notf);
            summary_notif = (TextView) itemView.findViewById(R.id.summary_notif);
            date_notif = (TextView) itemView.findViewById(R.id.date_notif);
            image_notif = (ImageView) itemView.findViewById(R.id.image_notif);
        }
    }
}