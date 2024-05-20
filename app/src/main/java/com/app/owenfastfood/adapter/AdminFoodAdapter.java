package com.app.owenfastfood.adapter;

import android.graphics.Paint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.owenfastfood.constant.Constant;
import com.app.owenfastfood.databinding.ItemAdminFoodBinding;
import com.app.owenfastfood.listener.IOnManagerFoodListener;
import com.app.owenfastfood.model.Cart;
import com.app.owenfastfood.model.FoodObject;
import com.app.owenfastfood.utils.GlideUtils;

import java.util.List;
public class AdminFoodAdapter extends RecyclerView.Adapter<AdminFoodAdapter.AdminFoodViewHolder> {
    private final List<FoodObject> food_list;
    public final IOnManagerFoodListener iOnManagerFoodListener;
    public AdminFoodAdapter(List<FoodObject> food_list, IOnManagerFoodListener listener) {
        this.food_list = food_list;
        this.iOnManagerFoodListener = listener;
    }
    @NonNull
    @Override
    public AdminFoodViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminFoodBinding itemAdminFoodBinding = ItemAdminFoodBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AdminFoodViewHolder(itemAdminFoodBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminFoodViewHolder holder, int position) {
        FoodObject food = food_list.get(position);
        if (food == null) {
            return;
        }
        GlideUtils.loadUrl(food.getImage(), holder.mItemAdminFoodBinding.imgFood);
        if (food.getSale() <= 0) {
            holder.mItemAdminFoodBinding.tvSaleOff.setVisibility(View.GONE);
            holder.mItemAdminFoodBinding.tvPrice.setVisibility(View.GONE);
            String strPrice = food.getPrice() + Constant.CURRENCY;
            holder.mItemAdminFoodBinding.tvPriceSale.setText(strPrice);
        } else {
            holder.mItemAdminFoodBinding.tvSaleOff.setVisibility(View.VISIBLE);
            holder.mItemAdminFoodBinding.tvPrice.setVisibility(View.VISIBLE);
            String strSale = "Discount " + food.getSale() + "%";
            holder.mItemAdminFoodBinding.tvSaleOff.setText(strSale);
            String strOldPrice = food.getPrice() + Constant.CURRENCY;
            holder.mItemAdminFoodBinding.tvPrice.setText(strOldPrice);
            holder.mItemAdminFoodBinding.tvPrice.setPaintFlags(holder.mItemAdminFoodBinding.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            String strRealPrice = food.getRealPrice() + Constant.CURRENCY;
            holder.mItemAdminFoodBinding.tvPriceSale.setText(strRealPrice);
        }
        holder.mItemAdminFoodBinding.tvFoodName.setText(food.getName());
        holder.mItemAdminFoodBinding.tvFoodDescription.setText(food.getDescription());
        if (food.isPopular()) {
            holder.mItemAdminFoodBinding.tvPopular.setText("Yes");
        } else {
            holder.mItemAdminFoodBinding.tvPopular.setText("No");
        }
        holder.mItemAdminFoodBinding.imgEdit.setOnClickListener(v -> iOnManagerFoodListener.onClickUpdateFood(food));
        holder.mItemAdminFoodBinding.imgDelete.setOnClickListener(v -> iOnManagerFoodListener.onClickDeleteFood(food));
    }

    @Override
    public int getItemCount() {
        return null == food_list ? 0 : food_list.size();
    }
    public static class AdminFoodViewHolder extends RecyclerView.ViewHolder {
        private final ItemAdminFoodBinding mItemAdminFoodBinding;
        public AdminFoodViewHolder(ItemAdminFoodBinding itemAdminFoodBinding) {
            super(itemAdminFoodBinding.getRoot());
            this.mItemAdminFoodBinding = itemAdminFoodBinding;
        }
    }
}
