package ru.buryachenko.moviedescription.database;


import android.content.Context;

import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import ru.buryachenko.moviedescription.App;
import ru.buryachenko.moviedescription.api.MovieLoader;
import ru.buryachenko.moviedescription.api.PageMoviesJson;
import ru.buryachenko.moviedescription.utilities.AppLog;
import ru.buryachenko.moviedescription.utilities.SharedPreferencesOperation;
import ru.buryachenko.moviedescription.utilities.SonicUtils;

import static ru.buryachenko.moviedescription.Constant.KEY_NEXT_TIME_TO_UPDATE;
import static ru.buryachenko.moviedescription.Constant.MAX_PAGES_TO_LOAD;
import static ru.buryachenko.moviedescription.Constant.SLEEP_SECONDS_BETWEEN_LOAD_PAGES;

public class UpdateDatabase extends Worker {

    private static final String apiKey = "c3e17ff26735628669886b00d573ab4d";
    private static final String language = "ru-RU";
    private static final String region = "RU";
    private static AtomicInteger page;

    public UpdateDatabase(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        page = new AtomicInteger(1);
    }


    @NonNull
    @Override
    public Result doWork() {
        AppLog.write("Update DB started");
        saveMoviesInDatabase(apiKey, language, region);
        SharedPreferencesOperation.save(KEY_NEXT_TIME_TO_UPDATE, String.valueOf(new Date().getTime() + 1000L * 60 * 60 * 24));
        AppLog.write("Update DB finished");
        return Result.success();
    }

    private void saveMoviesInDatabase(String apiKey, String language, String region) {
        int res = 0;
        PageMoviesJson data;
        Set<Integer> likedList = new HashSet<>(App.getInstance().movieDatabase.movieDao().getLikedList());
        do {
            AppLog.write("Page #" + page + " :");
            data = MovieLoader.getPage(apiKey, page.get(), language, region);
            if (data == null) {
                AppLog.write("...no data. Operation completed.");
                break;
            }
            AppLog.write("It was got " + page);
            for (MovieRecord record : MovieLoader.getMoviesFromPage(data, likedList)) {
                saveRecord(record);
                res++;
            }
            AppLog.write("...and stored to " + res + " records " + page);
            page.incrementAndGet();
            if ((MAX_PAGES_TO_LOAD > 0) && (page.get() >= MAX_PAGES_TO_LOAD)) {
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(SLEEP_SECONDS_BETWEEN_LOAD_PAGES);
            } catch (InterruptedException e) {
                break;
            }
        } while (true);
        AppLog.write("" + App.getInstance().movieDatabase.movieDao().getCount() + " movies");
        AppLog.write("" + App.getInstance().movieDatabase.tagDao().getCount() + " tags");
    }

    private void saveRecord(MovieRecord record) {
        App.getInstance().movieDatabase.movieDao().insert(record);
        App.getInstance().movieDatabase.tagDao().insert(SonicUtils.makeCodes(record));
    }
}
