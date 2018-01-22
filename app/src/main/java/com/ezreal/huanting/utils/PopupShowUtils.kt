package com.ezreal.huanting.utils

import android.app.Activity

/**
 * PopupWindow 显示时让背景变暗
 * Created by wudeng on 2018/1/22.
 */
object PopupShowUtils {

    fun lightOn(activity: Activity) {
        try {
            val attributes = activity.window?.attributes
            attributes?.alpha = 1.0f
            activity.window?.attributes = attributes
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun lightOff(activity: Activity) {
        try {
            val attributes = activity.window?.attributes
            attributes?.alpha = 0.6f
            activity.window?.attributes = attributes
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}