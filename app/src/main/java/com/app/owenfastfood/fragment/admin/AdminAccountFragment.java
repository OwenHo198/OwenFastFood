package com.app.owenfastfood.fragment.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.firebase.auth.FirebaseAuth;
import com.app.owenfastfood.R;
import com.app.owenfastfood.activity.AdminMainActivity;
import com.app.owenfastfood.activity.AdminReportActivity;
import com.app.owenfastfood.activity.ChangePasswordActivity;
import com.app.owenfastfood.activity.LoginActivity;
import com.app.owenfastfood.constant.GlobalFunction;
import com.app.owenfastfood.databinding.FragmentAdminAccountBinding;
import com.app.owenfastfood.fragment.BaseFragment;
import com.app.owenfastfood.prefs.DataStoreManager;

public class AdminAccountFragment extends BaseFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        FragmentAdminAccountBinding fragmentAdminAccountBinding = FragmentAdminAccountBinding.inflate(inflater, container, false);

        fragmentAdminAccountBinding.tvEmail.setText(DataStoreManager.getUser().getEmail());

        fragmentAdminAccountBinding.layoutReport.setOnClickListener(v -> onClickReport());
        fragmentAdminAccountBinding.layoutSignOut.setOnClickListener(v -> onClickSignOut());
        fragmentAdminAccountBinding.layoutChangePassword.setOnClickListener(v -> onClickChangePassword());

        return fragmentAdminAccountBinding.getRoot();
    }

    @Override
    protected void initToolbar() {
        if (getActivity() != null) {
            ((AdminMainActivity) getActivity()).setToolBar(getString(R.string.account));
        }
    }

    private void onClickReport() {
        GlobalFunction.startActivity(getActivity(), AdminReportActivity.class);
    }

    private void onClickChangePassword() {
        GlobalFunction.startActivity(getActivity(), ChangePasswordActivity.class);
    }

    private void onClickSignOut() {
        if (getActivity() == null) {
            return;
        }
        FirebaseAuth.getInstance().signOut();
        DataStoreManager.setUser(null);
        GlobalFunction.startActivity(getActivity(), LoginActivity.class);
        getActivity().finishAffinity();
    }
}
