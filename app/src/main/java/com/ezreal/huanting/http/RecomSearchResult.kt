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
}
