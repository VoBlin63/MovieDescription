package ru.buryachenko.moviedescription;

import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;

import java.util.Date;
import java.util.UUID;

import androidx.room.Room;
import androidx.work.Constraints;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.buryachenko.moviedescription.api.TmdbApiServiceRx;
import ru.buryachenko.moviedescription.database.MovieDatabase;
import ru.buryachenko.moviedescription.database.UpdateDatabase;
import ru.buryachenko.moviedescription.utilities.Config;
import ru.buryachenko.moviedescription.utilities.SharedPreferencesOperation;

import static ru.buryachenko.moviedescription.Constant.KEY_NEXT_TIME_TO_UPDATE;
import static ru.buryachenko.moviedescription.Constant.NOTIFICATION_CHANNEL_ID;
import static ru.buryachenko.moviedescription.Constant.UPDATE_DATABASE_WORK_TAG;

public class App extends Application {
    public TmdbApiServiceRx serviceHttp;
    public MovieDatabase movieDatabase;

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
        initNotificationChannel();
        initRetrofit();
        initFilmsDatabase();
        instance = this;
    }


    private void initFilmsDatabase() {
        movieDatabase = Room
                .databaseBuilder(this, MovieDatabase.class, "movies_db")
                .fallbackToDestructiveMigration()
//                .addMigrations(FilmsDatabase.MIGRATION_1_2)
                /*.addCallback(new DbCallback())*/
//                .addCallback(new DbCallbackInsertRelatedData())
                /*.allowMainThreadQueries()*/
                .build();
    }

    public static App getInstance() {
        return instance;
    }

    private void initRetrofit() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            interceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        } else {
            interceptor.setLevel(HttpLoggingInterceptor.Level.NONE);
        }
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request().newBuilder()
                            .build();
                    return chain.proceed(request);
                })
                .addInterceptor(interceptor)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(BuildConfig.baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();
        serviceHttp = retrofit.create(TmdbApiServiceRx.class);
    }

    private void initNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(R.string.notificationChannelName);
            String description = getString(R.string.notificationChannelDescription);
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
            channel.setDescription(description);
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public UUID setUpUpdateDatabase(boolean hardMode) {
        long timeToUpdate = Long.parseLong(SharedPreferencesOperation.load(KEY_NEXT_TIME_TO_UPDATE, "0"));
        Config config = Config.getInstance();
        if (hardMode || new Date().getTime() >= timeToUpdate) {
            WorkManager.getInstance().cancelAllWorkByTag(UPDATE_DATABASE_WORK_TAG);
            Constraints constraints = new Constraints.Builder()
                    .setRequiresBatteryNotLow(!hardMode)
                    .setRequiresDeviceIdle(!hardMode)
                    .setRequiredNetworkType(config.isUseOnlyWiFi() ? NetworkType.UNMETERED : NetworkType.CONNECTED)
                    .setRequiresStorageNotLow(true)
                    .build();
            OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(UpdateDatabase.class)
                    .addTag(UPDATE_DATABASE_WORK_TAG)
                    .setConstraints(constraints)
                    .build();
            WorkManager.getInstance().
                    enqueue(uploadWorkRequest);
            return uploadWorkRequest.getId();
        } else {
            return null;
        }
    }

}

