package com.synram.morningbucket.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BrandDatum {

    @SerializedName("brand_id")
    @Expose
    private String brandId;
    @SerializedName("brand_name")
    @Expose
    private String brandName;

    public String getBrandId() {
        return brandId;
    }

    public void setBrandId(String brandId) {
        this.brandId = brandId;
    }

    public String getBrandName() {
        return brandName;
    }

    public void setBrandName(String brandName) {
        this.brandName = brandName;
    }


    public BrandDatum(String brandId, String brandName) {
        this.brandId = brandId;
        this.brandName = brandName;
    }
}