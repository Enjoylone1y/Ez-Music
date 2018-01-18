package com.ezreal.huanting.utils

/**
 * Created by wudeng on 2017/12/27.
 */
object ConvertUtils {

    fun getTimeWithProcess(process:Int):String{
        val pSecond =  process / 1000
        if (pSecond<10) return "00:0" + pSecond.toString()
        if (pSecond < 60) return "00:" + pSecond
        val minute = pSecond / 60
        val second = pSecond % 60
        if (second<10) return minute.toString() + ":0" + second.toString()
        return minute.toString() + ":" + second.toString()
    }


}