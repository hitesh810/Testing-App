package com.synram.morningbucket.activities;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.synram.morningbucket.API.ApiClient;
import com.synram.morningbucket.API.ApiInterface;
import com.synram.morningbucket.Adapter.AllOrderDetailAdapter;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Modal.AllOrderDetail;
import com.synram.morningbucket.Modal.AllOrderList;
import com.synram.morningbucket.Modal.UserDataModal;
import com.synram.morningbucket.R;
import com.synram.morningbucket.SharedprefData.SharedPreference;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class OrderListActivity extends AppCompatActivity {


    private RecyclerView orderdetail_rv;
    private CompositeDisposable compositeDisposable;
    private SharedPreference sharedPreference;
    private ProgressDialog progressDialog;
    private String userid;
    private AllOrderDetailAdapter allOrderDetailAdapter;

    private List<AllOrderDetail> allOrderDetailList = new ArrayList<>();
    private View lyt_no_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_list);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        compositeDisposable = new CompositeDisposable();
        sharedPreference = new SharedPreference(this);
        progressDialog = new ProgressDialog(this);


        orderdetail_rv = findViewById(R.id.orderdetail_rv);
        lyt_no_item = (View) findViewById(R.id.lyt_no_item);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        orderdetail_rv.setLayoutManager(layoutManager);


        fetchuserdata(sharedPreference.getSharedPreferenceString(this,Constant.SELECTED_MOBILE_NO,""));


    }




    private void fetchuserdata(String mobile_number) {
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.fetchuserdata(mobile_number)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<UserDataModal>() {
                    @Override
                    public void onNext(UserDataModal value) {


                        if (!value.getError()) {

                            progressDialog.dismiss();


                            userid = value.getUserData().get(0).getId();

                            fetch_orderHistory(userid);


                        } else {

                            progressDialog.dismiss();

                            Toast.makeText(OrderListActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(OrderListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {


                    }
                }));


    }



    private void fetch_orderHistory(final String userid) {
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.fetchorderHistory(userid,"1")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<AllOrderList>() {
                    @Override
                    public void onNext(AllOrderList value) {



                        if (!value.getError()) {

                            allOrderDetailList.clear();
                            progressDialog.dismiss();

                            for (int i=0;i<value.getOrderDetails().size();i++){
                                AllOrderDetail allOrderDetail = new AllOrderDetail(
                                        value.getOrderDetails().get(i).getName(),
                                        value.getOrderDetails().get(i).getContact(),
                                        value.getOrderDetails().get(i).getEmail(),
                                        value.getOrderDetails().get(i).getAddress(),
                                        value.getOrderDetails().get(i).getZipCode(),
                                        value.getOrderDetails().get(i).getTotalAmount(),
                                        value.getOrderDetails().get(i).getOrderNo(),
                                        value.getOrderDetails().get(i).getOrderStatus(),
                                        value.getOrderDetails().get(i).getDate(),
                                        value.getOrderDetails().get(i).getDeliveryCharge(),
                                        value.getOrderDetails().get(i).getDiscount(),
                                        value.getOrderDetails().get(i).getCheckoutType(),
                                        value.getOrderDetails().get(i).getPayMoney(),
                                        value.getOrderDetails().get(i).getRemainingMaoney(),
                                        value.getOrderDetails().get(i).getMsg(),
                                        value.getOrderDetails().get(i).getOnedaycost()


                                );

                                allOrderDetailList.add(allOrderDetail);
                            }




                        } else {

                            progressDialog.dismiss();

//                            Toast.makeText(OrderListActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(OrderListActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        if (allOrderDetailList.size()>0){

                            allOrderDetailAdapter = new AllOrderDetailAdapter(OrderListActivity.this, allOrderDetailList, new AllOrderDetailAdapter.OnitemClicklistner() {
                                @Override
                                public void onitemclik(AllOrderDetail allOrderDetail, int position) {
                                    Intent intent = new Intent(OrderListActivity.this,OrderDetailActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("order_id",allOrderDetail.getOrderNo());
                                    bundle.putString("cust_id",userid);
                                    intent.putExtras(bundle);
                                    startActivity(intent);
                                }
                            });

                            orderdetail_rv.setAdapter(allOrderDetailAdapter);
                            lyt_no_item.setVisibility(View.GONE);


                        }else {


                                lyt_no_item.setVisibility(View.VISIBLE);

                        }


                    }
                }));


    }


}
