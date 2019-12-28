package ru.buryachenko.moviedescription.api;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface TmdbApiService {
    @GET("3/discover/movie")
    Call<PageMoviesJson> getMoviePage(@Query("api_key") String apiKey,
                                 @Query("page") int page,
                                 @Query("language") String language,
                                 @Query("region") String region);
}



