package com.synram.morningbucket.activities;

import android.app.ProgressDialog;
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
import com.synram.morningbucket.Adapter.CashbackHistoryAdapter;
import com.synram.morningbucket.Adapter.TransactionHistoryAdapter;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Modal.CashbackHistory;
import com.synram.morningbucket.Modal.CashbackHistoryResponse;
import com.synram.morningbucket.Modal.TransactionHistResponse;
import com.synram.morningbucket.Modal.TransactionHistory;
import com.synram.morningbucket.R;
import com.synram.morningbucket.SharedprefData.SharedPreference;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class CashbackHistoryActivity extends AppCompatActivity {


    private RecyclerView cashback_rv;

    private ProgressDialog progressDialog;
    private CompositeDisposable compositeDisposable;

    private List<CashbackHistory> cashbackHistoryList = new ArrayList<>();
    private CashbackHistoryAdapter cashbackHistoryAdapter;

    private SharedPreference sharedPreference;
    private View no_lyt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cashback_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        progressDialog = new ProgressDialog(this);
        compositeDisposable = new CompositeDisposable();
        sharedPreference = new SharedPreference(this);

        cashback_rv = findViewById(R.id.cashback_rv);
        no_lyt = findViewById(R.id.no_lyt);



        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);

        cashback_rv.setLayoutManager(layoutManager);
        cashback_rv.setItemAnimator(new DefaultItemAnimator());

        getCashback_history(sharedPreference.getSharedPreferenceString(this,Constant.USER_ID,""));

    }

    private void getCashback_history(String cust_id) {

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();



        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.fetchcashbackHistory(cust_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())


                .subscribeWith(new DisposableObserver<CashbackHistoryResponse>() {
                    @Override
                    public void onNext(CashbackHistoryResponse value) {

//                        Log.d("RESPOSUBORDER",new Gson().toJson(value));

                        if (!value.getError()) {

                            progressDialog.dismiss();



                            for(int i=0;i<value.getCashbackHistory().size();i++){
                                CashbackHistory cashbackHistory = new CashbackHistory(
                                        value.getCashbackHistory().get(i).getId(),
                                        value.getCashbackHistory().get(i).getCustId(),
                                        value.getCashbackHistory().get(i).getAmount(),
                                        value.getCashbackHistory().get(i).getTransactionId(),
                                        value.getCashbackHistory().get(i).getTDate(),
                                        value.getCashbackHistory().get(i).getType(),
                                        value.getCashbackHistory().get(i).getOrderId()

                                );


                                cashbackHistoryList.add(cashbackHistory);

                            }




                        } else {
                            progressDialog.dismiss();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(CashbackHistoryActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        if (cashbackHistoryList.size()>0){

                            no_lyt.setVisibility(View.GONE);
                            cashbackHistoryAdapter = new CashbackHistoryAdapter(CashbackHistoryActivity.this, cashbackHistoryList);
                            cashback_rv.setAdapter(cashbackHistoryAdapter);
                        }else {
                            no_lyt.setVisibility(View.VISIBLE);
                        }



                    }
                }));


    }



}
