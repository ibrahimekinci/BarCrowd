package com.ibrahimekinci.barcrowd;

import android.app.Application;
import android.content.Context;

import androidx.test.runner.AndroidJUnitRunner;

/**
 * This is a custom AndroidJUnitRunner.
 * * Its only job is to override the newApplication() method and force
 * the test runner to use our "clean" TestApplication class instead of the
 * real "BarCrowdApplication" class.
 */
public class CustomTestRunner extends AndroidJUnitRunner {
    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {

        // Force the runner to use TestApplication.class.getName()
        return super.newApplication(cl, TestApplication.class.getName(), context);
    }
}