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
import com.app.owenfastfood.model.FoodObject;
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
    private List<FoodObject> food_list;
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
        food_list = new ArrayList<>();
        mAdminFoodAdapter = new AdminFoodAdapter(food_list, new IOnManagerFoodListener() {
            @Override
            public void onClickUpdateFood(FoodObject food) {
                onClickEditFood(food);
            }
            @Override
            public void onClickDeleteFood(FoodObject food) {
                deleteFoodItem(food);
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

    private void onClickEditFood(FoodObject food) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_FOOD_OBJECT, food);
        GlobalFunction.startActivity(getActivity(), AddAndEditFoodActivity.class, bundle);
    }

    private void deleteFoodItem(FoodObject food) {
        new AlertDialog.Builder(getActivity()).setTitle(getString(R.string.ms_delete_title)).setMessage(getString(R.string.ms_confirm_delete)).setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> {
                    if (getActivity() == null) {
                        return;
                    }
                    ControllerApplication.get(getActivity()).getFoodDatabaseReference()
                            .child(String.valueOf(food.getId())).removeValue((error, ref) ->
                            Toast.makeText(getActivity(),
                                    getString(R.string.ms_delete_movie_success), Toast.LENGTH_SHORT).show());
                }).setNegativeButton(getString(R.string.action_cancel), null).show();
    }

    private void searchFood() {
        String strKey = mFragmentAdminHomeBinding.edtSearchName.getText().toString().trim();
        if (food_list != null) {
            food_list.clear();
        } else {
            food_list = new ArrayList<>();
        }
        getListFood(strKey);
        GlobalFunction.hideSoftKeyboard(getActivity());
    }

    public void getListFood(String keyword) {
        if (getActivity() == null) {
            return;
        }
        food_list.clear();
        ControllerApplication.get(getActivity()).getFoodDatabaseReference().addChildEventListener(new ChildEventListener() {
                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                        FoodObject food = dataSnapshot.getValue(FoodObject.class);
                        if (food == null || food_list == null || mAdminFoodAdapter == null) {
                            return;
                        }
                        if (StringUtil.isEmpty(keyword)) {
                            food_list.add(0, food);
                        } else {
                            if (GlobalFunction.getTextSearch(food.getName()).toLowerCase().trim()
                                    .contains(GlobalFunction.getTextSearch(keyword).toLowerCase().trim())) {
                                food_list.add(0, food);
                            }
                        }
                        mAdminFoodAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                        FoodObject food = dataSnapshot.getValue(FoodObject.class);
                        if (food == null || food_list == null
                                || food_list.isEmpty() || mAdminFoodAdapter == null) {
                            return;
                        }
                        for (int i = 0; i < food_list.size(); i++) {
                            if (food.getId() == food_list.get(i).getId()) {
                                food_list.set(i, food);
                                break;
                            }
                        }
                        mAdminFoodAdapter.notifyDataSetChanged();
                    }

                    @SuppressLint("NotifyDataSetChanged")
                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                        FoodObject food_remote = dataSnapshot.getValue(FoodObject.class);
                        if (food_remote == null || food_list == null
                                || food_list.isEmpty() || mAdminFoodAdapter == null) {
                            return;
                        }
                        for (FoodObject food: food_list) {
                            if (food.getId() == food_remote.getId()) {
                                food_list.remove(food);
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
