package com.synram.morningbucket.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class SubscribedOrderDetailRespo {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("error_msg")
    @Expose
    private String errorMsg;
    @SerializedName("plan_dates")
    @Expose
    private List<PlanDate> planDates = null;
    @SerializedName("subscripton_order")
    @Expose
    private SubscriptonOrder subscriptonOrder;

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

    public List<PlanDate> getPlanDates() {
        return planDates;
    }

    public void setPlanDates(List<PlanDate> planDates) {
        this.planDates = planDates;
    }

    public SubscriptonOrder getSubscriptonOrder() {
        return subscriptonOrder;
    }

    public void setSubscriptonOrder(SubscriptonOrder subscriptonOrder) {
        this.subscriptonOrder = subscriptonOrder;
    }
}
