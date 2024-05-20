package com.app.owenfastfood.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.owenfastfood.constant.Constant;
import com.app.owenfastfood.databinding.ItemCartBinding;
import com.app.owenfastfood.model.Cart;
import com.app.owenfastfood.utils.GlideUtils;

import java.util.List;

public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {
    private final List<Cart> mListCarts;
    private final IClickListener iClickListener;
    public interface IClickListener {
        void clickDeteteFood(Cart cart, int position);
        void updateItemFood(Cart cart, int position);
    }

    public CartAdapter(List<Cart> mListCarts, IClickListener iClickListener) {
        this.mListCarts = mListCarts;
        this.iClickListener = iClickListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCartBinding itemCartBinding = ItemCartBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CartViewHolder(itemCartBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        Cart cart = mListCarts.get(position);
        if (cart == null) {
            return;
        }
        GlideUtils.loadUrl(cart.image, holder.mItemCartBinding.imgFoodCart);
        holder.mItemCartBinding.tvFoodNameCart.setText(cart.food_name);
        String strFoodPriceCart = cart.price + Constant.CURRENCY;
        if (cart.sale > 0) {
            strFoodPriceCart = cart.getRealPrice() + Constant.CURRENCY;
        }
        holder.mItemCartBinding.tvFoodPriceCart.setText(strFoodPriceCart);
        holder.mItemCartBinding.tvCount.setText(String.valueOf(cart.quantity));
        holder.mItemCartBinding.tvSubtract.setOnClickListener(v -> {
            if (cart.quantity <= 1) {
                return;
            }
            cart.quantity--;
            holder.mItemCartBinding.tvCount.setText(String.valueOf(cart.quantity));

            int totalPrice = cart.getRealPrice() * cart.quantity;
            cart.price = totalPrice;
            iClickListener.updateItemFood(cart, holder.getAdapterPosition());
        });

        holder.mItemCartBinding.tvAdd.setOnClickListener(v -> {
            cart.quantity++;
            holder.mItemCartBinding.tvCount.setText(String.valueOf( cart.quantity));
            int totalPrice = cart.getRealPrice() *  cart.quantity;

            cart.price = totalPrice;
            iClickListener.updateItemFood(cart, holder.getAdapterPosition());
        });

        holder.mItemCartBinding.tvDelete.setOnClickListener(v
                -> iClickListener.clickDeteteFood(cart, holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return null == mListCarts ? 0 : mListCarts.size();
    }
    public static class CartViewHolder extends RecyclerView.ViewHolder {
        private final ItemCartBinding mItemCartBinding;
        public CartViewHolder(ItemCartBinding itemCartBinding) {
            super(itemCartBinding.getRoot());
            this.mItemCartBinding = itemCartBinding;
        }
    }
}
