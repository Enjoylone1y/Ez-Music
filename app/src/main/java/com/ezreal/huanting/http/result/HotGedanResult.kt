package com.ezreal.huanting.http.result

import java.util.ArrayList

/**
 * 热门歌单列表
 * Created by wudeng on 2018/2/5.
 */

class HotGedanResult {

    lateinit var content: ContentBean

    class ContentBean {

        lateinit var title: String
        var list: List<ListBean> = ArrayList()

        class ListBean {
            /**
             * listid : 7259
             * pic : http://business.cdn.qianqian.com/qianqian/pic/bdf.jpg
             * listenum : 44133
             * collectnum : 340
             * title : 东海音乐节，去海边沙滩听音乐
             * tag : 流行,摇滚,民谣
             * type : gedan
             */

            lateinit var listid: String
            lateinit var pic: String
            lateinit var listenum: String
            lateinit var title: String
        }
    }
}
