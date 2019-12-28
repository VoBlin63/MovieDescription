package ru.buryachenko.moviedescription.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;


@Database(entities = {MovieRecord.class, TagRecord.class},
        version = 1,
        exportSchema = false)
public abstract class MovieDatabase extends RoomDatabase {
    public abstract MovieDao movieDao();
    public abstract TagDao tagDao();

}