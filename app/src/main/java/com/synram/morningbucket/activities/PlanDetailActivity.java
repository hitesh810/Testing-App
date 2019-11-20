package com.synram.morningbucket.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.synram.morningbucket.API.ApiClient;
import com.synram.morningbucket.API.ApiInterface;
import com.synram.morningbucket.Adapter.SubscribedCalanderAdapter;
import com.synram.morningbucket.Adapter.SubscribedProductsListAdapter;
import com.synram.morningbucket.Database.DatabaseHandler;
import com.synram.morningbucket.Modal.Orderproduct;
import com.synram.morningbucket.Modal.PlanDate;
import com.synram.morningbucket.Modal.Set_Status_Response;
import com.synram.morningbucket.Modal.SubscribedOrderDetailRespo;
import com.synram.morningbucket.R;
import com.synram.morningbucket.Utilities.RecyclerTouchListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class PlanDetailActivity extends AppCompatActivity {


    RecyclerView calander_rv, _subproducts_rv;

    private List<PlanDate> plan_dateList = new ArrayList<>();
    private List<Orderproduct> subscribeProductsList = new ArrayList<>();

    private TextView clicked_Date;
    private SubscribedCalanderAdapter subscribedCalanderAdapter;
    private SubscribedProductsListAdapter subscribedProductsListAdapter;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressDialog progressDialog;
    private CompositeDisposable compositeDisposable;
    private String order_no;
    private String cust_id;
    private TextView date_tv, status_tv;
    private Switch switch_toggle;
    private TextView time_slot;
    private String selected_date;
    private boolean toggle_flag = false;
    private String order_status = "";
    private String order_status_data = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        progressDialog = new ProgressDialog(this);
        compositeDisposable = new CompositeDisposable();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();

    }


    public void initView() {

        calander_rv = findViewById(R.id.calander_rv);
        _subproducts_rv = findViewById(R.id._subproducts_rv);
        clicked_Date = findViewById(R.id.clicked_Date);
        switch_toggle = findViewById(R.id.switch_toggle);
        date_tv = findViewById(R.id.date_tv);
        status_tv = findViewById(R.id.status_tv);
        time_slot = findViewById(R.id.time_slot);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
//        calander_rv.addItemDecoration(new MyDividerItemDecoration(this, LinearLayoutManager.HORIZONTAL, 5).setDrawable(getContext().getResources().getDrawable(R.drawable.sk_line_divider)););

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this,
                LinearLayoutManager.HORIZONTAL);
        dividerItemDecoration.setDrawable(this.getResources().getDrawable(R.drawable.sk_line_divider));

        calander_rv.addItemDecoration(dividerItemDecoration);


        DatabaseHandler db = new DatabaseHandler(this);
        db.deleteActiveSubCart();

        calander_rv.setLayoutManager(layoutManager);
        calander_rv.setItemAnimator(new DefaultItemAnimator());


        RecyclerView.LayoutManager layoutManager_two = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);

        _subproducts_rv.setLayoutManager(layoutManager_two);
        _subproducts_rv.setItemAnimator(new DefaultItemAnimator());


        try {

            Intent intent = getIntent();
            Bundle bundle = intent.getExtras();
            order_no = bundle.getString("order_id");
            cust_id = bundle.getString("cust_id");
            order_status_data = bundle.getString("order_status");


            Log.d("ordered", order_status_data);

        } catch (Exception ex) {
            ex.printStackTrace();
        }


        subscribedorderDetail(order_no, cust_id);


        switch_toggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!toggle_flag) {

                    setplanstatus(order_no, selected_date, "0");

                    toggle_flag = true;


                    Log.d("RUNLOG", selected_date + "");

                } else {
                    setplanstatus(order_no, selected_date, "2");
                    toggle_flag = false;
                    Log.d("RUNLOG", selected_date + "else");

                }

            }
        });


//        if(formatdateReturnDate(subscribeProductsList.getDate()).before(c)){
//
//            switch_toggle.setClickable(false);
//        }else{
//            switch_toggle.setClickable(true);
//
//        }


//        Calendar cal = Calendar.getInstance();
//
//
//
////        cal.set(Calendar.MONTH, 1);
////        cal.set(Calendar.DAY_OF_MONTH, 1);
//
//        int maxDay = cal.getActualMaximum(Calendar.DAY_OF_MONTH);
//        Log.d("MAXDAY",String.valueOf(maxDay));
//        SimpleDateFormat df = new SimpleDateFormat("MMM dd, yyyy");
//
//        System.out.print(df.format(cal.getTime()));
//        int current_date = cal.get(Calendar.DAY_OF_YEAR);
//        int current_month = cal.get(Calendar.DAY_OF_MONTH)+2;
//        int current_year = cal.get(Calendar.DAY_OF_YEAR)-1;
//
//        for (int i = current_date; i <=current_month+14; i++) {
//
//
//                cal.add(Calendar.DAY_OF_MONTH,i);
//
//
//
//
//
//            Log.d("DATA",df.format(cal.getTime()));
//            System.out.print(", " + df.format(cal.getTime()));
//        }


//        Calendar cal = GregorianCalendar.getInstance();
//        cal.setTime(new Date());
//        cal.add(Calendar.DAY_OF_YEAR, 7);
//        Date _7daysBeforeDate = cal.getTime();
//
//        Log.d("data",cal.getTime().toString());

//        getdate();


        calander_rv.addOnItemTouchListener(new RecyclerTouchListener(this, calander_rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                PlanDate horizontalCalanderModal = plan_dateList.get(position);


                try {
                    date_tv.setText(String.valueOf(formatdateReturnDateWithPlusone(horizontalCalanderModal.getDate())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                try {
                    selected_date = formatdateReturnDateFORStatus(horizontalCalanderModal.getDate());
                } catch (ParseException e) {
                    e.printStackTrace();
                }


                Log.d("STATUS", horizontalCalanderModal.getStatus() + "");
                if (horizontalCalanderModal.getStatus().equals("0")) {
                    switch_toggle.setChecked(true);
                    toggle_flag = true;
                    _subproducts_rv.setAlpha(1f);
                    _subproducts_rv.setEnabled(true);


                } else {

                    if (horizontalCalanderModal.getStatus().equals("1")) {

                        switch_toggle.setClickable(false);

                    } else {
                        switch_toggle.setChecked(false);
                        toggle_flag = false;

                        _subproducts_rv.setAlpha(0.5f);

                        _subproducts_rv.setEnabled(false);
                    }
                }


                Date c = Calendar.getInstance().getTime();

                Log.d("STATUSTIME", horizontalCalanderModal.getStatus() + "--" + formatdateReturnDate(c.toString()));

                try {

                    if (order_status_data.equals("Cancelled")) {
                        switch_toggle.setClickable(false);
                        if (horizontalCalanderModal.getStatus().equals("1")) {
                            switch_toggle.setVisibility(View.GONE);
                            status_tv.setVisibility(View.VISIBLE);
                            status_tv.setText("Delivered");
                            status_tv.setTextColor(Color.parseColor("#398439"));

                        } else if (horizontalCalanderModal.getStatus().equals("2")) {
                            switch_toggle.setVisibility(View.GONE);
                            status_tv.setVisibility(View.VISIBLE);
                            status_tv.setText("Cancelled");
                            status_tv.setTextColor(Color.parseColor("#dc3545"));

                        }else if (horizontalCalanderModal.getStatus().equals("3")) {
                            switch_toggle.setVisibility(View.GONE);
                            status_tv.setVisibility(View.VISIBLE);
                            status_tv.setText("Cancelled");
                            status_tv.setTextColor(Color.parseColor("#dc3545"));

                        }else if (horizontalCalanderModal.getStatus().equals("4")) {
                            switch_toggle.setVisibility(View.GONE);
                            status_tv.setVisibility(View.VISIBLE);
                            status_tv.setText("In Process");
                            status_tv.setTextColor(Color.parseColor("#449285"));

                        } else {

                            switch_toggle.setVisibility(View.GONE);
                            status_tv.setVisibility(View.VISIBLE);
                            status_tv.setText("Money not deducted");
                            status_tv.setTextColor(Color.parseColor("#eea236"));

                        }

                    } else {
                        if (formatdateReturnDateWithCurrent(horizontalCalanderModal.getDate()).before(c)) {
//                        Log.d("STATUSTIME",horizontalCalanderModal.getStatus()+"--"+formatdateReturnDateWithCurrent(c.toString()));
                            switch_toggle.setClickable(false);
                            if (horizontalCalanderModal.getStatus().equals("1")) {
                                switch_toggle.setVisibility(View.GONE);
                                status_tv.setVisibility(View.VISIBLE);
                                status_tv.setText("Delivered");
                                status_tv.setTextColor(Color.parseColor("#398439"));

                            } else if (horizontalCalanderModal.getStatus().equals("2")) {
                                switch_toggle.setVisibility(View.GONE);
                                status_tv.setVisibility(View.VISIBLE);
                                status_tv.setText("Cancelled");
                                status_tv.setTextColor(Color.parseColor("#dc3545"));

                            }else if (horizontalCalanderModal.getStatus().equals("3")) {
                                switch_toggle.setVisibility(View.GONE);
                                status_tv.setVisibility(View.VISIBLE);
                                status_tv.setText("Cancelled");
                                status_tv.setTextColor(Color.parseColor("#dc3545"));

                            }else if (horizontalCalanderModal.getStatus().equals("4")) {
                                switch_toggle.setVisibility(View.GONE);
                                status_tv.setVisibility(View.VISIBLE);
                                status_tv.setText("In Process");
                                status_tv.setTextColor(Color.parseColor("#449285"));

                            } else {

                                switch_toggle.setVisibility(View.GONE);
                                status_tv.setVisibility(View.VISIBLE);
                                status_tv.setText("Money not deducted");
                                status_tv.setTextColor(Color.parseColor("#eea236"));

                            }


                        } else {
                            Log.d("STATUSELSETIME", horizontalCalanderModal.getStatus() + "");

                            if (horizontalCalanderModal.getStatus().equals("1")) {

                                switch_toggle.setClickable(false);
                                switch_toggle.setVisibility(View.GONE);
                                status_tv.setVisibility(View.VISIBLE);
                                status_tv.setText("Delivered");
                                status_tv.setTextColor(Color.parseColor("#398439"));

                            } else {


                                switch_toggle.setClickable(true);
                                switch_toggle.setVisibility(View.VISIBLE);
                                status_tv.setVisibility(View.GONE);
                            }
                        }

                    }

                } catch (ParseException e) {
                    e.printStackTrace();
                }

                if (!order_status.equals("Running")) {
                    switch_toggle.setClickable(false);
                } else {
//                    switch_toggle.setClickable(true);
                }

                subscribeProductsList.clear();

                for (int i = 0; i < horizontalCalanderModal.getOrderproduct().size(); i++) {

                    Orderproduct orderproduct = new Orderproduct(
                            horizontalCalanderModal.getOrderproduct().get(i).getPid(),
                            horizontalCalanderModal.getOrderproduct().get(i).getQty(),
                            horizontalCalanderModal.getOrderproduct().get(i).getAmount(),
                            horizontalCalanderModal.getOrderproduct().get(i).getProName()

                    );
                    subscribeProductsList.add(orderproduct);


                }


                if (subscribeProductsList.size() > 0) {

                    subscribedProductsListAdapter = new SubscribedProductsListAdapter(PlanDetailActivity.this, subscribeProductsList, horizontalCalanderModal.getStatus(), order_status_data, horizontalCalanderModal.getPlanid());
                    _subproducts_rv.setAdapter(subscribedProductsListAdapter);
                }

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });


//        Date c = Calendar.getInstance().getTime();
//
//        for (int i=0;i<plan_dateList.size();i++) {
//
//
//            if (formatdateReturnDate(plan_dateList.get(i).getDate()).equals(c)) {
//                if (plan_dateList.get(i).getStatus().equals("0")){
//                    switch_toggle.setChecked(true);
//                    toggle_flag =true;
//
//                }else {
//                    switch_toggle.setChecked(false);
//                    toggle_flag =false;
//                }
//
//            }
//        }


    }


//    public void getdate(){
//        final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//        Date current = null;
//
//        Calendar calendar = Calendar.getInstance();
//        int dayToComapre = calendar.get(Calendar.DAY_OF_YEAR)-5;
//        int monthToComapre = calendar.get(Calendar.MONTH) + 2;
//        int yearToComapre = calendar.get(Calendar.YEAR) - 1;
//
//        try {
//            current = sdf.parse(dayToComapre + "/" + monthToComapre + "/" + yearToComapre);
//        } catch (ParseException e) {
//            e.printStackTrace();
//        }
//
//        for (int i = dayToComapre; i < dayToComapre + 14; i++) {
//            try {
//                current = sdf.parse(i + "/" + monthToComapre + "/" + yearToComapre);
//            } catch (ParseException e) {
//                e.printStackTrace();
//            }
//
//            Log.e("date", current+"");
//
////            plan_dateList.add(current);
//
//
//        }
//
////
////        horizontalCalanderAdapter = new HorizontalCalanderAdapter(this,horizontalCalanderModalList);
////        calander_rv.setAdapter(horizontalCalanderAdapter);
//
//    }


    public String formatdate(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String convertedate = sdf.format(date);

        return convertedate;
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


    public Date formatdateReturnDate(String date_str) {

        DateFormat formatter = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        Date date = null;

        try {
            date = (Date) formatter.parse(date_str);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat sdf = new SimpleDateFormat("MMM dd, yyyy");
        String convertedate = sdf.format(date);

        return date;
    }


    public String formatdateReturnDateWithPlusone(String date_str) throws ParseException {


        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

        Date date = new Date(sdf.parse(date_str).getTime());
        date.setMonth(date.getMonth());

        SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy");
        String convertedate = sdf2.format(date);

        return convertedate;
    }


    public String formatdateReturnDateFORStatus(String date_str) throws ParseException {


        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

        Date date = new Date(sdf.parse(date_str).getTime());
        date.setMonth(date.getMonth());

        SimpleDateFormat sdf2 = new SimpleDateFormat("yyyy-MM-dd");
        String convertedate = sdf2.format(date);

        return convertedate;
    }


    public Date formatdateReturnDateWithCurrent(String date_str) throws ParseException {


        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");

        Date date = new Date(sdf.parse(date_str).getTime());
        date.setMonth(date.getMonth());

        /*SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy");
        String convertedate = sdf2.format(date);
        Date date2= new Date(sdf.parse(convertedate).getTime());
        date2.setMonth(date2.getMonth());*/

        return date;
    }


    private void subscribedorderDetail(String order_id, String cust_id) {

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.getsubscribedPlanDetail(order_id, cust_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())


                .subscribeWith(new DisposableObserver<SubscribedOrderDetailRespo>() {
                    @Override
                    public void onNext(SubscribedOrderDetailRespo value) {

//                        Log.d("RESPOSUBORDER",new Gson().toJson(value));
                        plan_dateList.clear();
                        subscribeProductsList.clear();
                        if (!value.getError()) {

                            progressDialog.dismiss();


//                            Log.d("ORDERSTATUS",value.getSubscriptonOrder().getOrderStatus());

                            if (!order_status.equals("Running")) {
                                switch_toggle.setClickable(false);
                            } else {
//                                switch_toggle.setClickable(true);
                            }

                            order_status = value.getSubscriptonOrder().getOrderStatus();


                            time_slot.setText(value.getSubscriptonOrder().getTime_slot());


                            for (int i = 0; i < value.getPlanDates().size(); i++) {
                                PlanDate plan_date = new PlanDate(
                                        value.getPlanDates().get(i).getDate(),
                                        value.getPlanDates().get(i).getDay(),
                                        value.getPlanDates().get(i).getStatus(),
                                        value.getPlanDates().get(i).getOrderproduct(),
                                        value.getPlanDates().get(i).getPlanid()

                                );
                                plan_dateList.add(plan_date);
                            }

//                            for(int i=0;i<value.getSubscriptonOrder().getSubscribeProduct().size();i++){
//                                SubscribeProduct subscribeProduct = new SubscribeProduct(
//                                        value.getSubscriptonOrder().getSubscribeProduct().get(i).getProName(),
//                                        value.getSubscriptonOrder().getSubscribeProduct().get(i).getPrice(),
//                                        value.getSubscriptonOrder().getSubscribeProduct().get(i).getQuantity()
//
//                                );
//
//
//                                subscribeProductsList.add(subscribeProduct);
//
//                            }


                            Date c = Calendar.getInstance().getTime();

                            for (int i = 0; i < plan_dateList.size(); i++) {

                                try {
                                    if (formatdateReturnDateWithCurrent(plan_dateList.get(i).getDate()).equals(c.toString())) {

                                        date_tv.setText(formatdate(c));


                                        if (plan_dateList.get(i).getStatus().equals("0")) {
                                            switch_toggle.setChecked(true);

                                        } else {
                                            switch_toggle.setChecked(false);

                                        }

                                    }
                                } catch (ParseException e) {
                                    e.printStackTrace();
                                }

                            }


//                            Toast.makeText(LoginActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        } else {
                            progressDialog.dismiss();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(PlanDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        if (plan_dateList.size() > 0) {

                            subscribedCalanderAdapter = new SubscribedCalanderAdapter(PlanDetailActivity.this, plan_dateList);
                            calander_rv.setAdapter(subscribedCalanderAdapter);


                            PlanDate horizontalCalanderModal = plan_dateList.get(0);


                            try {
                                date_tv.setText(String.valueOf(formatdateReturnDateWithPlusone(horizontalCalanderModal.getDate())));
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            try {
                                selected_date = formatdateReturnDateFORStatus(horizontalCalanderModal.getDate());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }


                            Log.d("STATUS", horizontalCalanderModal.getStatus() + "");
                            if (horizontalCalanderModal.getStatus().equals("0")) {
                                switch_toggle.setChecked(true);
                                toggle_flag = true;
                                _subproducts_rv.setAlpha(1f);
                                _subproducts_rv.setEnabled(true);


                            } else {
                                switch_toggle.setChecked(false);
                                toggle_flag = false;
                                _subproducts_rv.setAlpha(0.5f);
                                _subproducts_rv.setEnabled(false);

                            }


                            Date c = Calendar.getInstance().getTime();

                            Log.d("STATUSTIME", horizontalCalanderModal.getStatus() + "--" + formatdateReturnDate(c.toString()));

                            try {
                                if (formatdateReturnDateWithCurrent(horizontalCalanderModal.getDate()).before(c)) {
//                        Log.d("STATUSTIME",horizontalCalanderModal.getStatus()+"--"+formatdateReturnDateWithCurrent(c.toString()));

                                    switch_toggle.setClickable(false);


                                    if (horizontalCalanderModal.getStatus().equals("1")) {
                                        switch_toggle.setVisibility(View.GONE);
                                        status_tv.setVisibility(View.VISIBLE);
                                        status_tv.setText("Delivered");
                                        status_tv.setTextColor(Color.parseColor("#398439"));

                                    } else if (horizontalCalanderModal.getStatus().equals("2")) {
                                        switch_toggle.setVisibility(View.GONE);
                                        status_tv.setVisibility(View.VISIBLE);
                                        status_tv.setText("Canceled");
                                        status_tv.setTextColor(Color.parseColor("#dc3545"));

                                    }else if (horizontalCalanderModal.getStatus().equals("3")) {
                                        switch_toggle.setVisibility(View.GONE);
                                        status_tv.setVisibility(View.VISIBLE);
                                        status_tv.setText("Cancelled");
                                        status_tv.setTextColor(Color.parseColor("#dc3545"));

                                    }else if (horizontalCalanderModal.getStatus().equals("4")) {
                                        switch_toggle.setVisibility(View.GONE);
                                        status_tv.setVisibility(View.VISIBLE);
                                        status_tv.setText("In Process");
                                        status_tv.setTextColor(Color.parseColor("#449285"));

                                    } else {

                                        switch_toggle.setVisibility(View.GONE);
                                        status_tv.setVisibility(View.VISIBLE);
                                        status_tv.setText("Money not deducted");
                                        status_tv.setTextColor(Color.parseColor("#eea236"));

                                    }


                                } else {
                                    Log.d("STATUSELSETIME", horizontalCalanderModal.getStatus() + "");

                                    switch_toggle.setClickable(true);
                                    switch_toggle.setVisibility(View.VISIBLE);
                                    status_tv.setVisibility(View.GONE);

                                }
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }

                            if (!order_status.equals("Running")) {
                                switch_toggle.setClickable(false);
                            } else {
//                    switch_toggle.setClickable(true);
                            }

                            subscribeProductsList.clear();

                            for (int i = 0; i < horizontalCalanderModal.getOrderproduct().size(); i++) {

                                Orderproduct orderproduct = new Orderproduct(
                                        horizontalCalanderModal.getOrderproduct().get(i).getPid(),
                                        horizontalCalanderModal.getOrderproduct().get(i).getQty(),
                                        horizontalCalanderModal.getOrderproduct().get(i).getAmount(),
                                        horizontalCalanderModal.getOrderproduct().get(i).getProName()

                                );
                                subscribeProductsList.add(orderproduct);
                            }

                            if (subscribeProductsList.size() > 0) {

                                subscribedProductsListAdapter = new SubscribedProductsListAdapter(PlanDetailActivity.this, subscribeProductsList, horizontalCalanderModal.getStatus(), order_status_data, horizontalCalanderModal.getPlanid());
                                _subproducts_rv.setAdapter(subscribedProductsListAdapter);
                            }


                        }


                    }
                }));


    }


    private void setplanstatus(String o_id, String date, String status) {

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.setplanstatus(o_id, date, status)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())


                .subscribeWith(new DisposableObserver<Set_Status_Response>() {
                    @Override
                    public void onNext(Set_Status_Response value) {

//                        Log.d("RESPOSUBORDER",new Gson().toJson(value));

                        if (!value.getError()) {

                            progressDialog.dismiss();


                            Toast.makeText(PlanDetailActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        } else {
                            progressDialog.dismiss();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(PlanDetailActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        subscribedorderDetail(order_no, cust_id);


                    }
                }));


    }


    public boolean Check_valid_date(CharSequence d) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy");
        Date date_new = null;
        try {
            date_new = sdf.parse(d.toString());
            Date current_date = new Date();
            current_date = removeTime(current_date);
//            Log.d("CurentDate", current_date.toString());
//            Log.d("NEW date", date_new.toString());
            if (current_date.equals(date_new)) {
                return true;
            }
            return current_date.before(date_new);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Date removeTime(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

}
