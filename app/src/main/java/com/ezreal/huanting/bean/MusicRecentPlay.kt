package com.ezreal.huanting.bean

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by wudeng on 2018/1/2.
 */

open class MusicRecentPlay :RealmObject(){
    @PrimaryKey
    var musicId:Long ?= null

    var lastPlayTime:Long ?= null
}