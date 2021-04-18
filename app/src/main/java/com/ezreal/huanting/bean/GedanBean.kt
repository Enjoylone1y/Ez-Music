package com.ezreal.huanting.bean

//import io.realm.RealmList
//import io.realm.RealmObject
//import io.realm.annotations.PrimaryKey

/**
 * 歌单 bean
 * Created by wudeng on 2017/12/29.
 */

open class GedanBean {

    //    @PrimaryKey
    var listId:Long = -1

    lateinit var listName:String
    lateinit var creatorName:String
    var creatorId:Long = -1L
    var createTime:Long = 0

    var sortFieldName:String ?= null
    var coverPathByEd:String ?= null

    var musicList:List<MusicBean> = ArrayList()

    var isOnline = false

    /**
     * 网络歌单数据
     */
    lateinit var title: String
    lateinit var pic: String
    lateinit var listenum: String
    lateinit var collectnum: String
    lateinit var tag: String
    lateinit var desc: String
}