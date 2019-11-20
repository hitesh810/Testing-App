package com.synram.morningbucket.API;


import com.synram.morningbucket.Modal.AddMoneyModal;
import com.synram.morningbucket.Modal.Adds_response;
import com.synram.morningbucket.Modal.AllOrderList;
import com.synram.morningbucket.Modal.Banner_response;
import com.synram.morningbucket.Modal.CancleOrderResponse;
import com.synram.morningbucket.Modal.CashbackHistoryResponse;
import com.synram.morningbucket.Modal.ChargesResponse;
import com.synram.morningbucket.Modal.Checkout;
import com.synram.morningbucket.Modal.CityResponse;
import com.synram.morningbucket.Modal.CustomerDataUpdateResponse;
import com.synram.morningbucket.Modal.DairyProResponse;
import com.synram.morningbucket.Modal.LoginResponse;
import com.synram.morningbucket.Modal.OrganicProResponse;
import com.synram.morningbucket.Modal.PerticularOrderDetail;
import com.synram.morningbucket.Modal.ProductDescriptionModal;
import com.synram.morningbucket.Modal.ProductsCatResponse;
import com.synram.morningbucket.Modal.ProductsListResponse;
import com.synram.morningbucket.Modal.RechargeResponse;
import com.synram.morningbucket.Modal.SearchProdutsRespo;
import com.synram.morningbucket.Modal.Set_Status_Response;
import com.synram.morningbucket.Modal.ShiftResponse;
import com.synram.morningbucket.Modal.SubcatResponse;
import com.synram.morningbucket.Modal.SubscribedOrderDetailRespo;
import com.synram.morningbucket.Modal.SubscriptionChargesResponse;
import com.synram.morningbucket.Modal.TimeslotResponse;
import com.synram.morningbucket.Modal.TransactionHistResponse;
import com.synram.morningbucket.Modal.UserDataModal;
import com.synram.morningbucket.Modal.WalletAmtResponse;

import io.reactivex.Observable;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.POST;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Dell on 7/2/2018.
 */

public interface ApiInterface {

    @FormUrlEncoded
    @POST("admin/app_api/login/")
    Observable<LoginResponse> login(@Field("mobile_number") String mobile_number);

    @FormUrlEncoded
    @POST("admin/app_api/category_list/")
    Observable<ProductsCatResponse> loadCategories(@Field("city_id") String city_id);


    @POST("admin/app_api/banner/")
    Observable<Banner_response> loadbanners();

    @POST("admin/app_api/small_banner/")
    Observable<Adds_response> loadadds();



    @GET("admin/app_api/subcategory_list/{catid}/{city_id}")
    Observable<SubcatResponse> loadsubcat(@Path("catid") int  catid,@Path("city_id") String city_id);


    @FormUrlEncoded
    @POST("admin/app_api/product_list/")
    Observable<ProductsListResponse> loadProducts(@Field("cat_id") int  cat_id, @Field("sub_cat_id") int sub_cat_id,@Field("city_id") String city_id);


    @FormUrlEncoded
    @POST("admin/app_api/product_listbybrand/")
    Observable<ProductsListResponse> filterbybrand(@Field("brand_id") String  brand_id, @Field("type_id") String type_id,@Field("cat_id") String cat_id,@Field("sub_cat_id") String sub_cat_id,@Field("city_id") String city_id);


    @FormUrlEncoded
    @POST("admin/app_api/product_listbyid/")
    Observable<ProductDescriptionModal> getproductsdata(@Field("product_id") String product_id, @Field("weight") String weight);


    @POST("admin/app_api/get_city_by_locality/")
    Observable<CityResponse> getcitydata();

    @FormUrlEncoded
    @POST("admin/app_api/cust_data_update/")
    Observable<CustomerDataUpdateResponse> updatecustomer(@Field("mobile_number") String mobile_number,@Field("name") String name,@Field("email") String email,@Field("address") String address, @Field("city") String city, @Field("locality") String locality,@Field("b_no") String b_no,@Field("landmark") String landmark,@Field("pin_code") String pin_code,@Field("token") String token);

    @FormUrlEncoded
    @POST("admin/app_api/customer_detail/")
    Observable<UserDataModal> fetchuserdata(@Field("mobile_number") String mobile_number);

    @FormUrlEncoded
    @POST("admin/app_api/wallet_detailByid/")
    Observable<WalletAmtResponse> fetchWalletData(@Field("cust_id") String cust_id);

    @FormUrlEncoded
    @POST("admin/app_api/save_wallet_details/")
    Observable<AddMoneyModal> saveWalletDetail(@Field("transaction_id") String transaction_id, @Field("cust_id") String cust_id, @Field("amount") String amount);


    @POST("admin/app_api/get_charges/")
    Observable<ChargesResponse> getchargesValue();


    @POST("admin/app_api/get_plan_charges/")
    Observable<SubscriptionChargesResponse> getsubscriptionchargesValue();

    @FormUrlEncoded
    @POST("admin/app_api/checkout/")
    Observable<Checkout> checkout(@Field("customer_id") String customer_id  , @Field("customer_name") String customer_name , @Field("contact") String contact , @Field("email") String email , @Field("address") String address, @Field("zip_code") String zip_code, @Field("pro_array") String pro_array, @Field("amount") String amount, @Field("delivery_charge") String delivery_charge, @Field("discountamount") String discountamount,@Field("time_slot")String time_slot,@Field("shift") String shift,@Field("payment_type") String payment_type,@Field("checkout_type") String checkout_type,@Field("block_money") String block_money,@Field("cashback") String cashback);

     @FormUrlEncoded
    @POST("admin/app_api/cust_order_list/")
    Observable<AllOrderList> fetchorderHistory(@Field("cust_id") String cust_id,@Field("checkout_type") String checkout_type);

    @FormUrlEncoded
    @POST("admin/app_api/cust_order_product_list/")
    Observable<PerticularOrderDetail> fetchOrderDetail(@Field("order_no") String order_no, @Field("cust_id") String cust_id);

//    Transsaction History
    @FormUrlEncoded
    @POST("admin/app_api/transaction_history_wallet/")
    Observable<TransactionHistResponse> fetchTransactionHistory(@Field("cust_id") String cust_id);

 @FormUrlEncoded
    @POST("admin/app_api/transaction_history_cashback/")
    Observable<CashbackHistoryResponse> fetchcashbackHistory(@Field("cust_id") String cust_id);

    @POST("admin/app_api/get_shift/")
    Observable<ShiftResponse> getshift();

    @FormUrlEncoded
    @POST("admin/app_api/get_time_slot/")
    Observable<TimeslotResponse> gettimeingslot(@Field("shift") String shift);

  @FormUrlEncoded
    @POST("admin/app_api/plan_details_by_oid/")
    Observable<SubscribedOrderDetailRespo> getsubscribedPlanDetail(@Field("o_id") String o_id, @Field("cust_id") String cust_id);

  @FormUrlEncoded
    @POST("admin/app_api/updateplanquantity/")
    Observable<AddMoneyModal> updateplanquantity(@Field("plan_id") String plan_id,@Field("type") String type,@Field("order_status") String order_status, @Field("orderproduct") String orderproduct,@Field("cust_id") String cust_id);

    @FormUrlEncoded
    @POST("admin/app_api/plan_status_update/")
    Observable<Set_Status_Response> setplanstatus(@Field("o_id") String o_id, @Field("date") String date, @Field("status")String status);

 @FormUrlEncoded
    @POST("admin/app_api/dairy_subcategory_list/")
    Observable<DairyProResponse> loaddairycat(@Field("city_id") String city_id);

     @FormUrlEncoded
    @POST("admin/app_api/organic_subcategory_list/")
    Observable<OrganicProResponse> loadOrganicCat(@Field("city_id") String city_id);

    @FormUrlEncoded
    @POST("admin/app_api/cancel_monthly_order/")
    Observable<CancleOrderResponse> cancelOrder(@Field("order_id") String order_id, @Field("checkout_type") String checkout_type);

    @FormUrlEncoded
    @POST("admin/app_api/recharge/")
    Observable<RechargeResponse> rechargeOrder(@Field("o_no") String o_no, @Field("transaction_id") String transaction_id,@Field("amount") String amount,@Field("cashback") String cashback);

    @FormUrlEncoded
    @POST("admin/app_api/search_product_list/")
    Observable<SearchProdutsRespo> searchProducts(@Field("city_id") String city_id);




}