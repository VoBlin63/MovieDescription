package ru.buryachenko.moviedescription.database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Observable;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movierecord")
    Observable<List<MovieRecord>> getAll();

    @Query("UPDATE movierecord SET liked = :liked WHERE id in (:movieIds)")
    void setLikedList(List<Integer> movieIds, boolean liked);

    @Query("UPDATE movierecord SET liked = :liked WHERE id = :movieId")
    void setLiked(int movieId, boolean liked);

    @Query("SELECT count(*) FROM movierecord")
    int getCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MovieRecord movie);

}
