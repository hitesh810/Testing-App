package com.synram.morningbucket.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;

import com.squareup.picasso.Picasso;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Database.DatabaseHandler;
import com.synram.morningbucket.Modal.Cart;
import com.synram.morningbucket.R;
import com.synram.morningbucket.Utilities.Tools;
import com.synram.morningbucket.activities.ShopingCartActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

;


public class AdapterShoppingCart extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Context ctx;
//    private SharedPref sharedPref;
    private List<Cart> items = new ArrayList<>();
    private Boolean is_cart = true;

    private OnItemClickListener onItemClickListener;
    private DatabaseHandler db;
    private AlertDialog.Builder delcart_builder;
    private Dialog dialog;
    private boolean monthsubscflag = false;
    private boolean alternatesub_flag = false;

    public interface OnItemClickListener {
        void onItemClick(View view, Cart obj, int position);
    }

//    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
//        this.onItemClickListener = onItemClickListener;
//    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView title;
        public TextView amount;
        public TextView productsub_type,removeItem;

        public TextView price;
        public ImageView image,delete_cart_item;


        public RelativeLayout lyt_image;
        public MaterialRippleLayout lyt_parent;

        public ViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.title);
            amount = (TextView) v.findViewById(R.id.amount);
            productsub_type = (TextView) v.findViewById(R.id.productsub_type);
            removeItem = (TextView) v.findViewById(R.id.removeItem);
            price = (TextView) v.findViewById(R.id.price);
            image = (ImageView) v.findViewById(R.id.image);
            delete_cart_item = (ImageView) v.findViewById(R.id.delete_cart_item);
            lyt_parent = (MaterialRippleLayout) v.findViewById(R.id.lyt_parent);
            lyt_image = (RelativeLayout) v.findViewById(R.id.lyt_image);
        }
    }

    public AdapterShoppingCart(Context ctx, boolean is_cart, List<Cart> items, OnItemClickListener onItemClickListener) {
        this.ctx = ctx;
        this.items = items;
        this.is_cart = is_cart;
        this.onItemClickListener = onItemClickListener;
        db = new DatabaseHandler(ctx);
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_shopping_cart, parent, false);
        vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        if (holder instanceof ViewHolder) {
            final ViewHolder vItem = (ViewHolder) holder;
            final Cart c = items.get(position);
            vItem.title.setText(c.product_name);
            String price_str = String.format(Locale.US, "%1$,.2f", c.price_item);
            vItem.price.setText("₹"+price_str);
            vItem.amount.setText(c.amount + " " + ctx.getString(R.string.items));

            Picasso.get().load(c.image).into(vItem.image);

            vItem.productsub_type.setText(c.subscription_type);

//            vItem.lyt_parent.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(final View v) {
//                    if (onItemClickListener != null) {
//                        onItemClickListener.onItemClick(v, c);
//                    }
//                }
//            });

            vItem.delete_cart_item.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    onItemClickListener.onItemClick(v, c, position);


//                    Toast.makeText(ctx,"YAA!",Toast.LENGTH_SHORT).show();

//                    if (onItemClickListener != null) {
//                        onItemClickListener.onItemClick(v, c, position);
//                    }




                }
            });

            vItem.removeItem.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });


            if (is_cart) {
                vItem.lyt_image.setVisibility(View.VISIBLE);
                vItem.title.setMaxLines(2);
                vItem.lyt_parent.setEnabled(true);
                vItem.delete_cart_item.setVisibility(View.VISIBLE);

            } else {
                vItem.lyt_image.setVisibility(View.GONE);
                vItem.title.setMaxLines(1);
                vItem.lyt_parent.setEnabled(false);
                vItem.productsub_type.setVisibility(View.GONE);
                vItem.delete_cart_item.setVisibility(View.GONE);

            }




            vItem.productsub_type.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (vItem.productsub_type.getText().toString().equals(Constant.SUB_TYPE_ONCE)){

                    }else {

//                        showCustomDialog(ctx,c,vItem.productsub_type,vItem.productsub_type.getText().toString(),vItem.price,position);

                    }

                }
            });


        }


    }


    public void showCustomDialog(final Context context, final Cart cart, final TextView pview, String selectedSub, final TextView price_v, final int position) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View view = inflater.inflate(R.layout.custome_dialog_msg_popup, null, false);
        /*HERE YOU CAN FIND YOU IDS AND SET TEXTS OR BUTTONS*/

        final LinearLayout monthly_subscription = view.findViewById(R.id.monthly_subscription);

        final LinearLayout alternative_subscription = view.findViewById(R.id.alternative_subscription);


        if (selectedSub.equals(Constant.SUB_TYPE_MONTH)){
            monthly_subscription.setVisibility(View.GONE);
            alternative_subscription.setVisibility(View.VISIBLE);
            monthly_subscription.setClickable(true);
            alternative_subscription.setClickable(true);
        }else if (selectedSub.equals(Constant.SUB_TYPE_ALTER)){
            monthly_subscription.setVisibility(View.VISIBLE);
            alternative_subscription.setVisibility(View.GONE);
            monthly_subscription.setClickable(true);
            alternative_subscription.setClickable(true);

        }else {

            monthly_subscription.setClickable(false);
            alternative_subscription.setClickable(false);
        }


        monthly_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();


                    toggleCartButton(Constant.SUB_TYPE_MONTH,cart,price_v,position);

                    pview.setText(Constant.SUB_TYPE_MONTH);


            }
        });






        alternative_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                toggleCartButton(Constant.SUB_TYPE_ALTER,cart,price_v,position);
                pview.setText(Constant.SUB_TYPE_ALTER);

//                monthly_subscription.setVisibility(View.VISIBLE);
//                alternative_subscription.setVisibility(View.GONE);
            }
        });


        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setContentView(view);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;


        window.setGravity(Gravity.CENTER);
        dialog.show();
    }


    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<Cart> getItem() {
        return items;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<Cart> items) {
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


    private void toggleCartButton(String sub_type, Cart prevcart,TextView pview,int position) {


        Cart cart;
        if (sub_type.equals(Constant.SUB_TYPE_MONTH)){
            cart = new Cart(db.getActiveCartList().get(position).id, db.getActiveCartList().get(position).product_id,

                    db.getActiveCartList().get(position).product_name,
                    db.getActiveCartList().get(position).image,
                    db.getActiveCartList().get(position).amount,
                    (long)0,
                    db.getActiveCartList().get(position).price_item*2,
                    System.currentTimeMillis(),sub_type);
            db.saveCart(cart);

            pview.setText("₹"+String.valueOf(cart.price_item));
            ShopingCartActivity.price_total.setText("₹"+String.valueOf(cart.price_item*cart.amount));

        }else if(sub_type.equals(Constant.SUB_TYPE_ALTER)){
            cart = new Cart(db.getActiveCartList().get(position).id, db.getActiveCartList().get(position).product_id,

                    db.getActiveCartList().get(position).product_name,
                    db.getActiveCartList().get(position).image,
                    db.getActiveCartList().get(position).amount,
                    (long)0,
                    db.getActiveCartList().get(position).price_item/2,
                    System.currentTimeMillis(),sub_type);
            db.saveCart(cart);
            pview.setText("₹"+String.valueOf(cart.price_item));

            ShopingCartActivity.price_total.setText("₹"+String.valueOf(cart.price_item*cart.amount));


        }else {

            cart = new Cart(db.getActiveCartList().get(position).id, db.getActiveCartList().get(position).product_id,

                    db.getActiveCartList().get(position).product_name,
                    db.getActiveCartList().get(position).image,
                    db.getActiveCartList().get(position).amount,
                    (long)0,
                    db.getActiveCartList().get(position).price_item,
                    System.currentTimeMillis(),sub_type);

            db.saveCart(cart);

            pview.setText("₹"+String.valueOf(cart.price_item));
            ShopingCartActivity.price_total.setText(String.valueOf("₹"+cart.price_item*cart.amount));


        }




    }

}