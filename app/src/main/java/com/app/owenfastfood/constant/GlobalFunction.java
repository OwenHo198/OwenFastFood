package com.app.owenfastfood.constant;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.app.owenfastfood.activity.AdminMainActivity;
import com.app.owenfastfood.activity.MainActivity;
import com.app.owenfastfood.activity.LoginActivity;
import com.app.owenfastfood.fragment.AccountFragment;
import com.app.owenfastfood.listener.IGetDateListener;
import com.app.owenfastfood.prefs.DataStoreManager;
import com.app.owenfastfood.utils.StringUtil;

import java.text.Normalizer;
import java.util.Calendar;
import java.util.regex.Pattern;

public class GlobalFunction {

    public static void startActivity(Context context, Class<?> clz) {
        Intent intent = new Intent(context, clz);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void startActivity(Context context, Class<?> clz, Bundle bundle) {
        Intent intent = new Intent(context, clz);
        intent.putExtras(bundle);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void gotoMainActivity(Context context) {
        if (DataStoreManager.getUser().isAdmin()) {
            GlobalFunction.startActivity(context, AdminMainActivity.class);
        } else {
            GlobalFunction.startActivity(context, MainActivity.class);
        }
    }
    public static void gotoAccount(Context context) {
        GlobalFunction.startActivity(context, MainActivity.class);
    }
    public static void gotoSignInActivity(Context context) {
            GlobalFunction.startActivity(context, LoginActivity.class);
    }

    public static void hideSoftKeyboard(Activity activity) {
        try {
            InputMethodManager inputMethodManager = (InputMethodManager) activity.
                    getSystemService(Activity.INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
        } catch (NullPointerException ex) {
            ex.printStackTrace();
        }
    }


//    public static void callPhoneNumber(Activity activity) {
//        try {
//            if (Build.VERSION.SDK_INT > 22) {
//                if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                    ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.CALL_PHONE}, 101);
//                    return;
//                }
//            }
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//    }

    public static void showToastMessage(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    public static String getTextSearch(String input) {
        String nfdNormalizedString = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(nfdNormalizedString).replaceAll("");
    }

    public static void showDatePicker(Context context, String currentDate, final IGetDateListener getDateListener) {
        Calendar mCalendar = Calendar.getInstance();
        int currentDay = mCalendar.get(Calendar.DATE);
        int currentMonth = mCalendar.get(Calendar.MONTH);
        int currentYear = mCalendar.get(Calendar.YEAR);
        mCalendar.set(currentYear, currentMonth, currentDay);

        if (!StringUtil.isEmpty(currentDate)) {
            String[] split = currentDate.split("/");
            currentDay = Integer.parseInt(split[0]);
            currentMonth = Integer.parseInt(split[1]);
            currentYear = Integer.parseInt(split[2]);
            mCalendar.set(currentYear, currentMonth - 1, currentDay);
        }

        DatePickerDialog.OnDateSetListener callBack = (view, year, monthOfYear, dayOfMonth) -> {
            String date = StringUtil.getDoubleNumber(dayOfMonth) +
                    "/" + StringUtil.getDoubleNumber(monthOfYear + 1) +
                    "/" + year;
            getDateListener.getDate(date);
        };
        DatePickerDialog datePicker = new DatePickerDialog(context, callBack, mCalendar.get(Calendar.YEAR),
                mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DATE));
        datePicker.show();
    }
}