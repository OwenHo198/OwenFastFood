package com.app.owenfastfood.model;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.io.Serializable;
import java.util.List;

@Entity(tableName = "cart")
public class Cart implements Serializable {
    @PrimaryKey(autoGenerate = true)
    public long id;
    public String user_id;
    public long food_id;
    public String food_name;
    public String image;
    public int quantity;
    public int price;
    public int sale;

    public int getRealPrice() {
        if (sale <= 0) {
            return price;
        }
        return price - (price * sale / 100);
    }

    public Cart(String user_id, long food_id, String food_name, String image, int quantity, int price, int sale) {
        this.user_id = user_id;
        this.food_id = food_id;
        this.food_name = food_name;
        this.image = image;
        this.quantity = quantity;
        this.price = price;
        this.sale = sale;
    }
}
