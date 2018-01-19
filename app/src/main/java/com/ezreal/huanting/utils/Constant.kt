package com.ezreal.huanting.utils

import android.os.Environment
import java.io.File

/**
 * Created by wudeng on 2017/11/28.
 */
object Constant {

    val APP_MAIN_DIR_PATH = Environment.getExternalStorageDirectory()
            .absolutePath + File.separator + "Hunting"
    val APP_MUSIC_PATH = APP_MAIN_DIR_PATH + File.separator + "Music"
    val APP_LRC_PATH = APP_MAIN_DIR_PATH + File.separator + "Lrc"
    val APP_IMAGE_PATH = APP_MAIN_DIR_PATH + File.separator + "Image"

    val OPTION_TABLE = "TABLE_OPTION"
    val OPTION_PLAY_MODE = "OPTION_PLAY_MODE"
    val PLAY_MODE_LIST_RECYCLE = 1
    val PLAY_MODE_SINGLE_RECYCLE = 2
    val PLAY_MODE_RANDOM = 3

    val PLAY_STATUS_PLAYING = 1
    val PLAY_STATUS_PAUSE = 2
    val PLAY_STATUS_NORMAL = 3

    val LOCAL_MUSIC_LIST_ID = 0x1000L
    val RECENT_MUSIC_LIST_ID = 0x1000L + 1
    val TEMP_MUSIL_LIST_ID = 0x1000L + 2
    val MY_LOVE_MUSIC_LIST_ID = 0x1000L + 3

    val PRE_USER_TABLE = "USER_TABLE"
    val PRE_USER_NAME = "USER_NAME"
    val PRE_USER_PASS = "USER_PASS"
    val PRE_USER_ID = "USER_ID"
    val PRE_APP_OPTION_TABLE = "APP_OPTION_TABLE"
    val PRE_APP_OPTION_LOVE_CREATED = "APP_OPTION_LOVE_CREATED"

    // 歌曲搜索
    val url1 = "http://s.music.163.com/search/get/?type=1&s=%22%E8%83%8C%E5%8F%9B%22&limit=5&offset=0"
    // 歌词
    val url2 = "http://music.163.com/api/song/lyric?os=%22pc%22&id=34775141&lv=-1&kv=-1&tv=-1"
    // 歌曲信息
    val url3 = "http://music.163.com/api/song/detail/?id=471542265&ids=[471542265]"
}