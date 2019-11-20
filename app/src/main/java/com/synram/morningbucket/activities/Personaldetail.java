package com.synram.morningbucket.activities;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.synram.morningbucket.API.ApiClient;
import com.synram.morningbucket.API.ApiInterface;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Modal.UserDataModal;
import com.synram.morningbucket.R;
import com.synram.morningbucket.SharedprefData.SharedPreference;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.logging.LogRecord;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class Personaldetail extends AppCompatActivity {


    TextView user_name,email_id,mobile,address;

    FloatingActionButton logout;
    private  AlertDialog.Builder builder;

    private ProgressDialog progressDialog;
    private CompositeDisposable compositeDisposable;
    private ImageView back_btn;
    private SharedPreference sharedPreference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personaldetail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(this);
        compositeDisposable = new CompositeDisposable();
        sharedPreference = new SharedPreference(this);
        builder = new AlertDialog.Builder(this);


        initview();

    }


    public void initview(){
        user_name = findViewById(R.id.user_name);
        email_id = findViewById(R.id.email_id);
        mobile = findViewById(R.id.mobile);
        back_btn = findViewById(R.id.back_btn);
        address = findViewById(R.id.address);
        logout = findViewById(R.id.floatingActionButton);


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        fetchuserdata(SharedPreference.getSharedPreferenceString(this,Constant.SELECTED_MOBILE_NO,""));
//            fetchuserdata("8130137993");

            logout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    //Uncomment the below code to Set the message and title from the strings.xml file
                    builder.setMessage("Are you sure?") .setTitle("Logout");

                    //Setting message manually and performing action on button click
                    builder.setMessage("Are you sure?")
                            .setCancelable(true)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();
                                    sharedPreference.removeAllPrefs(Personaldetail.this);

                                    FirebaseAuth.getInstance().signOut();
                                    Intent intent = new Intent(Personaldetail.this,LoginActivity.class);
                                    startActivity(intent);


                                    finishAffinity();

                                }
                            })
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    //  Action for 'NO' Button
                                    dialog.dismiss();
//                                    Toast.makeText(getApplicationContext(),"you choose no action for alertbox",
//                                            Toast.LENGTH_SHORT).show();
                                }
                            });
                    //Creating dialog box
                    AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("Logout");
                    alert.show();
                }



            });
    }


    private void fetchuserdata(final String mobile_number) {

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

                            user_name.setText(value.getUserData().get(0).getName());
                            email_id.setText(value.getUserData().get(0).getEmailId());
                            mobile.setText(mobile_number);

//                            address.setText(value.getUserData().get(0).getBuildingNo()+", "+ value.getUserData().get(0).getAddress()+" "+value.getUserData().get(0).getCity_name()+", "+value.getUserData().get(0).getPincode());
                            address.setText(value.getUserData().get(0).getAddress()+" "+SharedPreference.getSharedPreferenceString(Personaldetail.this,Constant.SELECTED_CITY_NAME,""));
//                            block_no.setText(value.getUserData().get(0).getBuildingNo());
//                            landmark.setText(value.getUserData().get(0).getLandMark());
//                            pincode.setText(value.getUserData().get(0).getPincode());
//
//                            city.setText(search_city);


//                            Toast.makeText(Personaldetail.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        } else {

                            progressDialog.dismiss();

//                            Toast.makeText(Personaldetail.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(Personaldetail.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {


                    }
                }));


    }


}
