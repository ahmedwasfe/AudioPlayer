package com.oceanforit.audioplayer.favorite;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

@Dao
public interface FavoriteDAO {

    @Query("SELECT * FROM favorite ORDER BY id DESC")
    Flowable<List<FavoriteAudio>> getAllFavoritesAudios();

    @Query("SELECt * FROM favorite WHERE id=:id")
    Single<FavoriteAudio> getAudioById(String id);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    Completable addFavorite(FavoriteAudio... favoriteAudios);

    @Delete
    Single<Integer> deleteFavorite(FavoriteAudio favoriteAudio);

    @Query("DELETE FROM favorite")
    Single<Integer> clearFavorite();

}
