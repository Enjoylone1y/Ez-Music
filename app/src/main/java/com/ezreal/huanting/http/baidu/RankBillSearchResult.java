package com.ezreal.huanting.http.baidu;

import java.util.List;

/**
 * 歌曲排行榜搜索返回
 * Created by wudeng on 2018/1/26.
 */

public class RankBillSearchResult {

    private BillboardBean billboard;
    private int error_code;
    private List<BillSongBean> song_list;

    public BillboardBean getBillboard() {
        return billboard;
    }

    public void setBillboard(BillboardBean billboard) {
        this.billboard = billboard;
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public List<BillSongBean> getSong_list() {
        return song_list;
    }

    public void setSong_list(List<BillSongBean> song_list) {
        this.song_list = song_list;
    }

    public static class BillboardBean {
        /**
         * billboard_type : 16
         * billboard_no : 498
         * update_date : 2012-09-11
         * billboard_songnum : 1132
         * name : null
         * comment :
         * pic_s640 :
         * web_url : http://music.baidu.com
         */

        private String billboard_type;
        private String billboard_no;
        private String update_date;
        private String billboard_songnum;
        private String comment;
        private String pic_s640;
        private String web_url;

        public String getBillboard_type() {
            return billboard_type;
        }

        public void setBillboard_type(String billboard_type) {
            this.billboard_type = billboard_type;
        }

        public String getBillboard_no() {
            return billboard_no;
        }

        public void setBillboard_no(String billboard_no) {
            this.billboard_no = billboard_no;
        }

        public String getUpdate_date() {
            return update_date;
        }

        public void setUpdate_date(String update_date) {
            this.update_date = update_date;
        }

        public String getBillboard_songnum() {
            return billboard_songnum;
        }

        public void setBillboard_songnum(String billboard_songnum) {
            this.billboard_songnum = billboard_songnum;
        }

        public String getComment() {
            return comment;
        }

        public void setComment(String comment) {
            this.comment = comment;
        }

        public String getPic_s640() {
            return pic_s640;
        }

        public void setPic_s640(String pic_s640) {
            this.pic_s640 = pic_s640;
        }

        public String getWeb_url() {
            return web_url;
        }

        public void setWeb_url(String web_url) {
            this.web_url = web_url;
        }
    }

    public static class BillSongBean {
        /**
         * artist_id : 10490649
         * pic_big : http://qukufile2.qianqian.com14950780.jpg@s_1,w_150,h_150
         * album_no : 12
         * lrclink : http://qukufile2.qianqian.com/21/262349921.lrc
         * all_artist_ting_uid : 687850
         * all_artist_id : 10490649
         * style : 流行
         * file_duration : 217
         * song_id : 14950804
         * title : 我的歌声里
         * ting_uid : 687850
         * album_id : 14950780
         * album_title : Everything in the World
         * artist_name : 曲婉婷
         */

        private String artist_id;
        private String pic_big;
        private String album_no;
        private String lrclink;
        private String all_artist_ting_uid;
        private String style;
        private int file_duration;
        private String song_id;
        private String title;
        private String ting_uid;
        private String album_id;
        private String album_title;
        private String artist_name;

        public String getArtist_id() {
            return artist_id;
        }

        public void setArtist_id(String artist_id) {
            this.artist_id = artist_id;
        }

        public String getPic_big() {
            return pic_big;
        }

        public void setPic_big(String pic_big) {
            this.pic_big = pic_big;
        }

        public String getAlbum_no() {
            return album_no;
        }

        public void setAlbum_no(String album_no) {
            this.album_no = album_no;
        }

        public String getLrclink() {
            return lrclink;
        }

        public void setLrclink(String lrclink) {
            this.lrclink = lrclink;
        }

        public String getAll_artist_ting_uid() {
            return all_artist_ting_uid;
        }

        public void setAll_artist_ting_uid(String all_artist_ting_uid) {
            this.all_artist_ting_uid = all_artist_ting_uid;
        }

        public String getStyle() {
            return style;
        }

        public void setStyle(String style) {
            this.style = style;
        }

        public int getFile_duration() {
            return file_duration;
        }

        public void setFile_duration(int file_duration) {
            this.file_duration = file_duration;
        }

        public String getSong_id() {
            return song_id;
        }

        public void setSong_id(String song_id) {
            this.song_id = song_id;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTing_uid() {
            return ting_uid;
        }

        public void setTing_uid(String ting_uid) {
            this.ting_uid = ting_uid;
        }

        public String getAlbum_id() {
            return album_id;
        }

        public void setAlbum_id(String album_id) {
            this.album_id = album_id;
        }

        public String getAlbum_title() {
            return album_title;
        }

        public void setAlbum_title(String album_title) {
            this.album_title = album_title;
        }

        public String getArtist_name() {
            return artist_name;
        }

        public void setArtist_name(String artist_name) {
            this.artist_name = artist_name;
        }
    }
}
