package com.ezreal.huanting.activity

import android.os.Bundle
import com.ezreal.huanting.R
import com.ezreal.huanting.present.BasePresentImpl
import com.ezreal.huanting.view.BaseViewImpl

/**
 * 歌单信息编辑
 * Created by wudeng on 2018/2/2.
 */

class GedanEditActivity :BaseActivity<BaseViewImpl,BasePresentImpl>(), BaseViewImpl {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_info_edit)
    }


    override fun createPresent(): BasePresentImpl {
        return BasePresentImpl(this)
    }
}