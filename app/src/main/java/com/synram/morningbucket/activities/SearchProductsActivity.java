package com.synram.morningbucket.activities;

import android.annotation.SuppressLint;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.synram.morningbucket.API.ApiClient;
import com.synram.morningbucket.API.ApiInterface;
import com.synram.morningbucket.Adapter.ProductsSearchAdapter;
import com.synram.morningbucket.Adapter.RegisterCitiesAdapter;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Modal.CityDatum;
import com.synram.morningbucket.Modal.CityResponse;
import com.synram.morningbucket.Modal.SearchProduct;
import com.synram.morningbucket.Modal.SearchProdutsRespo;
import com.synram.morningbucket.R;
import com.synram.morningbucket.SharedprefData.SharedPreference;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class SearchProductsActivity extends AppCompatActivity {



    private RecyclerView cities_rv;
    private SwipeRefreshLayout swipe_refresh_layout;
    private ProductsSearchAdapter registerCitiesAdapter;
    private List<SearchProduct> searchProductList = new ArrayList<>();
    private CompositeDisposable compositeDisposable;
    private EditText search_products;
    private View lyt_no_item;
    private SharedPreference sharedPreference;
    private SearchView searchView;
    private Toolbar toolbar;
    private int locationSelect;
    private ImageView back_action;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_products);

        compositeDisposable = new CompositeDisposable();
        sharedPreference =new SharedPreference(this);
        initView();
    }



    public void initView(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cities_rv = findViewById(R.id.cities_rv);
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
        search_products = findViewById(R.id.search_products);
        back_action = findViewById(R.id.back_action);
        lyt_no_item = findViewById(R.id.lyt_no_item);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        cities_rv.setLayoutManager(layoutManager);
        cities_rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        loadProducts(cities_rv);



        search_products.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {



            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length()<=0){

                    cities_rv.setVisibility(View.GONE);
                    lyt_no_item.setVisibility(View.VISIBLE);

                }else {
                    cities_rv.setVisibility(View.VISIBLE);
                    lyt_no_item.setVisibility(View.GONE);

                }

            }

            @Override
            public void afterTextChanged(Editable s) {

                registerCitiesAdapter.getFilter().filter(s.toString());


            }
        });


        back_action.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


//    private void filter(String text) {
//        //new array list that will hold the filtered data
//        ArrayList<String> filterdNames = new ArrayList<>();
//
//        //looping through existing elements
//        for (String s : names) {
//            //if the existing elements contains the search input
//            if (s.toLowerCase().contains(text.toLowerCase())) {
//                //adding the element to filtered list
//                filterdNames.add(s);
//            }
//        }
//
//        //calling a method of the adapter class and passing the filtered list
//        adapter.filterList(filterdNames);
//    }


//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_search_city, menu);
//
//        // Associate searchable configuration with the SearchView
////        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
////        searchView = (SearchView) menu.findItem(R.id.action_search)
////                .getActionView();
////        searchView.setSearchableInfo(searchManager
////                .getSearchableInfo(getComponentName()));
////        searchView.setMaxWidth(Integer.MAX_VALUE);
////
////        // listening to search query text change
////        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
////            @Override
////            public boolean onQueryTextSubmit(String query) {
////                // filter recycler view when query submitted
////                registerCitiesAdapter.getFilter().filter(query);
////                return false;
////            }
////
////            @Override
////            public boolean onQueryTextChange(String query) {
////                // filter recycler view when text is changed
////                registerCitiesAdapter.getFilter().filter(query);
////                return false;
////            }
////        });
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_search) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onBackPressed() {
        // close search view on back button pressed
        if (!searchView.isIconified()) {
            searchView.setIconified(true);
            return;
        }
        super.onBackPressed();
    }

    private void whiteNotificationBar(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int flags = view.getSystemUiVisibility();
            flags |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
            view.setSystemUiVisibility(flags);
            getWindow().setStatusBarColor(Color.WHITE);
        }
    }


    private void loadProducts(final RecyclerView cities_rv) {

        swipe_refresh_layout.setRefreshing(true);
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.searchProducts(sharedPreference.getSharedPreferenceString(SearchProductsActivity.this,Constant.SELECTED_CITY_ID,""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<SearchProdutsRespo>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onNext(SearchProdutsRespo value) {

                        Log.d("2.0 getFeed > Full json res wrapped in gson => ",new Gson().toJson(value));

                        if (!value.getError()) {

                            swipe_refresh_layout.setRefreshing(false);

                            searchProductList.clear();

                            try{

                                for (int i=0;i<value.getSearchProduct().size();i++){

                                    SearchProduct searchProduct = new SearchProduct(
                                            value.getSearchProduct().get(i).getId(),
                                            value.getSearchProduct().get(i).getName()
                                    );

                                    searchProductList.add(searchProduct);

                                }


                            }catch (Exception ex){ex.printStackTrace();}


//                            Toast.makeText(MainActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        } else {

                            swipe_refresh_layout.setRefreshing(false);

                            Toast.makeText(SearchProductsActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        swipe_refresh_layout.setRefreshing(false);

                        Toast.makeText(SearchProductsActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        try{


                            if (searchProductList.size()>0){
                                registerCitiesAdapter = new ProductsSearchAdapter(SearchProductsActivity.this, searchProductList, new ProductsSearchAdapter.ContactsAdapterListener() {
                                    @Override
                                    public void onContactSelected(SearchProduct contact) {
//                                        Toast.makeText(getApplicationContext(), "Selected: " + contact.getCityName() + ", " + contact.getCityId(), Toast.LENGTH_LONG).show();

//                                        locationSelect = Constant.SEARCH_CITY;
//                                        Intent intent = new Intent();
//
//                                        intent.putExtra("city", contact.getCityName());
//                                        intent.putExtra("city_id", contact.getCityId());
//                                        intent.putExtra("locationSelect", locationSelect);
//
//                                        setResult(RESULT_OK, intent);
//                                        finish();
                                        sharedPreference.setSharedPreferenceString(SearchProductsActivity.this,Constant.SINGLE_PRODUCT_ID,contact.getId());

                                        Intent intent = new Intent(SearchProductsActivity.this,ProductsDescriptionActivity.class);
                                        startActivity(intent);


                                    }
                                });
                                cities_rv.setAdapter(registerCitiesAdapter);

                                registerCitiesAdapter.notifyDataSetChanged();
                            }
                        }catch (Exception ex){ex.printStackTrace();}


                    }
                }));


    }
}
