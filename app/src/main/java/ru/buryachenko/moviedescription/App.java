package ru.buryachenko.moviedescription;

import android.app.Application;

import java.util.UUID;

import androidx.room.Room;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import okhttp3.Request;
import retrofit2.converter.gson.GsonConverterFactory;
import ru.buryachenko.moviedescription.api.TmdbApiService;
import ru.buryachenko.moviedescription.database.MovieDatabase;
import ru.buryachenko.moviedescription.database.UpdateDatabase;

import static ru.buryachenko.moviedescription.Constant.UPDATE_DATABASE_WORK_TAG;

public class App extends Application {
    public TmdbApiService serviceHttp;
//    public GeoApiContext geoApiContext;
    public MovieDatabase movieDatabase;

    private static App instance;

    @Override
    public void onCreate() {
        super.onCreate();
//        initNotificationChannel();
        initRetrofit();
//        initGeoApi();
        initFilmsDatabase();
//        setUpUpdateDatabase();
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
                .baseUrl("https://api.themoviedb.org/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        serviceHttp = retrofit.create(TmdbApiService.class);
    }

//    private void initGeoApi() {
//        GeoApiContext.Builder builder = new GeoApiContext.Builder();
//        builder.apiKey(getString(R.string.googleMapApiKey));
//        geoApiContext = builder
//                .build();
//    }

//    private void initNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = getString(R.string.notificationChannelName);
//            String description = getString(R.string.notificationChannelDescription);
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, name, importance);
//            channel.setDescription(description);
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }

    public UUID setUpUpdateDatabase() {
        WorkManager.getInstance().cancelAllWorkByTag(UPDATE_DATABASE_WORK_TAG);
        OneTimeWorkRequest uploadWorkRequest = new OneTimeWorkRequest.Builder(UpdateDatabase.class)
                .addTag(UPDATE_DATABASE_WORK_TAG)
                .build();
        WorkManager.getInstance().enqueue(uploadWorkRequest);
        return uploadWorkRequest.getId();
    }

}

