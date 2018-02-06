package com.ezreal.huanting.http.result

import com.google.gson.annotations.SerializedName

/**
 * 推荐列表
 * Created by wudeng on 2018/2/6.
 */

class RecomListResult {

    var content =  ArrayList<ContentBean>()

    class ContentBean {

        @SerializedName("song_list")
        var list = ArrayList<SongListBean>()

        class SongListBean {
            /**
             * pic_big : http://qukufile2.qianqian.com/data2/music/182492173/252492173.jpg@s_0,w_150
             * pic_small : http://qukufile2.qianqian.com/data2/music/152492173.jpg@s_0,w_90
             * song_id : 7313983
             * title : 喜欢你
             * ting_uid : 1100
             * author : Beyond
             * album_id : 7311104
             * album_title : 传奇再续
             */

            lateinit var pic_big: String
            lateinit var pic_small: String
            lateinit var song_id: String
            lateinit var title: String
            lateinit var ting_uid: String
            lateinit var author: String
            lateinit var album_id: String
            lateinit var album_title: String
        }
    }
}
