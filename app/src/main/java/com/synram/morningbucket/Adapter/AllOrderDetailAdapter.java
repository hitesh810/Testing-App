package com.synram.morningbucket.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.synram.morningbucket.API.ApiClient;
import com.synram.morningbucket.API.ApiInterface;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Modal.AllOrderDetail;
import com.synram.morningbucket.Modal.CancleOrderResponse;
import com.synram.morningbucket.Modal.RechargeResponse;
import com.synram.morningbucket.Modal.SubscriptionChargesResponse;
import com.synram.morningbucket.Modal.WalletAmtResponse;
import com.synram.morningbucket.R;
import com.synram.morningbucket.SharedprefData.SharedPreference;
import com.synram.morningbucket.activities.AddMoneyActivity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class AllOrderDetailAdapter extends RecyclerView.Adapter<AllOrderDetailAdapter.MyViewHolder> {


    private Context context;
    private List<AllOrderDetail> allOrderDetailList = new ArrayList<>();

    private OnitemClicklistner onitemClicklistner;
    private ProgressDialog progressDialog;
    private CompositeDisposable compositeDisposable;
    private AlertDialog.Builder builder;
    private Dialog dialog;
    private Integer usable_money;
    private SharedPreference sharedPreference;
    private double _cashback_percent = 0D;
    private double cashback = 0D;

    public AllOrderDetailAdapter(Context context, List<AllOrderDetail> allOrderDetailList, OnitemClicklistner onitemClicklistner) {
        this.context = context;
        this.allOrderDetailList = allOrderDetailList;
        this.onitemClicklistner = onitemClicklistner;
        progressDialog = new ProgressDialog(context);
        compositeDisposable = new CompositeDisposable();
        builder = new AlertDialog.Builder(context);
        sharedPreference = new SharedPreference(context);
        fetchwalletData(sharedPreference.getSharedPreferenceString(context, Constant.USER_ID, ""));
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {

        View itemview = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.allorderlistitem, viewGroup, false);

        return new MyViewHolder(itemview);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder myViewHolder, final int i) {

        final AllOrderDetail allOrderDetail = allOrderDetailList.get(i);

        myViewHolder.order_status.setText(allOrderDetail.getOrderStatus());
        myViewHolder.order_id.setText("Order Number: " + allOrderDetail.getOrderNo());
        try {
            myViewHolder.full_history_date.setText(formatdateReturnDateWithPlusone(allOrderDetail.getDate()));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        myViewHolder.totalAmount.setText("₹" + allOrderDetail.getTotalAmount());

        myViewHolder.paidAmount.setText("₹" + allOrderDetail.getPayMoney());
        myViewHolder.remainAmount.setText("₹" + allOrderDetail.getRemainingMaoney());

        myViewHolder.recharge_alert.setText(allOrderDetail.getMsg());


        if (allOrderDetail.getCheckoutType().equals("1")) {

//            myViewHolder.paidAmt_ll.setVisibility(View.GONE);
//            myViewHolder.remainamt_ll.setVisibility(View.GONE);
            myViewHolder.cancel_btn.setVisibility(View.GONE);
            myViewHolder.recharge_btn.setVisibility(View.GONE);


            if (allOrderDetail.getOrderStatus().equals("Pending")) {

                myViewHolder.cancel_btn.setVisibility(View.VISIBLE);

            } else {
                myViewHolder.cancel_btn.setVisibility(View.GONE);

            }


        } else {

//            myViewHolder.paidAmt_ll.setVisibility(View.VISIBLE);
//            myViewHolder.remainamt_ll.setVisibility(View.VISIBLE);
            myViewHolder.cancel_btn.setVisibility(View.VISIBLE);
            myViewHolder.recharge_btn.setVisibility(View.VISIBLE);


            if (allOrderDetail.getOrderStatus().equals("Running")) {

                myViewHolder.cancel_btn.setVisibility(View.VISIBLE);
                myViewHolder.recharge_btn.setVisibility(View.VISIBLE);
                myViewHolder.recharge_alert.setVisibility(View.VISIBLE);


//                if (allOrderDetail.getRemainingMaoney().equals(0)){
////                    myViewHolder.cancel_btn.setVisibility(View.GONE);
//                    myViewHolder.recharge_btn.setVisibility(View.GONE);
//                }else {
////                    myViewHolder.cancel_btn.setVisibility(View.VISIBLE);
//                    myViewHolder.recharge_btn.setVisibility(View.VISIBLE);
//                }


                if (allOrderDetail.getRemainingMaoney() >= Integer.parseInt(allOrderDetail.getOnedaycost())) {

                    myViewHolder.recharge_btn.setVisibility(View.VISIBLE);
                } else {
                    myViewHolder.recharge_btn.setVisibility(View.VISIBLE);

                }


            } else {

                myViewHolder.cancel_btn.setVisibility(View.GONE);
                myViewHolder.recharge_btn.setVisibility(View.GONE);
                myViewHolder.recharge_alert.setVisibility(View.GONE);
            }


        }


        if (allOrderDetail.getMsg().equals("")) {
            myViewHolder.recharge_alert.setVisibility(View.GONE);

        } else {
            myViewHolder.recharge_alert.setVisibility(View.VISIBLE);

        }


        myViewHolder.seeOrderSummary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onitemClicklistner.onitemclik(allOrderDetail, i);
            }
        });


        myViewHolder.order_id.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onitemClicklistner.onitemclik(allOrderDetail, i);
            }
        });


        myViewHolder.cancel_btn.setTag(i);

//        if(allOrderDetail.getOrderStatus().equals("Running")){
//
//            myViewHolder.cancel_btn.setVisibility(View.VISIBLE);
////            myViewHolder.recharge_btn.setVisibility(View.VISIBLE);
//            myViewHolder.recharge_alert.setVisibility(View.VISIBLE);
//
//        }else {
//            myViewHolder.cancel_btn.setVisibility(View.GONE);
////            myViewHolder.recharge_btn.setVisibility(View.GONE);
//            myViewHolder.recharge_alert.setVisibility(View.GONE);
//
//        }


        myViewHolder.cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                //Uncomment the below code to Set the message and title from the strings.xml file
                builder.setMessage("Are you sure?").setTitle("Order Cancel");

                //Setting message manually and performing action on button click
                builder.setMessage("Are you sure?")
                        .setCancelable(true)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.dismiss();

                                cancleOrder(allOrderDetail.getOrderNo(), allOrderDetail.getCheckoutType());
                                myViewHolder.cancel_btn.setVisibility(View.GONE);
                                myViewHolder.recharge_btn.setVisibility(View.GONE);
                                myViewHolder.order_status.setText("Cancelled");

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
                alert.setTitle("Order Cancel");
                alert.show();

                //                Toast.makeText(context,"CANCELED"+myViewHolder.cancel_btn.getTag(),Toast.LENGTH_SHORT).show();
            }
        });


        myViewHolder.recharge_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                showRechargeDialog(context, allOrderDetail.getRemainingMaoney(), allOrderDetail.getOrderNo(), allOrderDetail);

            }
        });


    }

    @Override
    public int getItemCount() {
        return allOrderDetailList.size();
    }

    public String formatdate(String date_str) {

        DateFormat formatter = new SimpleDateFormat("yyyy-MM-DD");
        Date date = null;

        try {
            date = (Date) formatter.parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String convertedate = sdf.format(date);

        return convertedate;
    }

    public String formatdateReturnDateWithPlusone(String date_str) throws ParseException {

//        DateFormat formatter = new SimpleDateFormat("yyyy-MM-DD");
//        Date date = null;
//
//        try {
//            date = (Date)formatter.parse(date_str);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
//        String convertedate = sdf.format(date);
//
//        return date;
//


        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

        Date date = new Date(sdf.parse(date_str).getTime());
        date.setMonth(date.getMonth());

        SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy");
        String convertedate = sdf2.format(date);

        return convertedate;
    }

    private void cancleOrder(String order_id, String checkout_type) {
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.cancelOrder(order_id, checkout_type)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<CancleOrderResponse>() {
                    @Override
                    public void onNext(CancleOrderResponse value) {


                        if (!value.getError()) {
                            progressDialog.dismiss();

                            Toast.makeText(context, value.getErrorMsg(), Toast.LENGTH_SHORT).show();




//                            notifyDataSetChanged();

                        } else {

                            progressDialog.dismiss();

                            Toast.makeText(context, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {


                    }
                }));


    }

    private void recharge(String o_no, String amount, String cashback) {
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.rechargeOrder(o_no, "", amount, cashback)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<RechargeResponse>() {
                    @Override
                    public void onNext(RechargeResponse value) {


                        if (!value.getError()) {
                            progressDialog.dismiss();

                            Toast.makeText(context, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        } else {

                            progressDialog.dismiss();

                            Toast.makeText(context, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {


                    }
                }));


    }

    public void showRechargeDialog(final Context context, final int needed_money, final String order_no, final AllOrderDetail allOrderDetail) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.recharge_amt_dialog, null, false);
        /*HERE YOU CAN FIND YOU IDS AND SET TEXTS OR BUTTONS*/

        final EditText et_money_recharge = view.findViewById(R.id.et_money_recharge);
        final TextView onedaybtn = view.findViewById(R.id.onedaybtn);
        final TextView sevendaysbtn = view.findViewById(R.id.sevendaysbtn);

        int needmoney = needed_money;


//        totalWallet_Amount.setText("Total Amount to be pay:  "+"₹"+String.valueOf(_total_fees));
//        minium_amt.setText("Minimum Amount To be pay:  "+"₹"+String.valueOf(String.format("%.2f",sevendaysmoney)));

//        et_money_recharge.setText(String.valueOf(needed_money));

        onedaybtn.setText("₹" + allOrderDetail.getOnedaycost());

        final int sevendaycost = Integer.parseInt(allOrderDetail.getOnedaycost()) * 7;

        sevendaysbtn.setText("₹" +sevendaycost);

        onedaybtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_money_recharge.setText(String.valueOf(allOrderDetail.getOnedaycost()));
            }
        });

        sevendaysbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et_money_recharge.setText(String.valueOf(sevendaycost));

            }
        });

        Button recharge_btn_dialog = view.findViewById(R.id.recharge_btn_dialog);

        recharge_btn_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int needmoneyforvalidation = needed_money;
                String money = et_money_recharge.getText().toString();

                if (checkzero(et_money_recharge)) {
                    Toast toast = new Toast(context);
                    toast.setDuration(Toast.LENGTH_LONG);

                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view_toast = inflater.inflate(R.layout.your_custom_toast, null);
                    TextView text = view_toast.findViewById(R.id.msg);
                    text.setText("Minimum amount should be ₹" + onedaybtn.getText().toString());


                    toast.setView(view_toast);
                    toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 180);
                    toast.show();

                } else if (checkblank(et_money_recharge)) {
                    Toast toast = new Toast(context);
                    toast.setDuration(Toast.LENGTH_LONG);

                    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                    View view_toast = inflater.inflate(R.layout.your_custom_toast, null);
                    TextView text = view_toast.findViewById(R.id.msg);
                    text.setText("Minimum amount should be " +onedaybtn.getText().toString());

                    toast.setView(view_toast);
                    toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 180);

                    toast.show();

                } else {


//                    if (usable_money >= Integer.parseInt(et_money_recharge.getText().toString().trim())) {
//
//                        cashback = (Double.parseDouble(et_money_recharge.getText().toString()) * _cashback_percent) / 100;


//                        recharge(order_no, et_money_recharge.getText().toString().trim(), String.valueOf(cashback));

                        Intent intent = new Intent(context, AddMoneyActivity.class);
                        intent.putExtra("add_money",et_money_recharge.getText().toString());

                        context.startActivity(intent);

//
//                    } else {
//
//
//                        Toast toast = new Toast(context);
//                        toast.setDuration(Toast.LENGTH_LONG);
//
//                        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//                        View view_toast = inflater.inflate(R.layout.your_custom_toast, null);
//                        TextView text = view_toast.findViewById(R.id.msg);
//                        text.setText("Insufficient Money Please add money in to your wallet");
//
//                        toast.setView(view_toast);
//                        toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 180);
//
//                        toast.show();
//
//                    }


//                    callInstamojoPay(email_str,sharedPreference.getSharedPreferenceString(CheckoutActivity.this,Constant.SELECTED_MOBILE_NO,""),et_money.getText().toString(),"Morning Bucket Wallet",user_name);

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


/*    else if (checkmaxOrderrecharge(et_money_recharge, needmoneyforvalidation)) {
        Toast toast = new Toast(context);
        toast.setDuration(Toast.LENGTH_LONG);

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view_toast = inflater.inflate(R.layout.your_custom_toast, null);
        TextView text = view_toast.findViewById(R.id.msg);
        text.setText("Maximum amount should be ₹" + needmoneyforvalidation);

        toast.setView(view_toast);
        toast.setGravity(Gravity.TOP | Gravity.FILL_HORIZONTAL, 0, 180);

        toast.show();

    }*/


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

    public boolean checkminiOrderrecharge(EditText et_money, int remainamt) {

        try {
            if (Integer.parseInt(et_money.getText().toString()) < remainamt) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

    public boolean checkmaxOrderrecharge(EditText et_money, int remainamt) {

        try {
            if (Integer.parseInt(et_money.getText().toString()) > remainamt) {
                return true;
            } else {
                return false;
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }

//    public boolean checkminiOrder(EditText et_money){
//
//        try {
//            if (Integer.parseInt(et_money.getText().toString()) < sevendaysmoney) {
//                return true;
//            } else {
//                return false;
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//        return false;
//    }

    public boolean checkblank(EditText et_money) {

        if (et_money.getText().toString().equals("")) {

            return true;
        } else {
            return false;
        }
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

//                            walletMoney = Integer.parseInt(value.getAmount());
                            usable_money = value.getRemAmount();

                        } else {

                            progressDialog.dismiss();

                            Toast.makeText(context, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        checkSubscriptionchargesornot();


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


                            _cashback_percent = Double.parseDouble(value.getDisAmount());

//                             Log.d("CASHBACK",String.valueOf(value.getDisAmount()));


                        } else {

                            progressDialog.dismiss();

//                            Toast.makeText(LoginActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {


                    }
                }));


    }


    public interface OnitemClicklistner {

        void onitemclik(AllOrderDetail allOrderDetail, int position);

    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView order_id, full_history_date, itemMainTextviewOrder, totalAmount;
        TextView order_status, seeOrderSummary, cancel_btn, recharge_btn, recharge_alert;
        LinearLayout container_ll, paidAmt_ll, remainamt_ll;
        TextView paidAmount, remainAmount;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            order_id = itemView.findViewById(R.id.restName);
            full_history_date = itemView.findViewById(R.id.full_history_date);
            itemMainTextviewOrder = itemView.findViewById(R.id.itemMainTextviewOrder);
            totalAmount = itemView.findViewById(R.id.totalAmount);
            order_status = itemView.findViewById(R.id.order);
            container_ll = itemView.findViewById(R.id.container_ll);
            cancel_btn = itemView.findViewById(R.id.cancel_btn);
            recharge_btn = itemView.findViewById(R.id.recharge_btn);
            recharge_alert = itemView.findViewById(R.id.recharge_alert);
            paidAmount = itemView.findViewById(R.id.paidAmount);
            remainAmount = itemView.findViewById(R.id.remainAmount);

            seeOrderSummary = itemView.findViewById(R.id.seeOrderSummary);
            paidAmt_ll = itemView.findViewById(R.id.paidAmt_ll);
            remainamt_ll = itemView.findViewById(R.id.remainamt_ll);


        }
    }


}
