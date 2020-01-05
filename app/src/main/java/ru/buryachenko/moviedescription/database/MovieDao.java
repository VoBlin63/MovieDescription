package ru.buryachenko.moviedescription.database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;
import io.reactivex.Observable;

@Dao
public interface MovieDao {

    @Query("SELECT * FROM movierecord")
    Observable<List<MovieRecord>> getAll();

    @Query("SELECT count(*) FROM movierecord")
    int getCount();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MovieRecord movie);

}
