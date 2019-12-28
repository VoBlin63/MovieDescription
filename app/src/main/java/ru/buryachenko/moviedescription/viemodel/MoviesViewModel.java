package ru.buryachenko.moviedescription.viemodel;

import android.os.Build;
import android.util.SparseArray;

import java.util.Arrays;
import java.util.List;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ru.buryachenko.moviedescription.App;
import ru.buryachenko.moviedescription.database.MovieRecord;
import ru.buryachenko.moviedescription.utilities.AppLog;
import ru.buryachenko.moviedescription.utilities.ConvertibleTerms;
import ru.buryachenko.moviedescription.utilities.Metaphone;
import ru.buryachenko.moviedescription.utilities.SonicUtils;

public class MoviesViewModel extends ViewModel {

    private String textFilter = "";
    private boolean onlyTitle = false;

    private SparseArray<MovieRecord> movies = new SparseArray();
    private MutableLiveData<Boolean> listReady = new MutableLiveData<>();

    private final int EMPTY_USEFULNESS = 9999;


    @RequiresApi(api = Build.VERSION_CODES.N)
    public void init() {
        if (listReady.getValue() == null || !listReady.getValue()) {
            App.getInstance()
                    .movieDatabase
                    .movieDao()
                    .getAll()
                    .subscribe(new Observer<List<MovieRecord>>() {
                        @Override
                        public void onSubscribe(Disposable d) {
                            setListReady(false);
                            movies.clear();
                            AppLog.write("Begin reforming");
                        }

                        @Override
                        public void onNext(List<MovieRecord> movieRecords) {
                            movieRecords.forEach(it -> movies.put(it.getId(), it));
                            clearUsefulness();
                            setListReady(true);
                            AppLog.write("Got list");
                        }

                        @Override
                        public void onError(Throwable e) {
                            AppLog.write("Error in movieDao().getAll() " + e);
                        }

                        @Override
                        public void onComplete() {
                        }
                    });
        }
    }

    public MovieRecord[] getListMovies() {
        MovieRecord[] res = new MovieRecord[movies.size()];
        for (int index = 0; index < movies.size(); index++) {
            res[index] = movies.get(movies.keyAt(index));
        }
        Arrays.sort(res, (a, b) -> a.getCompareFlag().compareTo(b.getCompareFlag()));
        return res;
    }

    private void clearUsefulness() {
        for (int index = 0; index < movies.size(); index++) {
            movies.get(movies.keyAt(index)).setUsefulness(EMPTY_USEFULNESS);
        }
    }

    public void setFilter(String textFilter, boolean onlyTitle) {
        this.textFilter = textFilter;
        this.onlyTitle = onlyTitle;
        clearUsefulness();
        List<String> wordsList = SonicUtils.getWordsList(textFilter);
        if (wordsList.isEmpty()) {
            setListReady(true);
            return;
        }

        Observable.fromIterable(wordsList)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(w1 -> {AppLog.write(" слово: " + w1); return w1;})
                .map(Metaphone::metaphone)
                .map(w1 -> {AppLog.write(" код metaphone: " + w1); return w1;})
                .map(ConvertibleTerms::topWord)
                .map(w1 -> {AppLog.write(" код convertable: " + w1); return w1;})
                .map(code -> App.getInstance().movieDatabase.tagDao().getSyncMovieIdsByTags(code, onlyTitle))
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        setListReady(false);
                    }

                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onNext(List<Integer> integers) {
                        AppLog.write(" list: " + integers.size());
                        integers.forEach(it -> markRecognized(it));
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {
                        AppLog.write("Filter was set");
                        setListReady(true);
                    }
                });
    }

    private void markRecognized(int movieId) {
        MovieRecord tmp = movies.get(movieId);
        if (tmp != null) {
            tmp.setUsefulness(tmp.getUsefulness() - 1);
            AppLog.write("id " + movieId + " was marked");
        }
    }

    public String getTextFilter() {
        return textFilter;
    }

    public boolean getOnlyTitleFilter() {
        return onlyTitle;
    }

    public LiveData<Boolean> getListReady() {
        return listReady;
    }

    private void setListReady(boolean newValue) {
        if (listReady.getValue() == null || listReady.getValue() != newValue) {
            listReady.postValue(newValue);
        }
    }
}
