package com.marty.yummy.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

@Entity
public class FoodDetails {

    @PrimaryKey
    @SerializedName("item_name")
    @Expose
    @NonNull
    private String name;

    @SerializedName("item_price")
    @Expose
    private Double price;

    @SerializedName("average_rating")
    @Expose
    private Double rating;

    @SerializedName("image_url")
    @Expose
    private String imageUrl;

    @SerializedName("item_quantity")
    @Expose
    private Integer quantity = 0;

    public FoodDetails(@NonNull String name, Double price, Double rating, String imageUrl,Integer quantity) {
        this.name = name;
        this.price = price;
        this.rating = rating;
        this.imageUrl = imageUrl;
        this.quantity = quantity;
    }

    @NonNull
    public String getName() {
        return name;
    }

    public void setName(@NonNull String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Double getRating() {
        return rating;
    }

    public void setRating(Double rating) {
        this.rating = rating;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }
}
