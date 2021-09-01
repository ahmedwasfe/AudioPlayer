package com.oceanforit.audioplayer.favorite;

import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Single;

public class FavDataSource implements FavoriteDataSource {

    private FavoriteDAO favoriteDAO;

    public FavDataSource(FavoriteDAO favoriteDAO) {
        this.favoriteDAO = favoriteDAO;
    }

    @Override
    public Flowable<List<FavoriteAudio>> getAllFavoritesAudios() {
        return favoriteDAO.getAllFavoritesAudios();
    }

    @Override
    public Single<FavoriteAudio> getAudioById(String id) {
        return favoriteDAO.getAudioById(id);
    }

    @Override
    public Completable addFavorite(FavoriteAudio... favoriteAudios) {
        return favoriteDAO.addFavorite(favoriteAudios);
    }

    @Override
    public Single<Integer> deleteFavorite(FavoriteAudio favoriteAudio) {
        return favoriteDAO.deleteFavorite(favoriteAudio);
    }

    @Override
    public Single<Integer> clearFavorite() {
        return favoriteDAO.clearFavorite();
    }
}
