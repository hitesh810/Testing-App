package com.synram.morningbucket.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TransactionHistory {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("cust_id")
    @Expose
    private String custId;
    @SerializedName("amount")
    @Expose
    private String amount;
    @SerializedName("transaction_id")
    @Expose
    private String transactionId;
    @SerializedName("t_date")
    @Expose
    private String tDate;
    @SerializedName("type")
    @Expose
    private String type;

    @SerializedName("t_status")
    @Expose
    private String t_status;


    @SerializedName("order_id")
    @Expose
    private String orderId;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getTDate() {
        return tDate;
    }

    public void setTDate(String tDate) {
        this.tDate = tDate;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public TransactionHistory(String id, String custId, String amount, String transactionId, String tDate, String type, String orderId,String t_status) {
        this.id = id;
        this.custId = custId;
        this.amount = amount;
        this.transactionId = transactionId;
        this.tDate = tDate;
        this.type = type;
        this.orderId = orderId;
        this.t_status = t_status;
    }

    public String getT_status() {
        return t_status;
    }

    public void setT_status(String t_status) {
        this.t_status = t_status;
    }
}
