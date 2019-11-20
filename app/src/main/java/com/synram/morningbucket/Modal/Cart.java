package com.synram.morningbucket.Modal;

public class Cart {

    public Long id;
    public Long order_id = -1L;
    public int product_id;
    public String product_name;
    public String image;
    public Integer amount = 1;
    public Long stock = 0L;
    public Double price_item;
    public Long created_at = 0L;
    public String subscription_type;

    public Cart() {
    }

    public Cart(Long id,int product_id, String product_name, String image, Integer amount, Long stock, Double price_item, Long created_at,String subscription_type) {
        this.id = id;
        this.product_id = product_id;
        this.product_name = product_name;
        this.image = image;
        this.amount = amount;
        this.stock = stock;
        this.price_item = price_item;
        this.created_at = created_at;
        this.subscription_type  = subscription_type;
    }
}
