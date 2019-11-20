package com.synram.morningbucket.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllOrderDetail {


    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("contact")
    @Expose
    private String contact;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("zip_code")
    @Expose
    private String zipCode;
    @SerializedName("total_amount")
    @Expose
    private String totalAmount;
    @SerializedName("order_no")
    @Expose
    private String orderNo;
    @SerializedName("order_status")
    @Expose
    private String orderStatus;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("delivery_charge")
    @Expose
    private String deliveryCharge;
    @SerializedName("discount")
    @Expose
    private String discount;
    @SerializedName("checkout_type")
    @Expose
    private String checkoutType;
    @SerializedName("pay_money")
    @Expose
    private Integer payMoney;
    @SerializedName("remaining_maoney")
    @Expose
    private Integer remainingMaoney;
    @SerializedName("onedaycost")
    @Expose
    private String onedaycost;
    @SerializedName("msg")
    @Expose
    private String msg;


    public String getOnedaycost() {
        return onedaycost;
    }

    public void setOnedaycost(String onedaycost) {
        this.onedaycost = onedaycost;
    }

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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getZipCode() {
        return zipCode;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(String totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDeliveryCharge() {
        return deliveryCharge;
    }

    public void setDeliveryCharge(String deliveryCharge) {
        this.deliveryCharge = deliveryCharge;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    public String getCheckoutType() {
        return checkoutType;
    }

    public void setCheckoutType(String checkoutType) {
        this.checkoutType = checkoutType;
    }

    public Integer getPayMoney() {
        return payMoney;
    }

    public void setPayMoney(Integer payMoney) {
        this.payMoney = payMoney;
    }

    public Integer getRemainingMaoney() {
        return remainingMaoney;
    }

    public void setRemainingMaoney(Integer remainingMaoney) {
        this.remainingMaoney = remainingMaoney;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }


    public AllOrderDetail(String name, String contact, String email, String address, String zipCode, String totalAmount, String orderNo, String orderStatus, String date, String deliveryCharge, String discount, String checkoutType, Integer payMoney, Integer remainingMaoney, String msg,String onedaycost) {
        this.name = name;
        this.contact = contact;
        this.email = email;
        this.address = address;
        this.zipCode = zipCode;
        this.totalAmount = totalAmount;
        this.orderNo = orderNo;
        this.orderStatus = orderStatus;
        this.date = date;
        this.deliveryCharge = deliveryCharge;
        this.discount = discount;
        this.checkoutType = checkoutType;
        this.payMoney = payMoney;
        this.remainingMaoney = remainingMaoney;
        this.msg = msg;
        this.onedaycost = onedaycost;
    }
}
