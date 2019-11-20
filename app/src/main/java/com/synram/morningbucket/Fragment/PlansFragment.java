package com.synram.morningbucket.Fragment;


import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.gson.Gson;
import com.synram.morningbucket.API.ApiClient;
import com.synram.morningbucket.API.ApiInterface;
import com.synram.morningbucket.Adapter.AllOrderDetailAdapter;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Modal.AllOrderDetail;
import com.synram.morningbucket.Modal.AllOrderList;
import com.synram.morningbucket.Modal.UserDataModal;
import com.synram.morningbucket.R;
import com.synram.morningbucket.SharedprefData.SharedPreference;
import com.synram.morningbucket.activities.OrderDetailActivity;
import com.synram.morningbucket.activities.OrderListActivity;
import com.synram.morningbucket.activities.PlanDetailActivity;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlansFragment extends Fragment {



    private RecyclerView orderdetail_rv;
    private CompositeDisposable compositeDisposable;
    private SharedPreference sharedPreference;
    private ProgressDialog progressDialog;
    private String userid;
    private View lyt_no_item;
    private AllOrderDetailAdapter allOrderDetailAdapter;

    private List<AllOrderDetail> allOrderDetailList = new ArrayList<>();
    public PlansFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_plans, container, false);

        initview(view);
        return view;
    }


    public void initview(View view){

        compositeDisposable = new CompositeDisposable();
        sharedPreference = new SharedPreference(getActivity());
        progressDialog = new ProgressDialog(getActivity());


        orderdetail_rv = view.findViewById(R.id.orderdetail_rv);
        lyt_no_item = view.findViewById(R.id.lyt_no_item);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false);
        orderdetail_rv.setLayoutManager(layoutManager);




        fetchuserdata(sharedPreference.getSharedPreferenceString(getActivity(),Constant.SELECTED_MOBILE_NO,""));





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

        Date date= new Date(sdf.parse(date_str).getTime());
        date.setMonth(date.getMonth());

        SimpleDateFormat sdf2 = new SimpleDateFormat("MMM dd, yyyy");
        String convertedate = sdf2.format(date);

        return convertedate;
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

                            Toast.makeText(getActivity(), value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

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

        compositeDisposable.add(apiService.fetchorderHistory(userid,"2")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<AllOrderList>() {
                    @Override
                    public void onNext(AllOrderList value) {

                        Log.d("DATALIST",new Gson().toJson(value));

                        if (!value.getError()) {

                            allOrderDetailList.clear();
                            progressDialog.dismiss();

                            Log.d("DATALIST",new Gson().toJson(value));

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
                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        if (allOrderDetailList.size()>0){

                            allOrderDetailAdapter = new AllOrderDetailAdapter(getActivity(), allOrderDetailList, new AllOrderDetailAdapter.OnitemClicklistner() {
                                @Override
                                public void onitemclik(AllOrderDetail allOrderDetail, int position) {
                                    Intent intent = new Intent(getActivity(),PlanDetailActivity.class);
                                    Bundle bundle = new Bundle();
                                    bundle.putString("order_id",allOrderDetail.getOrderNo());
                                    bundle.putString("cust_id",userid);
                                    bundle.putString("order_status",allOrderDetail.getOrderStatus());
                                    intent.putExtras(bundle);
                                    startActivity(intent);

                                }
                            });

                            orderdetail_rv.setAdapter(allOrderDetailAdapter);
                            lyt_no_item.setVisibility(View.GONE);
                            orderdetail_rv.setVisibility(View.VISIBLE);

                        }else {

                            lyt_no_item.setVisibility(View.VISIBLE);
                            orderdetail_rv.setVisibility(View.GONE);


                        }


                    }
                }));


    }


}
