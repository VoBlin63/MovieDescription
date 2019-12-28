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

//    @Query("SELECT * FROM movierecord WHERE id = :id")
//    MovieRecord getById(int id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(MovieRecord movie);

//    @Update
//    void update(MovieRecord movie);

//    @Delete
//    void delete(MovieRecord movie);

//    @Insert(onConflict = OnConflictStrategy.REPLACE)
//    void insert(List<MovieRecord> movies);
}
