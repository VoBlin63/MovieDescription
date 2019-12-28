package ru.buryachenko.moviedescription.database;


import android.content.Context;

import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import ru.buryachenko.moviedescription.App;
import ru.buryachenko.moviedescription.api.MovieLoader;
import ru.buryachenko.moviedescription.api.PageMoviesJson;
import ru.buryachenko.moviedescription.utilities.AppLog;
import ru.buryachenko.moviedescription.utilities.SonicUtils;

import static ru.buryachenko.moviedescription.Constant.MAX_PAGES_TO_LOAD;
import static ru.buryachenko.moviedescription.Constant.SLEEP_SECONDS_BETWEEN_LOAD_PAGES;

public class UpdateDatabase extends Worker {

    private static final String apiKey = "c3e17ff26735628669886b00d573ab4d";
    private static final String language = "ru-RU";
    private static final String region = "RU";

    public UpdateDatabase(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        AppLog.write("Update DB started");
        saveMoviesInDatabase(apiKey, language, region);
        AppLog.write("Update DB finished");
        return Result.success();
    }

    private void saveMoviesInDatabase(String apiKey, String language, String region) {
        int page = 1;
        PageMoviesJson data;
        do {
            AppLog.write("Page #" + page + " :");
            data = MovieLoader.getPage(apiKey, page, language, region);
            AppLog.write("...was got");
            int res = 0;
            //App.getInstance().movieDatabase.movieDao().insert(MovieLoader.getMoviesFromPage(data));
            for ( MovieRecord record : MovieLoader.getMoviesFromPage(data)) {
                saveRecord(record);
                res ++;
            }
            AppLog.write("...and stored " + res + " records");
            page = page + 1;
            if ((MAX_PAGES_TO_LOAD > 0) && (page >= MAX_PAGES_TO_LOAD)) {
                break;
            }
            try {
                TimeUnit.SECONDS.sleep(SLEEP_SECONDS_BETWEEN_LOAD_PAGES);
            } catch (InterruptedException e) {
                break;
            }
        } while (data != null);
        AppLog.write("" + App.getInstance().movieDatabase.movieDao().getCount() + " movies");
        AppLog.write("" + App.getInstance().movieDatabase.tagDao().getCount() + " tags");
    }

    private void saveRecord(MovieRecord record) {
        App.getInstance().movieDatabase.movieDao().insert(record);
        App.getInstance().movieDatabase.tagDao().insert(SonicUtils.makeCodes(record));
    }
}
