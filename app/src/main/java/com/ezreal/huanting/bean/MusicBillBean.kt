package com.ezreal.huanting.bean

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey

/**
 * 歌单 bean
 * Created by wudeng on 2017/12/29.
 */

open class MusicBillBean :RealmObject(){

    @PrimaryKey
    var listId:Long = -1

    lateinit var listName:String
    var creatorId:Long = -1L
    lateinit var creatorName:String
    var createTime:Long = 0
    lateinit var sortFieldName:String
    lateinit var coverPathByEd:String
    var musicList:RealmList<MusicBean> = RealmList()

}