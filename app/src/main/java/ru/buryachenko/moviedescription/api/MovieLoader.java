package ru.buryachenko.moviedescription.api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ru.buryachenko.moviedescription.App;
import ru.buryachenko.moviedescription.database.MovieRecord;
import ru.buryachenko.moviedescription.utilities.AppLog;

public class MovieLoader {

    public static PageMoviesJson getPage(String apiKey, int page, String language, String region) {
        PageMoviesJson res = null;
        try {
            res = App.getInstance().serviceHttp.getMoviePage(apiKey, page, language, region).execute().body();
        } catch (IOException e) {
            AppLog.write("Ошибка получения страницы с фильмами: " + e);
        }
        return res;
    }

    public static List<MovieRecord> getMoviesFromPage(PageMoviesJson page) {
        List<MovieRecord> res = new ArrayList<>();
        if (page != null) {
            for (MovieJson filmJson : page.getResults()) {
                res.add(new MovieRecord(filmJson));
            }
        }
        return res;
    }

}
