package com.synram.morningbucket.activities;

import android.app.ProgressDialog;
import android.app.TimePickerDialog;
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
import android.widget.Toast;

import com.google.gson.Gson;
import com.synram.morningbucket.API.ApiClient;
import com.synram.morningbucket.API.ApiInterface;
import com.synram.morningbucket.Adapter.SubscribedCalanderAdapter;
import com.synram.morningbucket.Adapter.SubscribedProductsListAdapter;
import com.synram.morningbucket.Adapter.TransactionHistoryAdapter;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Modal.PlanDate;
import com.synram.morningbucket.Modal.SubscribeProduct;
import com.synram.morningbucket.Modal.SubscribedOrderDetailRespo;
import com.synram.morningbucket.Modal.TransactionHistResponse;
import com.synram.morningbucket.Modal.TransactionHistory;
import com.synram.morningbucket.R;
import com.synram.morningbucket.SharedprefData.SharedPreference;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Stream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class TransactionHistoryActivity extends AppCompatActivity {


    RecyclerView transaction_hist_rv;
    private ProgressDialog progressDialog;
    private CompositeDisposable compositeDisposable;

    private List<TransactionHistory> transactionHistoryList = new ArrayList<>();
    private TransactionHistoryAdapter transactionHistoryAdapter;

    private View lyt_no_item;
    private SharedPreference sharedPreference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaction_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        compositeDisposable = new CompositeDisposable();
        sharedPreference = new SharedPreference(this);

        transaction_hist_rv = findViewById(R.id.transaction_hist_rv);
        lyt_no_item = findViewById(R.id.lyt_no_item);



        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        transaction_hist_rv.setLayoutManager(layoutManager);
        transaction_hist_rv.setItemAnimator(new DefaultItemAnimator());

        getTransaction_history(sharedPreference.getSharedPreferenceString(this,Constant.USER_ID,""));

    }


    private void getTransaction_history(String cust_id) {

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();



        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.fetchTransactionHistory(cust_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())


                .subscribeWith(new DisposableObserver<TransactionHistResponse>() {
                    @Override
                    public void onNext(TransactionHistResponse value) {

                        Log.d("RESPOSUBORDER",new Gson().toJson(value));

                        if (!value.getError()) {

                            progressDialog.dismiss();



                            for(int i=0;i<value.getTransactionHistory().size();i++){
                                TransactionHistory transactionHistory = new TransactionHistory(
                                        value.getTransactionHistory().get(i).getId(),
                                        value.getTransactionHistory().get(i).getCustId(),
                                        value.getTransactionHistory().get(i).getAmount(),
                                        value.getTransactionHistory().get(i).getTransactionId(),
                                        value.getTransactionHistory().get(i).getTDate(),
                                        value.getTransactionHistory().get(i).getType(),
                                        value.getTransactionHistory().get(i).getOrderId(),
                                        value.getTransactionHistory().get(i).getT_status()

                                );


                                transactionHistoryList.add(transactionHistory);

                            }




                        } else {
                            progressDialog.dismiss();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(TransactionHistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        if (transactionHistoryList.size()>0){

                            lyt_no_item.setVisibility(View.GONE);

                            transactionHistoryAdapter = new TransactionHistoryAdapter(TransactionHistoryActivity.this, transactionHistoryList);
                            transaction_hist_rv.setAdapter(transactionHistoryAdapter);
                        }else {

                            lyt_no_item.setVisibility(View.VISIBLE);
                        }



                    }
                }));


    }


}
