package com.synram.morningbucket.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.Circle;
import com.synram.morningbucket.API.ApiClient;
import com.synram.morningbucket.API.ApiInterface;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Modal.UserDataModal;
import com.synram.morningbucket.Modal.WalletAmtResponse;
import com.synram.morningbucket.R;
import com.synram.morningbucket.SharedprefData.SharedPreference;

import java.util.Queue;
import java.util.logging.LogRecord;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class WalletActivity extends AppCompatActivity {


    private ProgressDialog progressDialog;
    private CompositeDisposable compositeDisposable;
    private SharedPreference sharedPreference;
    private ImageView back_btn;
    private TextView wallet_money;
    private TextView info_money;
    private TextView usable_money;
    private Button transhist_btn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        compositeDisposable = new CompositeDisposable();
        sharedPreference = new SharedPreference(this);
        progressDialog = new ProgressDialog(this);



        initViews();





    }



public void initViews(){

        back_btn = findViewById(R.id.back_btn);
        wallet_money = findViewById(R.id.wallet_money);
    usable_money = findViewById(R.id.usable_money);
    transhist_btn = findViewById(R.id.transhist_btn);
    info_money = findViewById(R.id.info_money);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });



        transhist_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WalletActivity.this,TransactionHistoryActivity.class);
                startActivity(intent);
            }
        });

        fetchuserdata(SharedPreference.getSharedPreferenceString(this,Constant.SELECTED_MOBILE_NO,""));
}


    public void AddmoneybtnAction(View view){

        String user_id = "1";
        startActivity(new Intent(WalletActivity.this,AddMoneyActivity.class).putExtra("bean",user_id ));

        finish();
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

                            fetchwalletData(value.getUserData().get(0).getId());


                        } else {

                            progressDialog.dismiss();

//                            Toast.makeText(LoginActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(WalletActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {


                    }
                }));


    }



    private void fetchwalletData(String cust_id) {

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.fetchWalletData(cust_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<WalletAmtResponse>() {
                    @Override
                    public void onNext(WalletAmtResponse value) {


                        if (!value.getError()) {

                            progressDialog.dismiss();

                            wallet_money.setText(value.getAmount());
                            info_money.setText("Money Used for Subscription Plan: "+value.getBlockMoney());
                            usable_money.setText("Usable Money :"+ ""+value.getRemAmount());

                        } else {

                            progressDialog.dismiss();

//                            Toast.makeText(LoginActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(WalletActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {


                    }
                }));


    }





}
