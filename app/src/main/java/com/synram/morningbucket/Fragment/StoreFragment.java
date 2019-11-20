package com.synram.morningbucket.Fragment;


import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.synram.morningbucket.API.ApiClient;
import com.synram.morningbucket.API.ApiInterface;
import com.synram.morningbucket.Adapter.AlbumsAdapter;
import com.synram.morningbucket.Adapter.ProductsInsideListAdapter;
import com.synram.morningbucket.Adapter.ProductsTagsAdapter;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Modal.Album;
import com.synram.morningbucket.Modal.BrandDatum;
import com.synram.morningbucket.Modal.ProductDatum;
import com.synram.morningbucket.Modal.ProductsListResponse;
import com.synram.morningbucket.Modal.TypeDatum;
import com.synram.morningbucket.R;
import com.synram.morningbucket.SharedprefData.SharedPreference;
import com.synram.morningbucket.Utilities.RecyclerTouchListener;
import com.synram.morningbucket.activities.ProductsDescriptionActivity;
import com.synram.morningbucket.activities.Productslistactivity;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreFragment extends Fragment {


    RecyclerView products_sub_rv;

    List<ProductDatum> productDatumList = new ArrayList<>();
    ProductsInsideListAdapter adapter;

    private CompositeDisposable compositeDisposable;
    SharedPreference sharedPreference;
    int tag_id,tab_id;
    private View lyt_no_item;

    public StoreFragment(){}

//@SuppressLint("ValidFragment")
//    public StoreFragment(List<ProductDatum> productDatumList) {
//     // Required empty public constructor
//
//        this.productDatumList = productDatumList;
//    }


    @SuppressLint("ValidFragment")
    public StoreFragment(int tag_id, int tab_id){

        this.tag_id = tag_id;
        this.tab_id = tab_id;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_store, container, false);

        compositeDisposable = new CompositeDisposable();

        sharedPreference = new SharedPreference(getActivity());
//        try {
//            Picasso.get().load(R.dra)with(this).load(R.drawable.cover).into((ImageView) findViewById(R.id.backdrop));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        initview(view);

        return view;
    }

    public void initview(View view){
        products_sub_rv = view.findViewById(R.id.products_sub_rv);
        lyt_no_item = view.findViewById(R.id.lyt_no_item);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL,false);
        products_sub_rv.setLayoutManager(mLayoutManager);

//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,dpToPx(10),true));
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));


        products_sub_rv.setItemAnimator(new DefaultItemAnimator());

        if (tag_id==0){
            filterbytags_or_tab(String.valueOf(sharedPreference.getSharedPreferenceInt(getActivity(),Constant.PARENT_PRODUCT_ID,0)),

                    String.valueOf(sharedPreference.getSharedPreferenceInt(getActivity(),Constant.SubCat_PRODUCT_ID,0)),String.valueOf(""),String.valueOf(tab_id));


        }else{

            filterbytags_or_tab(String.valueOf(sharedPreference.getSharedPreferenceInt(getActivity(),Constant.PARENT_PRODUCT_ID,0)),

                    String.valueOf(sharedPreference.getSharedPreferenceInt(getActivity(),Constant.SubCat_PRODUCT_ID,0)),String.valueOf(tag_id),String.valueOf(tab_id));

        }


//        if (productDatumList.size()>0){
//            adapter = new ProductsInsideListAdapter(getActivity(), productDatumList);
//
//            products_sub_rv.setAdapter(adapter);
//        }

        products_sub_rv.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), products_sub_rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                ProductDatum productDatum  = productDatumList.get(position);

                sharedPreference.setSharedPreferenceString(getActivity(),Constant.SINGLE_PRODUCT_ID,productDatum.getId());
                Intent intent = new Intent(getActivity(), ProductsDescriptionActivity.class);
                startActivity(intent);
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));



    }

    private void loadproducts(int cat_id, int sub_cat_id) {

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.loadProducts(cat_id,sub_cat_id,sharedPreference.getSharedPreferenceString(getActivity(),Constant.SELECTED_CITY_ID,""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<ProductsListResponse>() {
                    @Override
                    public void onNext(ProductsListResponse value) {


                        if (!value.getError()) {
                            productDatumList.clear();

                            for(int i=0;i<value.getProductData().size();i++){
                                ProductDatum productDatum = new ProductDatum(
                                        value.getProductData().get(i).getId(),
                                        value.getProductData().get(i).getCatId(),
                                        value.getProductData().get(i).getCatName(),
                                        value.getProductData().get(i).getSubCatId(),
                                        value.getProductData().get(i).getSubCatName(),
                                        value.getProductData().get(i).getTypeName(),
                                        value.getProductData().get(i).getTypeId(),
                                        value.getProductData().get(i).getBrandId(),
                                        value.getProductData().get(i).getBrandName(),
                                        value.getProductData().get(i).getProductName(),
                                        value.getProductData().get(i).getPrice(),
                                        value.getProductData().get(i).getMrp(),
                                        value.getProductData().get(i).getDescription(),
                                        value.getProductData().get(i).getWeight(),
                                        value.getProductData().get(i).getImage(),
                                        value.getProductData().get(i).getNo_of_variation()
                                );

                                productDatumList.add(productDatum);

                            }



//                            Toast.makeText(LoginActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();



                        } else {

                            Toast.makeText(getActivity(), value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        if (productDatumList.size()>0){
                            adapter = new ProductsInsideListAdapter(getActivity(), productDatumList);

                            products_sub_rv.setAdapter(adapter);
                        }



                    }
                }));


    }


    private void filterbytags_or_tab(String cat_id, String sub_cat_id, final String tag_id, final String tab_id) {

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.filterbybrand(tab_id,tag_id,cat_id,sub_cat_id,sharedPreference.getSharedPreferenceString(getActivity(),Constant.SELECTED_CITY_ID,""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<ProductsListResponse>() {
                    @Override
                    public void onNext(ProductsListResponse value) {

//                        Log.d("FilterbyBrand",new Gson().toJson(value));

//                        Log.d("TAG-TAB_ID",tag_id+" -"+ tab_id);

                        if (!value.getError()) {

                            productDatumList.clear();
                            for(int i=0;i<value.getProductData().size();i++){

                                ProductDatum productDatum = new ProductDatum(
                                        value.getProductData().get(i).getId(),
                                        value.getProductData().get(i).getCatId(),
                                        value.getProductData().get(i).getCatName(),
                                        value.getProductData().get(i).getSubCatId(),
                                        value.getProductData().get(i).getSubCatName(),
                                        value.getProductData().get(i).getTypeName(),
                                        value.getProductData().get(i).getTypeId(),
                                        value.getProductData().get(i).getBrandId(),
                                        value.getProductData().get(i).getBrandName(),
                                        value.getProductData().get(i).getProductName(),
                                        value.getProductData().get(i).getPrice(),
                                        value.getProductData().get(i).getMrp(),
                                        value.getProductData().get(i).getDescription(),
                                        value.getProductData().get(i).getWeight(),
                                        value.getProductData().get(i).getImage(),
                                        value.getProductData().get(i).getNo_of_variation()
                                );

                                productDatumList.add(productDatum);

//                                Log.d("Tab_id",String.valueOf(tab_id));
//                                Log.d("Tag_id",String.valueOf(tag_id));

                            }


                        } else {

//                            Toast.makeText(getActivity(), value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {


                        if (productDatumList.size()>0){
                            adapter = new ProductsInsideListAdapter(getActivity(), productDatumList);

                            products_sub_rv.setAdapter(adapter);

                            lyt_no_item.setVisibility(View.GONE);
                        }else {
                            lyt_no_item.setVisibility(View.VISIBLE);
                        }

                    }
                }));


    }


}
