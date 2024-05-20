package com.app.owenfastfood.activity;

import android.annotation.SuppressLint;
import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.app.owenfastfood.model.Cart;
import com.app.owenfastfood.model.FoodObject;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.app.owenfastfood.R;
import com.app.owenfastfood.constant.Constant;
import com.app.owenfastfood.database.FoodDatabase;
import com.app.owenfastfood.databinding.ActivityFoodDetailBinding;
import com.app.owenfastfood.event.ReloadListCartEvent;
import com.app.owenfastfood.utils.GlideUtils;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FoodDetailActivity extends BaseActivity {
    private ActivityFoodDetailBinding mActivityFoodDetailBinding;
    private FoodObject food;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityFoodDetailBinding = ActivityFoodDetailBinding.inflate(getLayoutInflater());
        setContentView(mActivityFoodDetailBinding.getRoot());
        getDataIntent();
        initToolbar();
        setDataFoodDetail();
        initListener();
    }

    private void getDataIntent() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            food = (FoodObject) bundle.get(Constant.KEY_INTENT_FOOD_OBJECT);
        }
    }

    private void initToolbar() {
        mActivityFoodDetailBinding.toolbar.imgBack.setVisibility(View.VISIBLE);
        mActivityFoodDetailBinding.toolbar.tvTitle.setText(getString(R.string.food_detail_title));
        mActivityFoodDetailBinding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
    }

    private void setDataFoodDetail() {
        if (food == null) {
            return;
        }
        GlideUtils.loadUrlBanner(food.getImage(), mActivityFoodDetailBinding.imageFood);
        if (food.getSale() <= 0) {
            mActivityFoodDetailBinding.tvSaleOff.setVisibility(View.GONE);
            mActivityFoodDetailBinding.tvPrice.setVisibility(View.GONE);
            String strPrice = food.getPrice() + Constant.CURRENCY;
            mActivityFoodDetailBinding.tvPriceSale.setText(strPrice);
        } else {
            mActivityFoodDetailBinding.tvSaleOff.setVisibility(View.VISIBLE);
            mActivityFoodDetailBinding.tvPrice.setVisibility(View.VISIBLE);
            String strSale = "Discount " + food.getSale() + "%";
            mActivityFoodDetailBinding.tvSaleOff.setText(strSale);
            String strPriceOld = food.getPrice() + Constant.CURRENCY;
            mActivityFoodDetailBinding.tvPrice.setText(strPriceOld);
            mActivityFoodDetailBinding.tvPrice.setPaintFlags(mActivityFoodDetailBinding.tvPrice.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            String strRealPrice = food.getRealPrice() + Constant.CURRENCY;
            mActivityFoodDetailBinding.tvPriceSale.setText(strRealPrice);
        }
        mActivityFoodDetailBinding.tvFoodName.setText(food.getName());
        mActivityFoodDetailBinding.tvFoodDescription.setText(food.getDescription());
        setStatusButtonAddToCart();
    }
    private void setStatusButtonAddToCart() {
        if (isFoodInCart()) {
            mActivityFoodDetailBinding.tvAddToCart.setBackgroundResource(R.drawable.bg_gray_shape_corner_6);
            mActivityFoodDetailBinding.tvAddToCart.setText(getString(R.string.added_to_cart));
            mActivityFoodDetailBinding.tvAddToCart.setTextColor(ContextCompat.getColor(this, R.color.lightRed));
        } else {
            mActivityFoodDetailBinding.tvAddToCart.setBackgroundResource(R.drawable.bg_green_shape_corner_6);
            mActivityFoodDetailBinding.tvAddToCart.setText(getString(R.string.add_to_cart));
            mActivityFoodDetailBinding.tvAddToCart.setTextColor(ContextCompat.getColor(this, R.color.white));
        }
    }
    private boolean isFoodInCart() {
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        String userId = currentUser.getUid();
        Cart cart = FoodDatabase.getInstance(this).cartDAO().findCartByFoodId(userId, food.getId());
        return cart != null;
    }
    private void initListener() {
        mActivityFoodDetailBinding.tvAddToCart.setOnClickListener(v -> onClickAddToCart());
    }
    private int food_count = 1;
    public void onClickAddToCart() {
        if (isFoodInCart()) {
            return;
        }
        @SuppressLint("InflateParams") View viewDialog = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_cart, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        bottomSheetDialog.setContentView(viewDialog);
        ImageView imgFoodCart = viewDialog.findViewById(R.id.img_food_cart);
        TextView tvFoodNameCart = viewDialog.findViewById(R.id.tv_food_name_cart);
        TextView tvFoodPriceCart = viewDialog.findViewById(R.id.tv_food_price_cart);
        TextView tvSubtractCount = viewDialog.findViewById(R.id.tv_subtract);
        TextView tvCount = viewDialog.findViewById(R.id.tv_count);
        TextView tvAddCount = viewDialog.findViewById(R.id.tv_add);
        TextView tvCancel = viewDialog.findViewById(R.id.tv_cancel);
        TextView tvAddCart = viewDialog.findViewById(R.id.tv_add_cart);
        GlideUtils.loadUrl(food.getImage(), imgFoodCart);
        tvFoodNameCart.setText(food.getName());
        int totalPrice = food.getRealPrice();
        String strTotalPrice = totalPrice + Constant.CURRENCY;
        tvFoodPriceCart.setText(strTotalPrice);
        tvSubtractCount.setOnClickListener(v -> {
            if (food_count <= 1) {
                return;
            }
            food_count--;
            tvCount.setText(String.valueOf(food_count));
            int newTotalPrice = food.getRealPrice() * food_count;
            String strNewTotalPrice = newTotalPrice + Constant.CURRENCY;
            tvFoodPriceCart.setText(strNewTotalPrice);
        });
        tvAddCount.setOnClickListener(v -> {
            food_count++;
            tvCount.setText(String.valueOf(food_count));
            int newTotalPrice = food.getRealPrice() * food_count;
            String strNewTotalPrice = newTotalPrice + Constant.CURRENCY;
            tvFoodPriceCart.setText(strNewTotalPrice);
        });

        tvCancel.setOnClickListener(v -> bottomSheetDialog.dismiss());

        tvAddCart.setOnClickListener(v -> {
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser == null) {
                return;
            }
            String userId = currentUser.getUid();
            Cart cart = new Cart(
                    userId, food.getId(),  food.getName(), food.getImage(), food_count, food.getPrice(), food.getSale()
            );

            FoodDatabase.getInstance(FoodDetailActivity.this).cartDAO().insertFood(cart);
            bottomSheetDialog.dismiss();
            setStatusButtonAddToCart();
            EventBus.getDefault().post(new ReloadListCartEvent());
        });
        bottomSheetDialog.show();
    }
}