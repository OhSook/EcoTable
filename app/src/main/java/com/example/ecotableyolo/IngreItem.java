package com.example.ecotableyolo;

public class IngreItem {
    private String foodName;
    private String imageUrl;

    public IngreItem(String foodName,String imageUrl) {
        this.foodName = foodName;
        this.imageUrl = imageUrl;

    }

    public String getFoodName() {
        return foodName;
    }
    public String getImageUrl() {
        return imageUrl;
    }
}
