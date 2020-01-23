package ru.buryachenko.moviedescription.utilities;

import android.content.SharedPreferences;

import javax.inject.Inject;

import ru.buryachenko.moviedescription.App;

public class SharedPreferencesOperation {

    private static SharedPreferencesOperation instance;

    @Inject
    public SharedPreferences sharedPreferences;

    private SharedPreferencesOperation() {
        App.getComponent().injectsSharedPreferencesOperation(this);
    }

    public static SharedPreferencesOperation getInstance() {
        if (instance == null) {
            instance = new SharedPreferencesOperation();
        }
        return instance;
    }

    public void save(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
        editor.commit();
    }

    public String load(String key, String defaultValue) {
        String res = defaultValue;
        if (sharedPreferences.contains(key)) {
            res = sharedPreferences.getString(key, defaultValue);
        }
        return res;
    }
}
