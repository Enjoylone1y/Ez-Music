package com.ezreal.huanting.http;

/**
 * 歌手信息搜索
 * Created by wudeng on 2018/1/26.
 */

public class ArtistSearchResult {

    private String ting_uid;
    private String url;
    private String albums_total;
    private String artist_id;
    private String constellation;
    private String intro;
    private String country;
    private int mv_total;
    private String songs_total;
    private String birth;
    private String avatar_big;
    private String name;
    private String company;

    public String getTing_uid() {
        return ting_uid;
    }

    public void setTing_uid(String ting_uid) {
        this.ting_uid = ting_uid;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getAlbums_total() {
        return albums_total;
    }

    public void setAlbums_total(String albums_total) {
        this.albums_total = albums_total;
    }

    public String getArtist_id() {
        return artist_id;
    }

    public void setArtist_id(String artist_id) {
        this.artist_id = artist_id;
    }

    public String getConstellation() {
        return constellation;
    }

    public void setConstellation(String constellation) {
        this.constellation = constellation;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public int getMv_total() {
        return mv_total;
    }

    public void setMv_total(int mv_total) {
        this.mv_total = mv_total;
    }

    public String getSongs_total() {
        return songs_total;
    }

    public void setSongs_total(String songs_total) {
        this.songs_total = songs_total;
    }

    public String getBirth() {
        return birth;
    }

    public void setBirth(String birth) {
        this.birth = birth;
    }

    public String getAvatar_big() {
        return avatar_big;
    }

    public void setAvatar_big(String avatar_big) {
        this.avatar_big = avatar_big;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }
}
