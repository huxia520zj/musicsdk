package com.eryanet.m85musicsdk.bean;

public class Announcer {
    int id;
    String nickname;
    String avatar_url;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    private String getAvatar_url() {
        return avatar_url;
    }

    private void setAvatar_url(String avatar_url) {
        this.avatar_url = avatar_url;
    }
}
