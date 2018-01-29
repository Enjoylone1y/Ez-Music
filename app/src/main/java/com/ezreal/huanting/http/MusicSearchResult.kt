package com.ezreal.huanting.http

/**
 * 在线歌曲信息搜索结果 bean
 * Created by wudeng on 2018/1/26.
 */

class MusicSearchResult {

    var error_code: Int = 0
    var songinfo: SonginfoBean? = null
    var bitrate: BitrateBean? = null

    class SonginfoBean {
        /**
         * ting_uid : 45561
         * author : 王菲
         * song_id : 569080829
         * title : 无问西东
         * artist_id : 15
         * lrclink : http://qukufile2.qianqian.com/data2/826.lrc
         * pic_big : http://qukufile2.qianqian.com/data290808280825.png
         * album_id : 569080827
         * all_artist_id : 15
         * all_artist_ting_uid : 45561
         * album_title : 无问西东
         */

        lateinit var ting_uid: String
        lateinit var author: String
        lateinit var song_id: String
        lateinit var title: String
        lateinit var artist_id: String
        lateinit var lrclink: String
        lateinit var pic_big: String
        lateinit var album_id: String
        lateinit var all_artist_id: String
        lateinit var all_artist_ting_uid: String
        lateinit var album_title: String
    }

    class BitrateBean {
        /**
         * show_link : http://zhangmenshiting.qianqia853.mp3?xcode=bb45c038dd045b5
         * file_size : 2319698
         * file_duration : 290
         * file_bitrate : 64
         * file_link : http://zhangmenshiting.qianqian421/569080853/569080853.mp3?xcode=bba5
         */

        lateinit var show_link: String
        lateinit var file_link: String
        var file_size: Long = 0
        var file_duration: Long = 0
        var file_bitrate: Int = 0
    }
}
