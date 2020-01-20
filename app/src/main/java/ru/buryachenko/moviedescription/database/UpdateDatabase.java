package ru.buryachenko.moviedescription.database;


import android.content.Context;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import ru.buryachenko.moviedescription.App;
import ru.buryachenko.moviedescription.BuildConfig;
import ru.buryachenko.moviedescription.R;
import ru.buryachenko.moviedescription.api.MovieJson;
import ru.buryachenko.moviedescription.api.PageMoviesJson;
import ru.buryachenko.moviedescription.utilities.AppLog;
import ru.buryachenko.moviedescription.utilities.FilmNotification;
import ru.buryachenko.moviedescription.utilities.SharedPreferencesOperation;
import ru.buryachenko.moviedescription.utilities.SonicUtils;

import static ru.buryachenko.moviedescription.Constant.KEY_NEXT_TIME_TO_UPDATE;
import static ru.buryachenko.moviedescription.Constant.MAX_PAGES_TO_LOAD;
import static ru.buryachenko.moviedescription.Constant.SLEEP_SECONDS_BETWEEN_LOAD_PAGES;

public class UpdateDatabase extends Worker {

    private static final String apiKey = BuildConfig.API_KEY_TMDB;
    private static final String language = "ru-RU";
    private static final String region = "RU";

    private Result result;
    private int updateCount;

    public UpdateDatabase(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    @NonNull
    @Override
    public Result doWork() {
        AppLog.write("Update DB started");
        AtomicInteger page = new AtomicInteger(1);
        Set<Integer> likedList = new HashSet<>(App.getInstance().movieDatabase.movieDao().getLikedList());
        result = Result.failure();
        updateCount = 0;
        while (page.get() >= 0) {
            loadNextPage(page, likedList, 0);
        }
        return this.result;
    }

    private void loadNextPage(AtomicInteger page, Set<Integer> likedList, int counter) {
        try {
            TimeUnit.SECONDS.sleep(SLEEP_SECONDS_BETWEEN_LOAD_PAGES);
        } catch (InterruptedException e) {
        }
        App.getInstance()
                .serviceHttp.getMoviePage(apiKey, page.getAndIncrement(), language, region)
                .toFlowable()
                .subscribe(pageMoviesJson -> acceptPage(pageMoviesJson, page, likedList),
                        throwable -> catchError(throwable, page, likedList, counter));
    }


    private void acceptPage(PageMoviesJson pageData, AtomicInteger page, Set<Integer> likedList) {
        if (pageData == null) {
            AppLog.write("Rx-retrofit got empty data => exit update");
            finishWork(page, Result.success());
            return;
        }
        for (MovieRecord record : getMoviesFromPage(pageData, likedList)) {
            updateCount += 1;
            saveRecord(record);
        }
        AppLog.write("Page #" + (page.get()-1) + " was loaded");
        if ((MAX_PAGES_TO_LOAD > 0) && (page.get() > MAX_PAGES_TO_LOAD)) {
            AppLog.write(" page.get() > MAX_PAGES_TO_LOAD   => exit update");
            finishWork(page, Result.success());
            return;
        }
        loadNextPage(page, likedList, 0);
    }

    private void finishWork(AtomicInteger page, Result result) {
        SharedPreferencesOperation.save(KEY_NEXT_TIME_TO_UPDATE, String.valueOf(new Date().getTime() + 1000L * 60 * 60 * 24));
        page.set(-1);
        this.result = result;
        String text = App.getInstance().getString(R.string.notificationChannelFinishBodyPart1) + " " + updateCount + " " + App.getInstance().getString(R.string.notificationChannelFinishBodyPart2);
        FilmNotification.pushMessage("", text);
        AppLog.write("" + App.getInstance().movieDatabase.movieDao().getCount() + " movies");
        AppLog.write("" + App.getInstance().movieDatabase.tagDao().getCount() + " tags");
        AppLog.write(text);
    }

    private void catchError(Throwable error, AtomicInteger page, Set<Integer> likedList, Integer counter) {
        AppLog.write("Error in update : " + error.toString());
        AppLog.write("'" + error.getMessage() + "'");
        switch (error.getMessage().trim()) {
            case "HTTP 429":
            case "HTTP 504":
            case "HTTP 507":
                AppLog.write("It'll repeat, attempt #" + counter);
                if (counter < 3) {
                    loadNextPage(page, likedList, counter + 1);
                } else {
                    AppLog.write("Sorry, error can't get round");
                    finishWork(page, Result.failure());
                }
                break;
            default:
                finishWork(page, Result.failure());
        }
    }

    private void saveRecord(MovieRecord record) {
        App.getInstance().movieDatabase.movieDao().insert(record);
        App.getInstance().movieDatabase.tagDao().insert(SonicUtils.makeCodes(record));
    }

    private List<MovieRecord> getMoviesFromPage(PageMoviesJson page, Set<Integer> likedList) {
        List<MovieRecord> res = new ArrayList<>();
        if (page != null) {
            for (MovieJson filmJson : page.getResults()) {
                res.add(new MovieRecord(filmJson, likedList));
            }
        }
        return res;
    }
}
