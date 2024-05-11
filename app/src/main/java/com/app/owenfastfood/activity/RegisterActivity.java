package com.app.owenfastfood.activity;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.app.owenfastfood.R;
import com.app.owenfastfood.constant.Constant;
import com.app.owenfastfood.constant.GlobalFunction;
import com.app.owenfastfood.databinding.ActivityRegisterBinding;
import com.app.owenfastfood.model.User;
import com.app.owenfastfood.prefs.DataStoreManager;
import com.app.owenfastfood.utils.StringUtil;

public class RegisterActivity extends BaseActivity {
    private ActivityRegisterBinding mActivitySignUpBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivitySignUpBinding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(mActivitySignUpBinding.getRoot());
        mActivitySignUpBinding.imgBack.setOnClickListener(v -> onBackPressed());
        mActivitySignUpBinding.layoutSignIn.setOnClickListener(v -> finish());
        mActivitySignUpBinding.btnSignUp.setOnClickListener(v -> onClickValidateSignUp());
    }

    private void onClickValidateSignUp() {
        String strEmail = mActivitySignUpBinding.edtEmail.getText().toString().trim();
        String strPassword = mActivitySignUpBinding.edtPassword.getText().toString().trim();
        if (StringUtil.isEmpty(strEmail)) {
            Toast.makeText(RegisterActivity.this, getString(R.string.ms_email_require), Toast.LENGTH_SHORT).show();
        } else if (StringUtil.isEmpty(strPassword)) {
            Toast.makeText(RegisterActivity.this, getString(R.string.ms_password_require), Toast.LENGTH_SHORT).show();
        } else if (!StringUtil.isValidEmail(strEmail)) {
            Toast.makeText(RegisterActivity.this, getString(R.string.ms_email_invalid), Toast.LENGTH_SHORT).show();
        } else {
            if (strEmail.contains(Constant.ADMIN_EMAIL_FORMAT)) {
                Toast.makeText(RegisterActivity.this, getString(R.string.ms_email_invalid_user), Toast.LENGTH_SHORT).show();
            } else {
                signUpUser(strEmail, strPassword);
            }
        }
    }

    private void signUpUser(String email, String password) {
        showProgressDialog(true);
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, task -> {showProgressDialog(false);
                    if (task.isSuccessful()) {
                        FirebaseUser user = firebaseAuth.getCurrentUser();
                        firebaseAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    if (user != null) {
                                        User userObject = new User(user.getEmail(), password);
                                        if (user.getEmail() != null && user.getEmail().contains(Constant.ADMIN_EMAIL_FORMAT)) {
                                            userObject.setAdmin(true);
                                        }
                                        DataStoreManager.setUser(userObject);
                                        GlobalFunction.gotoSignInActivity(RegisterActivity.this);
                                        finishAffinity();
                                        Toast.makeText(RegisterActivity.this, getString(R.string.ms_register_success), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        });
                    } else {
                        Toast.makeText(RegisterActivity.this, getString(R.string.ms_register_error), Toast.LENGTH_SHORT).show();
                    }
                });
    }
}