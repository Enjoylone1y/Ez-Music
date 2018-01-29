package com.ezreal.huanting.http.netease

/**
 * Created by wudeng on 2018/1/29.
 */

class NKeySearchResult {

    var result: ResultBean? = null
    var code: Int = 0

    class ResultBean {

        var songCount: Int = 0
        var songs: List<SongsBean>? = null

        class SongsBean {
            /**
             * id : 32507038
             * name : 演员
             * audio : http://m2.music.126.net/5s4gyKkcFNBHYimKcUWlPQ==/3234763212164647.mp3
             */
            var id: Long = -1L
            lateinit var name:String
            var audio: String? = null
        }
    }
}
