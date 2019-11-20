package com.synram.morningbucket.activities;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
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
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.synram.morningbucket.API.ApiClient;
import com.synram.morningbucket.API.ApiInterface;
import com.synram.morningbucket.API.JSONParser;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Modal.AddMoneyModal;
import com.synram.morningbucket.Modal.UserDataModal;
import com.synram.morningbucket.R;
import com.synram.morningbucket.SharedprefData.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import instamojo.library.InstamojoPay;
import instamojo.library.InstapayListener;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class AddMoneyActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback {


    LinearLayout loader;
    ConstraintLayout addmoney_lay;
    String phone_number = "";
    Button add_money_btn;
    SharedPreference sharedPreference;
    InstapayListener listener;
    private EditText et_money;
    private CompositeDisposable compositeDisposable;
    private SpinKitView progressBar;
    private ImageView back_btn;
    private String user_name;
    private String user_id;
    private String user_mob;
    private String email;
    private ProgressDialog progressDialog;
    private InstamojoPay instamojoPay;

    private String mid = "ZNCFax27821377936978";
    //    private String mid = "WQWHEA42261971626512";
    private String orderId = "";
    private String custId = "";
    private String rechargeMoney="";


    private void callInstamojoPay(String email, String phone, String amount, String purpose, String buyername) {
        final Activity activity = this;
        instamojoPay = new InstamojoPay();
        IntentFilter filter = new IntentFilter("ai.devsupport.instamojo");
        registerReceiver(instamojoPay, filter);

        JSONObject pay = new JSONObject();
        try {
            pay.put("email", email);
            pay.put("phone", phone);
            pay.put("purpose", purpose);
            pay.put("amount", amount);
            pay.put("name", buyername);
            pay.put("send_sms", true);
            pay.put("send_email", true);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        initListener(amount);
        instamojoPay.start(activity, pay, listener);
    }


    private void initListener(final String amount) {
        listener = new InstapayListener() {
            @Override
            public void onSuccess(String response) {
//                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG)
//                        .show();

//                addmorningBucketMoney(amount, response, user_id);

            }

            @Override
            public void onFailure(int code, String reason) {

                Toast.makeText(getApplicationContext(), "Failed: " + reason, Toast.LENGTH_LONG)
                        .show();
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_money);
        // Call the function callInstamojo to start payment here
        compositeDisposable = new CompositeDisposable();
        sharedPreference = new SharedPreference(this);
        progressDialog = new ProgressDialog(this);

        custId = "customer" + getRandomNumber(5, 100) + sharedPreference.getSharedPreferenceString(this, Constant.USER_ID, "");
        orderId = "order" + getRandomNumber(5, 100) + sharedPreference.getSharedPreferenceString(this, Constant.USER_ID, "");
        initViews();

        fetchuserdata(sharedPreference.getSharedPreferenceString(this, Constant.SELECTED_MOBILE_NO, ""));

        if (ContextCompat.checkSelfPermission(AddMoneyActivity.this, Manifest.permission.READ_SMS) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(AddMoneyActivity.this, new String[]{Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS}, 101);
        }


    }


    public void addmorningBucketMoney(String amount, String transaction_id, String user_id) {

        loader.setVisibility(View.VISIBLE);
        addmoney_lay.setVisibility(View.GONE);

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.saveWalletDetail(transaction_id, user_id, amount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<AddMoneyModal>() {
                    @Override
                    public void onNext(AddMoneyModal value) {


                        if (!value.getError()) {

                            Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    loader.setVisibility(View.GONE);
                                    addmoney_lay.setVisibility(View.VISIBLE);

                                    Toast.makeText(AddMoneyActivity.this, "Money Load Successfully", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(AddMoneyActivity.this, WalletActivity.class);
                                    startActivity(intent);
                                    finish();

                                }
                            }, 5000);


                        } else {
                            loader.setVisibility(View.GONE);
                            addmoney_lay.setVisibility(View.VISIBLE);

                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        loader.setVisibility(View.GONE);
                        addmoney_lay.setVisibility(View.VISIBLE);

                        Toast.makeText(AddMoneyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                }));


    }


    public void initViews() {


        et_money = findViewById(R.id.et_money);
        back_btn = findViewById(R.id.back_btn);
        loader = findViewById(R.id.loader);
        addmoney_lay = findViewById(R.id.addmoney_lay);

        add_money_btn = findViewById(R.id.add_money_btn);


        progressBar = (SpinKitView) findViewById(R.id.spin_kit);
        Sprite doubleBounce = new Circle();
        progressBar.setIndeterminateDrawable(doubleBounce);


        Intent intent = getIntent();

        if (intent.hasExtra("add_money")) {
         rechargeMoney =  intent.getStringExtra("add_money");
        }

        et_money.setText(rechargeMoney);

        add_money_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkzero()) {
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);

                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view_toast = inflater.inflate(R.layout.your_custom_toast, null);
                    toast.setView(view_toast);
                    toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 180);
                    toast.show();

                } else if (checklessthan100()) {
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);

                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view_toast = inflater.inflate(R.layout.your_custom_toast, null);
                    TextView text = view_toast.findViewById(R.id.msg);
                    text.setText("Minimum amount should be ₹10");

                    toast.setView(view_toast);
                    toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 180);

                    toast.show();

                } else if (checkblank()) {
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);

                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view_toast = inflater.inflate(R.layout.your_custom_toast, null);
                    toast.setView(view_toast);
                    toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 180);

                    toast.show();

                } else if (checkgraterthan100000()) {

                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);

                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view_toast = inflater.inflate(R.layout.your_custom_toast, null);
                    toast.setView(view_toast);
                    toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 180);

                    toast.show();

                } else {


//                    integrat Paytm over here


                    sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();
                    dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);


//                    callInstamojoPay(email,sharedPreference.getSharedPreferenceString(AddMoneyActivity.this,Constant.SELECTED_MOBILE_NO,""),et_money.getText().toString(),"Morning Bucket Wallet",user_name);


                }


            }
        });


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }


    public boolean checkzero() {
        try {
            if (Integer.parseInt(et_money.getText().toString()) == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }


    public boolean checklessthan100() {
        try {
            if (Integer.parseInt(et_money.getText().toString()) < 10) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean checkgraterthan100000() {

        try {
            if (Integer.parseInt(et_money.getText().toString()) > 10000) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean checkblank() {

        if (et_money.getText().toString().equals("")) {

            return true;
        } else {
            return false;
        }
    }


    public void add599(View view) {
        et_money.setText("599");
    }

    public void add999(View view) {
        et_money.setText("999");
    }

    public void add1999(View view) {
        et_money.setText("1999");
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

                            user_name = value.getUserData().get(0).getName();
                            user_id = value.getUserData().get(0).getId();
                            user_mob = value.getUserData().get(0).getMobileNumber();
                            email = value.getUserData().get(0).getEmailId();
//                            locality.setText(value.getUserData().get(0).getLocation());
//                            block_no.setText(value.getUserData().get(0).getBuildingNo());
//                            landmark.setText(value.getUserData().get(0).getLandMark());
//                            pincode.setText(value.getUserData().get(0).getPincode());

//                            city.setText(search_city);


//                            Toast.makeText(AddMoneyActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        } else {

                            progressDialog.dismiss();

//                            Toast.makeText(LoginActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(AddMoneyActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {


                    }
                }));


    }


    @Override
    protected void onStop() {
        super.onStop();

//            unregisterReceiver(instamojoPay);

        orderId = "order" + getRandomNumber(5, 100) + sharedPreference.getSharedPreferenceString(this, Constant.USER_ID, "");


    }

    private String getRandomNumber(int min, int max) {
        return String.valueOf((new Random()).nextInt((max - min) + 1) + min);
    }

    @Override
    public void onTransactionResponse(Bundle inResponse) {
        Log.d("checksum ", " respon true " + inResponse.toString());
//        Toast.makeText(this,inResponse+"",Toast.LENGTH_LONG).show();

        if (inResponse.getString("STATUS").equals("TXN_SUCCESS")) {

            addmorningBucketMoney(inResponse.getString("TXNAMOUNT"), inResponse.getString("TXNID"), user_id);

        }


    }

    @Override
    public void networkNotAvailable() {

        Toast.makeText(getApplicationContext(), "Network connection error: Check your internet connectivity", Toast.LENGTH_LONG).show();

    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {

        Toast.makeText(getApplicationContext(), "Authentication failed: Server error" + inErrorMessage.toString(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {
        Log.d("checksum ", " ui fail respon  " + inErrorMessage);
        Toast.makeText(getApplicationContext(), "UI Error " + inErrorMessage, Toast.LENGTH_LONG).show();

    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {

        Log.d("checksum ", " error loading pagerespon true " + iniErrorCode + "  s1 " + inErrorMessage);

        Toast.makeText(getApplicationContext(), "Unable to load webpage " + inErrorMessage.toString(), Toast.LENGTH_LONG).show();

    }

    @Override
    public void onBackPressedCancelTransaction() {
        Log.d("checksum ", " cancel call back respon  ");
        Toast.makeText(getApplicationContext(), "Transaction cancelled", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {

        Log.d("checksum ", "  transaction cancel ");
        Toast.makeText(getApplicationContext(), "Transaction Cancelled" + inResponse.toString(), Toast.LENGTH_LONG).show();

    }

    public class sendUserDetailTOServerdd extends AsyncTask<ArrayList<String>, Void, String> {
        //private String orderId , mid, custid, amt;
        String url = "https://www.morningbucket.in/generateChecksum.php";
        //        String url = "https://morningbucket.in/generateChecksumtest.php";
//                String callbackurl = "https://pguat.paytm.com/paytmchecksum/paytmCallback.jsp";
//        String callbackurl = "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID="+orderId;
        String callbackurl = "https://securegw.paytm.in/theia/paytmCallback?ORDER_ID=" + orderId;
        String varifyurl = "https://morningbucket.in/verifyChecksum.php";
        // "https://securegw-stage.paytm.in/theia/paytmCallback?ORDER_ID"+orderId;
        String CHECKSUMHASH = "";
        private ProgressDialog dialog = new ProgressDialog(AddMoneyActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }

        protected String doInBackground(ArrayList<String>... alldata) {
            JSONParser jsonParser = new JSONParser(AddMoneyActivity.this);
//            String param=
//                    "MID="+mid+
//                            "&ORDER_ID=" + orderId+
//                            "&CUST_ID="+sharedPreference.getSharedPreferenceString(AddMoneyActivity.this,Constant.USER_ID,"")+
//                            "&CHANNEL_ID=WAP&TXN_AMOUNT="+et_money.getText().toString()+"&WEBSITE=APPSTAGING"+
//                            "&INDUSTRY_TYPE_ID=Retail";
//            String param =
//                    "MID=" + mid +
//                            "&ORDER_ID="+orderId+
//                            "&CUST_ID="+custId+
//                            "&CALLBACK_URL="+ callbackurl+
//                            "&CHANNEL_ID=WAP&TXN_AMOUNT="+et_money.getText().toString().trim()+"&WEBSITE=DEFAULT"+
//                            "&INDUSTRY_TYPE_ID=Retail";


            String param =
                    "MID=" + mid +
                            "&ORDER_ID=" + orderId +
                            "&CUST_ID=" + custId +
                            "&CALLBACK_URL=" + callbackurl +
                            "&CHANNEL_ID=WAP&TXN_AMOUNT=" + et_money.getText().toString().trim() + "&WEBSITE=DEFAULT" +
                            "&INDUSTRY_TYPE_ID=Retail";


            JSONObject jsonObject = jsonParser.makeHttpRequest(url, "POST", param);
            // yaha per checksum ke saht order id or status receive hoga..
//          if (jsonObject!=null)
//            Log.e("CheckSum result >>",jsonObject.toString());

            Log.d("CheckSum result >>", jsonObject.toString());

            if (jsonObject != null) {
                Log.d("CheckSum result >>", jsonObject.toString());
                try {
                    CHECKSUMHASH = jsonObject.has("CHECKSUMHASH") ? jsonObject.getString("CHECKSUMHASH") : "";
                    Log.d("CheckSum result >>", CHECKSUMHASH);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            return CHECKSUMHASH;
        }

        @Override
        protected void onPostExecute(String result) {
            Log.e(" setup acc ", "  signup result  " + result);
            if (dialog.isShowing()) {
                dialog.dismiss();
            }
//            PaytmPGService Service = PaytmPGService.getStagingService();
            // when app is ready to publish use production service
            PaytmPGService Service = PaytmPGService.getProductionService();
            // now call paytm service here
            //below parameter map is required to construct PaytmOrder object, Merchant should replace below map values with his own values
            HashMap<String, String> paramMap = new HashMap<String, String>();
            //these are mandatory parameters
            paramMap.put("MID", mid); //MID provided by paytm
            paramMap.put("ORDER_ID", orderId);
            paramMap.put("CUST_ID", custId);
            paramMap.put("CHANNEL_ID", "WAP");
            paramMap.put("TXN_AMOUNT", et_money.getText().toString().trim());
            paramMap.put("WEBSITE", "DEFAULT");
            paramMap.put("CALLBACK_URL", callbackurl);
//            paramMap.put( "EMAIL" , "username@emailprovider.com");   // no need
//            paramMap.put( "MOBILE_NO" , "7777777777");  // no need
            paramMap.put("CHECKSUMHASH", CHECKSUMHASH);
            //paramMap.put("PAYMENT_TYPE_ID" ,"CC");    // no need
            paramMap.put("INDUSTRY_TYPE_ID", "Retail");

            PaytmOrder Order = new PaytmOrder(paramMap);

            Log.d("CHECKMY--", CHECKSUMHASH);
            Log.e("checksum ", "param " + paramMap.toString());

            Service.initialize(Order, null);
            // start payment service call here
            Service.startPaymentTransaction(AddMoneyActivity.this, true, true,
                    AddMoneyActivity.this);
        }
    }
}
