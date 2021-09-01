package com.oceanforit.audioplayer.favorite;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(version = 1, entities = FavoriteAudio.class, exportSchema = false)
public abstract class FavoriteDatabase extends RoomDatabase {

    public abstract FavoriteDAO favoriteDAO();

    private static FavoriteDatabase instance;

    public static FavoriteDatabase getInstance(Context context) {
        if (instance == null)
            instance = Room
                    .databaseBuilder(context, FavoriteDatabase.class, "FavoriteV1")
                    .allowMainThreadQueries()
                    .build();
        return instance;
    }
}
