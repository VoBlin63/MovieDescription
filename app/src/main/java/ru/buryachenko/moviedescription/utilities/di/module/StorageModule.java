package ru.buryachenko.moviedescription.utilities.di.module;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import dagger.Module;
import dagger.Provides;
import ru.buryachenko.moviedescription.App;

@Module
public class StorageModule {

    @Provides
    SharedPreferences provideSharedPreferences() {
        return PreferenceManager.getDefaultSharedPreferences(App.getInstance());
    }
}
