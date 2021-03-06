package com.moneyman.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.TextView;

import com.moneyman.CustomConstants;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

/**
 * Created by chatRG.
 */

public class UtilMisc {

    public static String calculate(String amt, String sum, String type, int flag) {
        switch (flag) {

            case 0:
                if (type.equals(CustomConstants.KEY_CREDIT)) {
                    sum = (Double.valueOf(sum) + Double.valueOf(amt)) + "";
                } else {
                    sum = (Double.valueOf(sum) - Double.valueOf(amt)) + "";
                }
                break;

            case 1:
                // delete transaction
                if (type.equals(CustomConstants.KEY_CREDIT)) {
                    sum = (Double.valueOf(sum) - Double.valueOf(amt)) + "";
                } else {
                    sum = (Double.valueOf(sum) + Double.valueOf(amt)) + "";
                }
                break;
        }
        return BigDecimal.valueOf(Double.valueOf(sum))
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue() + "".trim();
    }

    public static String getFormattedDate(Calendar calendar) {
        return new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(calendar.getTime());
    }

    public static String getTotal(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString("total", "0.0");
    }

    public static void setTotal(Context context, String s) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPref.edit().putString("total", s).apply();
    }

    public static void updateTotal(Context context, TextView tv) {
        tv.setText(getTotal(context));
    }
}
