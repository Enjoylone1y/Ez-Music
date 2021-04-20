package com.ezreal.huanting.activity

import android.os.Bundle
import com.ezreal.huanting.R
import com.ezreal.huanting.present.BasePresentImpl
import com.ezreal.huanting.view.BaseViewImpl

/**
 * 歌曲搜索页
 * Created by wudeng on 2018/2/2.
 */

class LocalSearchActivity :BaseActivity<BaseViewImpl,BasePresentImpl>(), BaseViewImpl {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_music_search)

    }


    override fun createPresent(): BasePresentImpl {
        return BasePresentImpl(this)
    }

}