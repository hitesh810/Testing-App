package com.synram.morningbucket.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class AllOrderList {


    @SerializedName("error")
    @Expose
    private boolean error;
    @SerializedName("order_details")
    @Expose
    private List<AllOrderDetail> orderDetails = null;

    public boolean getError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<AllOrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<AllOrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }
}
