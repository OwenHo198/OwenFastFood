package com.app.owenfastfood.listener;

import com.app.owenfastfood.model.Cart;
import com.app.owenfastfood.model.FoodObject;

public interface IOnManagerFoodListener {
    void onClickUpdateFood(FoodObject food);
    void onClickDeleteFood(FoodObject food);
}
