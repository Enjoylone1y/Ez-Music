package com.ezreal.huanting.http.result

import com.google.gson.annotations.SerializedName

/**
 * 榜单列表
 * Created by wudeng on 2018/2/6.
 */

class RankBillListResult {

    var content =  ArrayList<RankBillBean>()

    class RankBillBean {
        /**
         * name : 新歌榜
         * type : 1
         * pic_s444 : http://y.baidu.com/cms/app/640-640.jpg
         */

        lateinit var name: String
        var type: Int = 0

        @SerializedName("pic_s192")
        lateinit var pic: String
        var content =  ArrayList<RankMusicBean>()

        class RankMusicBean {
            /**
             * title : 无问西东
             * author : 王菲
             * song_id : 569080829
             * album_id : 569080827
             * album_title : 无问西东
             */

            lateinit var title: String
            lateinit var author: String
            lateinit var song_id: String
            lateinit var album_id: String
            lateinit var album_title: String
        }
    }
}
