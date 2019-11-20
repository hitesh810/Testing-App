package com.synram.morningbucket.Adapter;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.synram.morningbucket.API.ApiClient;
import com.synram.morningbucket.API.ApiInterface;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Database.DatabaseHandler;
import com.synram.morningbucket.Modal.AddMoneyModal;
import com.synram.morningbucket.Modal.Cart;
import com.synram.morningbucket.Modal.Orderproduct;
import com.synram.morningbucket.R;
import com.synram.morningbucket.SharedprefData.SharedPreference;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

;


public class SubscribedProductsListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private DatabaseHandler db;
    private Context ctx;
    //    private SharedPref sharedPref;
    private List<Orderproduct> items = new ArrayList<>();
    private ProgressDialog progressDialog;

    private String orderstatus;

    private String plan_id;
    private List<Cart> cartslist = new ArrayList<>();

    private String status;
    private AlertDialog.Builder delcart_builder;
    private CompositeDisposable compositeDisposable;


    public SubscribedProductsListAdapter(Context ctx, List<Orderproduct> items, String status,String orderstatus,String plan_id) {
        this.ctx = ctx;
        this.items = items;
        this.status = status;
        this.plan_id = plan_id;
        this.orderstatus = orderstatus;
        db = new DatabaseHandler(ctx);

        compositeDisposable = new CompositeDisposable();
        progressDialog = new ProgressDialog(ctx);

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.suborderedpro_row, parent, false);
        vh = new ViewHolder(v);

        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

        final Orderproduct product = items.get(position);

        if (holder instanceof ViewHolder) {
            final ViewHolder vItem = (ViewHolder) holder;

            int productPrice = product.getAmount() * product.getQty();

            Cart cart = new Cart(Long.parseLong(product.getPid().toString()), product.getPid(), product.getProName(), "", product.getQty(), (long) 0, Double.parseDouble(String.valueOf(productPrice)), System.currentTimeMillis(), "");
            db.saveSubCart(cart);

            cartslist = db.getActiveSubCartList();

            /*Gson gson = new Gson();
            // convert your list to json
            String jsonCartList = gson.toJson(cartslist);
            */
            vItem.product_name.setText(product.getProName());
            vItem.price.setText("₹" + product.getAmount());
            vItem.amount.setText(product.getQty() + " x Items");
            vItem.p_count.setText(product.getQty() + "");

            final Cart cart_Data = new Cart();
            cart_Data.amount = product.getQty();

            vItem.add.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (cart_Data.amount < 100) {
                        cart_Data.amount = cart_Data.amount + 1;

//                        cart_Data.amount = cart_Data.amount ;


                        int productPrice = product.getAmount() * cart_Data.amount;
                        Cart cart = new Cart(Long.parseLong(product.getPid().toString()), product.getPid(), product.getProName(), "", cart_Data.amount, (long) 0, Double.parseDouble(String.valueOf(productPrice)), System.currentTimeMillis(), "");

                        db.saveSubCart(cart);

                        cartslist = db.getActiveSubCartList();

                        // create a new Gson instance

                        Gson gson = new Gson();
                        // convert your list to json
                        String jsonCartList = gson.toJson(cartslist);
                        // print your generated json
                        System.out.println("jsonCartList: " + jsonCartList);

                        int data = cart_Data.amount;

                        updatePlan(plan_id,"plus",jsonCartList,orderstatus,data,vItem.p_count, SharedPreference.getSharedPreferenceString(ctx, Constant.USER_ID,""));


                    }
                }
            });
            vItem.remove.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if (cart_Data.amount > 0) {
                        cart_Data.amount = cart_Data.amount - 1;
//                        vItem.p_count.setText(cart_Data.amount + "");

                        int productPrice = product.getAmount() * cart_Data.amount;

                        Cart cart = new Cart(Long.parseLong(product.getPid().toString()), product.getPid(), product.getProName(), "", cart_Data.amount, (long) 0, Double.parseDouble(String.valueOf(productPrice)), System.currentTimeMillis(), "");
                        db.saveSubCart(cart);

                        cartslist = db.getActiveSubCartList();
                        // create a new Gson instance
                        Gson gson = new Gson();
                        // convert your list to json
                        String jsonCartList = gson.toJson(cartslist);
                        // print your generated json
                        System.out.println("jsonCartList: " + jsonCartList);

                        int data = cart_Data.amount;
                        updatePlan(plan_id,"minus",jsonCartList,orderstatus,data,vItem.p_count,SharedPreference.getSharedPreferenceString(ctx, Constant.USER_ID,""));

                    }


                }
            });


            if (status.equals("0")) {
                vItem.add.setEnabled(true);
                vItem.remove.setEnabled(true);


            } else {
                vItem.add.setEnabled(false);
                vItem.remove.setEnabled(false);
            }


        }


    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public List<Orderproduct> getItem() {
        return items;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void setItems(List<Orderproduct> items) {
        this.items = items;
        notifyDataSetChanged();
    }

    private void updatePlan(String plan_id, String type, String order_product, String order_status, final int data, final TextView plusbtn,String cust_id) {

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.updateplanquantity(plan_id,type,order_status, order_product,cust_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())


                .subscribeWith(new DisposableObserver<AddMoneyModal>() {
                    @Override
                    public void onNext(AddMoneyModal value) {

                        if (!value.getError()) {

                            Log.d("JSONRESpO", new Gson().toJson(value));
                            progressDialog.dismiss();

                            plusbtn.setText(data+ "");


                            dialogDeleteActiveConfirmation(value.getErrorMsg());


                        } else {

                            progressDialog.dismiss();
                            dialogDeleteActiveConfirmation(value.getErrorMsg());

                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();

//                        Toast.makeText(PlanDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {


                    }
                }));


    }


    public void dialogDeleteActiveConfirmation(String msg) {

        delcart_builder = new AlertDialog.Builder(ctx);
        delcart_builder.setTitle("Morning Bucket");
        delcart_builder.setMessage(msg);
        delcart_builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface di, int i) {
                di.dismiss();
            }
        });
        delcart_builder.show();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        public TextView product_name;
        public TextView amount;
        public TextView price;
        public TextView p_count;

        public ImageView add, remove;


        public ViewHolder(View v) {
            super(v);
            product_name = (TextView) v.findViewById(R.id.product_name);
            amount = (TextView) v.findViewById(R.id.quentity);
            price = (TextView) v.findViewById(R.id.price);
            p_count = (TextView) v.findViewById(R.id.p_count);
            add = v.findViewById(R.id.add);
            remove = v.findViewById(R.id.remove);

        }
    }


}