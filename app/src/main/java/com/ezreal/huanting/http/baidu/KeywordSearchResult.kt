package com.ezreal.huanting.http.baidu

/**
 * 在线音乐 关键词搜索返回 bean
 * Created by wudeng on 2018/1/24.
 */

class KeywordSearchResult {

    var error_code: Int = 0
    var order: String? = null
    var song: List<SongBean>? = null

    class SongBean {
        lateinit var weight: String
        lateinit var songname: String
        lateinit var songid: String
        lateinit var artistname: String
    }
}
