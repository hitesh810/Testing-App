package com.synram.morningbucket.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ProductsListResponse {

    @SerializedName("error")
    @Expose
    private boolean error;
    @SerializedName("error_msg")
    @Expose
    private String errorMsg;
    @SerializedName("brand_data")
    @Expose
    private List<BrandDatum> brandData = null;
    @SerializedName("type_data")
    @Expose
    private List<TypeDatum> typeData = null;
    @SerializedName("product_data")
    @Expose
    private List<ProductDatum> productData = null;

    public boolean getError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public List<BrandDatum> getBrandData() {
        return brandData;
    }

    public void setBrandData(List<BrandDatum> brandData) {
        this.brandData = brandData;
    }

    public List<TypeDatum> getTypeData() {
        return typeData;
    }

    public void setTypeData(List<TypeDatum> typeData) {
        this.typeData = typeData;
    }

    public List<ProductDatum> getProductData() {
        return productData;
    }

    public void setProductData(List<ProductDatum> productData) {
        this.productData = productData;
    }
}
