package com.app.owenfastfood.listener;

import com.app.owenfastfood.model.Cart;

public interface IOnManagerFoodListener {
    void onClickUpdateFood(Cart cart);
    void onClickDeleteFood(Cart cart);
}
