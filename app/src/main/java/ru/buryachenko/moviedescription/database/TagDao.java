package ru.buryachenko.moviedescription.database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Observable;

@Dao
public interface TagDao {

//        @Query("SELECT * FROM tagrecord ORDER BY codeWord")
//        Observable<List<TagRecord>> getAllTags();

//        @Query("SELECT movieId FROM tagrecord WHERE codeWord LIKE :code AND ((NOT :onlyTitle) OR inTitle)")
//        Observable<List<Integer>> getMovieIdsByTags(String code, boolean onlyTitle);

        @Query("SELECT movieId FROM tagrecord WHERE codeWord LIKE :code AND ((NOT :onlyTitle) OR inTitle)")
        List<Integer> getSyncMovieIdsByTags(String code, boolean onlyTitle);

        @Query("SELECT count(*) FROM tagrecord")
        int getCount();

//        @Insert(onConflict = OnConflictStrategy.REPLACE)
//        void insert(TagRecord tag);

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(List<TagRecord> tags);

//        @Query("SELECT DISTINCT movieId FROM TagRecord WHERE codeWord LIKE :tag")
//        List<Integer> getMovieIdByCodeWord(String tag);

//        @Query("DELETE FROM TagRecord WHERE movieId = :id")
//        void clearTagsByMovieId(int id);

}
