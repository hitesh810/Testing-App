package com.synram.morningbucket.Modal;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ProductDatum {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("cat_id")
    @Expose
    private String catId;
    @SerializedName("cat_name")
    @Expose
    private String catName;
    @SerializedName("sub_cat_id")
    @Expose
    private String subCatId;
    @SerializedName("sub_cat_name")
    @Expose
    private String subCatName;
    @SerializedName("type_name")
    @Expose
    private String typeName;
    @SerializedName("type_id")
    @Expose
    private String typeId;
    @SerializedName("brand_id")
    @Expose
    private String brandId;
    @SerializedName("brand_name")
    @Expose
    private String brandName;
    @SerializedName("product_name")
    @Expose
    private String productName;
    @SerializedName("price")
    @Expose
    private String price;
    @SerializedName("mrp")
    @Expose
    private String mrp;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("weight")
    @Expose
    private String weight;
    @SerializedName("image")
    @Expose
    private String image;
@SerializedName("no_of_variation")
    @Expose
    private String no_of_variation;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getSubCatId() {
        return subCatId;
    }

    public void setSubCatId(String subCatId) {
        this.subCatId = subCatId;
    }

    public String getSubCatName() {
        return subCatName;
    }

    public void setSubCatName(String subCatName) {
        this.subCatName = subCatName;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public String getTypeId() {
        return typeId;
    }

    public void setTypeId(String typeId) {
        this.typeId = typeId;
    }

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

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getMrp() {
        return mrp;
    }

    public void setMrp(String mrp) {
        this.mrp = mrp;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getWeight() {
        return weight;
    }

    public void setWeight(String weight) {
        this.weight = weight;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNo_of_variation() {
        return no_of_variation;
    }

    public void setNo_of_variation(String no_of_variation) {
        this.no_of_variation = no_of_variation;
    }

    public ProductDatum(String id, String catId, String catName, String subCatId, String subCatName, String typeName, String typeId, String brandId, String brandName, String productName, String price, String mrp, String description, String weight, String image,String no_of_variation) {
        this.id = id;
        this.catId = catId;
        this.catName = catName;
        this.subCatId = subCatId;
        this.subCatName = subCatName;
        this.typeName = typeName;
        this.typeId = typeId;
        this.brandId = brandId;
        this.brandName = brandName;
        this.productName = productName;
        this.price = price;
        this.mrp = mrp;
        this.description = description;
        this.weight = weight;
        this.image = image;
        this.no_of_variation = no_of_variation;
    }
}