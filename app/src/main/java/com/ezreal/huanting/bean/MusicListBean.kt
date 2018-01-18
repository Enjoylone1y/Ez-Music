package com.ezreal.huanting.bean

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * 歌单 bean
 * Created by wudeng on 2017/12/29.
 */
open class MusicListBean :RealmObject(){

    @PrimaryKey
    var listId:Long = -1

    var listName:String ?= null
    var creatorId:Long ?= null
    var creatorName:String ?= null
    var createTime:Long ?= null
    var sortFieldName:String ?= null
    var musicList:RealmList<MusicBean> = RealmList()

}