package com.synram.morningbucket.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.gson.Gson;
import com.synram.morningbucket.API.ApiClient;
import com.synram.morningbucket.API.ApiInterface;
import com.synram.morningbucket.Adapter.AlbumsAdapter;
import com.synram.morningbucket.Adapter.SubCategoriesAdapter;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Modal.CategoryDatum;
import com.synram.morningbucket.Modal.ProductsCatResponse;
import com.synram.morningbucket.Modal.SubCatview;
import com.synram.morningbucket.Modal.SubcatResponse;
import com.synram.morningbucket.R;
import com.synram.morningbucket.SharedprefData.SharedPreference;
import com.synram.morningbucket.Utilities.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.DoubleStream;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class SubcatActivity extends AppCompatActivity {


    RecyclerView subcat_rv;
    private SubCategoriesAdapter subCategoriesAdapter;
    List<SubCatview> subCatviewList = new ArrayList<>();
    private CompositeDisposable compositeDisposable;
    private int parent_product_id;
    private String parent_product_img;
    private String parent_product_name;

    SharedPreference sharedPreference;
    private SwipeRefreshLayout swipe_refresh_layout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subcat);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        compositeDisposable = new CompositeDisposable();
        sharedPreference = new SharedPreference(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


        try{

            parent_product_id = sharedPreference.getSharedPreferenceInt(this,Constant.PARENT_PRODUCT_ID,0);
            parent_product_img = sharedPreference.getSharedPreferenceString(this,Constant.PARENT_PRODUCT_IMG,"");
            parent_product_name = sharedPreference.getSharedPreferenceString(this,Constant.PARENT_PRODUCT_NAME,"");


        }catch (Exception ex){ex.printStackTrace();}


        initview();
    }



    public void initview(){

        subcat_rv = findViewById(R.id.subcat_rv);
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);



        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false);
        subcat_rv.setLayoutManager(mLayoutManager);
        //        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,dpToPx(10),true));
//        subcat_rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        subcat_rv.setItemAnimator(new DefaultItemAnimator());

        loadSubcategories(parent_product_id);

        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadSubcategories(parent_product_id);
            }
        });


        subcat_rv.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), subcat_rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                if (position>0){

                    SubCatview subCatview = subCatviewList.get(position-1);

                    Intent intent = new Intent(SubcatActivity.this,Productslistactivity.class);
//                    Bundle bundle = new Bundle();
//                    bundle.putInt(Constant.PARENT_PRODUCT_ID,parent_product_id);
//                    bundle.putString(Constant.SubCat_PRODUCT_ID,subCatview.getId());


                        sharedPreference.setSharedPreferenceString(SubcatActivity.this,Constant.SubCat_PRODUCT_ID,subCatview.getId());


//                    intent.putExtras(bundle);

//                    ActivityOptionsCompat options = ActivityOptionsCompat.
//                            makeSceneTransitionAnimation(MainActivity.this, (View)view, "parent_cat");
//                    startActivity(intent, options.toBundle());
//

                    startActivity(intent);
                }


//                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }




    private void loadSubcategories(int subCat_id) {

        swipe_refresh_layout.setRefreshing(true);


        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.loadsubcat(subCat_id,sharedPreference.getSharedPreferenceString(this,Constant.SELECTED_CITY_ID,""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<SubcatResponse>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onNext(SubcatResponse value) {

//                        Log.d("2.0 getFeed > Full json res wrapped in gson => ",new Gson().toJson(value));

                        if (!value.getError()) {

                            swipe_refresh_layout.setRefreshing(false);

                            subCatviewList.clear();

                            try{

                                for (int i=0;i<value.getCategoryData().size();i++){

                                    SubCatview subCatview = new SubCatview(
                                            value.getCategoryData().get(i).getId(),
                                            value.getCategoryData().get(i).getName(),
                                            value.getCategoryData().get(i).getImage()

                                    );

                                    subCatviewList.add(subCatview);

                                }


                            }catch (Exception ex){ex.printStackTrace();}




//                            Toast.makeText(MainActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        } else {

                            swipe_refresh_layout.setRefreshing(false);

                            Toast.makeText(SubcatActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        swipe_refresh_layout.setRefreshing(false);

                        Toast.makeText(SubcatActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        try{

                            if (subCatviewList.size()>0){
                                subCategoriesAdapter = new SubCategoriesAdapter(SubcatActivity.this, subCatviewList,parent_product_name,parent_product_img);

                                subcat_rv.setAdapter(subCategoriesAdapter);

                            }
                        }catch (Exception ex){ex.printStackTrace();}


                    }
                }));


    }
}
