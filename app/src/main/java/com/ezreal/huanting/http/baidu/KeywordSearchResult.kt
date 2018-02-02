package com.ezreal.huanting.http.baidu

/**
 * 关键词搜索返回结果 bean
 * Created by wudeng on 2018/1/24.
 */

class KeywordSearchResult {

    var song = ArrayList<SongBean>()
    var album = ArrayList<AlbumBean>()
    var artist = ArrayList<ArtistBean>()

    class SongBean {
        /**
         * songname : Intro 世界正在导入
         * songid : 568007934
         * artistname : 朱元冰
         */
        lateinit var songname: String
        lateinit var songid: String
        lateinit var artistname: String
    }

    class AlbumBean {
        /**
         * albumname : Introducing Orchestral Music for Children
         * artistname : Wilhelm Furtwängler
         * artistpic : http://qukufile2.qianqian.com/da3/280805066/280805066.jpg@s_0,w_40
         * albumid : 280805055
         */

        lateinit var albumname: String
        lateinit var artistname: String
        lateinit var albumid: String

        var artistpic: String? = null
    }

    class ArtistBean {
        /**
         * artistname : Intro
         * artistid : 87991281
         * artistpic : http://qukufile2.qianqian.com/52345505.jpg@s_0,w_48
         */

        lateinit var artistname: String
        lateinit var artistid: String
        var artistpic: String? = null
    }
}
