package com.ezreal.huanting.bean

import com.ezreal.huanting.http.result.RankBillSearchResult

/**
 *
 * 音乐榜单
 *
 * 6.KTV热歌榜,8.Billboard, 11,摇滚,20.华语金曲榜,21、欧美金曲榜，
 * 22、经典老歌榜，23、情歌对唱榜，24、影视金曲榜，25、网络歌曲榜
 *
 * Created by wudeng on 2018/1/26.
 */
class RankBillBean {

    /** 榜单 ID*/
    var billId: Int = -1
    /** 榜单 类型*/
    var billType:Int = -1
    /** 更新时间 */
    lateinit var update:String


    /** 榜单名称*/
    var billName: String ?= null
    /** 榜单描述 */
    var billIntro: String ?= null
    /** 榜单封面 Url */
    var billCoverUrl: String ?= null

    /**第一首歌曲*/
    var musicFirst: RankBillSearchResult.BillSongBean ?= null
    /**第二首歌曲*/
    var musicSecond: RankBillSearchResult.BillSongBean ?= null
    /**第三首歌曲*/
    var musicThird: RankBillSearchResult.BillSongBean ?= null
}