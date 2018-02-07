package com.ezreal.huanting.http.result

import com.google.gson.annotations.SerializedName

/**
 * 网络歌单列表数据
 * Created by wudeng on 2018/2/6.
 */

class GedanListResult {

    var total: Int = 0

    var content =  ArrayList<GedanBean>()

    class GedanBean {
        /**
         * pic_300 : http://hiphotos.qianqian.com/acaf2edd98a7.jpg
         * title : 聚散终有时 青春不散场
         * listenum : 491683
         * listid : 4368
         */

        @SerializedName("pic_300")
        lateinit var pic: String

        lateinit var title: String
        lateinit var listenum: String
        lateinit var listid: String
        var desc:String ?= null
        var tag:String  ?= null
    }
}
