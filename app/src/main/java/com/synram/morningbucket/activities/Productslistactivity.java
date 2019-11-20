package com.synram.morningbucket.activities;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.synram.morningbucket.API.ApiClient;
import com.synram.morningbucket.API.ApiInterface;
import com.synram.morningbucket.Adapter.AlbumsAdapter;
import com.synram.morningbucket.Adapter.ProductsTagsAdapter;
import com.synram.morningbucket.Adapter.SubCategoriesAdapter;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Fragment.StoreFragment;
import com.synram.morningbucket.Modal.BrandDatum;
import com.synram.morningbucket.Modal.CategoryDatum;
import com.synram.morningbucket.Modal.LoginResponse;
import com.synram.morningbucket.Modal.ProductDatum;
import com.synram.morningbucket.Modal.ProductsListResponse;
import com.synram.morningbucket.Modal.SubCatview;
import com.synram.morningbucket.Modal.SubcatResponse;
import com.synram.morningbucket.Modal.TypeDatum;
import com.synram.morningbucket.R;
import com.synram.morningbucket.SharedprefData.SharedPreference;
import com.synram.morningbucket.Utilities.LockableViewPager;
import com.synram.morningbucket.Utilities.RecyclerTouchListener;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class Productslistactivity extends AppCompatActivity {


//    subcategores data type
    RecyclerView subcat_rv;
    private SubCategoriesAdapter subCategoriesAdapter;
    List<SubCatview> subCatviewList = new ArrayList<>();

    private String parent_product_img;
    private String parent_product_name;

    private ProgressDialog progressDialog;



    private SwipeRefreshLayout swipeRefreshLayout;

    private TabLayout tabLayout;
    private FrameLayout viewPager;
    private CompositeDisposable compositeDisposable;

    List<BrandDatum> brandDatumList = new ArrayList<>();
    List<TypeDatum> typeDatumList = new ArrayList<>();
    List<ProductDatum> productDatumList = new ArrayList<>();
    private int parent_product_id;
    private String subcat_product_id;
    private SharedPreference sharedPreference;
    private RecyclerView tags_rv;
    private ProductsTagsAdapter adapter;
    private int tabposition=0;
    private StoreFragment storeFragment;
    private int typeposition=0;
    private int tabPositionfor_type=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_productslistactivity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        sharedPreference = new SharedPreference(this);
        compositeDisposable = new CompositeDisposable();
        progressDialog = new ProgressDialog(this);

        try{
            parent_product_id = sharedPreference.getSharedPreferenceInt(this,Constant.PARENT_PRODUCT_ID,0);

            //            subcat_product_id = sharedPreference.getSharedPreferenceString(this,Constant.SubCat_PRODUCT_ID,"");

            parent_product_img = sharedPreference.getSharedPreferenceString(this,Constant.PARENT_PRODUCT_IMG,"");
            parent_product_name = sharedPreference.getSharedPreferenceString(this,Constant.PARENT_PRODUCT_NAME,"");


        }catch (Exception ex){ex.printStackTrace();}




        initview();




    }

    public void initview(){


        subcat_rv = findViewById(R.id.subcat_rv);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        subcat_rv.setLayoutManager(mLayoutManager);
        //        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,dpToPx(10),true));
//        subcat_rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        subcat_rv.setItemAnimator(new DefaultItemAnimator());



        subcat_rv.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), subcat_rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                    SubCatview subCatview = subCatviewList.get(position);

                    sharedPreference.setSharedPreferenceInt(Productslistactivity.this,Constant.SubCat_PRODUCT_ID,Integer.parseInt(subCatview.getId()));


                    loadproducts(viewPager,parent_product_id,Integer.parseInt(subCatview.getId()));

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        tabLayout = (TabLayout) findViewById(R.id.tabs);

        viewPager = (FrameLayout) findViewById(R.id.viewpager);
        tags_rv = (RecyclerView) findViewById(R.id.tags_rv);
        RecyclerView.LayoutManager mLayoutManager_2 = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false);
        tags_rv.setLayoutManager(mLayoutManager_2);
//        tags_rv.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), true));

//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,dpToPx(10),true));
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));


        tags_rv.setItemAnimator(new DefaultItemAnimator());

        loadSubcategories(parent_product_id);


//        loadproducts(viewPager,parent_product_id,Integer.parseInt(subcat_product_id));


        bindWidgetsWithAnEvent();

//        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                loadSubcategories(parent_product_id);
//            }
//        });





        tags_rv.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), subcat_rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                typeposition=position;

                if (brandDatumList.size()>0){
                    BrandDatum brandDatum = brandDatumList.get(tabPositionfor_type);
                    TypeDatum typeDatum  = typeDatumList.get(typeposition);

                    storeFragment = new StoreFragment(Integer.parseInt(typeDatum.getTypeId()),Integer.parseInt(brandDatum.getBrandId()));

                    replaceFragment(storeFragment);

                }


            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

    }


    private void loadproducts(FrameLayout viewPager, int cat_id, int sub_cat_id) {
        brandDatumList.clear();
        typeDatumList.clear();
        productDatumList.clear();

        progressDialog.setMessage("Please Wait...");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();


        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.loadProducts(cat_id,sub_cat_id,sharedPreference.getSharedPreferenceString(Productslistactivity.this,Constant.SELECTED_CITY_ID,""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<ProductsListResponse>() {


                    @Override
                    public void onNext(ProductsListResponse value) {
                        Log.d("BRAND,PRODUCTS,TYPE",new Gson().toJson(value));

                        if (!value.getError()) {

                        progressDialog.dismiss();
                        for(int i=0;i<value.getBrandData().size();i++){
                                BrandDatum brandDatum = new BrandDatum(value.getBrandData().get(i).getBrandId(),value.getBrandData().get(i).getBrandName());

                                brandDatumList.add(brandDatum);


                            }

                            for(int i=0;i<value.getTypeData().size();i++){

                                TypeDatum typeDatum = new TypeDatum(value.getTypeData().get(i).getTypeId(),value.getTypeData().get(i).getTypeName());

                                typeDatumList.add(typeDatum);

                            }

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

                            progressDialog.dismiss();

                            Toast.makeText(Productslistactivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.dismiss();

                        Toast.makeText(Productslistactivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        if (typeDatumList.size()>0){
                            adapter = new ProductsTagsAdapter(Productslistactivity.this, typeDatumList);

                            tags_rv.setAdapter(adapter);
                        }

                        Log.d("BRANDDATUM",String.valueOf(brandDatumList.size()));
                        if (brandDatumList.size()>0){

                            setupTabLayout(brandDatumList,typeDatumList);


//                            setupViewPager(viewPager,brandDatumList,productDatumList,tabposition);

                        }


                    }
                }));


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Intent intent = new Intent(Productslistactivity.this,MainActivity.class);
        startActivity(intent);
        finish();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            super.onBackPressed();


            Intent intent = new Intent(Productslistactivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }


    /**
     * RecyclerView item decoration - give equal margin around grid item
     */
    public class GridSpacingItemDecoration extends RecyclerView.ItemDecoration {

        private int spanCount;
        private int spacing;
        private boolean includeEdge;

        public GridSpacingItemDecoration(int spanCount, int spacing, boolean includeEdge) {
            this.spanCount = spanCount;
            this.spacing = spacing;
            this.includeEdge = includeEdge;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view); // item position
            int column = position % spanCount; // item column

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount; // spacing - column * ((1f / spanCount) * spacing)
                outRect.right = (column + 1) * spacing / spanCount; // (column + 1) * ((1f / spanCount) * spacing)

                if (position < spanCount) { // top edge
                    outRect.top = spacing;
                }
                outRect.bottom = spacing; // item bottom
            } else {
                outRect.left = column * spacing / spanCount; // column * ((1f / spanCount) * spacing)
                outRect.right = spacing - (column + 1) * spacing / spanCount; // spacing - (column + 1) * ((1f /    spanCount) * spacing)
                if (position >= spanCount) {
                    outRect.top = spacing; // item top
                }
            }
        }
    }



    private void filterbytags_or_tab(String cat_id, String sub_cat_id,String tag_id,String tab_id) {

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.filterbybrand(tab_id,tag_id,cat_id,sub_cat_id,sharedPreference.getSharedPreferenceString(Productslistactivity.this,Constant.SELECTED_CITY_ID,""))
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


                        } else {

                            Toast.makeText(Productslistactivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(Productslistactivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        if (brandDatumList.size()>0){

//                            setupViewPager(viewPager,brandDatumList,productDatumList,tabposition);
                        }


                    }
                }));


    }


    private void bindWidgetsWithAnEvent()
    {
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                tabposition = tab.getPosition();


                setCurrentTabFragment(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void setupTabLayout(List<BrandDatum> brandDatumList,List<TypeDatum>typeDatumList) {

//        BrandDatum brandDatum = brandDatumList.get(0);
        TypeDatum typeDatum  = typeDatumList.get(0);

        tabLayout.removeAllTabs();

        for (int i=0;i<brandDatumList.size();i++){
            storeFragment = new StoreFragment(Integer.parseInt(typeDatum.getTypeId()),Integer.parseInt(brandDatumList.get(i).getBrandId()));

            if (i==0){
                tabLayout.addTab(tabLayout.newTab().setText(brandDatumList.get(i).getBrandName()),true);

            }else {
                tabLayout.addTab(tabLayout.newTab().setText(brandDatumList.get(i).getBrandName()),false);

            }

        }


//        for (int i=0;i<brandDatumList.size();i++){
//
//            if (i==0){
//                tabLayout.addTab(tabLayout.newTab().setText(brandDatumList.get(i).getBrandName()),true);
//
//            }else {
//
//
//            }
//

//        }


    }

    private void setCurrentTabFragment(int tabPosition)
    {

        tabPositionfor_type = tabposition;
        if (brandDatumList.size()>0){
            BrandDatum brandDatum = brandDatumList.get(tabPosition);
            TypeDatum typeDatum  = typeDatumList.get(typeposition);

            storeFragment = new StoreFragment(0,Integer.parseInt(brandDatum.getBrandId()));

            replaceFragment(storeFragment);

        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        ft.replace(R.id.viewpager, fragment);
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        ft.commit();
    }

    @Override
    protected void onStop() {
        super.onStop();

        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    private void loadSubcategories(int subCat_id) {

        swipeRefreshLayout.setRefreshing(true);


        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.loadsubcat(subCat_id,sharedPreference.getSharedPreferenceString(this,Constant.SELECTED_CITY_ID,""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<SubcatResponse>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onNext(SubcatResponse value) {

                        Log.d("SubCategories",new Gson().toJson(value));

                        if (!value.getError()) {

                            swipeRefreshLayout.setRefreshing(false);

                            subCatviewList.clear();

                            try{

//                                sharedPreference.setSharedPreferenceString(Productslistactivity.this,Constant.SubCat_PRODUCT_ID,value.getCategoryData().get(0).getId());
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

                            swipeRefreshLayout.setRefreshing(false);

                            Toast.makeText(Productslistactivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        swipeRefreshLayout.setRefreshing(false);

                        Toast.makeText(Productslistactivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        try{

                            if (subCatviewList.size()>0){
                                subCategoriesAdapter = new SubCategoriesAdapter(Productslistactivity.this, subCatviewList,parent_product_name,parent_product_img);

                                subcat_rv.setAdapter(subCategoriesAdapter);

                               int subcategoryId = sharedPreference.getSharedPreferenceInt(Productslistactivity.this,Constant.SubCat_PRODUCT_ID,0);

//                               Log.d("SCATID",subcategoryId);
                                loadproducts(viewPager,parent_product_id,subcategoryId);

                            }
                        }catch (Exception ex){ex.printStackTrace();}


                    }
                }));


    }
}
