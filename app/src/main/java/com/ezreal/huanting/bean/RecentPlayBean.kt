package com.ezreal.huanting.bean

import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * Created by wudeng on 2018/1/2.
 */

open class RecentPlayBean :RealmObject(){
    @PrimaryKey
    var musicId: Long = -1L

    var lastPlayTime: Long = 0
}