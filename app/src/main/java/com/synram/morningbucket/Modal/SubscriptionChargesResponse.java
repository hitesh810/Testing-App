package com.synram.morningbucket.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SubscriptionChargesResponse {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("error_msg")
    @Expose
    private String errorMsg;
    @SerializedName("del_min_amount")
    @Expose
    private String delMinAmount;
    @SerializedName("delivery_amount")
    @Expose
    private String deliveryAmount;
    @SerializedName("dis_min_amount")
    @Expose
    private String disMinAmount;
    @SerializedName("dis_amount")
    @Expose
    private String disAmount;

    public Boolean getError() {
        return error;
    }

    public void setError(Boolean error) {
        this.error = error;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public String getDelMinAmount() {
        return delMinAmount;
    }

    public void setDelMinAmount(String delMinAmount) {
        this.delMinAmount = delMinAmount;
    }

    public String getDeliveryAmount() {
        return deliveryAmount;
    }

    public void setDeliveryAmount(String deliveryAmount) {
        this.deliveryAmount = deliveryAmount;
    }

    public String getDisMinAmount() {
        return disMinAmount;
    }

    public void setDisMinAmount(String disMinAmount) {
        this.disMinAmount = disMinAmount;
    }

    public String getDisAmount() {
        return disAmount;
    }

    public void setDisAmount(String disAmount) {
        this.disAmount = disAmount;
    }
}
