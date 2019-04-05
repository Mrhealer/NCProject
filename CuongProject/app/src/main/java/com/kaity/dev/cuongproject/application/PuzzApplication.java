package com.kaity.dev.cuongproject.application;

import android.app.Application;
import android.os.Handler;

public class PuzzApplication extends Application {
    private static Handler sHandler = new Handler();

    public static Handler getMainHandler() {
        return sHandler;
    }
}
