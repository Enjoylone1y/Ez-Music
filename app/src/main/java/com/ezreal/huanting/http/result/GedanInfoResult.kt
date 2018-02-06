package com.ezreal.huanting.http.result

import com.google.gson.annotations.SerializedName

/**
 * 歌单信息数据
 * Created by wudeng on 2018/2/6.
 */

class GedanInfoResult {

    /**
     * listid : 4368
     * title : 聚散终有时 青春不散场
     * pic_500 : http://hiphotos.qianqian.com/ting/pic/item/b3b7d0a206acaf2edd98a7.jpg
     * listenum : 491683
     * collectnum : 2941
     * tag : 校园,民谣,流行
     * desc : 聚也匆匆，散也匆匆，人生哪有几回的再度相逢。风轻轻地拂过，就会落红满地，也许会有残花空留枝头
     */

    lateinit var listid: String
    lateinit var title: String

    @SerializedName("pic_500")
    lateinit var pic: String

    lateinit var listenum: String
    lateinit var collectnum: String
    lateinit var tag: String
    lateinit var desc: String

    var content =  ArrayList<ContentBean>()

    class ContentBean {
        var song_id: String? = null
    }
}
