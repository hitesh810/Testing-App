package com.synram.morningbucket.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;
import com.google.gson.Gson;
import com.paytm.pgsdk.PaytmOrder;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;
import com.synram.morningbucket.API.ApiClient;
import com.synram.morningbucket.API.ApiInterface;
import com.synram.morningbucket.API.JSONParser;
import com.synram.morningbucket.Adapter.AdapterShoppingCart;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Database.DatabaseHandler;
import com.synram.morningbucket.Modal.AddMoneyModal;
import com.synram.morningbucket.Modal.Cart;
import com.synram.morningbucket.Modal.ChargesResponse;
import com.synram.morningbucket.Modal.Checkout;
import com.synram.morningbucket.Modal.Product;
import com.synram.morningbucket.Modal.ShiftResponse;
import com.synram.morningbucket.Modal.SubscriptionChargesResponse;
import com.synram.morningbucket.Modal.TimeslotResponse;
import com.synram.morningbucket.Modal.UserDataModal;
import com.synram.morningbucket.Modal.WalletAmtResponse;
import com.synram.morningbucket.R;
import com.synram.morningbucket.SharedprefData.SharedPreference;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import instamojo.library.InstamojoPay;
import instamojo.library.InstapayListener;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class CheckoutActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback {


    // construct dialog progress
    ProgressDialog progressDialog = null;
    private View parent_view;
    private Spinner date_shipping_sp, time_shiping_sp, payment_type_sp;
    private ImageButton bt_date_shipping;
    private TextView date_shipping;
    private RecyclerView recyclerView;
    private MaterialRippleLayout lyt_add_cart;
    private TextView total_order, tax, price_tax, total_fees, discount;
    private TextInputLayout buyer_name_lyt, email_lyt, phone_lyt, address_lyt, comment_lyt;
    private EditText buyer_name, email, phone, address, comment;
    private TextView discount_percent;
    private DatePickerDialog datePickerDialog;
    private AdapterShoppingCart adapter;
    private DatabaseHandler db;
    private SharedPreference sharedPreference;
    private Double _total_fees = 0D;
    private String _total_fees_str;
    private CompositeDisposable compositeDisposable;
    private List<Product> productList_ordered = new ArrayList<>();
    private String _price_total_tax_str;
    private String user_name;
    private String user_id;
    private String user_mob;
    private String email_str;
    private String address_str;
    private String zip_code;
    private int discount_charges;
    private int delivery_charges;
    private String _total_order_str = "";
    private String _price_tax_str = "";
    private String _total_discount_str = "";
    private List<Cart> items = new ArrayList<>();
    private int walletMoney;
    private Dialog dialog;
    private InstapayListener listener;
    private String subscriptionPlanType;
    private double sevendaysmoney = 0D;
    private List<String> shipping_date_list = new ArrayList<>();

    private List<String> shipping_time_list = new ArrayList<>();
    private List<String> payment_type_list = new ArrayList<>();
    private String selected_shift = "";
    private String selected_time = "";
    private String selected_payment_optn = "";
    private int subscriptionPlanId = 0;
    private double _cashback_price = 0D;
    private double remaining_money = 0;
    private double blockmoney = 0;
    private double cashback = 0D;
    private String appstatus = "0";
    private android.app.AlertDialog.Builder builder;
    private String statusmsg = "";


    //    private String mid = "WQWHEA42261971626512";
//    WQWHEA42261971626512
    private String mid = "ZNCFax27821377936978";

    private String orderId = "";
    private String custId = "";
    private String money = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_checkout);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        progressDialog = new ProgressDialog(CheckoutActivity.this);

        builder = new android.app.AlertDialog.Builder(this);
        db = new DatabaseHandler(this);
        sharedPreference = new SharedPreference(this);
        compositeDisposable = new CompositeDisposable();


        iniComponent();


        fetchuserdata(sharedPreference.getSharedPreferenceString(CheckoutActivity.this, Constant.SELECTED_MOBILE_NO, ""));

        custId = "customer" + getRandomNumber(5, 100) + sharedPreference.getSharedPreferenceString(this, Constant.USER_ID, "");
        orderId = "order" + getRandomNumber(5, 100) + sharedPreference.getSharedPreferenceString(this, Constant.USER_ID, "");

    }


    private void iniComponent() {
        parent_view = findViewById(android.R.id.content);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        lyt_add_cart = (MaterialRippleLayout) findViewById(R.id.lyt_add_cart);

        // cost view
        total_order = (TextView) findViewById(R.id.total_order);
        discount_percent = (TextView) findViewById(R.id.discount_percent);
        tax = (TextView) findViewById(R.id.tax);
        price_tax = (TextView) findViewById(R.id.price_tax);
        total_fees = (TextView) findViewById(R.id.total_fees);
        discount = (TextView) findViewById(R.id.discount);

        // form view
        buyer_name = (EditText) findViewById(R.id.buyer_name);
        email = (EditText) findViewById(R.id.email);
        phone = (EditText) findViewById(R.id.phone);
        address = (EditText) findViewById(R.id.address);
        comment = (EditText) findViewById(R.id.comment);


        buyer_name_lyt = (TextInputLayout) findViewById(R.id.buyer_name_lyt);
        email_lyt = (TextInputLayout) findViewById(R.id.email_lyt);
        phone_lyt = (TextInputLayout) findViewById(R.id.phone_lyt);
        address_lyt = (TextInputLayout) findViewById(R.id.address_lyt);
        comment_lyt = (TextInputLayout) findViewById(R.id.comment_lyt);

        bt_date_shipping = (ImageButton) findViewById(R.id.bt_date_shipping);
        date_shipping = (TextView) findViewById(R.id.date_shipping);
        date_shipping_sp = (Spinner) findViewById(R.id.date_shipping_sp);
        time_shiping_sp = (Spinner) findViewById(R.id.time_shiping_sp);
        payment_type_sp = (Spinner) findViewById(R.id.payment_type_sp);


        getshiftData();


        subscriptionPlanType = sharedPreference.getSharedPreferenceString(this, Constant.SELECTED_SUB_PLAN, "");

        if (subscriptionPlanType.equals("Get Once")) {

            payment_type_list.add("Cash On Delivery");
            payment_type_list.add("Wallet");

        } else {

//            payment_type_list.add("Cash On Delivery");
            payment_type_list.add("Wallet");


        }


        // Initialize and set Adapter
        ArrayAdapter adapter_payment_type = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, payment_type_list.toArray());
        adapter_payment_type.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        payment_type_sp.setAdapter(adapter_payment_type);


        lyt_add_cart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                if (appstatus.equals("0")) {


                    if (subscriptionPlanType.equals("Get Once")) {
                        if (selected_payment_optn.equals("Wallet")) {

                            if (remaining_money >= Double.parseDouble(_total_fees_str)) {

//                    Intent intent = new Intent(CheckoutActivity.this, CheckoutActivity.class);
//                    startActivity(intent);


                                submitForm();

                            } else {

                                try {

                                    if (remaining_money == 0) {

                                        showAddMoneyDialog(CheckoutActivity.this, String.valueOf(_total_fees - remaining_money));

                                    } else if (remaining_money > _total_fees) {

                                        showAddMoneyDialog(CheckoutActivity.this, String.valueOf(remaining_money - _total_fees));

                                    } else {

                                        showAddMoneyDialog(CheckoutActivity.this, String.valueOf(_total_fees - remaining_money));

                                    }


                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }


                        } else {


                            submitForm();
                        }

                    } else {

//                        Log.d("SelectedPaymentType", selected_payment_optn);

                        if (selected_payment_optn.equals("Wallet")) {

                            if (remaining_money >= sevendaysmoney) {

//                    Intent intent = new Intent(CheckoutActivity.this, CheckoutActivity.class);
//                    startActivity(intent);

                                if (remaining_money <= _total_fees) {
                                    blockmoney = remaining_money;

//                                    Log.d("remainMoney", String.valueOf(remaining_money));
                                    cashback = (blockmoney * _cashback_price) / 100;


//                                    Log.d("Cashback is: ", String.valueOf(cashback));
                                    submitForm();


                                } else {
                                    blockmoney = _total_fees;

//                                cashback =_cashback_price;

                                    cashback = (blockmoney * _cashback_price) / 100;


//                                    Log.d("Cashback is:", String.valueOf(cashback));


                                    submitForm();

//                                            after that i need to submit data with some of Extra keys
                                }

                            } else {

                                try {

                                    if (remaining_money == 0) {
                                        showAddMoneyDialog(CheckoutActivity.this, String.valueOf(sevendaysmoney - remaining_money));

                                    } else if (remaining_money > sevendaysmoney) {
                                        showAddMoneyDialog(CheckoutActivity.this, String.valueOf(remaining_money - sevendaysmoney));

                                    } else {
                                        showAddMoneyDialog(CheckoutActivity.this, String.valueOf(sevendaysmoney - remaining_money));

                                    }


                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }

                            }


                        } else {


                            submitForm();
                        }


                    }
                } else {

                    //Uncomment the below code to Set the message and title from the strings.xml file
                    builder.setMessage(statusmsg).setTitle("Sorry");


                    //Setting message manually and performing action on button click
                    builder.setMessage(statusmsg)
                            .setCancelable(true)
                            .setIcon(R.mipmap.ic_launcher)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.dismiss();

                                }
                            });

                    //Creating dialog box
                    android.app.AlertDialog alert = builder.create();
                    //Setting the title manually
                    alert.setTitle("Sorry");
                    alert.show();

                }


            }
        });


        date_shipping_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                selected_shift = date_shipping_sp.getSelectedItem().toString();
                gettimeslot(date_shipping_sp.getSelectedItem().toString());

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        time_shiping_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selected_time = time_shiping_sp.getSelectedItem().toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        payment_type_sp.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {


                if (subscriptionPlanType.equals("Get Once")) {
                    if (position == 0) {
                        selected_payment_optn = "COD";

                    }
                    if (position == 1) {
                        selected_payment_optn = "Wallet";

                    }

                } else {
                    selected_payment_optn = "Wallet";
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        if (subscriptionPlanType.equals("Get Once")) {

            date_shipping_sp.setVisibility(View.VISIBLE);
            time_shiping_sp.setVisibility(View.VISIBLE);

            subscriptionPlanId = 1;

        } else if (subscriptionPlanType.equals("Monthly")) {
            date_shipping_sp.setVisibility(View.VISIBLE);
            time_shiping_sp.setVisibility(View.VISIBLE);

            subscriptionPlanId = 2;


        } else if (subscriptionPlanType.equals("Alternative")) {

            date_shipping_sp.setVisibility(View.VISIBLE);
            time_shiping_sp.setVisibility(View.VISIBLE);

            subscriptionPlanId = 3;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        displayData();
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

                            walletMoney = Integer.parseInt(value.getAmount());
                            remaining_money = value.getRemAmount();
                            appstatus = value.getOnline_status();
                            statusmsg = value.getStatus_msg();


                        } else {

                            progressDialog.dismiss();

                            Toast.makeText(CheckoutActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(CheckoutActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {


                    }
                }));


    }


    private void getshiftData() {

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.getshift()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<ShiftResponse>() {
                    @Override
                    public void onNext(ShiftResponse value) {


                        shipping_date_list.clear();
                        if (!value.getError()) {

                            progressDialog.dismiss();

                            for (int i = 0; i < value.getShiftData().size(); i++) {


                                shipping_date_list.add(value.getShiftData().get(i).getShiftName());


                            }


                        } else {

                            progressDialog.dismiss();

                            Toast.makeText(CheckoutActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(CheckoutActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        try {
                            if (shipping_date_list.size() > 0) {

                                // Initialize and set Adapter
                                ArrayAdapter adapter_shipping_date = new ArrayAdapter<>(CheckoutActivity.this, android.R.layout.simple_spinner_item, shipping_date_list.toArray());
                                adapter_shipping_date.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                date_shipping_sp.setAdapter(adapter_shipping_date);


                            }


                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                }));


    }

    private void gettimeslot(String shift) {

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.gettimeingslot(shift)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(new DisposableObserver<TimeslotResponse>() {
                    @Override
                    public void onNext(TimeslotResponse value) {

                        shipping_time_list.clear();
                        if (!value.getError()) {

                            progressDialog.dismiss();

                            for (int i = 0; i < value.getTimeSlotData().size(); i++) {


                                shipping_time_list.add(value.getTimeSlotData().get(i).getTimeSlot());


                            }


                        } else {

                            progressDialog.dismiss();

                            Toast.makeText(CheckoutActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(CheckoutActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        try {
                            if (shipping_date_list.size() > 0) {

                                // Initialize and set Adapter
                                ArrayAdapter adapter_shipping_time = new ArrayAdapter<>(CheckoutActivity.this, android.R.layout.simple_spinner_item, shipping_time_list.toArray());
                                adapter_shipping_time.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                time_shiping_sp.setAdapter(adapter_shipping_time);

                            }


                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }

                    }
                }));


    }


    private void displayData() {
        List<Cart> items = db.getActiveCartList();
        adapter = new AdapterShoppingCart(CheckoutActivity.this, false, items, new AdapterShoppingCart.OnItemClickListener() {
            @Override
            public void onItemClick(View view, Cart obj, int position) {

            }
        });
        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);
        setTotalPrice();

//        if (buyerProfile != null) {
//            buyer_name.setText(buyerProfile.name);
//            email.setText(buyerProfile.email);
//            phone.setText(buyerProfile.phone);
//            address.setText(buyerProfile.address);
//        }
    }

    private void setTotalPrice() {

        if (subscriptionPlanType.equals("Get Once")) {

            checkchargesornot();

        } else {

            checkSubscriptionchargesornot();
        }


    }


    private void submitForm() {

//
//        buyerProfile = new BuyerProfile();
//        buyerProfile.name = buyer_name.getText().toString();
//        buyerProfile.email = email.getText().toString();
//        buyerProfile.phone = phone.getText().toString();
//        buyerProfile.address = address.getText().toString();
//        sharedPref.setBuyerProfile(buyerProfile);
//
        // hide keyboard
        hideKeyboard();

        // show dialog confirmation
        dialogConfirmCheckout();
    }


//    private void submitOrderData() {
//        // prepare checkout data
//        Checkout checkout = new Checkout();
//        ProductOrder productOrder = new ProductOrder(buyerProfile, shipping.getSelectedItem().toString(), date_ship_millis, comment.getText().toString().trim());
//        productOrder.status = "WAITING";
//        productOrder.total_fees = _total_fees;
//        productOrder.tax = info.tax;
//
//        checkout.product_order = productOrder;
//        checkout.product_order_detail = new ArrayList<>();
//        for (Cart c : adapter.getItem()) {
//            ProductOrderDetail pod = new ProductOrderDetail(c.product_id, c.product_name, c.amount, c.price_item);
//            checkout.product_order_detail.add(pod);
//        }
//
//        // submit data to server
//        API api = RestAdapter.createAPI();
//        callbackCall = api.submitProductOrder(checkout);
//        callbackCall.enqueue(new Callback<CallbackOrder>() {
//            @Override
//            public void onResponse(Call<CallbackOrder> call, Response<CallbackOrder> response) {
//                CallbackOrder resp = response.body();
//                if (resp != null && resp.status.equals("success")) {
//                    Order order = new Order(resp.data.id, resp.data.code, _total_fees_str);
//                    for (Cart c : adapter.getItem()) {
//                        c.order_id = order.id;
//                        order.cart_list.add(c);
//                    }
//                    db.saveOrder(order);
//                    dialogSuccess(order.code);
//                } else {
//                    dialogFailedRetry();
//                }
//
//            }
//
//            @Override
//            public void onFailure(Call<CallbackOrder> call, Throwable t) {
//                Log.e("onFailure", t.getMessage());
//                if (!call.isCanceled()) dialogFailedRetry();
//            }
//        });
//    }

    // give delay when submit data to give good UX
    private void delaySubmitOrderData() {
        progressDialog.show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

            }
        }, 2000);
    }

    public void dialogConfirmCheckout() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.confirmation);
        builder.setMessage(getString(R.string.confirm_checkout));
        builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // create a new Gson instance
                Gson gson = new Gson();
                // convert your list to json
                String jsonCartList = gson.toJson(items);
                // print your generated json
                System.out.println("jsonCartList: " + jsonCartList);


                submitCheckoutData(user_id, user_name, user_mob, email_str, address.getText().toString(), zip_code, jsonCartList, _total_fees_str, _price_tax_str, _total_discount_str, selected_time, selected_shift, selected_payment_optn, String.valueOf(subscriptionPlanId), String.valueOf(blockmoney), String.valueOf(cashback));

            }
        });
        builder.setNegativeButton(R.string.NO, null);
        builder.show();
    }


    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
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

                            user_name = value.getUserData().get(0).getName();
                            user_id = value.getUserData().get(0).getId();
                            user_mob = value.getUserData().get(0).getMobileNumber();
                            email_str = value.getUserData().get(0).getEmailId();
                            address_str = value.getUserData().get(0).getAddress();
                            zip_code = value.getUserData().get(0).getPincode();

                            buyer_name.setText(user_name);
                            email.setText(email_str);
                            phone.setText(user_mob);
                            address.setText(address_str);
                            comment.setText(address_str);


                        } else {

                            progressDialog.dismiss();

//                            Toast.makeText(LoginActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(CheckoutActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {


                    }
                }));


    }


    private void checkchargesornot() {

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.getchargesValue()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<ChargesResponse>() {
                    @SuppressLint("DefaultLocale")
                    @Override
                    public void onNext(ChargesResponse value) {


                        if (!value.getError()) {

                            progressDialog.dismiss();
//                            Logic Start from here

                            items = adapter.getItem();
                            Double _total_order = 0D, _price_tax = 0D;
                            Double _discount_price = 0D;
                            //        _total_order_str,

                            for (Cart c : items) {
                                _total_order = _total_order + (c.amount * c.price_item);
                            }


//                            end of logic start from here


                            if (_total_order >= Double.parseDouble(value.getDelMinAmount())) {
                                delivery_charges = 0;

                            } else {


                                delivery_charges = Integer.parseInt(value.getDeliveryAmount());


                            }

                            if (_total_order >= Double.parseDouble(value.getDisMinAmount())) {

                                discount_charges = Integer.parseInt(value.getDisAmount());

                            } else {
                                discount_charges = 0;
                            }

//                            fetchwalletData(value.getUserData().get(0).getId());


                            _price_tax = Double.parseDouble(String.valueOf(delivery_charges));

                            _discount_price = (_total_order * discount_charges) / 100;

                            _total_fees = _total_order + _price_tax - _discount_price;

                            _price_tax_str = String.format("%.2f", _price_tax);
                            _total_order_str = String.format("%.2f", _total_order);
                            _total_fees_str = String.format("%.2f", _total_fees);
                            _total_discount_str = String.format("%.2f", _discount_price);

                            // set to display
                            total_order.setText("₹ " + _total_order_str);
//        tax.setText(getString(R.string.tax) + _price_tax_str);
                            tax.setText("Delivery Charges");

                            price_tax.setText("₹ " + _price_tax_str);

                            if (discount_charges == 0) {

                                discount_percent.setText("No Discount");


                            } else {
                                discount_percent.setText(discount_charges + "%");

                            }


                            total_fees.setText("₹" + _total_fees_str);


                        } else {

                            progressDialog.dismiss();

//                            Toast.makeText(LoginActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(CheckoutActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {


                    }
                }));


    }


    private void checkSubscriptionchargesornot() {

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.getsubscriptionchargesValue()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<SubscriptionChargesResponse>() {
                    @Override
                    public void onNext(SubscriptionChargesResponse value) {


                        if (!value.getError()) {

                            progressDialog.dismiss();
//                            Logic Start from here

                            items = adapter.getItem();
                            Double _total_order = 0D, _price_tax = 0D;
                            _cashback_price = 0D;
                            //        _total_order_str,

                            for (Cart c : items) {
                                _total_order = _total_order + (c.amount * c.price_item);
                            }


//                            end of logic start from here


                            Log.d("DILIVERY", value.getDeliveryAmount().toString());
                            if (_total_order >= Double.parseDouble(value.getDelMinAmount())) {
                                delivery_charges = 0;

                            } else {


                                delivery_charges = Integer.parseInt(value.getDeliveryAmount());


                            }

//                            if (_total_order >= Double.parseDouble(value.getDisMinAmount())){
//
//                                discount_charges = Integer.parseInt(value.getDisAmount());
//
//                            }else {
//
//                                discount_charges = 0;
//
//                            }

//                            fetchwalletData(value.getUserData().get(0).getId());


                            _price_tax = Double.parseDouble(String.valueOf(delivery_charges));

//                            _discount_price = (_total_order * discount_charges)/100;

                            _total_fees = _total_order + _price_tax;

                            _price_tax_str = String.format("%.2f", _price_tax);
                            _total_order_str = String.format("%.2f", _total_order);
                            _total_fees_str = String.format("%.2f", _total_fees);
//                            _total_discount_str = String.format(Locale.US, "%1$,.2f", _discount_price);

//                            _cashback_price =(_total_fees*Integer.parseInt(value.getDisAmount()))/100;
                            _cashback_price = Double.parseDouble(value.getDisAmount());

                            Log.d("Cashback_DATA", String.valueOf(_cashback_price + "of %" + value.getDisAmount()));


                            // set to display
                            total_order.setText("₹ " + _total_order_str);
//        tax.setText(getString(R.string.tax) + _price_tax_str);
                            tax.setText("Delivery Charges");

                            price_tax.setText("₹ " + _price_tax_str);

//                            discount_percent.setText(discount_charges+ "%");
                            discount_percent.setVisibility(View.GONE);

                            total_fees.setText("₹" + _total_fees_str);

                            Log.d("TOTALFEE", _total_fees_str.toString());

                            discount.setVisibility(View.GONE);

                            sevendaysmoney = (_total_fees / 30) * 7;


                        } else {

                            progressDialog.dismiss();

//                            Toast.makeText(LoginActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(CheckoutActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {


                    }
                }));


    }


    private void submitCheckoutData(String cust_id, String cust_name, String mobile_number, String email, String address, String zipcode, String pro_array, String amount, String delivery_charges, String discountCharges, String time_slot, String shift, String payment_type, String checkout_type, String block_money, String cash_back) {

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        Log.d("AMT", String.valueOf(amount.toString()));
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.checkout(cust_id, cust_name, mobile_number, email, address, zipcode, pro_array, amount, delivery_charges, discountCharges, time_slot, shift, payment_type, checkout_type, block_money, cash_back)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<Checkout>() {
                    @Override
                    public void onNext(Checkout value) {

//                        Log.d("RESPO_CHECKOUT",new Gson().toJson(value));

                        if (!value.getError()) {

                            progressDialog.dismiss();


                            for (int i = 0; i < value.getOrderDetails().getProduct().size(); i++) {
                                Product product = new Product(
                                        value.getOrderDetails().getProduct().get(i).getProName(),
                                        value.getOrderDetails().getProduct().get(i).getPrice(),
                                        value.getOrderDetails().getProduct().get(i).getQuantity()
                                );

                                productList_ordered.add(product);
                            }


                            Gson gson = new Gson();
                            // convert your list to json
                            String jsonCartList = gson.toJson(productList_ordered);
                            // print your generated json
                            System.out.println("jsonCartList: " + jsonCartList);

                            sharedPreference.setSharedPreferenceString(CheckoutActivity.this, Constant.CHECKOUT_ORDERED_PRODUCTS, jsonCartList);
                            sharedPreference.setSharedPreferenceString(CheckoutActivity.this, Constant.CHECKOUT_USERNAME, value.getOrderDetails().getName());
                            sharedPreference.setSharedPreferenceString(CheckoutActivity.this, Constant.CHECKOUT_CONTACT, value.getOrderDetails().getContact());
                            sharedPreference.setSharedPreferenceString(CheckoutActivity.this, Constant.CHECKOUT_EMAIL, value.getOrderDetails().getEmail());
                            sharedPreference.setSharedPreferenceString(CheckoutActivity.this, Constant.CHECKOUT_TOTALAMT, value.getOrderDetails().getTotalAmount());
                            sharedPreference.setSharedPreferenceString(CheckoutActivity.this, Constant.CHECKOUT_ORDERED_NUMBER, value.getOrderDetails().getOrderNo());
                            sharedPreference.setSharedPreferenceString(CheckoutActivity.this, Constant.CHECKOUT_ADDRESS, value.getOrderDetails().getAddress());
                            sharedPreference.setSharedPreferenceString(CheckoutActivity.this, Constant.CHECKOUT_DISCOUNT, value.getOrderDetails().getDiscount());
                            sharedPreference.setSharedPreferenceString(CheckoutActivity.this, Constant.CHECKOUT_DEVLIVERY_CHARGE, value.getOrderDetails().getDeliveryCharge());
                            sharedPreference.setSharedPreferenceString(CheckoutActivity.this, Constant.CHECKOUT_ORDER_DATE, value.getOrderDetails().getOrderDate());
                            sharedPreference.setSharedPreferenceString(CheckoutActivity.this, Constant.CHECKOUT_ORDER_DAY, value.getOrderDetails().getDay());
                            sharedPreference.setSharedPreferenceString(CheckoutActivity.this, Constant.CHECKOUT_ORDER_ID, value.getOrderDetails().getOrderNo());
                            sharedPreference.setSharedPreferenceString(CheckoutActivity.this, Constant.CHECKOUT_CART_TOTAL, total_order.getText().toString());


                            db.deleteActiveCart();
                            Intent intent = new Intent(CheckoutActivity.this, OrderPlaceSuccesActivity.class);


                            startActivity(intent);
                            finish();
//

//                            fetchwalletData(value.getUserData().get(0).getId());


                        } else {

                            progressDialog.dismiss();

                            Toast.makeText(CheckoutActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(CheckoutActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {


                    }
                }));


    }


    public void showAddMoneyDialog(final Context context, final String needed_money) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.wallet_money_insufficient_dialog, null, false);
        /*HERE YOU CAN FIND YOU IDS AND SET TEXTS OR BUTTONS*/

        final EditText et_money = view.findViewById(R.id.et_money);
        final LinearLayout offerWindow = view.findViewById(R.id.offerWindow);
        final TextView offer = view.findViewById(R.id.offer);
        final TextView totalWallet_Amount = view.findViewById(R.id.totalWallet_Amount);
        final TextView minium_amt = view.findViewById(R.id.minium_amt);

        offer.setText("Offer: Get " + _cashback_price + "%" + " cashback on every payment made for monthly subscription plan ");

        if (subscriptionPlanType.equals(Constant.SUB_TYPE_MONTH) || subscriptionPlanType.equals(Constant.SUB_TYPE_ALTER)) {
            offerWindow.setVisibility(View.VISIBLE);
        } else {
            offerWindow.setVisibility(View.GONE);

        }


        totalWallet_Amount.setText("Total Amount to be pay:  " + "₹" + String.valueOf(_total_fees));
        minium_amt.setText("Minimum Amount To be pay:  " + "₹" + String.valueOf(String.format("%.2f", sevendaysmoney)));

        et_money.setText(String.valueOf(needed_money));

        Button addmoney = view.findViewById(R.id.addmoney);

        addmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                money = et_money.getText().toString();

                if (checkzero(et_money)) {
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);

                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view_toast = inflater.inflate(R.layout.your_custom_toast, null);
                    toast.setView(view_toast);
                    TextView text = view_toast.findViewById(R.id.msg);
                    text.setText("Minimum amount should be ₹10");

                    toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 180);
                    toast.show();

                } else if (checklessthan100(et_money)) {
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);

                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view_toast = inflater.inflate(R.layout.your_custom_toast, null);
                    TextView text = view_toast.findViewById(R.id.msg);
                    text.setText("Minimum amount should be ₹10");

                    toast.setView(view_toast);
                    toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 180);

                    toast.show();

                } else if (checkblank(et_money)) {
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);

                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view_toast = inflater.inflate(R.layout.your_custom_toast, null);
                    toast.setView(view_toast);
                    toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 180);

                    toast.show();

                } else if (checkgraterthan100000(et_money)) {
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);

                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view_toast = inflater.inflate(R.layout.your_custom_toast, null);
                    toast.setView(view_toast);
                    toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 180);

                    toast.show();

                } else if (checkminiOrder(et_money,needed_money)) {


                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);

                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view_toast = inflater.inflate(R.layout.your_custom_toast, null);
                    TextView text = view_toast.findViewById(R.id.msg);



                    text.setText("Minimum amount should be " + getResources().getString(R.string.currency) + needed_money);

                    toast.setView(view_toast);
                    toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 180);

                    toast.show();

                } else {


                    sendUserDetailTOServerdd dl = new sendUserDetailTOServerdd();
                    dl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);

                    dialog.dismiss();


                }

            }
        });


        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setContentView(view);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setGravity(Gravity.CENTER);
        dialog.show();
    }


    private void callInstamojoPay(String email, String phone, String amount, String purpose, String buyername) {
        final Activity activity = this;
        InstamojoPay instamojoPay = new InstamojoPay();
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

    private String getRandomNumber(int min, int max) {
        return String.valueOf((new Random()).nextInt((max - min) + 1) + min);
    }

    private void initListener(final String amount) {
        listener = new InstapayListener() {
            @Override
            public void onSuccess(String response) {
//                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG)
//                        .show();

//                dialog.dismiss();
                addmorningBucketMoney(amount, response, user_id);

            }

            @Override
            public void onFailure(int code, String reason) {
                Toast.makeText(getApplicationContext(), "Failed: " + reason, Toast.LENGTH_LONG)
                        .show();
            }
        };
    }

    public void addmorningBucketMoney(String amount, String transaction_id, final String user_id) {

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.saveWalletDetail(transaction_id, user_id, amount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<AddMoneyModal>() {
                    @Override
                    public void onNext(AddMoneyModal value) {


                        if (!value.getError()) {


                            progressDialog.dismiss();
                            Toast.makeText(CheckoutActivity.this, "Money Load Successfully", Toast.LENGTH_SHORT).show();

//                            Intent intent = new Intent(CheckoutActivity.this,CheckoutActivity.class);
//                            startActivity(intent);
//                            finish();

                            Gson gson = new Gson();
                            // convert your list to json
                            String jsonCartList = gson.toJson(items);
                            // print your generated json
                            System.out.println("jsonCartList: " + jsonCartList);


//                            submitCheckoutData(user_id,user_name,user_mob,email_str,address.getText().toString(),zip_code,jsonCartList,_total_fees_str,_price_tax_str,_total_discount_str,selected_time,selected_shift,selected_payment_optn,String.valueOf(subscriptionPlanId));

                            fetchwalletData(user_id);
//
//                            if (subscriptionPlanType.equals("Get Once")){
//                                if (selected_payment_optn.equals("Wallet")){
//
//                                    if (remaining_money>=Double.parseDouble(_total_fees_str)){
//
////                    Intent intent = new Intent(CheckoutActivity.this, CheckoutActivity.class);
////                    startActivity(intent);
//
//
//                                        submitForm();
//
//                                    }else {
//
//                                        try{
//
//                                            if (remaining_money==0){
//                                                showAddMoneyDialog(CheckoutActivity.this,String.valueOf(_total_fees-remaining_money));
//
//                                            }else if (remaining_money > _total_fees){
//                                                showAddMoneyDialog(CheckoutActivity.this,String.valueOf(remaining_money-_total_fees));
//
//                                            }else{
//                                                showAddMoneyDialog(CheckoutActivity.this,String.valueOf(_total_fees-remaining_money));
//
//                                            }
//
//
//                                        }catch (Exception ex){
//                                            ex.printStackTrace();
//                                        }
//
//                                    }
//
//
//                                }else {
//
//
//                                    submitForm();
//                                }
//
//                            }else {
//
//                                if (selected_payment_optn.equals("Wallet")){
//
//                                    if (remaining_money>=sevendaysmoney){
//
////                    Intent intent = new Intent(CheckoutActivity.this, CheckoutActivity.class);
////                    startActivity(intent);
//
//                                        if (remaining_money<=_total_fees){
//                                            blockmoney =remaining_money;
//                                            cashback =0;
//                                            submitForm();
//
//                                        }else {
//                                            blockmoney = _total_fees;
//                                            cashback =_cashback_price;
//
//                                            submitForm();
//
////                                            after that i need to submit data with some of Extra keys
//                                        }
//
//
//
//
//                                    }else {
//
//                                        try{
//
//                                            if (remaining_money==0){
//                                                showAddMoneyDialog(CheckoutActivity.this,String.valueOf(sevendaysmoney-remaining_money));
//
//                                            }else if (remaining_money > sevendaysmoney){
//                                                showAddMoneyDialog(CheckoutActivity.this,String.valueOf(remaining_money-sevendaysmoney));
//
//                                            }else{
//                                                showAddMoneyDialog(CheckoutActivity.this,String.valueOf(sevendaysmoney-remaining_money));
//
//                                            }
//
//
//                                        }catch (Exception ex){
//                                            ex.printStackTrace();
//                                        }
//
//                                    }
//
//
//                                }else {
//
//
//                                    submitForm();
//                                }


//                            submitForm();


//                            }


                        } else {
                            progressDialog.dismiss();

                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(CheckoutActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                }));


    }

    public boolean checkzero(EditText et_money) {
        try {
            if (Float.parseFloat(et_money.getText().toString()) == 0) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean checklessthan100(EditText et_money) {
        try {
            if (Float.parseFloat(et_money.getText().toString()) < 10) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean checkgraterthan100000(EditText et_money) {

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

    public boolean checkminiOrder(EditText et_money,String neededmoney) {

        try {
            if (Float.parseFloat(et_money.getText().toString()) < Float.parseFloat(neededmoney)) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean checkblank(EditText et_money) {

        if (et_money.getText().toString().equals("")) {

            return true;
        } else {
            return false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dialog != null) {
            dialog.dismiss();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (dialog != null) {
            dialog.dismiss();
        }

        orderId = "order" + getRandomNumber(5, 100) + sharedPreference.getSharedPreferenceString(this, Constant.USER_ID, "");

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
        private ProgressDialog dialog = new ProgressDialog(CheckoutActivity.this);

        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Please wait");
            this.dialog.show();
        }

        protected String doInBackground(ArrayList<String>... alldata) {
            JSONParser jsonParser = new JSONParser(CheckoutActivity.this);
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
                            "&CHANNEL_ID=WAP&TXN_AMOUNT=" + money + "&WEBSITE=DEFAULT" +
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
            paramMap.put("TXN_AMOUNT", money);
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
            Service.startPaymentTransaction(CheckoutActivity.this, true, true,
                    CheckoutActivity.this);
        }
    }
}
