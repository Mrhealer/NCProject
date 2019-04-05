package com.kaity.dev.cuongproject.utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

public class UIUtils {
    public static void toast(Context context, String msg, boolean longLength) {
        Toast.makeText(context, msg, longLength ? Toast.LENGTH_LONG : Toast.LENGTH_SHORT).show();
    }

    public static void toast(Context context, String msg) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void dialog(Context context, String title, String msg) {
        new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(msg)
                .show();
    }
}
