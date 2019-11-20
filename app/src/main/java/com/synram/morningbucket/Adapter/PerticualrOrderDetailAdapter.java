package com.synram.morningbucket.Adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.synram.morningbucket.Modal.AllOrderDetail;
import com.synram.morningbucket.Modal.Product;
import com.synram.morningbucket.R;

import java.util.ArrayList;
import java.util.List;

public class PerticualrOrderDetailAdapter extends RecyclerView.Adapter<PerticualrOrderDetailAdapter.MyViewHolder> {


    private Context context;
    private List<Product> productList = new ArrayList<>();


    public class MyViewHolder extends RecyclerView.ViewHolder {

        // each data item is just a string in this case
        public TextView product_name;
        public TextView amount;
        public TextView price;


        public MyViewHolder(View v) {
            super(v);
            product_name = (TextView) v.findViewById(R.id.product_name);
            amount = (TextView) v.findViewById(R.id.quentity);
            price = (TextView) v.findViewById(R.id.price);

        }
    }

    public PerticualrOrderDetailAdapter(Context context, List<Product> productList) {
        this.context = context;
        this.productList = productList;

    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemview = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.ordered_product_item,viewGroup,false);

        return new MyViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder myViewHolder, final int i) {

        Product product = productList.get(i);

        myViewHolder.product_name.setText(product.getProName());
        myViewHolder.price.setText("₹"+product.getPrice());
        myViewHolder.amount.setText(product.getQuantity() +" x Items");

    }

    @Override
    public int getItemCount() {
        return productList.size();
    }



}
