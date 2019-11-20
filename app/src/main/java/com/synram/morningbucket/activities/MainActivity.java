package com.synram.morningbucket.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import com.github.javiersantos.appupdater.AppUpdater;
import com.github.javiersantos.appupdater.AppUpdaterUtils;
import com.github.javiersantos.appupdater.enums.AppUpdaterError;
import com.github.javiersantos.appupdater.enums.Display;
import com.github.javiersantos.appupdater.enums.UpdateFrom;
import com.github.javiersantos.appupdater.objects.Update;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Picasso;
import com.synram.morningbucket.API.ApiClient;
import com.synram.morningbucket.API.ApiInterface;
import com.synram.morningbucket.Adapter.AdapterCities;
import com.synram.morningbucket.Adapter.AlbumsAdapter;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Database.DatabaseHandler;
import com.synram.morningbucket.Fragment.DeliveryFragment;
import com.synram.morningbucket.Fragment.HomeFragment;
import com.synram.morningbucket.Fragment.MyAccount;
import com.synram.morningbucket.Fragment.PlansFragment;
import com.synram.morningbucket.Modal.Album;
import com.synram.morningbucket.Modal.BannerImage;
import com.synram.morningbucket.Modal.Banner_response;
import com.synram.morningbucket.Modal.CategoryDatum;
import com.synram.morningbucket.Modal.CityDatum;
import com.synram.morningbucket.Modal.CityResponse;
import com.synram.morningbucket.Modal.ProductsCatResponse;
import com.synram.morningbucket.R;
import com.synram.morningbucket.SharedprefData.SharedPreference;
import com.synram.morningbucket.Utilities.CountDrawable;
import com.synram.morningbucket.Utilities.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

import io.fabric.sdk.android.Fabric;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {


    private static final String REGISTRATION_COMPLETE = "registrationComplete";
    private static final String PUSH_NOTIFICATION = "pushNotification";
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle mDrawerToggle;
    SharedPreference sharedPreference;
    private ViewPager viewPager;
    private MyViewPagerAdapter myViewPagerAdapter;
    private LinearLayout dotsLayout;
    private ImageView[] dots;
    private TypedArray about_images_array;
    //  viewpager change listener
    ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);

            if (position == about_images_array.length() - 1) {

            } else {

            }
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };
    private SwipeRefreshLayout swipe_refresh_layout;
    private List<CategoryDatum> categoryDatumList = new ArrayList<>();
    private List<BannerImage> bannerImageList = new ArrayList<>();
    private List<CityDatum> cityDatumList = new ArrayList<>();
    private TextView city_toolbar;
    private RecyclerView recyclerView;
    private AlbumsAdapter adapter;
    private List<Album> albumList;
    private CompositeDisposable compositeDisposable;
    private NavigationView leftDrawer;
    private View lytDrawer;
    private DatabaseHandler db;
    private Menu defaultMenu;
    private Dialog dialog;
    private AdapterCities adapterCities;
    private SearchView searchView;
    private String city_id;
    private boolean doubleBackToExitPressedOnce = false;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private BottomNavigationView navigation;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            Fragment fragment;
            switch (item.getItemId()) {
                case R.id.store:
                    fragment = new HomeFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.delivery:
                    fragment = new DeliveryFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.plans:
                    fragment = new PlansFragment();
                    loadFragment(fragment);
                    return true;
                case R.id.me:
                    fragment = new MyAccount();
                    loadFragment(fragment);
                    return true;
            }

            return false;
        }
    };

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        db = new DatabaseHandler(this);
        compositeDisposable = new CompositeDisposable();
        sharedPreference = new SharedPreference(this);
        Fabric.with(this, new Crashlytics());


//        Tools.setSystemBarColor(this,R.color.colorPrimary);

//        toolbar.setTitle("dsd");
        getSupportActionBar().setTitle("");
        getSupportActionBar().setIcon(R.mipmap.ic_launcher);

        leftDrawer = (NavigationView) findViewById(R.id.navigation_view_base_appcompat);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout_appcompat);


        city_toolbar = findViewById(R.id.city_toolbar);

        if (!SharedPreference.getSharedPreferenceString(this, Constant.SELECTED_CITY_ID, "").equals("")) {

            city_toolbar.setText(SharedPreference.getSharedPreferenceString(this, Constant.SELECTED_CITY_NAME, ""));
            city_toolbar.setClickable(false);

        } else {

            city_toolbar.setText("Select City");
            city_toolbar.setClickable(true);

        }


//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
////            toolbar.setPadding(0, App.getStatusBarHeight(getApplicationContext()), 0, 0);
//            leftDrawer.setPadding(0, 0, 0, 0);
//            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
//            getWindow().addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
//        }

        viewPager = (ViewPager) findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) findViewById(R.id.layoutDots);
        swipe_refresh_layout = findViewById(R.id.swipe_refresh_layout);
        recyclerView = findViewById(R.id.productcat_rv);
        about_images_array = getResources().obtainTypedArray(R.array.slider_images_array);

        recyclerView.setNestedScrollingEnabled(false);
        // adding bottom dots
        addBottomDots(0);

        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);


        albumList = new ArrayList<>();


        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(this, 2);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), true));

//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,dpToPx(10),true));
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        recyclerView.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(adapter);

//        loadbanners();
//        loadCategories();


//        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
//            @Override
//            public void onRefresh() {
//                loadCategories();
//            }
//        });


//        mDrawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar,
//                R.string.drawer_open, R.string.drawer_close);
//        mDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,toolbar,R.string.drawer_open, R.string.drawer_close) {
//
//            /** Called when a drawer has settled in a completely closed state. */
//            public void onDrawerClosed(View view) {
//                super.onDrawerClosed(view);
//                /*				if(view == rightDrawer) {
//                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, leftDrawer);
//				} else if(view == leftDrawer) {
//					drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED, rightDrawer);
//				}*/
//            }
//
//            /** Called when a drawer has settled in a completely open state. */
//            public void onDrawerOpened(View drawerView) {
//                super.onDrawerOpened(drawerView);
//                /*				if(drawerView == rightDrawer) {
//                    drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, leftDrawer);
//				} else if(drawerView == leftDrawer) {
//					drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED, rightDrawer);
//				}*/
//            }
//        };


        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
//        mDrawerToggle.setDrawerIndicatorEnabled(false);
//        Drawable drawable = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_menuicon, getTheme());
//        mDrawerToggle.setHomeAsUpIndicator(drawable);
//        mDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if (drawerLayout.isDrawerVisible(GravityCompat.START)) {
////                    drawerLayout.closeDrawer(GravityCompat.START);
////                } else {
////                    drawerLayout.openDrawer(GravityCompat.START);
////                }
//            }
//        });


//        mDrawerToggle.setDrawerIndicatorEnabled(false);
//        DrawerArrowDrawable drawerArrowDrawable = new DrawerArrowDrawable(this);
//        drawerArrowDrawable.setAlpha(1);
//        drawerArrowDrawable.setSpinEnabled(false);
//        drawerArrowDrawable.setDirection(DrawerArrowDrawable.ARROW_DIRECTION_LEFT);
//        drawerArrowDrawable.setColor(Color.BLACK);

//        mDrawerToggle.setDrawerArrowDrawable(drawerArrowDrawable);
//        mDrawerToggle.syncState();
//        drawerLayout.addDrawerListener(mDrawerToggle);
        setLeftDrawer();


        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                CategoryDatum categoryDatum = categoryDatumList.get(position);


                Intent intent = new Intent(MainActivity.this, Productslistactivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putInt(Constant.PARENT_PRODUCT_ID,categoryDatum.getId());
//                bundle.putString(Constant.PARENT_PRODUCT_IMG,categoryDatum.getImage());
//                bundle.putString(Constant.PARENT_PRODUCT_NAME,categoryDatum.getName());
//
//                intent.putExtras(bundle);


                sharedPreference.setSharedPreferenceInt(MainActivity.this, Constant.PARENT_PRODUCT_ID, categoryDatum.getId());
                sharedPreference.setSharedPreferenceString(MainActivity.this, Constant.PARENT_PRODUCT_IMG, categoryDatum.getImage());
                sharedPreference.setSharedPreferenceString(MainActivity.this, Constant.PARENT_PRODUCT_NAME, categoryDatum.getName());

                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(MainActivity.this, (View) view, "parent_cat");
                startActivity(intent, options.toBundle());


//                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        city_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCitySelectorDialog(MainActivity.this);

            }
        });


        loadFragment(new HomeFragment());

        navigation = (BottomNavigationView) findViewById(R.id.navigation);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        AppUpdaterUtils appUpdaterUtils = new AppUpdaterUtils(this).withListener(new AppUpdaterUtils.UpdateListener() {
            @Override
            public void onSuccess(Update update, Boolean aBoolean) {

                if (aBoolean) {
                    new AppUpdater(MainActivity.this)
                            .setUpdateFrom(UpdateFrom.GOOGLE_PLAY)
                            .setDisplay(Display.DIALOG)
                            .showAppUpdated(true).setCancelable(false)
                            .setCancelable(false)

                            .setButtonDismiss(null)
                            .setButtonDoNotShowAgain(null)
                            .start();

                }

            }

            @Override
            public void onFailed(AppUpdaterError appUpdaterError) {

            }
        });

        appUpdaterUtils.start();

        String firebaseToken = FirebaseInstanceId.getInstance().getToken();

//        Log.d("TOKENDATA",firebaseToken);

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // checking for type intent filter
                if (intent.getAction().equals(REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications

                    // FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);

                    displayFirebaseRegId();

                } else if (intent.getAction().equals(PUSH_NOTIFICATION)) {
                    // new push notification is received

                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();


                }
            }
        };

        displayFirebaseRegId();

//        Crashlytics.getInstance().crash();

    }

    private void loadFragment(Fragment fragment) {
        // load fragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.frame_container, fragment);
//        transaction.addToBackStack(null);
        transaction.commit();
    }

    public void showCitySelectorDialog(final Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custom_city_selector_layout, null, false);
        /*HERE YOU CAN FIND YOU IDS AND SET TEXTS OR BUTTONS*/

        RecyclerView cityes_rv = view.findViewById(R.id.cityes_rv);

        cityes_rv.setLayoutManager(new LinearLayoutManager(this));

        cityes_rv.setNestedScrollingEnabled(false);

        cityes_rv.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                CityDatum movie = cityDatumList.get(position);
                city_toolbar.setText(movie.getCityName());
                sharedPreference.setSharedPreferenceString(MainActivity.this, Constant.SELECTED_CITY_ID, movie.getCityId());
                sharedPreference.setSharedPreferenceString(MainActivity.this, Constant.SELECTED_CITY_NAME, movie.getCityName());

                loadFragment(new HomeFragment());

                dialog.dismiss();
            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));

        loadCities(cityes_rv);


        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setContentView(view);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);
        window.setGravity(Gravity.CENTER);
        dialog.show();
    }

    // Fetches reg id from shared preferences
    // and displays on the screen
    private void displayFirebaseRegId() {
        String regId = sharedPreference.getSharedPreferenceString(this, "regId", "");

        Log.e("TOKEN", "Firebase reg id: " + regId);


    }

    @Override
    protected void onResume() {
        super.onResume();

        // register GCM registration complete receiver
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(REGISTRATION_COMPLETE));

        // register new push message receiver
        // by doing this, the activity will be notified each time a new message arrives
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(PUSH_NOTIFICATION));
    }

    @Override
    protected void onPause() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        setCount(MainActivity.this, String.valueOf(db.getCartSize()));


//        new GuideView.Builder(this)
//                .setTitle("Guide Title Text")
//                .setContentText("Guide Description Text\n .....Guide Description Text\n .....Guide Description Text .....")
//
//                .setDismissType(DismissType.anywhere) //optional - default DismissType.targetView
//                .setTargetView()
//                .setContentTextSize(12)//optional
//                .setTitleTextSize(14)//optional
//                .build()
//                .show();

        return super.onPrepareOptionsMenu(menu);


    }

    public void setCount(Context context, String count) {
        MenuItem menuItem = defaultMenu.findItem(R.id.action_cart);
        LayerDrawable icon = (LayerDrawable) menuItem.getIcon();

        CountDrawable badge;


        // Reuse drawable if possible
        Drawable reuse = icon.findDrawableByLayerId(R.id.ic_group_count);
        if (reuse != null && reuse instanceof CountDrawable) {
            badge = (CountDrawable) reuse;
        } else {
            badge = new CountDrawable(context);
        }

        badge.setCount(count);
        icon.mutate();
        icon.setDrawableByLayerId(R.id.ic_group_count, badge);
    }

    private void setLeftDrawer() {

        LayoutInflater inflater = getLayoutInflater();
        lytDrawer = inflater.inflate(R.layout.layout_drawer, null);

//        txtName = (TextView) lytDrawer.findViewById(R.id.txt_drawer_name);
//        txtEmail = (TextView) lytDrawer.findViewById(R.id.txt_drawer_email);
//        addmoney = (TextView) lytDrawer.findViewById(R.id.addmoney);


//        leftDrawer.addView(lytDrawer);

/*        ivUserDP = (ImageView) lytDrawer.findViewById(R.id.iv_drawer_profile_photo);
        txtName = (CustomTextView) lytDrawer.findViewById(R.id.txt_drawer_name);
        txtPhone = (CustomTextView) lytDrawer.findViewById(R.id.txt_drawer_phone);
        txtEmail = (CustomTextView) lytDrawer.findViewById(R.id.txt_drawer_email);*/

        leftDrawer.addView(lytDrawer);

    }

    private void addBottomDots(int currentPage) {
        dots = new ImageView[about_images_array.length()];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(this);
            int width_height = 20;
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(new ViewGroup.LayoutParams(width_height, width_height));
            params.setMargins(10, 10, 10, 10);
            dots[i].setLayoutParams(params);
            dots[i].setImageResource(R.drawable.shape_circle_outline);
            dotsLayout.addView(dots[i]);
        }
        if (dots.length > 0) {
            dots[currentPage].setBackgroundResource(R.drawable.shape_circle);

        }
    }

    private int getItem(int i) {
        return viewPager.getCurrentItem() + i;
    }

    /**
     * Converting dp to pixel
     */
    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }

    private void loadCategories() {

        swipe_refresh_layout.setRefreshing(true);


        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.loadCategories(sharedPreference.getSharedPreferenceString(this, Constant.SELECTED_CITY_ID, ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<ProductsCatResponse>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onNext(ProductsCatResponse value) {

//                        Log.d("2.0 getFeed > Full json res wrapped in gson => ",new Gson().toJson(value));

                        if (!value.getError()) {

                            swipe_refresh_layout.setRefreshing(false);

                            categoryDatumList.clear();

                            try {

                                for (int i = 0; i < value.getCategoryData().size(); i++) {

                                    CategoryDatum categoryDatum = new CategoryDatum(
                                            value.getCategoryData().get(i).getId(),
                                            value.getCategoryData().get(i).getName(),
                                            value.getCategoryData().get(i).getImage(),
                                            value.getCategoryData().get(i).getSubcat_id()

                                    );

                                    categoryDatumList.add(categoryDatum);

                                }


                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }


//                            Toast.makeText(MainActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        } else {

                            swipe_refresh_layout.setRefreshing(false);

                            Toast.makeText(MainActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        swipe_refresh_layout.setRefreshing(false);

                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        try {

                            if (categoryDatumList.size() > 0) {
                                adapter = new AlbumsAdapter(MainActivity.this, categoryDatumList);

                                recyclerView.setAdapter(adapter);

                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }


                    }
                }));


    }

    private void loadbanners() {

        swipe_refresh_layout.setRefreshing(true);


        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.loadbanners()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<Banner_response>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onNext(Banner_response value) {

//                        Log.d("2.0 getFeed > Full json res wrapped in gson => ",new Gson().toJson(value));

                        if (!value.getError()) {

                            swipe_refresh_layout.setRefreshing(false);

                            bannerImageList.clear();

                            try {

                                for (int i = 0; i < value.getBannerImages().size(); i++) {

                                    BannerImage bannerImage = new BannerImage(
                                            value.getBannerImages().get(i).getId(),
                                            value.getBannerImages().get(i).getBanner()

                                    );

                                    bannerImageList.add(bannerImage);

                                }


                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }


//                            Toast.makeText(MainActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        } else {

                            swipe_refresh_layout.setRefreshing(false);

                            Toast.makeText(MainActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        swipe_refresh_layout.setRefreshing(false);

                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        try {

                            if (bannerImageList.size() > 0) {
                                myViewPagerAdapter = new MyViewPagerAdapter(bannerImageList);
                                viewPager.setAdapter(myViewPagerAdapter);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }


                    }
                }));


    }

    private void loadCities(final RecyclerView cities_rv) {

//        swipe_refresh_layout.setRefreshing(true);


        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.getcitydata()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<CityResponse>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onNext(CityResponse value) {

//                        Log.d("2.0 getFeed > Full json res wrapped in gson => ",new Gson().toJson(value));

                        if (!value.getError()) {

//                            swipe_refresh_layout.setRefreshing(false);

                            cityDatumList.clear();

                            try {

                                for (int i = 0; i < value.getCityData().size(); i++) {

                                    CityDatum cityDatum = new CityDatum(
                                            value.getCityData().get(i).getCityId(),
                                            value.getCityData().get(i).getCityName()

                                    );

                                    cityDatumList.add(cityDatum);

                                }


                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }


//                            Toast.makeText(MainActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        } else {

                            swipe_refresh_layout.setRefreshing(false);

                            Toast.makeText(MainActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
//                        swipe_refresh_layout.setRefreshing(false);

                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        try {

                            if (cityDatumList.size() > 0) {
                                adapterCities = new AdapterCities(MainActivity.this, cityDatumList);
                                cities_rv.setAdapter(adapterCities);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }


                    }
                }));


    }

    //this function is used for create menu in activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        defaultMenu = menu;
        getMenuInflater().inflate(R.menu.menu_home, menu);


//        wishlist_menu = menu.findItem(R.id.action_wish);
//        refreshWishlistMenu();


//        cartbutton = menuItem.getActionView();


        return true;
    }

    @Override
    public void onBackPressed() {
//        super.onBackPressed();
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, 2000);

    }

    private void onBackAction() {
        setCount(getApplicationContext(), String.valueOf(db.getCartSize()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            onBackAction();
        } else if (item_id == R.id.action_cart) {


            Intent i = new Intent(this, ShopingCartActivity.class);
            startActivity(i);

        } else if (item_id == R.id.action_search) {


            Intent i = new Intent(this, SearchProductsActivity.class);
            startActivity(i);

        }
        return super.onOptionsItemSelected(item);
    }

    public void navigateToOrders(View view) {

        Intent intent = new Intent(MainActivity.this, OrderListActivity.class);
        startActivity(intent);
    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        private List<BannerImage> bannerImageList = new ArrayList<>();

        public MyViewPagerAdapter(List<BannerImage> bannerImageList) {
            this.bannerImageList = bannerImageList;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            View view = layoutInflater.inflate(R.layout.item_slider, container, false);
            ImageView slider_image = ((ImageView) view.findViewById(R.id.img_slider));

            Picasso.get().load(bannerImageList.get(position).getBanner()).into(slider_image);
            container.addView(view);

            return view;
        }

        @Override
        public int getCount() {
            return bannerImageList.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object obj) {
            return view == obj;
        }


        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            View view = (View) object;
            container.removeView(view);
        }
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
}
