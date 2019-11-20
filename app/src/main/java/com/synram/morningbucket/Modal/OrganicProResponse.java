package com.synram.morningbucket.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class OrganicProResponse {


    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("error_msg")
    @Expose
    private String errorMsg;

    @SerializedName("category_id")
    @Expose
    private String category_id;

    @SerializedName("category_data")
    @Expose
    private List<OrganicCatRespo> categoryData = null;

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

    public List<OrganicCatRespo> getCategoryData() {
        return categoryData;
    }

    public void setCategoryData(List<OrganicCatRespo> categoryData) {
        this.categoryData = categoryData;
    }

    public String getCategory_id() {
        return category_id;
    }

    public void setCategory_id(String category_id) {
        this.category_id = category_id;
    }
}
