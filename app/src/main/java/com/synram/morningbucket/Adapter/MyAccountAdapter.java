package com.synram.morningbucket.Adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import com.synram.morningbucket.Modal.AcountListModal;
import com.synram.morningbucket.R;

import java.util.ArrayList;
import java.util.List;

public class MyAccountAdapter extends RecyclerView.Adapter<MyAccountAdapter.MyviewHolder> {


    private Context context;
    private List<AcountListModal> acountListModalList = new ArrayList<>();


    public MyAccountAdapter(Context context, List<AcountListModal> acountListModalList) {
        this.context = context;
        this.acountListModalList = acountListModalList;
    }

    public class MyviewHolder extends RecyclerView.ViewHolder {
        TextView menu_title;
        ImageView menu_img;

        public MyviewHolder(@NonNull View itemView) {
            super(itemView);

            menu_title = itemView.findViewById(R.id.menu_title);
            menu_img = itemView.findViewById(R.id.menu_img);

        }
    }

    @NonNull
    @Override
    public MyviewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemview = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.myac_menu_row,viewGroup,false);

        return new MyviewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull MyviewHolder myviewHolder, int i) {

        AcountListModal acountListModal = acountListModalList.get(i);

        myviewHolder.menu_title.setText(acountListModal.getMenu_title());

//        Log.d("DATA",String.valueOf(acountListModal.getMenu_image()));

        Glide.with(context).load(acountListModal.getMenu_image()).into(myviewHolder.menu_img);




    }

    @Override
    public int getItemCount() {
        return acountListModalList.size();
    }



}
