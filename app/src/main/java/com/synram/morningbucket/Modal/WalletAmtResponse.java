package com.synram.morningbucket.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class WalletAmtResponse {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("error_msg")
    @Expose
    private String errorMsg;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("block_money")
    @Expose
    private String blockMoney;
    @SerializedName("rem_amount")
    @Expose
    private Integer remAmount;

    @SerializedName("online_status")
    @Expose
    private String online_status;

    @SerializedName("status_msg")
    @Expose
    private String status_msg;



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

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getBlockMoney() {
        return blockMoney;
    }

    public void setBlockMoney(String blockMoney) {
        this.blockMoney = blockMoney;
    }

    public Integer getRemAmount() {
        return remAmount;
    }

    public void setRemAmount(Integer remAmount) {
        this.remAmount = remAmount;
    }

    public String getOnline_status() {
        return online_status;
    }

    public void setOnline_status(String online_status) {
        this.online_status = online_status;
    }

    public String getStatus_msg() {
        return status_msg;
    }

    public void setStatus_msg(String status_msg) {
        this.status_msg = status_msg;
    }
}
