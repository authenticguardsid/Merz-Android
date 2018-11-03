package com.realmattersid.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.realmattersid.DetailNotifActivity;
import com.realmattersid.R;
import com.realmattersid.model.DataClinic;
import com.realmattersid.model.DataNotif;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class ClinicAdapter extends RecyclerView.Adapter<ClinicAdapter.ViewHolder> {
    private ArrayList<DataClinic> listdata;
    private Context context;


    public ClinicAdapter(Context context, ArrayList<DataClinic> listdata) {
        this.listdata = listdata;
        this.context = context;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_clinic_list,parent,false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        holder.name_clinic.setText(listdata.get(position).getName_clinic());
        holder.address_clinic.setText(listdata.get(position).getAddress_clinic());
        holder.phone_clinic.setText(listdata.get(position).getPhone_clinic());
        holder.website_clinic.setText(listdata.get(position).getWebsite_clinic());
    }
    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView name_clinic,address_clinic,phone_clinic,website_clinic;

        public ViewHolder(View itemView) {
            super(itemView);
            name_clinic = (TextView) itemView.findViewById(R.id.fclistClinicname);
            address_clinic = (TextView) itemView.findViewById(R.id.fclistClinicaddress);
            website_clinic = (TextView) itemView.findViewById(R.id.fclistClinicwebsite);
        }
    }
}