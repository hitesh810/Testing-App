package com.synram.morningbucket.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class CashbackHistoryResponse {

    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("error_msg")
    @Expose
    private String errorMsg;
    @SerializedName("cashback_history")
    @Expose
    private List<CashbackHistory> cashbackHistory = null;

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

    public List<CashbackHistory> getCashbackHistory() {
        return cashbackHistory;
    }

    public void setCashbackHistory(List<CashbackHistory> cashbackHistory) {
        this.cashbackHistory = cashbackHistory;
    }

}
