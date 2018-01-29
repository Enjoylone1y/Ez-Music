package com.ezreal.huanting.http

/**
 * 推荐歌曲返回结果
 * Created by wudeng on 2018/1/25.
 */

class RecomSearchResult {

    var error_code: Int = 0
    var result: ResultBean? = null

    class ResultBean {
        var list: List<RecomSongBean>? = null
    }

    class RecomSongBean {
        /**
         * artist_id : 14378906
         * all_artist_id : 14378906
         * pic_big : http://qukufile2.qianqian.com/data2..
         * hot : 6644
         * file_duration : 204
         * song_id : 14902630
         * title : 不再联系
         * ting_uid : 8865311
         * author : 夏天Alex
         * album_id : 14902631
         * album_title : 不再联系
         */

        lateinit var artist_id: String
        lateinit var all_artist_id: String
        lateinit var all_artist_ting_uid: String
        lateinit var pic_big: String
        lateinit var hot: String
        lateinit var file_duration: String
        lateinit var song_id: String
        lateinit var title: String
        lateinit var ting_uid: String
        lateinit var author: String
        lateinit var album_id: String
        lateinit var album_title: String
    }
}
