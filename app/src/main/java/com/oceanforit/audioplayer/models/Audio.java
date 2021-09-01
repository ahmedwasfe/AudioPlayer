package com.oceanforit.audioplayer.models;

public class Audio {

    private String id;
    private String path;
    private String title;
    private String artist;
    private String artistId;
    private String album;
    private String albumID;
    private String duration;

    public Audio() {
    }

    public Audio(String id, String path, String title, String artist, String artistId,
                 String album, String albumID, String duration) {
        this.id = id;
        this.path = path;
        this.title = title;
        this.artist = artist;
        this.artistId = artistId;
        this.album = album;
        this.albumID = albumID;
        this.duration = duration;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getArtistId() {
        return artistId;
    }

    public void setArtistId(String artistId) {
        this.artistId = artistId;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getAlbumID() {
        return albumID;
    }

    public void setAlbumID(String albumID) {
        this.albumID = albumID;
    }

    public String getDuration() {
        return duration;
    }

    public void setDuration(String duration) {
        this.duration = duration;
    }

}
