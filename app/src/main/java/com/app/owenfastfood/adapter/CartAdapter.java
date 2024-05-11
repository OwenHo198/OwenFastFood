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
        GlideUtils.loadUrl(cart.getImage(), holder.mItemCartBinding.imgFoodCart);
        holder.mItemCartBinding.tvFoodNameCart.setText(cart.getName());
        String strFoodPriceCart = cart.getPrice() + Constant.CURRENCY;
        if (cart.getSale() > 0) {
            strFoodPriceCart = cart.getRealPrice() + Constant.CURRENCY;
        }
        holder.mItemCartBinding.tvFoodPriceCart.setText(strFoodPriceCart);
        holder.mItemCartBinding.tvCount.setText(String.valueOf(cart.getCount()));
        holder.mItemCartBinding.tvSubtract.setOnClickListener(v -> {
            String strCount = holder.mItemCartBinding.tvCount.getText().toString();
            int count = Integer.parseInt(strCount);
            if (count <= 1) {
                return;
            }
            int newCount = count - 1;
            holder.mItemCartBinding.tvCount.setText(String.valueOf(newCount));

            int totalPrice = cart.getRealPrice() * newCount;
            cart.setCount(newCount);
            cart.setTotalPrice(totalPrice);
            iClickListener.updateItemFood(cart, holder.getAdapterPosition());
        });

        holder.mItemCartBinding.tvAdd.setOnClickListener(v -> {
            int newCount = Integer.parseInt(holder.mItemCartBinding.tvCount.getText().toString()) + 1;
            holder.mItemCartBinding.tvCount.setText(String.valueOf(newCount));
            int totalPrice = cart.getRealPrice() * newCount;
            cart.setCount(newCount);
            cart.setTotalPrice(totalPrice);
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
