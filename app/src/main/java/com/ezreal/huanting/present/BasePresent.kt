package com.ezreal.huanting.present

import com.ezreal.huanting.view.BaseView

interface BasePresent<V:BaseView> {

    fun onCreate()

    fun onResume()

    fun onPause()

    fun onDestroy()
}