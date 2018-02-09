package com.ezreal.huanting.http.result

import com.google.gson.annotations.SerializedName

/**
 * 专辑信息
 * Created by wudeng on 2018/2/9.
 */

class AlbumInfoResult {

    lateinit var albumInfo: AlbumInfoBean
    var songlist =  ArrayList<AlbumSongBean>()

    class AlbumInfoBean {
        /**
         * album_id : 572588561
         * author : 刘力扬
         * title : Work For Light
         * publishcompany :  北京摩登天空文化发展有限公司 
         * songs_total : 1
         * info : 2017年8月签约摩登天空，10月与嘻哈音乐人满 Light，向着梦想向着光。
         * publishtime : 2018-02-09
         * pic_big : http://qukufile2.qianqian.com/data2/pic/05035298_150
         * collect_num : 1
         */

        lateinit var album_id: String
        lateinit var author: String
        lateinit var title: String

        @SerializedName("pic_big")
        lateinit var pic: String

        var favorites_num: Int = 0
        var collect_num: Int = 0

        var publishcompany: String? = null
        var songs_total: String? = null
        var info: String? = null
        var publishtime: String? = null
    }

    class AlbumSongBean {
        lateinit var song_id: String
    }
}
