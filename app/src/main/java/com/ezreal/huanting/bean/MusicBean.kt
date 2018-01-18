package com.ezreal.huanting.bean
import com.ezreal.huanting.utils.Constant
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey

/**
 * 单曲 bean
 * Created by wudeng on 2017/12/29.
 */

open class MusicBean : RealmObject() {
    @PrimaryKey
    var musicId: Long? = null

    var musicTitle: String? = null
    var artist: String? = null
    var dataPath:String ?= null
    var album: String? = null
    var albumId: Long? = null
    var albumUri: String? = null
    var size: Long? = null
    var lrcPath:String ?= null
    var duration: Int? = null

    /**
     * 歌曲播放状态，不保存入数据库中
     */
    @Ignore
    var status:Int =  Constant.PLAY_STATUS_NORMAL
}
