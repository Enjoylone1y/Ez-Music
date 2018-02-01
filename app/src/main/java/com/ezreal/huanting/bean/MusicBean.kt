package com.ezreal.huanting.bean

import com.ezreal.huanting.utils.Constant
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

/**
 * 歌曲 bean 基类
 * Created by wudeng on 2018/1/26.
 */
open class MusicBean :RealmObject() {

    /**
     * 必要属性
     */

    @PrimaryKey
    var musicId:Long = 0

    var musicTitle:String = ""
    var artistName:String = ""
    var albumName:String = ""

    /**
     * 是否为在线音乐
     */
    var isOnline:Boolean = false

    var artistId:Long = 0
    var albumId:Long = 0
    var duration:Long = 0
    var fileSize:Long = 0

    var playCount:Long = 0
    var lastPlayTime:Long = 0

    /**
     * 本地音乐独有
     */
    var filePath:String = ""
    var lrcPath:String = ""
    var albumUri:String = ""


    // 在线播放歌曲封面
    var bigPic:String = ""
    // 百度音乐歌手 ID
    var tingUid:String = ""
    // 在线播放歌词链接
    var lrcLink:String = ""
    // 在线播放音乐链接
    var fileLink:String = ""

    // 歌词下载保存路径
    var lrcLocal:String = ""
    // 封面下载保存路径
    var picLocal:String = ""
    // 歌曲下载路径
    var musicLocal:String = ""
    // 缓冲路径
    var cachePath:String = ""


    /**
     * 播放辅助变量，不会存入数据库
     */
    @Ignore
    var playStatus:Int = Constant.PLAY_STATUS_NORMAL

    @Ignore
    var playFromListId:Long = 0
}