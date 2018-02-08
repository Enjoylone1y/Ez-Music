package com.ezreal.huanting.http.result

/**
 * Created by wudeng on 2018/2/8.
 */

class LrcPicSearchResult {

    var songinfo =  ArrayList<SongInfoBean>()

    class SongInfoBean {
        /**
         * lrclink : http://qukufile2.qianqian.com/data2/lrc/b02df89/549929948.lrc
         * album_id : 275347355
         * author : 赵雷
         * song_title : 成都
         * song_id : 274841326
         * pic_s500 : http://qukufile2.qianqian.com/data2/pic/cd8dcc4f4355/275347355.jpg
         * title : 成都
         * artist_id : 13874366
         */

        lateinit var lrclink: String
        lateinit var title: String
        lateinit var author: String
        var song_id: Int = 0
        var album_id: Int = 0

        var pic_s500: String? = null
    }
}
