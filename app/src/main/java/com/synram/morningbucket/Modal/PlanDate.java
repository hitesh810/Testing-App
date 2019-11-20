package com.synram.morningbucket.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PlanDate {

    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("day")
    @Expose
    private String day;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("planid")
    @Expose
    private String planid;
    @SerializedName("orderproduct")
    @Expose
    private List<Orderproduct> orderproduct = null;

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<Orderproduct> getOrderproduct() {
        return orderproduct;
    }

    public void setOrderproduct(List<Orderproduct> orderproduct) {
        this.orderproduct = orderproduct;
    }

    public String getPlanid() {
        return planid;
    }

    public void setPlanid(String planid) {
        this.planid = planid;
    }

    public PlanDate(String date, String day, String status, List<Orderproduct> orderproduct,String planid) {
        this.date = date;
        this.day = day;
        this.status = status;
        this.orderproduct = orderproduct;
        this.planid = planid;
    }
}
