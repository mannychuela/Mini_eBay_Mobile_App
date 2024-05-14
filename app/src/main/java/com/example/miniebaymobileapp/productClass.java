package com.example.miniebaymobileapp;

import java.io.Serializable;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;

public class productClass implements Serializable {

    private String id;
    private String name;
    private String price;
    private String description;
    private String department;
    private String dueDate;
    private String seller;


    public productClass(String id, String name, String price, String description, String department, String dueDate, String seller) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.description = description;
        this.department = department;
        this.dueDate = dueDate;
        this.seller = seller;

    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getDepartment() {
        return department;
    }

    public String getDueDate() {
        return dueDate;
    }

    public String getSeller() {
        return seller;
    }
}