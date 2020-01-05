package ru.buryachenko.moviedescription.database;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import io.reactivex.Observable;

@Dao
public interface TagDao {

        @Query("SELECT movieId FROM tagrecord WHERE codeWord LIKE :code AND ((NOT :onlyTitle) OR inTitle)")
        List<Integer> getSyncMovieIdsByTags(String code, boolean onlyTitle);

        @Query("SELECT count(*) FROM tagrecord")
        int getCount();

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        void insert(List<TagRecord> tags);

}
