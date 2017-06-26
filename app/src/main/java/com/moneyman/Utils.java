package com.moneyman;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.widget.TextView;

import java.math.BigDecimal;
import java.math.RoundingMode;

/**
 * Created by chatRG.
 */

class Utils {

    static String calculate(String amt, String sum, String type, int flag) {
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

    static String getFormattedMonth(int month) {
        return month < 10 ? ("0" + month) : month + "";
    }

    static String getTotal(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getString("total", "0.0");
    }

    static void setTotal(Context context, String s) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        sharedPref.edit().putString("total", s).apply();
    }

    static void updateTotal(Context context, TextView tv) {
        tv.setText(getTotal(context));
    }
}
