package com.app.owenfastfood.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import com.app.owenfastfood.ControllerApplication;
import com.app.owenfastfood.R;
import com.app.owenfastfood.constant.Constant;
import com.app.owenfastfood.constant.GlobalFunction;
import com.app.owenfastfood.databinding.ActivityAddFoodBinding;
import com.app.owenfastfood.model.Cart;
import com.app.owenfastfood.model.FoodObject;
import com.app.owenfastfood.utils.StringUtil;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class AddAndEditFoodActivity extends BaseActivity {
    private static final int PICK_IMAGE_REQUEST = 1;
    private Uri selectedImageUri;
    private ActivityAddFoodBinding mActivityAddFoodBinding;
    private boolean isUpdate;
    private Cart mCart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityAddFoodBinding = ActivityAddFoodBinding.inflate(getLayoutInflater());
        setContentView(mActivityAddFoodBinding.getRoot());
        getDataIntent();
        initToolbar();
        initView();
        mActivityAddFoodBinding.btnAddOrEdit.setOnClickListener(v -> addOrEditFood());
        mActivityAddFoodBinding.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                choossimg();
            }
        });
    }

    private void choossimg() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            Glide.with(this).load(selectedImageUri).into(mActivityAddFoodBinding.imageView);
        } else {
            setDefaultImage();
        }
    }

    private void getDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            isUpdate = true;
            mCart = (Cart) bundleReceived.get(Constant.KEY_INTENT_FOOD_OBJECT);
        }
    }

    private void initToolbar() {
        mActivityAddFoodBinding.toolbar.imgBack.setVisibility(View.VISIBLE);
        mActivityAddFoodBinding.toolbar.imgBack.setOnClickListener(v -> onBackPressed());
    }

    private void initView() {
        if (isUpdate) {
            mActivityAddFoodBinding.toolbar.tvTitle.setText(getString(R.string.edit_food));
            mActivityAddFoodBinding.btnAddOrEdit.setText(getString(R.string.action_edit));
            mActivityAddFoodBinding.edtName.setText(mCart.getName());
            mActivityAddFoodBinding.edtDescription.setText(mCart.getDescription());
            mActivityAddFoodBinding.edtPrice.setText(String.valueOf(mCart.getPrice()));
            mActivityAddFoodBinding.edtDiscount.setText(String.valueOf(mCart.getSale()));
            mActivityAddFoodBinding.chbPopular.setChecked(mCart.isPopular());
        } else {
            mActivityAddFoodBinding.toolbar.tvTitle.setText(getString(R.string.add_food));
            mActivityAddFoodBinding.btnAddOrEdit.setText(getString(R.string.action_add));
        }
    }
    private void setDefaultImage() {
        Glide.with(this).load(R.drawable.img_no_image).into(mActivityAddFoodBinding.imageView);
    }

    private void addOrEditFood() {
        String strName = mActivityAddFoodBinding.edtName.getText().toString().trim();
        String strDescription = mActivityAddFoodBinding.edtDescription.getText().toString().trim();
        String strPrice = mActivityAddFoodBinding.edtPrice.getText().toString().trim();
        String strDiscount = mActivityAddFoodBinding.edtDiscount.getText().toString().trim();
        boolean isPopular = mActivityAddFoodBinding.chbPopular.isChecked();
        if (StringUtil.isEmpty(strName) || StringUtil.isEmpty(strDescription) ||
                StringUtil.isEmpty(strPrice) || StringUtil.isEmpty(strDiscount)) {
            if (StringUtil.isEmpty(strName)) {
                Toast.makeText(this, getString(R.string.ms_name_food_require), Toast.LENGTH_SHORT).show();
                return;
            }
            if (StringUtil.isEmpty(strDescription)) {
                Toast.makeText(this, getString(R.string.ms_desc_food_require), Toast.LENGTH_SHORT).show();
                return;
            }
            if (StringUtil.isEmpty(strPrice)) {
                Toast.makeText(this, getString(R.string.ms_price_food_require), Toast.LENGTH_SHORT).show();
                return;
            }
            if (StringUtil.isEmpty(strDiscount)) {
                Toast.makeText(this, getString(R.string.ms_discount_food_require), Toast.LENGTH_SHORT).show();
                return;
            }
        }
        boolean isNewImageSelected = (selectedImageUri != null);
        if (isUpdate) {
            Map<String, Object> updateMap = new HashMap<>();
            updateMap.put("name", strName);
            updateMap.put("description", strDescription);
            updateMap.put("price", Integer.parseInt(strPrice));
            updateMap.put("sale", Integer.parseInt(strDiscount));
            updateMap.put("popular", isPopular);
            if (isNewImageSelected) {
                StorageReference storageRef = FirebaseStorage.getInstance().getReference().child("food/" + UUID.randomUUID().toString() + ".jpg");
                UploadTask uploadTask = storageRef.putFile(selectedImageUri);
                uploadTask.addOnSuccessListener(taskSnapshot -> {
                    storageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        updateMap.put("image", uri.toString());
                        ControllerApplication.get(this).getFoodDatabaseReference().child(String.valueOf(mCart.getId())).updateChildren(updateMap, (error, ref) -> {
                                    showUpdateResultMessage(error);
                                });
                    }).addOnFailureListener(exception -> {
                        Toast.makeText(AddAndEditFoodActivity.this, getString(R.string.get_img_url_failed), Toast.LENGTH_SHORT).show();
                        showProgressDialog(false);
                    });
                }).addOnFailureListener(e -> {
                    Toast.makeText(AddAndEditFoodActivity.this, getString(R.string.upload_img_failed), Toast.LENGTH_SHORT).show();
                    showProgressDialog(false);
                });
            } else {
                ControllerApplication.get(this).getFoodDatabaseReference().child(String.valueOf(mCart.getId())).updateChildren(updateMap, (error, ref) -> {
                            showUpdateResultMessage(error);
                        });
            }
        } else {
            FoodObject food = new FoodObject(
                    System.currentTimeMillis(), strName, strDescription, Integer.parseInt(strPrice), Integer.parseInt(strDiscount), isNewImageSelected ? selectedImageUri.toString() : "URL_img_default", isPopular
            );
            ControllerApplication.get(AddAndEditFoodActivity.this).getFoodDatabaseReference().child(String.valueOf(food.getId())).setValue(food, (error, ref) -> {
                        showAddResultMessage(error);
                    });
        }
    }

    private void showUpdateResultMessage(DatabaseError error) {
        if (error == null) {
            Toast.makeText(AddAndEditFoodActivity.this, getString(R.string.edit_food_success), Toast.LENGTH_SHORT).show();
            showProgressDialog(false);
            finish();
        } else {
            Toast.makeText(AddAndEditFoodActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
        }
    }

    private void showAddResultMessage(DatabaseError error) {
        if (error == null) {
            Toast.makeText(AddAndEditFoodActivity.this, getString(R.string.add_food_success), Toast.LENGTH_SHORT).show();
            clearInputFields();
        } else {
            Toast.makeText(AddAndEditFoodActivity.this, getString(R.string.error_message), Toast.LENGTH_SHORT).show();
        }
        GlobalFunction.gotoMainActivity(this);
        finishAffinity();
    }

    private void clearInputFields() {
        mActivityAddFoodBinding.edtName.setText("");
        mActivityAddFoodBinding.edtDescription.setText("");
        mActivityAddFoodBinding.edtPrice.setText("");
        mActivityAddFoodBinding.edtDiscount.setText("");
        mActivityAddFoodBinding.chbPopular.setChecked(false);
        selectedImageUri = null;
        setDefaultImage();
    }

}