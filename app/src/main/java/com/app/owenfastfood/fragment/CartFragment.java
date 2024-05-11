package com.app.owenfastfood.fragment;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.owenfastfood.model.Cart;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.app.owenfastfood.ControllerApplication;
import com.app.owenfastfood.R;
import com.app.owenfastfood.activity.MainActivity;
import com.app.owenfastfood.adapter.CartAdapter;
import com.app.owenfastfood.constant.Constant;
import com.app.owenfastfood.constant.GlobalFunction;
import com.app.owenfastfood.database.FoodDatabase;
import com.app.owenfastfood.databinding.FragmentCartBinding;
import com.app.owenfastfood.event.ReloadListCartEvent;
import com.app.owenfastfood.model.Order;
import com.app.owenfastfood.prefs.DataStoreManager;
import com.app.owenfastfood.utils.StringUtil;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

public class CartFragment extends BaseFragment {
    private FragmentCartBinding mFragmentCartBinding;
    private CartAdapter mCartAdapter;
    private List<Cart> mListCart;
    private int mAmount;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentCartBinding = FragmentCartBinding.inflate(inflater, container, false);
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        displayListFoodInCart();
        mFragmentCartBinding.tvOrderCart.setOnClickListener(v -> onClickOrderCart());
        return mFragmentCartBinding.getRoot();
    }

    @Override
    protected void initToolbar() {
        if (getActivity() != null) {
            ((MainActivity) getActivity()).setToolBar(false, getString(R.string.cart));
        }
    }

    private void displayListFoodInCart() {
        if (getActivity() == null) {
            return;
        }
        if (isUserLoggedIn()) {
            String currentUser = getCurrentUser();
            mListCart = FoodDatabase.getInstance(getActivity()).foodDAO().getListFoodCartByUser(currentUser);
            initRecyclerView();
        }
    }

    private boolean isUserLoggedIn() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null;
    }

    private String getCurrentUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            return user.getUid();
        } else {
            return "";
        }
    }

    private void initRecyclerView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentCartBinding.rcvFoodCart.setLayoutManager(linearLayoutManager);
        DividerItemDecoration itemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
        mFragmentCartBinding.rcvFoodCart.addItemDecoration(itemDecoration);
        if (mListCart != null && !mListCart.isEmpty()) {
            mCartAdapter = new CartAdapter(mListCart, new CartAdapter.IClickListener() {
                @Override
                public void clickDeteteFood(Cart cart, int position) {
                    deleteFoodFromCart(cart, position);
                }
                @Override
                public void updateItemFood(Cart cart, int position) {
                    FoodDatabase.getInstance(getActivity()).foodDAO().updateFood(cart);
                    mCartAdapter.notifyItemChanged(position);
                    calculateTotalPrice();
                }
            });
            mFragmentCartBinding.rcvFoodCart.setAdapter(mCartAdapter);
            calculateTotalPrice();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private void clearCart() {
        if (mListCart != null) {
            mListCart.clear();
        }
        mCartAdapter.notifyDataSetChanged();
        calculateTotalPrice();
    }

    private void calculateTotalPrice() {
        List<Cart> listCartCart = FoodDatabase.getInstance(getActivity()).foodDAO().getListFoodCart();
        if (listCartCart == null || listCartCart.isEmpty()) {
            String strZero = 0 + Constant.CURRENCY;
            mFragmentCartBinding.tvTotalPrice.setText(strZero);
            mAmount = 0;
            return;
        }
        int totalPrice = 0;
        for (Cart cart : listCartCart) {
            totalPrice = totalPrice + cart.getTotalPrice();
        }
        String strTotalPrice = totalPrice + Constant.CURRENCY;
        mFragmentCartBinding.tvTotalPrice.setText(strTotalPrice);
        mAmount = totalPrice;
    }

    private void deleteFoodFromCart(Cart cart, int position) {
        new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.confirm_delete_food)).setMessage(getString(R.string.ms_delete_food)).setPositiveButton(getString(R.string.delete), (dialog, which) -> {
                    FoodDatabase.getInstance(getActivity()).foodDAO().deleteFood(cart);
                    mListCart.remove(position);
                    mCartAdapter.notifyItemRemoved(position);
                    calculateTotalPrice();
                }).setNegativeButton(getString(R.string.dialog_cancel), (dialog, which) -> dialog.dismiss()).show();
    }

    public void onClickOrderCart() {
        if (getActivity() == null) {
            return;
        }
        if (mListCart == null || mListCart.isEmpty()) {
            return;
        }
        @SuppressLint("InflateParams") View viewDialog = getLayoutInflater().inflate(R.layout.layout_bottom_sheet_order, null);
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity());
        bottomSheetDialog.setContentView(viewDialog);
        bottomSheetDialog.getBehavior().setState(BottomSheetBehavior.STATE_EXPANDED);
        TextView tvFoodsOrder = viewDialog.findViewById(R.id.tv_foods_order);
        TextView tvPriceOrder = viewDialog.findViewById(R.id.tv_price_order);
        TextView edtNameOrder = viewDialog.findViewById(R.id.edt_name_order);
        TextView edtPhoneOrder = viewDialog.findViewById(R.id.edt_phone_order);
        TextView edtAddressOrder = viewDialog.findViewById(R.id.edt_address_order);
        TextView tvCancelOrder = viewDialog.findViewById(R.id.tv_cancel_order);
        TextView tvCreateOrder = viewDialog.findViewById(R.id.tv_create_order);
        tvFoodsOrder.setText(getStringListFoodsOrder());
        tvPriceOrder.setText(mFragmentCartBinding.tvTotalPrice.getText().toString());
        tvCancelOrder.setOnClickListener(v -> bottomSheetDialog.dismiss());
        tvCreateOrder.setOnClickListener(v -> {
            String strName = edtNameOrder.getText().toString().trim();
            String strPhone = edtPhoneOrder.getText().toString().trim();
            String strAddress = edtAddressOrder.getText().toString().trim();
            if (StringUtil.isEmpty(strName) || StringUtil.isEmpty(strPhone) || StringUtil.isEmpty(strAddress)) {
                if (StringUtil.isEmpty(strName))  {
                    GlobalFunction.showToastMessage(getActivity(), getString(R.string.ms_full_name_order));
                    return;
                }
                if(StringUtil.isEmpty(strPhone)){
                    GlobalFunction.showToastMessage(getActivity(), getString(R.string.ms_phone_order));
                    return;
                }
                if(StringUtil.isEmpty(strAddress)){
                    GlobalFunction.showToastMessage(getActivity(), getString(R.string.ms_address_order));
                    return;
                }
            }
            else {
                long id = System.currentTimeMillis();
                String strEmail = DataStoreManager.getUser().getEmail();
                Order order = new Order(id, strName, strEmail, strPhone, strAddress,
                        mAmount, getStringListFoodsOrder(), Constant.TYPE_PAYMENT_METHOD, false);
                ControllerApplication.get(getActivity()).getBookingDatabaseReference().child(String.valueOf(id)).setValue(order, (error1, ref1) -> {
                            GlobalFunction.showToastMessage(getActivity(), getString(R.string.ms_order_success));
                            GlobalFunction.hideSoftKeyboard(getActivity());
                            bottomSheetDialog.dismiss();
                            FoodDatabase.getInstance(getActivity()).foodDAO().deleteAllFood();
                            clearCart();
                        });
            }
        });
        bottomSheetDialog.show();
    }

    private String getStringListFoodsOrder() {
        if (mListCart == null || mListCart.isEmpty()) {
            return "";
        }
        String result = "";
        for (Cart cart : mListCart) {
            if (StringUtil.isEmpty(result)) {
                result = "- " + cart.getName() + " (" + cart.getRealPrice() + Constant.CURRENCY + ") " + "- " + getString(R.string.quantity) + " " + cart.getCount();
            } else {
                result = result + "\n" + "- " + cart.getName() + " (" + cart.getRealPrice() + Constant.CURRENCY + ") " + "- " + getString(R.string.quantity) + " " + cart.getCount();
            }
        }
        return result;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(ReloadListCartEvent event) {
        displayListFoodInCart();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }
}
