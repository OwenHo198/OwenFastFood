package com.app.owenfastfood.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.owenfastfood.constant.Constant;
import com.app.owenfastfood.databinding.ItemFoodGridBinding;
import com.app.owenfastfood.listener.IOnClickFoodItemListener;
import com.app.owenfastfood.model.Cart;
import com.app.owenfastfood.utils.GlideUtils;

import java.util.List;

public class FoodGridAdapter extends RecyclerView.Adapter<FoodGridAdapter.FoodGridViewHolder> {

    private final List<Cart> mListCarts;
    public final IOnClickFoodItemListener iOnClickFoodItemListener;

    public FoodGridAdapter(List<Cart> mListCarts, IOnClickFoodItemListener iOnClickFoodItemListener) {
        this.mListCarts = mListCarts;
        this.iOnClickFoodItemListener = iOnClickFoodItemListener;
    }

    @NonNull
    @Override
    public FoodGridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFoodGridBinding itemFoodGridBinding = ItemFoodGridBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new FoodGridViewHolder(itemFoodGridBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull FoodGridViewHolder holder, int position) {
        Cart cart = mListCarts.get(position);
        if (cart == null) {
            return;
        }
        GlideUtils.loadUrl(cart.getImage(), holder.mItemFoodGridBinding.imgFood);
        if (cart.getSale() <= 0) {
            holder.mItemFoodGridBinding.tvSaleOff.setVisibility(View.GONE);
            holder.mItemFoodGridBinding.tvPrice.setVisibility(View.GONE);

            String strPrice = cart.getPrice() + Constant.CURRENCY;
            holder.mItemFoodGridBinding.tvPriceSale.setText(strPrice);
        } else {
            holder.mItemFoodGridBinding.tvSaleOff.setVisibility(View.VISIBLE);
            holder.mItemFoodGridBinding.tvPrice.setVisibility(View.VISIBLE);

            String strSale = "Discount " + cart.getSale() + "%";
            holder.mItemFoodGridBinding.tvSaleOff.setText(strSale);

            String strOldPrice = cart.getPrice() + Constant.CURRENCY;
            holder.mItemFoodGridBinding.tvPrice.setText(strOldPrice);
            holder.mItemFoodGridBinding.tvPrice.setPaintFlags(holder.mItemFoodGridBinding.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);

            String strRealPrice = cart.getRealPrice() + Constant.CURRENCY;
            holder.mItemFoodGridBinding.tvPriceSale.setText(strRealPrice);
        }
        holder.mItemFoodGridBinding.tvFoodName.setText(cart.getName());

        holder.mItemFoodGridBinding.layoutItem.setOnClickListener(v -> iOnClickFoodItemListener.onClickItemFood(cart));
    }

    @Override
    public int getItemCount() {
        return null == mListCarts ? 0 : mListCarts.size();
    }

    public static class FoodGridViewHolder extends RecyclerView.ViewHolder {

        private final ItemFoodGridBinding mItemFoodGridBinding;

        public FoodGridViewHolder(ItemFoodGridBinding itemFoodGridBinding) {
            super(itemFoodGridBinding.getRoot());
            this.mItemFoodGridBinding = itemFoodGridBinding;
        }
    }
}
