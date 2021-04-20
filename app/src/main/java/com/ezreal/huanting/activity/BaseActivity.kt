package com.ezreal.huanting.activity

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import com.ezreal.huanting.present.BasePresent
import com.ezreal.huanting.view.BaseView

/**
 * BaseActivity
 * Created by wudeng on 2018/2/2.
 */

abstract class BaseActivity<V:BaseView,P:BasePresent<V>> : AppCompatActivity(),BaseView {

    var present:P? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        present = createPresent()
        present?.onCreate()
    }


    abstract fun createPresent():P

    override fun getContext(): Context {
        return this
    }
}