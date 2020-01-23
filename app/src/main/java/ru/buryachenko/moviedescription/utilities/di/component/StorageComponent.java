package ru.buryachenko.moviedescription.utilities.di.component;

import dagger.Component;
import ru.buryachenko.moviedescription.utilities.SharedPreferencesOperation;
import ru.buryachenko.moviedescription.utilities.di.module.StorageModule;

@Component(modules = StorageModule.class)
public interface StorageComponent {
    void injectsSharedPreferencesOperation(SharedPreferencesOperation sharedPreferencesOperation);
}
