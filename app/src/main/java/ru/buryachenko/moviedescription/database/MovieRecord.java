package ru.buryachenko.moviedescription.database;

import android.annotation.SuppressLint;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import ru.buryachenko.moviedescription.api.MovieJson;

@Entity
public class MovieRecord {

    @PrimaryKey
    private Integer id;
    private Double popularity;
    private Integer voteCount;
    private String posterPath;
    private Boolean adult;
    private String originalLanguage;
    private String originalTitle;
    private String title;
    private Float voteAverage;
    private String overview;
    private String releaseDate;
    private boolean liked;
    @Ignore
    private int usefulness;

    public MovieRecord(MovieJson filmJson) {
        title = filmJson.getTitle();
        overview = filmJson.getOverview();
        id = filmJson.getId();
        posterPath = "https://image.tmdb.org/t/p/w500/" + filmJson.getPosterPath();
        popularity = filmJson.getPopularity();
        voteCount = filmJson.getVoteCount();
        adult = filmJson.getAdult();
        originalLanguage = filmJson.getOriginalLanguage();
        originalTitle = filmJson.getOriginalTitle();
        voteAverage = filmJson.getVoteAverage();
        releaseDate =  filmJson.getReleaseDate();
        liked = false;
//        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
//        try {
//            releaseDate = formatter.parse(filmJson.getReleaseDate());
//        } catch (ParseException ignored) {}


    }

    public MovieRecord() {
    }

    public Integer getId() {
        return id;
    }

    public Double getPopularity() {
        return popularity;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public Boolean getAdult() {
        return adult;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public String getTitle() {
        return title;
    }

    @SuppressLint("DefaultLocale")
    public String getCompareFlag() {
        return String.format("%4d", usefulness) + title;
    }


    public Float getVoteAverage() {
        return voteAverage;
    }

    public String getOverview() {
        return overview;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public void setVoteCount(Integer voteCount) {
        this.voteCount = voteCount;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setVoteAverage(Float voteAverage) {
        this.voteAverage = voteAverage;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public boolean isLiked() {
        return liked;
    }

    public int getUsefulness() {
        return usefulness;
    }

    public void setLiked(boolean liked) {
        this.liked = liked;
    }

    public void setUsefulness(int usefulness) {
        this.usefulness = usefulness;
    }
}