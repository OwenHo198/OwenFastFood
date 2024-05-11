package com.app.owenfastfood.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.app.owenfastfood.model.Cart;

import java.util.List;

@Dao
public interface FoodDAO {
    @Insert
    void insertFood(Cart cart);
    @Query("SELECT * FROM Cart WHERE idacc=:idacc")
    List<Cart> getListFoodCartByUser(String idacc);
    @Query("SELECT * FROM Cart")
    List<Cart> getListFoodCart();
    @Query("SELECT * FROM Cart WHERE id=:id")
    List<Cart> checkFoodInCart(long id);
    @Delete
    void deleteFood(Cart cart);
    @Update
    void updateFood(Cart cart);
    @Query("DELETE from Cart")
    void deleteAllFood();
}
