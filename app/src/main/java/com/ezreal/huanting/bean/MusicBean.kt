package com.ezreal.huanting.bean

import android.net.Uri
import com.ezreal.huanting.utils.Constant
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

/**
 * 歌曲 bean 基类
 * Created by wudeng on 2018/1/26.
 */
open class MusicBean :RealmObject() {

    @PrimaryKey
    var musicId:Long = 0

    lateinit var musicTitle:String
    lateinit var artistName:String
    lateinit var albumName:String

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
    lateinit var filePath:String
    lateinit var lrcPath:String
    lateinit var albumUri:String

    /**
     * 在线音乐独有
     */
    lateinit var bigPic:String
    lateinit var tingUid:String
    lateinit var lrcLink:String
    lateinit var fileLink:String

    /**
     * 播放辅助变量，不会存入数据库
     */
    @Ignore
    var playStatus:Int = Constant.PLAY_STATUS_NORMAL

    @Ignore
    var playFromListId:Long = 0
}