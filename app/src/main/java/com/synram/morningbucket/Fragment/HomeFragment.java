package com.synram.morningbucket.Fragment;


import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.synram.morningbucket.API.ApiClient;
import com.synram.morningbucket.API.ApiInterface;
import com.synram.morningbucket.Adapter.AlbumsAdapter;
import com.synram.morningbucket.Adapter.OrganiccatAlbumsAdapter;
import com.synram.morningbucket.Adapter.SubcatAlbumsAdapter;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Database.DatabaseHandler;
import com.synram.morningbucket.Modal.AddsImage;
import com.synram.morningbucket.Modal.Adds_response;
import com.synram.morningbucket.Modal.Album;
import com.synram.morningbucket.Modal.BannerImage;
import com.synram.morningbucket.Modal.Banner_response;
import com.synram.morningbucket.Modal.CategoryDatum;
import com.synram.morningbucket.Modal.DairyCatRespo;
import com.synram.morningbucket.Modal.DairyProResponse;
import com.synram.morningbucket.Modal.OrganicCatRespo;
import com.synram.morningbucket.Modal.OrganicProResponse;
import com.synram.morningbucket.Modal.ProductsCatResponse;
import com.synram.morningbucket.Modal.SubCatview;
import com.synram.morningbucket.R;
import com.synram.morningbucket.SharedprefData.SharedPreference;
import com.synram.morningbucket.Utilities.RecyclerTouchListener;
import com.synram.morningbucket.activities.MainActivity;
import com.synram.morningbucket.activities.Productslistactivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * A simple {@link Fragment} subclass.
 */


public class HomeFragment extends Fragment {


    final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
    final long PERIOD_MS = 10000; // time in milliseconds between successive task executions.
    SharedPreference sharedPreference;
    int currentPage = 0;
    private LinearLayout dotsLayout, addbanner_two_ll, addbanner_one_ll;
    private RecyclerView dairy_products_rv, organic_products_rv;
    private RelativeLayout dairy_rl, organic_rl;
    private ImageView addbaner_one, addbaner_two;
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
    private List<SubCatview> subCatviewList = new ArrayList<>();
    private List<DairyCatRespo> dairyCatRespoList = new ArrayList<>();
    private List<OrganicCatRespo> organicCatRespoList = new ArrayList<>();
    private List<BannerImage> bannerImageList = new ArrayList<>();
    private List<AddsImage> addsImageList = new ArrayList<>();
    private TextView city_toolbar;
    private RecyclerView recyclerView;
    private AlbumsAdapter adapter;
    private SubcatAlbumsAdapter subcatAlbumsAdapter;
    private OrganiccatAlbumsAdapter organiccatAlbumsAdapter;
    private List<Album> albumList;
    private CompositeDisposable compositeDisposable;
    private DatabaseHandler db;
    private MyViewPagerAdapter myViewPagerAdapter;
    private ViewPager viewPager;
    private Timer timer;
    private String dairy_cat_id;
    private String organic_cat_id;
    private String addtwourl = "";
    private String addone_url = "";


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        db = new DatabaseHandler(getActivity());
        compositeDisposable = new CompositeDisposable();
        sharedPreference = new SharedPreference(getActivity());

        viewPager = (ViewPager) view.findViewById(R.id.view_pager);
        dotsLayout = (LinearLayout) view.findViewById(R.id.layoutDots);
        swipe_refresh_layout = view.findViewById(R.id.swipe_refresh_layout);
        recyclerView = view.findViewById(R.id.productcat_rv);
        dairy_products_rv = view.findViewById(R.id.dairy_products_rv);
        organic_products_rv = view.findViewById(R.id.organic_products_rv);
        addbanner_two_ll = view.findViewById(R.id.addbanner_two_ll);
        addbanner_one_ll = view.findViewById(R.id.addbanner_one_ll);
        addbaner_one = view.findViewById(R.id.addbannerone);
        addbaner_two = view.findViewById(R.id.addbannertwo);
        dairy_rl = view.findViewById(R.id.dairy_rl);
        organic_rl = view.findViewById(R.id.organic_rl);
        about_images_array = getResources().obtainTypedArray(R.array.slider_images_array);

        recyclerView.setNestedScrollingEnabled(false);
        // adding bottom dots
        addBottomDots(0);


        albumList = new ArrayList<>();


//        confirmdialog();
        RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 2);
        RecyclerView.LayoutManager mLayoutManager_dairy = new GridLayoutManager(getActivity(), 3);
        RecyclerView.LayoutManager mLayoutManager_organic = new GridLayoutManager(getActivity(), 3);

        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(1), true));


        dairy_products_rv.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(1), true));
        dairy_products_rv.setLayoutManager(mLayoutManager_dairy);

        organic_products_rv.addItemDecoration(new GridSpacingItemDecoration(3, dpToPx(1), true));
        organic_products_rv.setLayoutManager(mLayoutManager_organic);

//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,dpToPx(10),true));
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));


        recyclerView.setItemAnimator(new DefaultItemAnimator());
        dairy_products_rv.setItemAnimator(new DefaultItemAnimator());
        organic_products_rv.setItemAnimator(new DefaultItemAnimator());
//        recyclerView.setAdapter(adapter);

        loadbanners();
        loadCategories();
        loadadds();


        loadDairy_sub_Categories(sharedPreference.getSharedPreferenceString(getActivity(), Constant.SELECTED_CITY_ID, ""));

        loadOrganic_sub_Categories(sharedPreference.getSharedPreferenceString(getActivity(), Constant.SELECTED_CITY_ID, ""));


        swipe_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                loadCategories();
            }
        });

        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                CategoryDatum categoryDatum = categoryDatumList.get(position);


                Intent intent = new Intent(getActivity(), Productslistactivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putInt(Constant.PARENT_PRODUCT_ID,categoryDatum.getId());
//                bundle.putString(Constant.PARENT_PRODUCT_IMG,categoryDatum.getImage());
//                bundle.putString(Constant.PARENT_PRODUCT_NAME,categoryDatum.getName());
//
//                intent.putExtras(bundle);

                sharedPreference.setSharedPreferenceInt(getActivity(), Constant.SubCat_PRODUCT_ID, Integer.parseInt(categoryDatum.getSubcat_id()));


                sharedPreference.setSharedPreferenceInt(getActivity(), Constant.PARENT_PRODUCT_ID, categoryDatum.getId());
                sharedPreference.setSharedPreferenceString(getActivity(), Constant.PARENT_PRODUCT_IMG, categoryDatum.getImage());
                sharedPreference.setSharedPreferenceString(getActivity(), Constant.PARENT_PRODUCT_NAME, categoryDatum.getName());

                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(getActivity(), (View) view, "parent_cat");
                startActivity(intent, options.toBundle());


//                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        addbanner_one_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (!addone_url.equals("")) {
                    try {
                        Intent viewIntent =
                                new Intent("android.intent.action.VIEW",
                                        Uri.parse(addone_url));
                        startActivity(viewIntent);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }

                }
            }
        });

        addbanner_two_ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!addtwourl.equals("")) {
                    try {
                        Intent viewIntent =
                                new Intent("android.intent.action.VIEW",
                                        Uri.parse(addtwourl));
                        startActivity(viewIntent);

                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }
        });


        dairy_products_rv.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                DairyCatRespo categoryDatum = dairyCatRespoList.get(position);

                sharedPreference.setSharedPreferenceInt(getActivity(), Constant.PARENT_PRODUCT_ID, Integer.parseInt(dairy_cat_id));

                sharedPreference.setSharedPreferenceInt(getActivity(), Constant.SubCat_PRODUCT_ID, categoryDatum.getId());
                sharedPreference.setSharedPreferenceString(getActivity(), Constant.PARENT_PRODUCT_IMG, categoryDatum.getImage());
                sharedPreference.setSharedPreferenceString(getActivity(), Constant.PARENT_PRODUCT_NAME, categoryDatum.getName());


                Intent intent = new Intent(getActivity(), Productslistactivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putInt(Constant.PARENT_PRODUCT_ID,categoryDatum.getId());
//                bundle.putString(Constant.PARENT_PRODUCT_IMG,categoryDatum.getImage());
//                bundle.putString(Constant.PARENT_PRODUCT_NAME,categoryDatum.getName());
//
//                intent.putExtras(bundle);


                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(getActivity(), (View) view, "parent_cat");
                startActivity(intent, options.toBundle());


//                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        organic_products_rv.addOnItemTouchListener(new RecyclerTouchListener(getActivity(), recyclerView, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {
                OrganicCatRespo categoryDatum = organicCatRespoList.get(position);

                sharedPreference.setSharedPreferenceInt(getActivity(), Constant.PARENT_PRODUCT_ID, Integer.parseInt(organic_cat_id));

                sharedPreference.setSharedPreferenceInt(getActivity(), Constant.SubCat_PRODUCT_ID, categoryDatum.getId());
                sharedPreference.setSharedPreferenceString(getActivity(), Constant.PARENT_PRODUCT_IMG, categoryDatum.getImage());
                sharedPreference.setSharedPreferenceString(getActivity(), Constant.PARENT_PRODUCT_NAME, categoryDatum.getName());


                Intent intent = new Intent(getActivity(), Productslistactivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putInt(Constant.PARENT_PRODUCT_ID,categoryDatum.getId());
//                bundle.putString(Constant.PARENT_PRODUCT_IMG,categoryDatum.getImage());
//                bundle.putString(Constant.PARENT_PRODUCT_NAME,categoryDatum.getName());
//
//                intent.putExtras(bundle);


                ActivityOptionsCompat options = ActivityOptionsCompat.
                        makeSceneTransitionAnimation(getActivity(), (View) view, "parent_cat");
                startActivity(intent, options.toBundle());


//                startActivity(intent);

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        return view;
    }

    private void addBottomDots(int currentPage) {
        dots = new ImageView[about_images_array.length()];

        dotsLayout.removeAllViews();
        for (int i = 0; i < dots.length; i++) {
            dots[i] = new ImageView(getActivity());
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

        compositeDisposable.add(apiService.loadCategories(sharedPreference.getSharedPreferenceString(getActivity(), Constant.SELECTED_CITY_ID, ""))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<ProductsCatResponse>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onNext(ProductsCatResponse value) {

                        Log.d("2.0 getFeed > Full json res wrapped in gson => ",new Gson().toJson(value));

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

                            Toast.makeText(getActivity(), value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        swipe_refresh_layout.setRefreshing(false);

                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        try {

                            if (categoryDatumList.size() > 0) {
                                adapter = new AlbumsAdapter(getActivity(), categoryDatumList);

                                recyclerView.setAdapter(adapter);

                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }


                    }
                }));


    }

    private void loadDairy_sub_Categories(String city_id) {

        swipe_refresh_layout.setRefreshing(true);


        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.loaddairycat(city_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<DairyProResponse>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onNext(DairyProResponse value) {

                        Log.d("2.0 getFeed > Full json res wrapped in gson => ",new Gson().toJson(value));

                        if (!value.getError()) {

                            swipe_refresh_layout.setRefreshing(false);

                            dairyCatRespoList.clear();

                            dairy_cat_id = value.getCategory_id();

                            try {

                                for (int i = 0; i < value.getCategoryData().size(); i++) {

                                    DairyCatRespo subCatview = new DairyCatRespo(
                                            value.getCategoryData().get(i).getId(),
                                            value.getCategoryData().get(i).getName(),
                                            value.getCategoryData().get(i).getImage()

                                    );

                                    dairyCatRespoList.add(subCatview);

                                }


                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }


//                            Toast.makeText(MainActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        } else {

                            swipe_refresh_layout.setRefreshing(false);

                            Toast.makeText(getActivity(), value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        swipe_refresh_layout.setRefreshing(false);

                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {
                        try {

                            if (dairyCatRespoList.size() > 0) {
                                dairy_rl.setVisibility(View.VISIBLE);
                                subcatAlbumsAdapter = new SubcatAlbumsAdapter(getActivity(), dairyCatRespoList);

                                dairy_products_rv.setAdapter(subcatAlbumsAdapter);

                            } else {
                                dairy_rl.setVisibility(View.GONE);
                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }


                    }
                }));


    }

    private void loadOrganic_sub_Categories(String city_id) {

        swipe_refresh_layout.setRefreshing(true);


        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.loadOrganicCat(city_id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<OrganicProResponse>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onNext(OrganicProResponse value) {

                        Log.d("Organic => ",new Gson().toJson(value));

                        if (!value.getError()) {

                            swipe_refresh_layout.setRefreshing(false);

                            organicCatRespoList.clear();

                            organic_cat_id = value.getCategory_id();

                            try {

                                for (int i = 0; i < value.getCategoryData().size(); i++) {

                                    OrganicCatRespo subCatview = new OrganicCatRespo(
                                            value.getCategoryData().get(i).getId(),
                                            value.getCategoryData().get(i).getName(),
                                            value.getCategoryData().get(i).getImage()

                                    );

                                    organicCatRespoList.add(subCatview);

                                }


                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }


//                            Toast.makeText(MainActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        } else {

                            swipe_refresh_layout.setRefreshing(false);

                            Toast.makeText(getActivity(), value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        swipe_refresh_layout.setRefreshing(false);

                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {
                        try {

                            if (organicCatRespoList.size() > 0) {
                                organic_rl.setVisibility(View.VISIBLE);
                                organiccatAlbumsAdapter = new OrganiccatAlbumsAdapter(getActivity(), organicCatRespoList);

                                organic_products_rv.setAdapter(organiccatAlbumsAdapter);

                            } else {
                                organic_rl.setVisibility(View.GONE);
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

                            Toast.makeText(getActivity(), value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        swipe_refresh_layout.setRefreshing(false);

                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {

                        try {

                            if (bannerImageList.size() > 0) {
                                myViewPagerAdapter = new MyViewPagerAdapter(bannerImageList);
                                viewPager.setAdapter(myViewPagerAdapter);


                                /*After setting the adapter use the timer */
                                final Handler handler = new Handler();
                                final Runnable Update = new Runnable() {
                                    public void run() {
                                        if (currentPage == bannerImageList.size()) {
                                            currentPage = 0;
                                        }
                                        viewPager.setCurrentItem(currentPage++, true);
                                    }
                                };

                                timer = new Timer(); // This will create a new Thread
                                timer.schedule(new TimerTask() { // task to be scheduled

                                    @Override
                                    public void run() {
                                        handler.post(Update);
                                    }
                                }, DELAY_MS, PERIOD_MS);


                            }
                        } catch (Exception ex) {
                            ex.printStackTrace();
                        }


                    }
                }));


    }

    private void loadadds() {

        swipe_refresh_layout.setRefreshing(true);

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.loadadds()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<Adds_response>() {
                    @SuppressLint("LongLogTag")
                    @Override
                    public void onNext(Adds_response value) {

//                        Log.d("Adds:DATA",new Gson().toJson(value));

                        if (!value.getError()) {

                            swipe_refresh_layout.setRefreshing(false);


                            try {

                                if (!value.getAddOneImages().equals("")) {


                                    addbanner_one_ll.setVisibility(View.VISIBLE);
                                    Picasso.get().load(value.getAddOneImages()).placeholder(R.drawable.morning_logo).into(addbaner_one);

                                    addone_url = value.getAddOneUrl();


                                } else {
                                    addbanner_one_ll.setVisibility(View.GONE);

                                }


                                if (!value.getAddTwoImages().equals("")) {


                                    addbanner_two_ll.setVisibility(View.VISIBLE);
                                    Picasso.get().load(value.getAddTwoImages()).into(addbaner_two);

                                    addtwourl = value.getAddTwoUrl();


                                } else {
                                    addbanner_two_ll.setVisibility(View.GONE);

                                }


                            } catch (Exception ex) {
                                ex.printStackTrace();
                            }


//                            Toast.makeText(MainActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        } else {

                            swipe_refresh_layout.setRefreshing(false);

                            Toast.makeText(getActivity(), value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }


                    }

                    @Override
                    public void onError(Throwable e) {
                        swipe_refresh_layout.setRefreshing(false);

                        Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {


                    }
                }));


    }

    public class MyViewPagerAdapter extends PagerAdapter {
        private LayoutInflater layoutInflater;
        private List<BannerImage> bannerImageList = new ArrayList<>();

        public MyViewPagerAdapter(List<BannerImage> bannerImageList) {
            this.bannerImageList = bannerImageList;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);

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




    private void confirmdialog() {

        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        LayoutInflater li = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v1 = li.inflate(R.layout.undermaintainence, null, false);
        dialog.setContentView(v1);
        dialog.setCancelable(false);


        TextView msg = (TextView) v1.findViewById(R.id.msg);
//        msg.setText("Are you sure? Do you want to Proceed...");
        Button btn_cancel = (Button) v1.findViewById(R.id.btn_cancel);
        Button btn_submit = (Button) v1.findViewById(R.id.btn_submit);



        dialog.show();
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

    }

}
