package com.synram.morningbucket.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubscriptonOrder {

    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("contact")
    @Expose
    private String contact;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("total_amount")
    @Expose
    private String totalAmount;
    @SerializedName("order_status")
    @Expose
    private String orderStatus;
    @SerializedName("order_no")
    @Expose
    private String orderNo;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("delivery_charge")
    @Expose
    private String deliveryCharge;

    @SerializedName("time_slot")
    @Expose
    private String time_slot;

    @SerializedName("order_date")
    @Expose
    private String orderDate;
    @SerializedName("day")
    @Expose
    private String day;
    @SerializedName("subscribe_product")
    @Expose
    private List<SubscribeProduct> subscribeProduct = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContact() {
        return contact;
    }

    public void setContact(String contact) {
        this.contact = contact;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public List<SubscribeProduct> getSubscribeProduct() {
        return subscribeProduct;
    }

    public void setSubscribeProduct(List<SubscribeProduct> subscribeProduct) {
        this.subscribeProduct = subscribeProduct;
    }

    public String getTime_slot() {
        return time_slot;
    }

    public void setTime_slot(String time_slot) {
        this.time_slot = time_slot;
    }
}
