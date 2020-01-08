package ru.buryachenko.moviedescription.viemodel;

import android.os.Build;
import android.util.SparseArray;
import android.util.SparseBooleanArray;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subjects.PublishSubject;
import ru.buryachenko.moviedescription.App;
import ru.buryachenko.moviedescription.database.MovieRecord;
import ru.buryachenko.moviedescription.utilities.AppLog;
import ru.buryachenko.moviedescription.utilities.Config;
import ru.buryachenko.moviedescription.utilities.ConvertibleTerms;
import ru.buryachenko.moviedescription.utilities.Metaphone;
import ru.buryachenko.moviedescription.utilities.SonicUtils;

public class MoviesViewModel extends ViewModel {
    private String textFilter = "";
    private int indexForOpenDetail = -1;
    private Config config = Config.getInstance();
    private SparseArray<MovieRecord> movies = new SparseArray();
    private MutableLiveData<Boolean> listReady = new MutableLiveData<>();
    private SparseBooleanArray cacheLiked = new SparseBooleanArray();
    private final int EMPTY_USEFULNESS = 9999;
    private MovieRecord[] moviesOnScreen;
    private PublishSubject<String> filterQueue = PublishSubject.create();
    private ModeView mode = ModeView.MAIN_LIST;

    public void setMode(ModeView mode) {
        if (mode != this.mode) {
            moviesOnScreen = null;
            this.mode = mode;
            listReady.postValue(true);
        }
    }

    public ModeView getMode() {
        return mode;
    }


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
                            listReady.postValue(false);
                            movies.clear();
                            moviesOnScreen = null;
                            AppLog.write("Begin reforming");
                        }

                        @Override
                        public void onNext(List<MovieRecord> movieRecords) {
                            movieRecords.forEach(it -> movies.put(it.getId(), it));
                            clearUsefulness();
                            listReady.postValue(true);
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
        filterQueue
                .debounce(700L, TimeUnit.MILLISECONDS)
                .subscribe(value -> setFilterAfterDebounce(value));
        filterQueue.onNext(getTextFilter());
    }

    public MovieRecord[] getListMovies() {
        if (moviesOnScreen == null)
            if (mode == ModeView.MAIN_LIST) {
                fillMoviesOnScreen();
            } else {
                fillLikedOnScreen();
            }
        return moviesOnScreen;
    }

    private void fillLikedOnScreen() {
        int count = 0;
        for (int index = 0; index < movies.size(); index++) {
            if (movies.valueAt(index).isLiked()) {
                count = count + 1;
            }
        }
        MovieRecord[] res = new MovieRecord[count];
        int currentIndex = 0;
        for (int index = 0; index < movies.size(); index++) {
            if (movies.valueAt(index).isLiked()) {
                res[currentIndex++] = movies.valueAt(index);
            }
        }
        Arrays.sort(res, (a, b) -> a.getCompareFlag().compareTo(b.getCompareFlag()));
        moviesOnScreen = res;
    }

    private void fillMoviesOnScreen() {
        int count = 0;
        if (!config.isShowOnlyFitFilter()
                || getTextFilter().isEmpty()) {
            count = movies.size();
        } else {
            for (int index = 0; index < movies.size(); index++) {
                if (movies.valueAt(index).getUsefulness() != EMPTY_USEFULNESS) {
                    count = count + 1;
                }
            }
        }
        MovieRecord[] res = new MovieRecord[count];
        int currentIndex = 0;
        for (int index = 0; index < movies.size(); index++) {
            if (!config.isShowOnlyFitFilter()
                    || movies.valueAt(index).getUsefulness() != EMPTY_USEFULNESS
                    || getTextFilter().isEmpty()
            ) {
                res[currentIndex++] = movies.valueAt(index);
            }
        }

        Arrays.sort(res, (a, b) -> a.getCompareFlag().compareTo(b.getCompareFlag()));
        moviesOnScreen = res;
    }

    private void clearUsefulness() {
        for (int index = 0; index < movies.size(); index++) {
            movies.get(movies.keyAt(index)).setUsefulness(EMPTY_USEFULNESS);
        }
    }

    public void setFilter(String textFilter) {
        filterQueue.onNext(textFilter);
    }

    public void turnLiked(int adapterPosition) {
        MovieRecord movie = moviesOnScreen[adapterPosition];
        movie.turnLiked();
        cacheLiked.put(movie.getId(), movie.isLiked());
    }

    public void pushLiked(boolean exitOnFinish) {
        Observable.range(0, cacheLiked.size())
                .observeOn(Schedulers.io())
                .subscribe(new Observer<Integer>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                    }

                    @Override
                    public void onNext(Integer index) {
                        AppLog.write(" Change liked for " + index);
                        App.getInstance().movieDatabase.movieDao().setLiked(cacheLiked.keyAt(index), cacheLiked.valueAt(index));
                    }

                    @Override
                    public void onError(Throwable e) {
                    }

                    @Override
                    public void onComplete() {
                        cacheLiked.clear();
                        if (exitOnFinish) {
                            System.exit(0);
                        }
                    }
                });
    }

    public void resetList() {
        moviesOnScreen = null;
    }


    private void setFilterAfterDebounce(String textFilter) {
        AppLog.write("got DEBOUNCED filter: '" + textFilter + "'");
        this.textFilter = textFilter;
        clearUsefulness();
        List<String> wordsList = SonicUtils.getWordsList(textFilter);
        resetList();
        if (wordsList.isEmpty()) {
            listReady.postValue(true);
            return;
        }

        Observable.fromIterable(wordsList)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map(w1 -> {AppLog.write(" слово: " + w1); return w1;})
                .map(Metaphone::code)
                .map(w1 -> {AppLog.write(" код code: " + w1); return w1;})
                .map(ConvertibleTerms::topWord)
                .map(w1 -> {AppLog.write(" код convertable: " + w1); return w1;})
                .map(code -> App.getInstance().movieDatabase.tagDao().getSyncMovieIdsByTags(code, !config.isUseOverview()))
                .subscribe(new Observer<List<Integer>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        listReady.postValue(false);
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
                        listReady.postValue(true);
                    }
                });
    }

    private void markRecognized(int movieId) {
        MovieRecord tmp = movies.get(movieId);
        if (tmp != null) {
            tmp.setUsefulness(tmp.getUsefulness() - 1);
            AppLog.write("id " + movieId + " was marked");
            AppLog.write(movies.get(movieId).getTitle() + "  " + movies.get(movieId).getId());
        }
    }

    public String getTextFilter() {
        return textFilter;
    }

    public LiveData<Boolean> getListReady() {
        return listReady;
    }

    public int getIndexForOpenDetail() {
        return indexForOpenDetail;
    }

    public void setIndexForOpen(int indexForOpen) {
        this.indexForOpenDetail = indexForOpen;
    }

    public String getFragmentTitle(String tag) {
        return "!" + tag;
    }

    public enum ModeView {MAIN_LIST, LIKED_LIST}
}
