package ru.buryachenko.moviedescription.api;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TmdbApiServiceRx {
    @GET("3/discover/movie")
    Single<PageMoviesJson> getMoviePage(@Query("api_key") String apiKey,
                                        @Query("page") int page,
                                        @Query("language") String language,
                                        @Query("region") String region);
}
