package com.synram.morningbucket.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Orderproduct {
    @SerializedName("pid")
    @Expose
    private Integer pid;
    @SerializedName("qty")
    @Expose
    private Integer qty;
    @SerializedName("amount")
    @Expose
    private Integer amount;
    @SerializedName("pro_name")
    @Expose
    private String proName;

    public Integer getPid() {
        return pid;
    }

    public void setPid(Integer pid) {
        this.pid = pid;
    }

    public Integer getQty() {
        return qty;
    }

    public void setQty(Integer qty) {
        this.qty = qty;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public String getProName() {
        return proName;
    }

    public void setProName(String proName) {
        this.proName = proName;
    }

    public Orderproduct(Integer pid, Integer qty, Integer amount, String proName) {
        this.pid = pid;
        this.qty = qty;
        this.amount = amount;
        this.proName = proName;
    }
}
