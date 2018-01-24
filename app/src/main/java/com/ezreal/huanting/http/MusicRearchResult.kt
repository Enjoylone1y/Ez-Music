package com.ezreal.huanting.http

/**
 * Created by wudeng on 2018/1/24.
 */

class MusicRearchResult {

    var error_code: Int = 0
    var order: String? = null
    var song: List<SongBean>? = null

    class SongBean {
        /**
         * weight : 12041099
         * songname : 演员
         * resource_type : 0
         * songid : 242078437
         * has_mv : 0
         * yyr_artist : 0
         * resource_type_ext : 0
         * artistname : 薛之谦
         * info :
         * resource_provider : 1
         */

        var weight: String? = null
        var songname: String? = null
        var resource_type: String? = null
        var songid: String? = null
        var has_mv: String? = null
        var yyr_artist: String? = null
        var resource_type_ext: String? = null
        var artistname: String? = null
        var info: String? = null
        var resource_provider: String? = null

    }
}
