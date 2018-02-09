package com.ezreal.huanting.utils

import android.os.Environment
import java.io.File

/**
 * 常量
 * Created by wudeng on 2017/11/28.
 */
object Constant {

    /**
     * APP 文件夹
     */
    val APP_MAIN_DIR_PATH = Environment.getExternalStorageDirectory()
            .absolutePath + File.separator + "Hunting"
    val APP_MUSIC_PATH = APP_MAIN_DIR_PATH + File.separator + "Music"
    val APP_LRC_PATH = APP_MAIN_DIR_PATH + File.separator + "Lrc"
    val APP_IMAGE_PATH = APP_MAIN_DIR_PATH + File.separator + "Image"

    /**
     * 播放模式
     */
    val OPTION_TABLE = "TABLE_OPTION"
    val OPTION_PLAY_MODE = "OPTION_PLAY_MODE"
    val PLAY_MODE_LIST_RECYCLE = 0
    val PLAY_MODE_SINGLE_RECYCLE = 1
    val PLAY_MODE_RANDOM = 2

    /**
     * 播放状态
     */
    val PLAY_STATUS_PLAYING = 1
    val PLAY_STATUS_PAUSE = 2
    val PLAY_STATUS_NORMAL = 3

    /**
     * 默认歌单ID
     */

    val TEMP_MUSIC_LIST_ID = 0L         //TEMP

    val NEW_MUSIC_LIST_ID = 1L          //新歌榜
    val HOT_MUSIC_LIST_ID = 2L          //热歌榜

    val KTV_MUSIC_LIST_ID = 6L          //KTV排行榜
    val BILL_BOARD_LIST_ID = 8L         //BILL_BOARD
    val ROCL_MUSIC_LIST_ID = 11L        //摇滚排行榜
    val CHINESE_MUSIC_LIST_ID = 20L     //华语排行榜
    val ENGLISH_MUSIC_LIST_ID = 21L     //欧美排行榜
    val CLASSICAL_MUSIC_LIST_ID = 22L   //经典排行榜
    val MOVIE_MUSIC_LIST_ID = 24L       //影视排行榜
    val NETWORK_MUSIC_LIST_ID = 25L     //网络排行榜

    val RECOM_MUSIC_LIST_ID = 30L       //推荐榜

    val LOCAL_MUSIC_LIST_ID = 40L        //本地音乐
    val RECENT_MUSIC_LIST_ID = 41L       //最近播放
    val DOWNLOAD_MUSIC_LIST_ID = 42L     //下载的音乐


    val MY_LOVE_MUSIC_LIST_ID = 50L      //我喜欢的音乐


    /**
     * 网络下载文件类型
     */
    val DOWNLOAD_TYPE_LRC = 0x10        // 歌词下载
    val DOWLOAD_TYPE_PIC = 0x11         // 封面下载
    val DOWLOAD_TYPE_MUSIC = 0x12       // 歌曲下载

    /**
     * 用户信息
     */
    val PRE_USER_TABLE = "USER_TABLE"
    val PRE_USER_NAME = "USER_NAME"
    val PRE_USER_PASS = "USER_PASS"
    val PRE_USER_ID = "USER_ID"

    /**
     * APP 设置
     */
    val PRE_APP_OPTION_TABLE = "APP_OPTION_TABLE"
    val PRE_APP_DEFAULT_LIST_CREATED = "APP_DEFAULT_LIST_CREATED"
    val PRE_APP_OPTION_INIT_SYNC = "APP_OPTION_INIT_SYNC"

    /**
     * 推荐歌曲更新时间
     */
    val PRE_LOAD_RECOM_TIME = "LOAD_RECOM_TIME"
}