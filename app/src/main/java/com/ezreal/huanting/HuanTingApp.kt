package com.ezreal.huanting

import android.app.Application
import cn.hotapk.fastandrutils.utils.FUtils
import com.lzy.okgo.OkGo


/**
 * Created by wudeng on 2017/11/20.
 */

class HuanTingApp : Application(){
    override fun onCreate() {
        super.onCreate()
        // 初始化工具包
        FUtils.init(this)

        //Http 访问框架初始化
        OkGo.getInstance().init(this)
    }
}
