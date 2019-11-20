package com.synram.morningbucket.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;
import com.synram.morningbucket.Modal.HorizontalCalanderModal;
import com.synram.morningbucket.Modal.ProductDatum;
import com.synram.morningbucket.Modal.TypeDatum;
import com.synram.morningbucket.R;

import java.util.ArrayList;
import java.util.List;

public class ProductsInsideListAdapter extends RecyclerView.Adapter<ProductsInsideListAdapter.MyViewHolder> {


    private Context mContext;
    private List<ProductDatum> productDatumList = new ArrayList<>();

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView product_title,product_qty,product_price,variation,product_mrp;
        public ImageView product_img;
        public LinearLayout progressBar;

        public MyViewHolder(View view) {
            super(view);
            product_title = (TextView) view.findViewById(R.id.product_title);
            product_qty = (TextView) view.findViewById(R.id.product_qty);
            product_price = (TextView) view.findViewById(R.id.product_price);
            variation = (TextView) view.findViewById(R.id.variation);
            product_mrp = (TextView) view.findViewById(R.id.product_mrp);
            product_img = (ImageView) view.findViewById(R.id.product_img);
            progressBar = (LinearLayout) view.findViewById(R.id.progressBar);
        }
    }


    public ProductsInsideListAdapter(Context mContext, List<ProductDatum> productDatumList) {
        this.mContext = mContext;
        this.productDatumList = productDatumList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.products_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {

        ProductDatum productDatum = productDatumList.get(position);
        holder.product_price.setText("₹"+productDatum.getPrice());
        holder.product_mrp.setText(Html.fromHtml("<strike>"+"₹"+productDatum.getMrp()+" "+"</strike>"));
        holder.product_qty.setText(productDatum.getWeight());
        holder.product_title.setText(productDatum.getProductName());

        try {
            if (productDatum.getPrice().equals(productDatum.getMrp())){
                holder.product_mrp.setVisibility(View.GONE);
            }else {
                holder.product_mrp.setVisibility(View.VISIBLE);

            }

        }catch (Exception ex){
            ex.printStackTrace();
        }



        if(!productDatum.getNo_of_variation().equals("+ 0 More variation")){
            holder.variation.setVisibility(View.VISIBLE);

            holder.variation.setText(productDatum.getNo_of_variation());
        }else {
            holder.variation.setVisibility(View.GONE);

        }

        Picasso.get().load(productDatum.getImage()).into(holder.product_img, new Callback() {
            @Override
            public void onSuccess() {
                holder.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onError(Exception e) {

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

    @Override
    public int getItemCount() {
        return productDatumList.size();
    }
}