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

    fun getTypeName(type:Int):String{
        when(type){
            1-> return "新歌榜"
            2-> return "热歌榜"
            20 -> return "华语金曲榜"
            21 -> return "欧美金曲榜"
            22 -> return "经典老歌榜"
            23 -> return "情歌对唱榜"
            24 -> return "影视金曲榜"
            25 -> return "网络歌曲榜"
            6 -> return "KTV热歌榜"
            8 -> return "Billboard"
            7 -> return "叱咤歌曲榜"
            18 -> return "Hito 中文榜"
            11 -> return "摇滚榜"
            else -> return ""
        }
    }

}