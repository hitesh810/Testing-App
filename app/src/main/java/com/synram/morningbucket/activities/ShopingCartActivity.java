package com.synram.morningbucket.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.synram.morningbucket.API.ApiClient;
import com.synram.morningbucket.API.ApiInterface;
import com.synram.morningbucket.Adapter.AdapterShoppingCart;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Database.DatabaseHandler;
import com.synram.morningbucket.Modal.AddMoneyModal;
import com.synram.morningbucket.Modal.Cart;
import com.synram.morningbucket.Modal.ChargesResponse;
import com.synram.morningbucket.Modal.UserDataModal;
import com.synram.morningbucket.Modal.WalletAmtResponse;
import com.synram.morningbucket.R;
import com.synram.morningbucket.SharedprefData.SharedPreference;
import com.synram.morningbucket.Utilities.CountDrawable;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Queue;
import java.util.logging.LogRecord;

import instamojo.library.InstamojoPay;
import instamojo.library.InstapayListener;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class ShopingCartActivity extends AppCompatActivity {


    private View parent_view;
    private RecyclerView recyclerView;
    private DatabaseHandler db;
    private AdapterShoppingCart adapter;
    public static TextView price_total;
//    private SharedPref sharedPref;
//    private Info info;
    InstapayListener listener;

    private SharedPreference sharedPreference;
    private ImageView back_btn;
    private AlertDialog.Builder delcart_builder;
    private TextView check_out;
    private ProgressDialog progressDialog;
    private CompositeDisposable compositeDisposable;
    private int walletMoney;
    private String _price_total_tax_str;
    private Dialog dialog;
    private String user_name;
    private String user_id;
    private String user_mob;
    private String email;
    private List<Cart> items = new ArrayList<>();
    private int delivery_charges;
    private int discount_charges;
    private String activity_type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shoping_cart);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayShowHomeEnabled(true);
        progressDialog = new ProgressDialog(this);
        compositeDisposable = new CompositeDisposable();
        sharedPreference = new SharedPreference(this);
        db = new DatabaseHandler(this);


        activity_type = sharedPreference.getSharedPreferenceString(this,Constant.Activity_Type,"");

        fetchuserdata(sharedPreference.getSharedPreferenceString(this,Constant.SELECTED_MOBILE_NO,""));
        iniComponent();
    }


    private void iniComponent() {
        parent_view = findViewById(android.R.id.content);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        price_total = (TextView) findViewById(R.id.price_total);
        check_out = (TextView) findViewById(R.id.check_out);
        back_btn = (ImageView) findViewById(R.id.back_btn);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
            @Override
            public void onClick(View v) {

                if (activity_type.equals("pd")){
                    Intent intent = new Intent(ShopingCartActivity.this,ProductsDescriptionActivity.class);
                    startActivity(intent);
                    sharedPreference.setSharedPreferenceString(ShopingCartActivity.this,Constant.Activity_Type,"");
                    finish();

                }else{
                    Intent intent = new Intent(ShopingCartActivity.this,MainActivity.class);
                    startActivity(intent);
                    finishAffinity();


                }


            }
        });
        check_out.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (adapter.getItemCount() > 0) {

//                    if (walletMoney>=Double.parseDouble(_price_total_tax_str)){

                    Intent intent = new Intent(ShopingCartActivity.this, CheckoutActivity.class);
                    startActivity(intent);


//                    }else {


//                        showAddMoneyDialog(ShopingCartActivity.this,String.valueOf(Double.parseDouble(_price_total_tax_str)-walletMoney));

//                    }

                } else {
                    Snackbar.make(parent_view, R.string.msg_cart_empty, Snackbar.LENGTH_SHORT).show();
                }

            }
        });

    }




    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_shopping_cart, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            super.onBackPressed();


//            Intent intent = new Intent(ShopingCartActivity.this,MainActivity.class);
//            startActivity(intent);
//            finish();
        }
//        else if (item_id == R.id.action_checkout) {
//            if (adapter.getItemCount() > 0) {
//                Intent intent = new Intent(ActivityShoppingCart.this, ActivityCheckout.class);
//                startActivity(intent);
//            } else {
//                Snackbar.make(parent_view, R.string.msg_cart_empty, Snackbar.LENGTH_SHORT).show();
//            }
//        }
        else if (item_id == R.id.action_delete) {
            if (adapter.getItemCount() == 0) {
                Snackbar.make(parent_view, R.string.msg_cart_empty, Snackbar.LENGTH_SHORT).show();
                return true;
            }
            dialogDeleteConfirmation();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
//        new AlertDialog.Builder(this)
//                .setTitle("Exiting the App")
//                .setMessage("Are you sure?")
//                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int whichButton) {
//                        // The user wants to leave - so dismiss the dialog and exit
////                        finish();
//                        dialog.dismiss();
//                    }
//                }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int whichButton) {
//                // The user is not sure, so you can exit or just stay
//                dialog.dismiss();
//            }
//        }).show();

    }






    @Override
    protected void onResume() {
        super.onResume();
        displayData();
    }

    private void displayData() {
        final List<Cart> items = db.getActiveCartList();
        adapter = new AdapterShoppingCart(this, true, items, new AdapterShoppingCart.OnItemClickListener() {

            @Override
            public void onItemClick(View view, Cart obj, int position) {



                    dialogDeleteActiveConfirmation(obj.product_id,position,items);

            }
        });

        recyclerView.setAdapter(adapter);
        recyclerView.setNestedScrollingEnabled(false);

//        adapter.setOnItemClickListener(new AdapterShoppingCart.OnItemClickListener() {
//            @Override
//            public void onItemClick(View view, Cart obj) {
////                dialogCartAction(obj);
//            }
//        });
        View lyt_no_item = (View) findViewById(R.id.lyt_no_item);
        if (adapter.getItemCount() == 0) {
            lyt_no_item.setVisibility(View.VISIBLE);
        } else {
            lyt_no_item.setVisibility(View.GONE);
        }
        setTotalPrice();
    }

    private void setTotalPrice() {
        List<Cart> items = adapter.getItem();
        Double _price_total = 0D;
//        _price_total_tax_str;
        for (Cart c : items) {
            _price_total = _price_total + (c.amount * c.price_item);
        }
        _price_total_tax_str = String.format(Locale.US, "%1$,.2f", _price_total);
        price_total.setText("₹" + _price_total_tax_str );
    }







    @Override
    protected void onStop() {
        super.onStop();



    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void dialogDeleteConfirmation() {

        delcart_builder = new AlertDialog.Builder(this);
        delcart_builder.setTitle(R.string.title_delete_confirm);
        delcart_builder.setMessage(getString(R.string.content_delete_confirm) + getString(R.string.title_activity_cart));
        delcart_builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface di, int i) {
                di.dismiss();
                db.deleteActiveCart();
                onResume();
                Snackbar.make(parent_view, R.string.delete_success, Snackbar.LENGTH_SHORT).show();
            }
        });
        delcart_builder.setNegativeButton(R.string.CANCEL, null);
        delcart_builder.show();
    }


    public void dialogDeleteActiveConfirmation(final int id, final int position, final List<Cart>items) {

        delcart_builder = new AlertDialog.Builder(this);
        delcart_builder.setTitle(R.string.title_delete_confirm);
        delcart_builder.setMessage(getString(R.string.content_delete_confirm_one) + getString(R.string.title_activity_cart));
        delcart_builder.setPositiveButton(R.string.YES, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface di, int i) {
                di.dismiss();

                db.deleteActiveCart(id);
                items.remove(position);
                adapter.notifyItemRemoved(position);
                onResume();
                Snackbar.make(parent_view, R.string.delete_success, Snackbar.LENGTH_SHORT).show();
            }
        });
        delcart_builder.setNegativeButton(R.string.CANCEL, null);
        delcart_builder.show();
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

                        } else {

                            progressDialog.dismiss();

//                            Toast.makeText(LoginActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(ShopingCartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {


                    }
                }));


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
                            email = value.getUserData().get(0).getEmailId();


                        } else {

                            progressDialog.dismiss();

//                            Toast.makeText(LoginActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(ShopingCartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

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

        et_money.setText(needed_money);

        Button addmoney = view.findViewById(R.id.addmoney);

        addmoney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String money = et_money.getText().toString();

                if (checkzero(et_money)){
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);

                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view_toast = inflater.inflate(R.layout.your_custom_toast, null);
                    toast.setView(view_toast);
                    toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL, 0, 180);
                    toast.show();

                }else if (checklessthan100(et_money)){
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);

                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view_toast = inflater.inflate(R.layout.your_custom_toast, null);
                    TextView text = view_toast.findViewById(R.id.msg);
                    text.setText("Minimum amount should be ₹10");

                    toast.setView(view_toast);
                    toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL, 0, 180);

                    toast.show();

                }else if (checkblank(et_money)){
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);

                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view_toast = inflater.inflate(R.layout.your_custom_toast, null);
                    toast.setView(view_toast);
                    toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL, 0, 180);

                    toast.show();

                }else if (checkgraterthan100000(et_money)){
                    Toast toast = new Toast(getApplicationContext());
                    toast.setDuration(Toast.LENGTH_LONG);

                    LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view_toast = inflater.inflate(R.layout.your_custom_toast, null);
                    toast.setView(view_toast);
                    toast.setGravity(Gravity.TOP|Gravity.FILL_HORIZONTAL, 0, 180);

                    toast.show();

                }else {




                    callInstamojoPay(email,sharedPreference.getSharedPreferenceString(ShopingCartActivity.this,Constant.SELECTED_MOBILE_NO,""),et_money.getText().toString(),"Morning Bucket Wallet",user_name);



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


    private void initListener(final String amount) {
        listener = new InstapayListener() {
            @Override
            public void onSuccess(String response) {
//                Toast.makeText(getApplicationContext(), response, Toast.LENGTH_LONG)
//                        .show();

                addmorningBucketMoney(amount,response,user_id);

            }

            @Override
            public void onFailure(int code, String reason) {
                Toast.makeText(getApplicationContext(), "Failed: " + reason, Toast.LENGTH_LONG)
                        .show();
            }
        };
    }




    public void addmorningBucketMoney(String amount,String transaction_id,String user_id){

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.saveWalletDetail(transaction_id, user_id,amount)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<AddMoneyModal>() {
                    @Override
                    public void onNext(AddMoneyModal value) {


                        if (!value.getError()) {


                                    progressDialog.dismiss();
                                    Toast.makeText(ShopingCartActivity.this, "Money Load Successfully", Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(ShopingCartActivity.this,CheckoutActivity.class);
                                    startActivity(intent);
                                    finish();






                        } else {
                            progressDialog.dismiss();

                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                       progressDialog.dismiss();
                        Toast.makeText(ShopingCartActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onComplete() {

                    }
                }));


    }

    public boolean checkzero(EditText et_money) {
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

    public boolean checklessthan100(EditText et_money) {
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

    public boolean checkgraterthan100000(EditText et_money){

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

    public boolean checkblank(EditText et_money){

        if (et_money.getText().toString().equals("")){

            return true;
        }else {
            return false;
        }
    }
}
