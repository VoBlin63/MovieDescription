package ru.buryachenko.moviedescription.utilities;

import android.util.Log;

import ru.buryachenko.moviedescription.BuildConfig;

import static ru.buryachenko.moviedescription.Constant.LOGTAG;
public class AppLog {
    public static void write(String message) {
        if (BuildConfig.DEBUG) {
            Log.v(LOGTAG + "[" + Thread.currentThread().getName() + "]", message);
        }
    }
}
