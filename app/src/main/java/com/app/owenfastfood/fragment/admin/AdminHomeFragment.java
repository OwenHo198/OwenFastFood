package com.app.owenfastfood.fragment.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.app.owenfastfood.model.Cart;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.app.owenfastfood.ControllerApplication;
import com.app.owenfastfood.R;
import com.app.owenfastfood.activity.AddAndEditFoodActivity;
import com.app.owenfastfood.activity.AdminMainActivity;
import com.app.owenfastfood.adapter.AdminFoodAdapter;
import com.app.owenfastfood.constant.Constant;
import com.app.owenfastfood.constant.GlobalFunction;
import com.app.owenfastfood.databinding.FragmentAdminHomeBinding;
import com.app.owenfastfood.fragment.BaseFragment;
import com.app.owenfastfood.listener.IOnManagerFoodListener;
import com.app.owenfastfood.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class AdminHomeFragment extends BaseFragment {

    private FragmentAdminHomeBinding mFragmentAdminHomeBinding;
    private List<Cart> mListCart;
    private AdminFoodAdapter mAdminFoodAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentAdminHomeBinding = FragmentAdminHomeBinding.inflate(inflater, container, false);

        initView();
        initListener();
        getListFood("");
        return mFragmentAdminHomeBinding.getRoot();
    }

    @Override
    protected void initToolbar() {
        if (getActivity() != null) {
            ((AdminMainActivity) getActivity()).setToolBar(getString(R.string.home));
        }
    }

    private void initView() {
        if (getActivity() == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentAdminHomeBinding.rcvFood.setLayoutManager(linearLayoutManager);
        mListCart = new ArrayList<>();
        mAdminFoodAdapter = new AdminFoodAdapter(mListCart, new IOnManagerFoodListener() {
            @Override
            public void onClickUpdateFood(Cart cart) {
                onClickEditFood(cart);
            }

            @Override
            public void onClickDeleteFood(Cart cart) {
                deleteFoodItem(cart);
            }
        });
        mFragmentAdminHomeBinding.rcvFood.setAdapter(mAdminFoodAdapter);
    }

    private void initListener() {
        mFragmentAdminHomeBinding.btnAddFood.setOnClickListener(v -> onClickAddFood());

        mFragmentAdminHomeBinding.imgSearch.setOnClickListener(view1 -> searchFood());

        mFragmentAdminHomeBinding.edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchFood();
                return true;
            }
            return false;
        });

        mFragmentAdminHomeBinding.edtSearchName.addTextChangedListener(new TextWatcher() {
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
                    searchFood();
                }
            }
        });
    }

    private void onClickAddFood() {
        GlobalFunction.startActivity(getActivity(), AddAndEditFoodActivity.class);
    }

    private void onClickEditFood(Cart cart) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_FOOD_OBJECT, cart);
        GlobalFunction.startActivity(getActivity(), AddAndEditFoodActivity.class, bundle);
    }

    private void deleteFoodItem(Cart cart) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.ms_delete_title))
                .setMessage(getString(R.string.ms_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> {
                    if (getActivity() == null) {
                        return;
                    }
                    ControllerApplication.get(getActivity()).getFoodDatabaseReference()
                            .child(String.valueOf(cart.getId())).removeValue((error, ref) ->
                            Toast.makeText(getActivity(),
                                    getString(R.string.ms_delete_movie_success), Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void searchFood() {
        String strKey = mFragmentAdminHomeBinding.edtSearchName.getText().toString().trim();
        if (mListCart != null) {
            mListCart.clear();
        } else {
            mListCart = new ArrayList<>();
        }
        getListFood(strKey);
        GlobalFunction.hideSoftKeyboard(getActivity());
    }

    public void getListFood(String keyword) {
        if (getActivity() == null) {
            return;
        }
        mListCart.clear();
        ControllerApplication.get(getActivity()).getFoodDatabaseReference()
                .addChildEventListener(new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        Cart cart = dataSnapshot.getValue(Cart.class);
                        if (cart == null || mListCart == null || mAdminFoodAdapter == null) {
                            return;
                        }
                        if (StringUtil.isEmpty(keyword)) {
                            mListCart.add(0, cart);
                        } else {
                            if (GlobalFunction.getTextSearch(cart.getName()).toLowerCase().trim()
                                    .contains(GlobalFunction.getTextSearch(keyword).toLowerCase().trim())) {
                                mListCart.add(0, cart);
                            }
                        }
                        mAdminFoodAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                        Cart cart = dataSnapshot.getValue(Cart.class);
                        if (cart == null || mListCart == null
                                || mListCart.isEmpty() || mAdminFoodAdapter == null) {
                            return;
                        }
                        for (int i = 0; i < mListCart.size(); i++) {
                            if (cart.getId() == mListCart.get(i).getId()) {
                                mListCart.set(i, cart);
                                break;
                            }
                        }
                        mAdminFoodAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        Cart cart = dataSnapshot.getValue(Cart.class);
                        if (cart == null || mListCart == null
                                || mListCart.isEmpty() || mAdminFoodAdapter == null) {
                            return;
                        }
                        for (Cart cartObject : mListCart) {
                            if (cart.getId() == cartObject.getId()) {
                                mListCart.remove(cartObject);
                                break;
                            }
                        }
                        mAdminFoodAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                    }
                });
    }
}
