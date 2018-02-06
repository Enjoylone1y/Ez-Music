package com.ezreal.huanting.http.result

/**
 * 歌手信息搜索
 * Created by wudeng on 2018/1/26.
 */

class ArtistSearchResult {

    lateinit var ting_uid: String
    lateinit var name: String
    lateinit var artist_id: String
    lateinit var avatar_big: String

    var albums_total: String? = null
    var constellation: String? = null
    var intro: String? = null
    var country: String? = null
    var songs_total: String? = null
    var birth: String? = null
    var url: String? = null
    var company: String? = null
}
