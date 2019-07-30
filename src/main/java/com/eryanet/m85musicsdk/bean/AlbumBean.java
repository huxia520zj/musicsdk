package com.eryanet.m85musicsdk.bean;

import java.util.List;

public class AlbumBean {
    boolean isSuccess;
    List<Album> albumList;

    public boolean isSuccess() {
        return isSuccess;
    }

    public void setSuccess(boolean success) {
        isSuccess = success;
    }

    public List<Album> getAlbumList() {
        return albumList;
    }

    public void setAlbumList(List<Album> albumList) {
        this.albumList = albumList;
    }
}
