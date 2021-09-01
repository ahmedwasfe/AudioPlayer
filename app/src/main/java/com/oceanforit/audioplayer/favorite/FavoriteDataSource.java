package com.oceanforit.audioplayer.favorite;

import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public interface FavoriteDataSource {

    Flowable<List<FavoriteAudio>> getAllFavoritesAudios();

    Single<FavoriteAudio> getAudioById(String id);

    Completable addFavorite(FavoriteAudio... favoriteAudios);

    Single<Integer> deleteFavorite(FavoriteAudio favoriteAudio);

    Single<Integer> clearFavorite();
}
