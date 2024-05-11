package com.app.owenfastfood.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.owenfastfood.databinding.ItemFoodPopularBinding;
import com.app.owenfastfood.listener.IOnClickFoodItemListener;
import com.app.owenfastfood.model.Cart;
import com.app.owenfastfood.utils.GlideUtils;

import java.util.List;

public class FoodPopularAdapter extends RecyclerView.Adapter<FoodPopularAdapter.FoodPopularViewHolder> {

    private final List<Cart> mListCarts;
    public final IOnClickFoodItemListener iOnClickFoodItemListener;

    public FoodPopularAdapter(List<Cart> mListCarts, IOnClickFoodItemListener iOnClickFoodItemListener) {
        this.mListCarts = mListCarts;
        this.iOnClickFoodItemListener = iOnClickFoodItemListener;
    }

    @NonNull
    @Override
    public FoodPopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoodPopularBinding itemFoodPopularBinding = ItemFoodPopularBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FoodPopularViewHolder(itemFoodPopularBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodPopularViewHolder holder, int position) {
        Cart cart = mListCarts.get(position);
        if (cart == null) {
            return;
        }
        GlideUtils.loadUrlBanner(cart.getImage(), holder.mItemFoodPopularBinding.imageFood);
        if (cart.getSale() <= 0) {
            holder.mItemFoodPopularBinding.tvSaleOff.setVisibility(View.GONE);
        } else {
            holder.mItemFoodPopularBinding.tvSaleOff.setVisibility(View.VISIBLE);
            String strSale = "Discount " + cart.getSale() + "%";
            holder.mItemFoodPopularBinding.tvSaleOff.setText(strSale);
        }
        holder.mItemFoodPopularBinding.layoutItem.setOnClickListener(v -> iOnClickFoodItemListener.onClickItemFood(cart));
    }

    @Override
    public int getItemCount() {
        if (mListCarts != null) {
            return mListCarts.size();
        }
        return 0;
    }

    public static class FoodPopularViewHolder extends RecyclerView.ViewHolder {

        private final ItemFoodPopularBinding mItemFoodPopularBinding;

        public FoodPopularViewHolder(@NonNull ItemFoodPopularBinding itemFoodPopularBinding) {
            super(itemFoodPopularBinding.getRoot());
            this.mItemFoodPopularBinding = itemFoodPopularBinding;
        }
    }
}
