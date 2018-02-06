package com.ezreal.huanting.http.result

import java.util.ArrayList

/**
 * 歌单标签列表返回
 * Created by wudeng on 2018/2/6.
 */

class CategoryListResult {

    var content = ArrayList<Category>()

    class Category {
        /**
         * tags : [{"type":"zhuanti","tag":"音乐专题"}]
         * title : 精选推荐
         * num : 1
         */

        lateinit var title: String
        var num: Int = 0
        var tags = ArrayList<TagBean>()

        class TagBean {
            /**
             * type : zhuanti
             * tag : 音乐专题
             */
            lateinit var type: String
            lateinit var tag: String
        }
    }
}
