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
import android.widget.Toast;

import com.google.gson.Gson;
import com.synram.morningbucket.API.ApiClient;
import com.synram.morningbucket.API.ApiInterface;
import com.synram.morningbucket.Adapter.AdapterCities;
import com.synram.morningbucket.Adapter.RegisterCitiesAdapter;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Modal.CityDatum;
import com.synram.morningbucket.Modal.CityResponse;
import com.synram.morningbucket.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class SearchCitiesActivity extends AppCompatActivity {


    private RecyclerView cities_rv;
    private SwipeRefreshLayout swipe_refresh_layout;
    private RegisterCitiesAdapter registerCitiesAdapter;
    private List<CityDatum> cityDatumList = new ArrayList<>();
    private CompositeDisposable compositeDisposable;
    private EditText search_city_et;
    private SearchView searchView;
    private Toolbar toolbar;
    private int locationSelect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_cities);
        compositeDisposable = new CompositeDisposable();

        initView();


    }

    public void initView(){

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        cities_rv = findViewById(R.id.cities_rv);
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false);
        cities_rv.setLayoutManager(layoutManager);
        cities_rv.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        loadCities(cities_rv);



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_search_city, menu);

        // Associate searchable configuration with the SearchView
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search)
                .getActionView();
        searchView.setSearchableInfo(searchManager
                .getSearchableInfo(getComponentName()));
        searchView.setMaxWidth(Integer.MAX_VALUE);

        // listening to search query text change
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // filter recycler view when query submitted
                registerCitiesAdapter.getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String query) {
                // filter recycler view when text is changed
                registerCitiesAdapter.getFilter().filter(query);
                return false;
            }
        });
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_search) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

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


    private void loadCities(final RecyclerView cities_rv) {

        swipe_refresh_layout.setRefreshing(true);
        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.getcitydata()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<CityResponse>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onNext(CityResponse value) {

                        Log.d("2.0 getFeed > Full json res wrapped in gson => ",new Gson().toJson(value));

                        if (!value.getError()) {

                            swipe_refresh_layout.setRefreshing(false);

                            cityDatumList.clear();

                            try{

                                for (int i=0;i<value.getCityData().size();i++){

                                    CityDatum cityDatum = new CityDatum(
                                            value.getCityData().get(i).getCityId(),
                                            value.getCityData().get(i).getCityName()

                                    );

                                    cityDatumList.add(cityDatum);

                                }


                            }catch (Exception ex){ex.printStackTrace();}


//                            Toast.makeText(MainActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        } else {

                            swipe_refresh_layout.setRefreshing(false);

                            Toast.makeText(SearchCitiesActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        swipe_refresh_layout.setRefreshing(false);

                        Toast.makeText(SearchCitiesActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        try{


                            if (cityDatumList.size()>0){
                                registerCitiesAdapter = new RegisterCitiesAdapter(SearchCitiesActivity.this, cityDatumList, new RegisterCitiesAdapter.ContactsAdapterListener() {
                                    @Override
                                    public void onContactSelected(CityDatum contact) {
//                                        Toast.makeText(getApplicationContext(), "Selected: " + contact.getCityName() + ", " + contact.getCityId(), Toast.LENGTH_LONG).show();

                                        locationSelect = Constant.SEARCH_CITY;
                                        Intent intent = new Intent();

                                        intent.putExtra("city", contact.getCityName());
                                        intent.putExtra("city_id", contact.getCityId());
                                        intent.putExtra("locationSelect", String.valueOf(locationSelect));

                                        setResult(RESULT_OK, intent);
                                        finish();


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
