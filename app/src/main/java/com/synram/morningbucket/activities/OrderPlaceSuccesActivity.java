package com.synram.morningbucket.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.synram.morningbucket.Adapter.OrderedProductsListAdapter;
import com.synram.morningbucket.Constants.Constant;
import com.synram.morningbucket.Modal.Product;
import com.synram.morningbucket.R;
import com.synram.morningbucket.SharedprefData.SharedPreference;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class OrderPlaceSuccesActivity extends AppCompatActivity {


    private Button continue_btn;
    private TextView total_order,delivery_charge,discount_percent,total_fees,buyer_name,discount;
    private TextView email,phone,address;
    private SharedPreference sharedPreference;
    RecyclerView orderedPro_rv;
    private String totalorder_str;
    private String delivery_charge_str;
    private String discount_percent_str;
    private String buyer_name_str;
    private String email_str;
    private String phone_str;
    private String address_str;
    private List<Product> productList = new ArrayList<>();
    private OrderedProductsListAdapter orderedProductsListAdapter;
    private String order_no_str;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_place_succes);

        sharedPreference = new SharedPreference(this);
        continue_btn = findViewById(R.id.continue_btn);
        total_order = findViewById(R.id.total_order);
        delivery_charge = findViewById(R.id.delivery_charge);
        discount_percent = findViewById(R.id.discount_percent);
        discount = findViewById(R.id.discount);
        total_fees = findViewById(R.id.total_fees);
        buyer_name = findViewById(R.id.buyer_name);
        email = findViewById(R.id.email);
        phone = findViewById(R.id.phone);
        address = findViewById(R.id.address);
        orderedPro_rv = findViewById(R.id.orderedPro_rv);


        totalorder_str = sharedPreference.getSharedPreferenceString(this,Constant.CHECKOUT_TOTALAMT,"");
        delivery_charge_str = sharedPreference.getSharedPreferenceString(this,Constant.CHECKOUT_DEVLIVERY_CHARGE,"");
        discount_percent_str = sharedPreference.getSharedPreferenceString(this,Constant.CHECKOUT_DISCOUNT,"");
        discount_percent_str = sharedPreference.getSharedPreferenceString(this,Constant.CHECKOUT_DISCOUNT,"");
        buyer_name_str = sharedPreference.getSharedPreferenceString(this,Constant.CHECKOUT_USERNAME,"");
        email_str = sharedPreference.getSharedPreferenceString(this,Constant.CHECKOUT_EMAIL,"");
        phone_str = sharedPreference.getSharedPreferenceString(this,Constant.CHECKOUT_CONTACT,"");
        address_str = sharedPreference.getSharedPreferenceString(this,Constant.CHECKOUT_ADDRESS,"");
//        address_str = sharedPreference.getSharedPreferenceString(this,Constant.CHECKOUT_ADDRESS,"");


        order_no_str = sharedPreference.getSharedPreferenceString(this,Constant.CHECKOUT_ORDER_ID,"");
        total_fees.setText("₹"+totalorder_str);
        delivery_charge.setText("₹"+delivery_charge_str);
        buyer_name.setText(buyer_name_str);
        email.setText(email_str);
        phone.setText(phone_str);
        address.setText(address_str);
        address.setText(address_str);

        total_order.setText(sharedPreference.getSharedPreferenceString(this,Constant.CHECKOUT_CART_TOTAL,""));


        Gson gson = new Gson();
        // Converts JSON string into a List of Product object
        Type type = new TypeToken<List<Product>>(){}.getType();
        List<Product> prodList = gson.fromJson(sharedPreference.getSharedPreferenceString(this,Constant.CHECKOUT_ORDERED_PRODUCTS,""), type);

        // print your List<Product>
        System.out.println("prodList: " + prodList);


        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(OrderPlaceSuccesActivity.this,LinearLayoutManager.VERTICAL,false);

        orderedPro_rv.setLayoutManager(layoutManager);
        orderedProductsListAdapter = new OrderedProductsListAdapter(this,prodList);

        orderedPro_rv.setAdapter(orderedProductsListAdapter);

        if (sharedPreference.getSharedPreferenceString(this,Constant.SELECTED_SUB_PLAN,"").equals("Get Once")){


            Log.d("DISC",discount_percent_str+"");
            if (discount_percent_str.equals("0.00")){
                discount_percent.setText("No Discount");

            }else {
                discount_percent.setText("₹"+discount_percent_str);

            }

            discount_percent.setVisibility(View.VISIBLE);
            discount.setVisibility(View.VISIBLE);

        }else{

            discount_percent.setVisibility(View.GONE);
            discount.setVisibility(View.GONE);

        }


        continue_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(OrderPlaceSuccesActivity.this,MainActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }
}
