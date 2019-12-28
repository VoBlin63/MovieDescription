package ru.buryachenko.moviedescription.database;

import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        foreignKeys = @ForeignKey(entity = MovieRecord.class,
                parentColumns = "id",
                childColumns = "movieId",
                onDelete = ForeignKey.CASCADE),
        indices = {
                @Index("movieId"),
                @Index("codeWord"),
        }
)

public class TagRecord {
    @PrimaryKey(autoGenerate = true)
    private Integer id;
    private Integer movieId;
    private String codeWord;
    private Boolean inTitle;

    public TagRecord() {
    }

    @Ignore
    public TagRecord(Integer movieId, String codeWord, Boolean inTitle) {
        this.movieId = movieId;
        this.codeWord = codeWord;
        this.inTitle = inTitle;
    }

    public Integer getId() {
        return id;
    }

    public Integer getMovieId() {
        return movieId;
    }

    public String getCodeWord() {
        return codeWord;
    }

    public Boolean getInTitle() {
        return inTitle;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public void setMovieId(Integer movieId) {
        this.movieId = movieId;
    }

    public void setCodeWord(String codeWord) {
        this.codeWord = codeWord;
    }

    public void setInTitle(Boolean inTitle) {
        this.inTitle = inTitle;
    }
}