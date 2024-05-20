package com.app.owenfastfood.database;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.app.owenfastfood.model.Cart;

@Database(entities = {Cart.class}, version = 1)
public abstract class FoodDatabase extends RoomDatabase {
    private static final String DATABASE_NAME = "food.db";
    private static FoodDatabase instance;
    public static synchronized FoodDatabase getInstance(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), FoodDatabase.class, DATABASE_NAME).allowMainThreadQueries().build();
        }
        return instance;
    }

    public abstract CartDAO cartDAO();
}
