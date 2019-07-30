package com.eryanet.m85musicsdk.bean;

public class Track {
    int id;
    String title;
    int duration;
    Announcer announcer;
    Album album;
    String play_url;
    PlayInfo play_info;
    boolean isCollected;
    int favor_id;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public Announcer getAnnouncer() {
        return announcer;
    }

    public void setAnnouncer(Announcer announcer) {
        this.announcer = announcer;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public String getPlay_url() {
        return play_url;
    }

    public void setPlay_url(String play_url) {
        this.play_url = play_url;
    }

    public boolean isCollected() {
        return isCollected;
    }

    public void setCollected(boolean collected) {
        isCollected = collected;
    }

    public int getFavor_id() {
        return favor_id;
    }

    public void setFavor_id(int favor_id) {
        this.favor_id = favor_id;
    }

    private PlayInfo getPlay_info() {
        return play_info;
    }

    private void setPlay_info(PlayInfo play_info) {
        this.play_info = play_info;
    }
}
