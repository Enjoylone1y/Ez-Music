package com.ezreal.huanting.http.result

import com.google.gson.annotations.SerializedName

/**
 * 歌曲排行榜搜索返回
 * Created by wudeng on 2018/1/26.
 */

class RankBillSearchResult {

    lateinit var billboard: BillboardBean

    @SerializedName("song_list")
    var list = ArrayList<BillSongBean>()

    class BillboardBean {
        /**
         * billboard_type : 16
         * update_date : 2012-09-11
         * pic_s444 :
         */
        @SerializedName("billboard_type")
        lateinit var type: String

        @SerializedName("update_date")
        lateinit var update: String

        @SerializedName("pic_s192")
        lateinit var pic: String
    }

    class BillSongBean {
        /**
         * artist_id : 10490649
         * pic_big : http://qukufile2.qianqian.com14950780.jpg@s_1,w_150,h_150
         * lrclink : http://qukufile2.qianqian.com/21/262349921.lrc
         * all_artist_ting_uid : 687850
         * all_artist_id : 10490649
         * song_id : 14950804
         * title : 我的歌声里
         * ting_uid : 687850
         * album_id : 14950780
         * album_title : Everything in the World
         * artist_name : 曲婉婷
         */

        lateinit var song_id: String
        lateinit var title: String
        lateinit var pic_big: String
        lateinit var artist_name: String
        lateinit var ting_uid: String
        lateinit var album_id: String
        lateinit var album_title: String

        var artist_id: String ?= null
        var lrclink: String? = null
    }
}
