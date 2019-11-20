package com.synram.morningbucket.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.synram.morningbucket.Modal.AttributeDatum;
import com.synram.morningbucket.Modal.ProductDescriptionModal;
import com.synram.morningbucket.R;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.disposables.CompositeDisposable;

public class ProductsWeightAdapter extends RecyclerView.Adapter<ProductsWeightAdapter.MyViewHolder> {


    public OnItemClickListener listener;
    private Context mContext;
    private List<AttributeDatum> attributeDatumArrayList = new ArrayList<>();
    private List<ProductDescriptionModal> productDescriptionModalList = new ArrayList<>();
    private int selectedPos = RecyclerView.NO_POSITION;
    private CompositeDisposable compositeDisposable;

    public ProductsWeightAdapter(Context mContext, List<AttributeDatum> attributeDatumArrayList, OnItemClickListener onItemClickListener) {
        this.mContext = mContext;
        this.attributeDatumArrayList = attributeDatumArrayList;
        compositeDisposable = new CompositeDisposable();
        this.listener = onItemClickListener;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.weight_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {

        final AttributeDatum attributeDatum = attributeDatumArrayList.get(position);
        holder.weight.setText(attributeDatum.getWeight());

//        holder.itemView.setSelected(selectedPos == position);


        holder.weight_cv.setCardBackgroundColor(selectedPos == position ? Color.parseColor("#7d7513") : Color.parseColor("#cf7777"));


/*        if (position == 0) {

            holder.test.callOnClick();

        }*/

        /*holder.test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                holder.weight_cv.setCardBackgroundColor(selectedPos == position ? Color.parseColor("#7d7513") : Color.parseColor("#cf7777"));
//
//                notifyItemChanged(selectedPos);
//                selectedPos = holder.getLayoutPosition();
//                notifyItemChanged(selectedPos);
//
//                listener.onItemClick(attributeDatumArrayList, position);


            }
        });*/

        holder.weight_cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                holder.weight_cv.setCardBackgroundColor(selectedPos == position ? Color.parseColor("#7d7513") : Color.parseColor("#cf7777"));

                notifyItemChanged(selectedPos);
                selectedPos = holder.getLayoutPosition();
                notifyItemChanged(selectedPos);

                listener.onItemClick(attributeDatumArrayList, position);


            }
        });
    }

    /**
     * Showing popup menu when tapping on 3 dots
     */
    private void showPopupMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.menu_album, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    @Override
    public int getItemCount() {
        return attributeDatumArrayList.size();
    }

    public void set_card_color(MyViewHolder viewHolder, int position, List<AttributeDatum> attributeDatumArrayList) {

        for (int i = 0; i < attributeDatumArrayList.size(); i++) {
            if (position == i) {

                viewHolder.weight_cv.setCardBackgroundColor(Color.parseColor("#7d7513"));

            } else {
                viewHolder.weight_cv.setCardBackgroundColor(Color.parseColor("#cf7777"));

            }

        }


    }

    public interface OnItemClickListener {
        void onItemClick(List<AttributeDatum> item, int position);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView weight;
        public CardView weight_cv;
        public Button test;
        public LinearLayout cal_ll;

        public MyViewHolder(View view) {
            super(view);
            weight = (TextView) view.findViewById(R.id.weight);
//            test = (Button) view.findViewById(R.id.test);
            weight_cv = (CardView) view.findViewById(R.id.weight_cv);

        }
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }

        @Override
        public boolean onMenuItemClick(MenuItem menuItem) {
            switch (menuItem.getItemId()) {
                case R.id.action_add_favourite:
                    Toast.makeText(mContext, "Add to favourite", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.action_play_next:
                    Toast.makeText(mContext, "Play next", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
        }
    }


}