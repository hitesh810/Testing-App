package com.synram.morningbucket.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.balysv.materialripple.MaterialRippleLayout;
import com.squareup.picasso.Picasso;
import com.synram.morningbucket.Database.DatabaseHandler;
import com.synram.morningbucket.Modal.Cart;
import com.synram.morningbucket.Modal.CityDatum;
import com.synram.morningbucket.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

;


public class AdapterCities extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;
//    private SharedPref sharedPref;
    private List<CityDatum> items = new ArrayList<>();

    private OnItemClickListener onItemClickListener;
    private AlertDialog.Builder delcart_builder;

    public interface OnItemClickListener {
        void onItemClick(View view, Cart obj, int position);
    }

//    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
//        this.onItemClickListener = onItemClickListener;
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView city_name;

        public ViewHolder(View v) {
            super(v);
            city_name = (TextView) v.findViewById(R.id.city_name);
        }
    }

    public AdapterCities(Context ctx,List<CityDatum> items) {
        this.ctx = ctx;
        this.items = items;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_city_row, parent, false);
        vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ViewHolder) {
            ViewHolder vItem = (ViewHolder) holder;
            CityDatum cityDatum = items.get(position);

            vItem.city_name.setText(cityDatum.getCityName());


        }


    }



    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<CityDatum> getItem() {
        return items;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<CityDatum> items) {
        this.items = items;
        notifyDataSetChanged();
    }



}