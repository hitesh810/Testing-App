package com.synram.morningbucket.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.synram.morningbucket.API.ApiClient;
import com.synram.morningbucket.API.ApiInterface;
import com.synram.morningbucket.Adapter.PerticualrOrderDetailAdapter;
import com.synram.morningbucket.Modal.PerticularOrderDetail;
import com.synram.morningbucket.Modal.Product;
import com.synram.morningbucket.Modal.UserDataModal;
import com.synram.morningbucket.R;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class OrderDetailActivity extends AppCompatActivity {

    private ProgressDialog progressDialog;
    private CompositeDisposable compositeDisposable;
    private String order_no;
    private String cust_id;

    RecyclerView order_rv;
    private List<Product> productList = new ArrayList<>();
    private TextView shpNameInSummary,idOfOrderInSummary,dateToSetInOrderSummary,phoneInSummary,addressInSummary,deliveryCharge,discountGet,totalprice_val;
    private PerticualrOrderDetailAdapter perticualrOrderDetailAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog = new ProgressDialog(this);
        compositeDisposable = new CompositeDisposable();


        try{

            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
             order_no = bundle.getString("order_id");
             cust_id = bundle.getString("cust_id");

        }catch (Exception ex){ex.printStackTrace();}

        shpNameInSummary = findViewById(R.id.shpNameInSummary);
        idOfOrderInSummary = findViewById(R.id.idOfOrderInSummary);
        dateToSetInOrderSummary = findViewById(R.id.dateToSetInOrderSummary);
        phoneInSummary = findViewById(R.id.phoneInSummary);
        addressInSummary = findViewById(R.id.addressInSummary);
        deliveryCharge = findViewById(R.id.deliveryCharge);
        discountGet = findViewById(R.id.discountGet);
        order_rv = findViewById(R.id.order_rv);
        totalprice_val = findViewById(R.id.totalprice_val);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        order_rv.setLayoutManager(layoutManager);

        order_rv.setItemAnimator(new DefaultItemAnimator());

        fetchorderDetail(order_no,cust_id);
    }


    private void fetchorderDetail(final String order_no, String user_id) {
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.fetchOrderDetail(order_no,user_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<PerticularOrderDetail>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onNext(PerticularOrderDetail value) {

                        Log.d("2.0 getFeed > Full json res wrapped in gson => ",new Gson().toJson(value));


                        if (!value.getError()) {

                            progressDialog.dismiss();


                            shpNameInSummary.setText(value.getName());
                            idOfOrderInSummary.setText(order_no);
                            idOfOrderInSummary.setText(order_no);
                            dateToSetInOrderSummary.setText(value.getOrderDate());
                            phoneInSummary.setText(value.getContact());
                            addressInSummary.setText(value.getAddress());
                            deliveryCharge.setText("₹"+value.getDeliveryCharge());
                            discountGet.setText("₹"+value.getDiscount());
                            totalprice_val.setText("₹"+value.getTotalAmount());


                            for (int i=0;i<value.getOrderDetails().size();i++){
                                Product product = new Product(
                                        value.getOrderDetails().get(i).getProName(),
                                        value.getOrderDetails().get(i).getPrice(),
                                        value.getOrderDetails().get(i).getQuantity()
                                );


                                productList.add(product);


                            }

                            Log.d("SIZE", String.valueOf(productList.size()));


                        } else {

                            progressDialog.dismiss();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(OrderDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        if (productList.size()>0){

                            perticualrOrderDetailAdapter = new PerticualrOrderDetailAdapter(OrderDetailActivity.this,productList);

                            order_rv.setAdapter(perticualrOrderDetailAdapter);

                        }


                    }
                }));


    }

}
