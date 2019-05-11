package com.example.currencyconverterlocationbased;

import android.app.Activity;
import android.view.WindowManager;

public class UIHelper {

    public static void disableUserInteraction(Activity activity) {
        activity.getWindow().setFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );
    }

    public static void enableUserInteraction(Activity activity) {
        activity.getWindow().clearFlags(
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
        );
    }
}
