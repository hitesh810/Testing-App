package com.synram.morningbucket.activities;

import android.animation.Animator;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.synram.morningbucket.API.ApiClient;
import com.synram.morningbucket.API.ApiInterface;
import com.synram.morningbucket.Adapter.ProductsWeightAdapter;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Database.DatabaseHandler;
import com.synram.morningbucket.Dialogs.PopupMessage;
import com.synram.morningbucket.Modal.AttributeDatum;
import com.synram.morningbucket.Modal.Cart;
import com.synram.morningbucket.Modal.ProductDescriptionModal;
import com.synram.morningbucket.R;
import com.synram.morningbucket.SharedprefData.SharedPreference;
import com.synram.morningbucket.Utilities.CircleAnimationUtil;
import com.synram.morningbucket.Utilities.CountDrawable;
import com.synram.morningbucket.Utilities.RecyclerTouchListener;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

public class ProductsDescriptionActivity extends AppCompatActivity {

    RecyclerView pro_weight_rv;


    ImageView back_btn, product_image, add, remove, cart_btn_duplicate;
    TextView product_name, mrp;
    TextView p_count;
    TextView price;
    TextView description;
    Button get_once, subscribe;
    PopupMessage popupMessage;
    private CompositeDisposable compositeDisposable;
    private List<AttributeDatum> attributeDatumArrayList = new ArrayList<>();
    private ProductsWeightAdapter adapter;
    private SharedPreference sharedPreference;
    private Cart cart;
    private Dialog dialog;
    private Menu defaultMenu;
    private DatabaseHandler db;
    private String product_id_weight;
    private String title_value;
    private String pro_img;
    private Cart cart_Data;
    private Double price_value;
    private View cartbutton;
    private View cart_img;
    private LayerDrawable icon;
    private int subscription_flag = 0;
    private int subscription_type;


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_products_description);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        compositeDisposable = new CompositeDisposable();
        sharedPreference = new SharedPreference(this);
        popupMessage = new PopupMessage(this);
        db = new DatabaseHandler(this);
        cart_Data = new Cart();

        initView();
    }


    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void initView() {
        pro_weight_rv = findViewById(R.id.pro_weight_rv);
        back_btn = findViewById(R.id.back_btn);
        product_image = findViewById(R.id.product_image);
        add = findViewById(R.id.add);
        cart_btn_duplicate = findViewById(R.id.cart_btn_duplicate);
        product_name = findViewById(R.id.product_name);
        mrp = findViewById(R.id.mrp);
        p_count = findViewById(R.id.p_count);
        remove = findViewById(R.id.remove);
        price = findViewById(R.id.price);
        description = findViewById(R.id.description);
        get_once = findViewById(R.id.get_once);
        subscribe = findViewById(R.id.subscribe);

        cart = new Cart();

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        pro_weight_rv.setLayoutManager(mLayoutManager);

//        recyclerView.addItemDecoration(new GridSpacingItemDecoration(2,dpToPx(10),true));
//        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.HORIZONTAL));

        adapter = new ProductsWeightAdapter(ProductsDescriptionActivity.this, attributeDatumArrayList, new ProductsWeightAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(List<AttributeDatum> item, int position) {
                price.setText("₹" + item.get(position).getPrice());

                if (item.get(position).getPrice().equals(item.get(position).getMrp())) {

                    mrp.setVisibility(View.GONE);
                } else {
                    mrp.setVisibility(View.VISIBLE);
                }

                mrp.setText(Html.fromHtml("<strike>" + "₹" + item.get(position).getMrp() + "</strike>"));

                Picasso.get().load(item.get(position).getImage()).into(product_image);
            }
        });
        pro_weight_rv.setAdapter(adapter);


        pro_weight_rv.setItemAnimator(new DefaultItemAnimator());

        loadproductDescription(sharedPreference.getSharedPreferenceString(ProductsDescriptionActivity.this, Constant.SINGLE_PRODUCT_ID, "0"), "");


        pro_weight_rv.addOnItemTouchListener(new RecyclerTouchListener(ProductsDescriptionActivity.this, pro_weight_rv, new RecyclerTouchListener.ClickListener() {
            @Override
            public void onClick(View view, int position) {

                AttributeDatum attributeDatum = attributeDatumArrayList.get(position);
                product_id_weight = attributeDatum.getId();
                price_value = Double.parseDouble(attributeDatum.getPrice());
                pro_img = attributeDatum.getImage();

            }

            @Override
            public void onLongClick(View view, int position) {

            }
        }));


        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cart_Data.amount < 50) {
                    cart_Data.amount = cart_Data.amount + 1;
                    p_count.setText(cart_Data.amount + "");
                }
            }
        });
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (cart_Data.amount > 1) {
                    cart_Data.amount = cart_Data.amount - 1;
                    p_count.setText(cart_Data.amount + "");
                }
            }
        });


    }


    private void loadproductDescription(final String product_id, String weight) {

        ApiInterface apiService =
                ApiClient.getClient().create(ApiInterface.class);

        compositeDisposable.add(apiService.getproductsdata(product_id, weight)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())

                .subscribeWith(new DisposableObserver<ProductDescriptionModal>() {
                    @Override
                    public void onNext(ProductDescriptionModal value) {


                        Log.d("RESPO", new Gson().toJson(value));
                        if (!value.getError()) {
                            attributeDatumArrayList.clear();

                            for (int i = 0; i < value.getAttributeData().size(); i++) {
                                AttributeDatum attributeDatum = new AttributeDatum(
                                        value.getAttributeData().get(i).getId(),
                                        value.getAttributeData().get(i).getPrice(),
                                        value.getAttributeData().get(i).getMrp(),
                                        value.getAttributeData().get(i).getWeight(),
                                        value.getAttributeData().get(i).getImage()
                                );

                                attributeDatumArrayList.add(attributeDatum);

                            }

                            if(value.getAttributeData().size()>0){
                                if (value.getAttributeData().get(0).getPrice().equals(value.getAttributeData().get(0).getMrp())){

                                    mrp.setVisibility(View.GONE);
                                }else{
                                    mrp.setVisibility(View.VISIBLE);
                                }

                            }


                            product_name.setText(value.getProductName());
                            Picasso.get().load(value.getImage()).into(product_image);
                            price.setText("₹" + value.getAttributeData().get(0).getPrice());
                            description.setText(value.getDescription());

                            mrp.setText(Html.fromHtml("<strike>" + "₹" + value.getAttributeData().get(0).getMrp() + "</strike>"));

                            title_value = value.getProductName();
                            product_id_weight = value.getAttributeData().get(0).getId();
                            price_value = Double.parseDouble(value.getAttributeData().get(0).getPrice());
                            pro_img = value.getAttributeData().get(0).getImage();


//                            Toast.makeText(LoginActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        } else {

                            Toast.makeText(ProductsDescriptionActivity.this, value.getErrorMsg(), Toast.LENGTH_SHORT).show();


                        }




                    }

                    @Override
                    public void onError(Throwable e) {

                        Toast.makeText(ProductsDescriptionActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();

                    }

                    @Override
                    public void onComplete() {



                        adapter.notifyDataSetChanged();




                    }
                }));


    }


    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        setCount(ProductsDescriptionActivity.this, String.valueOf(db.getCartSize()));


        return super.onPrepareOptionsMenu(menu);


    }
//this function create count in menu...


    public void setCount(Context context, String count) {
        MenuItem menuItem = defaultMenu.findItem(R.id.action_cart);
        icon = (LayerDrawable) menuItem.getIcon();

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

    //this function is used for create menu in activity
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        defaultMenu = menu;
        getMenuInflater().inflate(R.menu.menu_activity_product_details, menu);
//        wishlist_menu = menu.findItem(R.id.action_wish);
//        refreshWishlistMenu();


//        cartbutton = menuItem.getActionView();


        return true;
    }


    public void get_oncedialog(View view) {

        sharedPreference.setSharedPreferenceString(this, Constant.SELECTED_SUB_PLAN, Constant.SUB_TYPE_ONCE);


        subscription_type = sharedPreference.getSharedPreferenceInt(this, Constant.SUBSCRIPTION_FLAG, 0);


        if (subscription_type == 2 || subscription_type == 3) {

            resetcartButton();
            toggleCartButton(Constant.SUB_TYPE_ONCE);
            sharedPreference.setSharedPreferenceInt(this, Constant.SUBSCRIPTION_FLAG, 1);


        } else {

            sharedPreference.setSharedPreferenceInt(this, Constant.SUBSCRIPTION_FLAG, 1);

            toggleCartButton(Constant.SUB_TYPE_ONCE);

        }

//            subscription_flag=2;

//        }else{
//
//
//
//        }


//        toggleCartButton();

//        makeFlyAnimation(product_image);

    }


    public void get_subscribedialog(View view) {


        showCustomDialog(this);


    }


    @Override
    protected void onStop() {
        super.onStop();
        if (dialog != null) {
            dialog.dismiss();
        }
    }


    private void toggleCartButton(String sub_type) {

        if (sub_type.equals(Constant.SUB_TYPE_ONCE)) {

            Cart cart = new Cart(Long.parseLong(product_id_weight), Integer.parseInt(product_id_weight), title_value, pro_img, cart_Data.amount, (long) 0, price_value, System.currentTimeMillis(), sub_type);

            db.saveCart(cart);

        }
        if (sub_type.equals(Constant.SUB_TYPE_MONTH)) {

            Cart cart = new Cart(Long.parseLong(product_id_weight), Integer.parseInt(product_id_weight), title_value, pro_img, cart_Data.amount, (long) 0, price_value * 30, System.currentTimeMillis(), sub_type);

            db.saveCart(cart);

        }
        if (sub_type.equals(Constant.SUB_TYPE_ALTER)) {

            Cart cart = new Cart(Long.parseLong(product_id_weight), Integer.parseInt(product_id_weight), title_value, pro_img, cart_Data.amount, (long) 0, price_value * 15, System.currentTimeMillis(), sub_type);

            db.saveCart(cart);


        }

        setCount(getApplicationContext(), String.valueOf(db.getCartSize()));

        Toast.makeText(this, R.string.add_cart, Toast.LENGTH_SHORT).show();
    }

    private void resetcartButton() {

        db.deleteActiveCart();
        setCount(getApplicationContext(), String.valueOf(db.getCartSize()));

    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (dialog != null) {
            dialog.dismiss();
        }
    }

    public void showCustomDialog(final Context context) {
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.custome_dialog_msg_popup, null, false);
        /*HERE YOU CAN FIND YOU IDS AND SET TEXTS OR BUTTONS*/

        final LinearLayout monthly_subscription = view.findViewById(R.id.monthly_subscription);

        final LinearLayout alternative_subscription = view.findViewById(R.id.alternative_subscription);

        monthly_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                sharedPreference.setSharedPreferenceString(context, Constant.SELECTED_SUB_PLAN, Constant.SUB_TYPE_MONTH);


                subscription_type = sharedPreference.getSharedPreferenceInt(context, Constant.SUBSCRIPTION_FLAG, 0);


                if (subscription_type == 1 || subscription_type == 3) {

                    resetcartButton();
                    toggleCartButton(Constant.SUB_TYPE_MONTH);

                    sharedPreference.setSharedPreferenceInt(context, Constant.SUBSCRIPTION_FLAG, 2);


                } else {

                    sharedPreference.setSharedPreferenceInt(context, Constant.SUBSCRIPTION_FLAG, 2);

//                    subscription_flag = 1;
                    toggleCartButton(Constant.SUB_TYPE_MONTH);

                }


            }
        });

        alternative_subscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                dialog.dismiss();
                sharedPreference.setSharedPreferenceString(context, Constant.SELECTED_SUB_PLAN, Constant.SUB_TYPE_ALTER);


                subscription_type = sharedPreference.getSharedPreferenceInt(context, Constant.SUBSCRIPTION_FLAG, 0);

                if (subscription_type == 1 || subscription_type == 2) {

                    resetcartButton();
                    toggleCartButton(Constant.SUB_TYPE_ALTER);
                    sharedPreference.setSharedPreferenceInt(context, Constant.SUBSCRIPTION_FLAG, 3);


                } else {

//                    subscription_flag = 1;
                    toggleCartButton(Constant.SUB_TYPE_ALTER);
                    sharedPreference.setSharedPreferenceInt(context, Constant.SUBSCRIPTION_FLAG, 3);

                }


            }
        });

        ((Activity) context).getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE | WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        dialog.setContentView(view);
        final Window window = dialog.getWindow();
        window.setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
        window.setBackgroundDrawableResource(android.R.color.transparent);

        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;


        window.setGravity(Gravity.CENTER);
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        onBackAction();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int item_id = item.getItemId();
        if (item_id == android.R.id.home) {
            onBackAction();
        } else if (item_id == R.id.action_cart) {
            Intent i = new Intent(this, ShopingCartActivity.class);
            sharedPreference.setSharedPreferenceString(this, Constant.Activity_Type, "pd");
            startActivity(i);
        }


        return super.onOptionsItemSelected(item);
    }

    private void onBackAction() {
        setCount(getApplicationContext(), String.valueOf(db.getCartSize()));
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    private void makeFlyAnimation(ImageView targetView) {

//        RelativeLayout destView = (RelativeLayout) findViewById(R.id.cartRelativeLayout);
        new CircleAnimationUtil().attachActivity(this).setTargetView(targetView).setMoveDuration(1000).setDestView(cartbutton).setAnimationListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {

                toggleCartButton("Get Once");
                Toast.makeText(ProductsDescriptionActivity.this, "Continue Shopping...", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        }).startAnimation();


    }


}
