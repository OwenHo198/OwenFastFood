package com.app.owenfastfood.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.app.owenfastfood.model.Cart;

import java.util.List;

@Dao
public interface CartDAO {
    @Query("SELECT * FROM Cart WHERE food_id=:food_id AND user_id = :user_id")
    Cart findCartByFoodId(String user_id, long food_id);
    @Insert
    void insertFood(Cart cart);
    @Query("SELECT * FROM Cart WHERE user_id=:user_id")
    List<Cart> getListFoodCartByUser( String user_id);
    @Query("SELECT * FROM Cart")
    List<Cart> getListFoodCart();
    @Delete
    void deleteFood(Cart cart);
    @Update
    void updateFood(Cart cart);
    @Query("DELETE from Cart")
    void deleteAllFood();
}
