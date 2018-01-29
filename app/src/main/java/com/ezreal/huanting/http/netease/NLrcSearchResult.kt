package com.ezreal.huanting.http.netease

/**
 * Created by wudeng on 2018/1/29.
 */

class NLrcSearchResult {

    var lrc: LrcBean? = null
    var code: Int = 0

    class LrcBean {
        lateinit var lyric: String
    }
}
