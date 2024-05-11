package com.app.owenfastfood.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.app.owenfastfood.model.Cart;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.app.owenfastfood.ControllerApplication;
import com.app.owenfastfood.R;
import com.app.owenfastfood.activity.FoodDetailActivity;
import com.app.owenfastfood.activity.MainActivity;
import com.app.owenfastfood.adapter.FoodGridAdapter;
import com.app.owenfastfood.adapter.FoodPopularAdapter;
import com.app.owenfastfood.constant.Constant;
import com.app.owenfastfood.constant.GlobalFunction;
import com.app.owenfastfood.databinding.FragmentHomeBinding;
import com.app.owenfastfood.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends BaseFragment {
    private FragmentHomeBinding mFragmentHomeBinding;
    private List<Cart> mListCart;
    private List<Cart> mListCartPopular;
    private final Handler mHandlerBanner = new Handler();
    private final Runnable mRunnableBanner = new Runnable() {
        @Override
        public void run() {
            if (mListCartPopular == null || mListCartPopular.isEmpty()) {
                return;
            }
            if (mFragmentHomeBinding.viewpager2.getCurrentItem() == mListCartPopular.size() - 1) {
                mFragmentHomeBinding.viewpager2.setCurrentItem(0);
                return;
            }
            mFragmentHomeBinding.viewpager2.setCurrentItem(mFragmentHomeBinding.viewpager2.getCurrentItem() + 1);
        }
    };
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);
        getListFoodFromFirebase("");
        initListener();
        return mFragmentHomeBinding.getRoot();
    }

    @Override
    protected void initToolbar() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolBar(true, getString(R.string.home));
        }
    }

    private void initListener() {
        mFragmentHomeBinding.edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    if (mListCart != null) mListCart.clear();
                    getListFoodFromFirebase("");
                }
            }
        });

        mFragmentHomeBinding.imgSearch.setOnClickListener(view -> searchFood());
        mFragmentHomeBinding.edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchFood();
                return true;
            }
            return false;
        });
    }

    private void displayListFoodPopular() {
        FoodPopularAdapter mFoodPopularAdapter = new FoodPopularAdapter(getListFoodPopular(), this::goToFoodDetail);
        mFragmentHomeBinding.viewpager2.setAdapter(mFoodPopularAdapter);
        mFragmentHomeBinding.indicator3.setViewPager(mFragmentHomeBinding.viewpager2);
        mFragmentHomeBinding.viewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mHandlerBanner.removeCallbacks(mRunnableBanner);
                mHandlerBanner.postDelayed(mRunnableBanner, 3000);
            }
        });
    }

    private void displayListFoodSuggest() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mFragmentHomeBinding.rcvFood.setLayoutManager(gridLayoutManager);
        FoodGridAdapter mFoodGridAdapter = new FoodGridAdapter(mListCart, this::goToFoodDetail);
        mFragmentHomeBinding.rcvFood.setAdapter(mFoodGridAdapter);
    }

    private List<Cart> getListFoodPopular() {
        mListCartPopular = new ArrayList<>();
        if (mListCart == null || mListCart.isEmpty()) {
            return mListCartPopular;
        }
        for (Cart cart : mListCart) {
            if (cart.isPopular()) {
                mListCartPopular.add(cart);
            }
        }
        return mListCartPopular;
    }

    private void getListFoodFromFirebase(String key) {
        if (getActivity() == null) {
            return;
        }
        ControllerApplication.get(getActivity()).getFoodDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mFragmentHomeBinding.layoutContent.setVisibility(View.VISIBLE);
                mListCart = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Cart cart = dataSnapshot.getValue(Cart.class);
                    if (cart == null) {
                        return;
                    }
                    if (StringUtil.isEmpty(key)) {
                        mListCart.add(0, cart);
                    } else {
                        if (GlobalFunction.getTextSearch(cart.getName()).toLowerCase().trim()
                                .contains(GlobalFunction.getTextSearch(key).toLowerCase().trim())) {
                            mListCart.add(0, cart);
                        }
                    }
                }
                displayListFoodPopular();
                displayListFoodSuggest();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFunction.showToastMessage(getActivity(), getString(R.string.ms_get_date_error));
            }
        });
    }

    private void searchFood() {
        String strKey = mFragmentHomeBinding.edtSearchName.getText().toString().trim();
        if (mListCart != null) mListCart.clear();
        getListFoodFromFirebase(strKey);
        GlobalFunction.hideSoftKeyboard(getActivity());
    }

    private void goToFoodDetail(@NonNull Cart cart) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_FOOD_OBJECT, cart);
        GlobalFunction.startActivity(getActivity(), FoodDetailActivity.class, bundle);
    }

    @Override
    public void onPause() {
        super.onPause();
        mHandlerBanner.removeCallbacks(mRunnableBanner);
    }

    @Override
    public void onResume() {
        super.onResume();
        mHandlerBanner.postDelayed(mRunnableBanner, 3000);
    }
}
