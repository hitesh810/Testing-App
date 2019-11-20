package com.synram.morningbucket.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Adds_response {
    @SerializedName("error")
    @Expose
    private Boolean error;
    @SerializedName("error_msg")
    @Expose
    private String errorMsg;
    @SerializedName("add_one_images")
    @Expose
    private String addOneImages;
    @SerializedName("add_one_url")
    @Expose
    private String addOneUrl;
    @SerializedName("add_two_images")
    @Expose
    private String addTwoImages;
    @SerializedName("add_two_url")
    @Expose
    private String addTwoUrl;

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

    public String getAddOneImages() {
        return addOneImages;
    }

    public void setAddOneImages(String addOneImages) {
        this.addOneImages = addOneImages;
    }

    public String getAddOneUrl() {
        return addOneUrl;
    }

    public void setAddOneUrl(String addOneUrl) {
        this.addOneUrl = addOneUrl;
    }

    public String getAddTwoImages() {
        return addTwoImages;
    }

    public void setAddTwoImages(String addTwoImages) {
        this.addTwoImages = addTwoImages;
    }

    public String getAddTwoUrl() {
        return addTwoUrl;
    }

    public void setAddTwoUrl(String addTwoUrl) {
        this.addTwoUrl = addTwoUrl;
    }

}
