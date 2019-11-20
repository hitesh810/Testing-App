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
import com.synram.morningbucket.Modal.Product;
import com.synram.morningbucket.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

;


public class OrderedProductsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;
//    private SharedPref sharedPref;
    private List<Product> items = new ArrayList<>();



    private AlertDialog.Builder delcart_builder;


    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView product_name;
        public TextView amount;
        public TextView price;


        public ViewHolder(View v) {
            super(v);
            product_name = (TextView) v.findViewById(R.id.product_name);
            amount = (TextView) v.findViewById(R.id.quentity);
            price = (TextView) v.findViewById(R.id.price);

        }
    }

    public OrderedProductsListAdapter(Context ctx, List<Product> items) {
        this.ctx = ctx;
        this.items = items;

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.ordered_product_item, parent, false);
        vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        Product product = items.get(position);

        if (holder instanceof ViewHolder) {
            ViewHolder vItem = (ViewHolder) holder;

        vItem.product_name.setText(product.getProName());
        vItem.price.setText("₹"+product.getPrice());
        vItem.amount.setText(product.getQuantity() +" x Items");

        }


    }



    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<Product> getItem() {
        return items;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<Product> items) {
        this.items = items;
        notifyDataSetChanged();
    }


//    public void dialogDeleteActiveConfirmation(final int id) {
//
//        delcart_builder = new AlertDialog.Builder();
//        delcart_builder.setTitle(R.string.title_delete_confirm);
//        delcart_builder.setMessage(getString(R.string.content_delete_confirm) + getString(R.string.title_activity_cart));
//        delcart_builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface di, int i) {
//                di.dismiss();
//                db.deleteActiveCart(id);
//                onResume();
//                Snackbar.make(parent_view, R.string.delete_success, Snackbar.LENGTH_SHORT).show();
//            }
//        });
//        delcart_builder.setNegativeButton(R.string.CANCEL, null);
//        delcart_builder.show();
//    }


}