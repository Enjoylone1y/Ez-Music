package com.ezreal.huanting.http.result

import java.util.ArrayList

/**
 * Created by wudeng on 2018/2/5.
 */

class UrlLoadResult {

    var pic: List<PicBean> = ArrayList()

    class PicBean {
        /**
         * code : http://music.baidu.com/h5pc/spec_detail?id=940&columnid=96
         * randpic : http://business.cdn.qianqian.com/qianqian/pic/bb49233588049670f.jpg
         */

        lateinit var code: String
        lateinit var randpic: String
    }
}
