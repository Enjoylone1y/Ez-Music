package com.ezreal.huanting.helper

import android.text.TextUtils
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.http.baidu.BaiduMusicApi
import com.ezreal.huanting.http.baidu.KeywordSearchResult
import com.ezreal.huanting.http.netease.NKeySearchResult
import com.ezreal.huanting.http.netease.NLrcSearchResult
import com.ezreal.huanting.http.netease.NeteaseMusicApi
import com.ezreal.huanting.utils.Constant
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * 歌词装载工具类
 * Created by wudeng on 2018/1/24.
 */
object LrcLoadHelper {

    fun loadLrcFileBaidu(musicBean: MusicBean, listener: OnLoadLrcListener) {
        try {
            val name =  if (musicBean.musicTitle.length < 50)  musicBean.musicTitle
                else  musicBean.musicTitle.substring(0, 50)
            val artist = if (musicBean.artistName.length < 50)  musicBean.artistName
                 else  musicBean.artistName.substring(0, 50)
            val path = Constant.APP_LRC_PATH + File.separator + name + "_" + artist + ".lrc"
            if (File(path).exists()) {
                listener.onSuccess(path)
                return
            }
            listener.onLoadOnline()
            val keyWord = musicBean.musicTitle + " " + musicBean.artistName
            BaiduMusicApi.searchMusicByKey(keyWord, object : BaiduMusicApi.OnKeywordSearchListener {
                override fun onResult(code: Int, result: KeywordSearchResult.SongBean?, message: String?) {
                    if (code == 0 && result != null) {
                        getLrcOnlineFromBaidu(result.songid, path, listener)
                    } else {
                        listener.onFailed()
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            listener.onFailed()
        }
    }

    fun loadLrcFromNetease(musicBean: MusicBean, listener: OnLoadLrcListener) {
        try {
            val name = if (musicBean.musicTitle.length < 50) musicBean.musicTitle
            else musicBean.musicTitle.substring(0, 50)
            val artist = if (musicBean.artistName.length < 50) musicBean.artistName
            else musicBean.artistName.substring(0, 50)
            val path = Constant.APP_LRC_PATH + File.separator + name + "_" + artist + ".lrc"
            listener.onLoadOnline()
            val keyWord = musicBean.musicTitle + " " + musicBean.artistName
            NeteaseMusicApi.searchMusicByKeyWord(keyWord, 1, 0, object :
                    NeteaseMusicApi.OnKeywordSearchListener {
                override fun onResult(code: Int, result: NKeySearchResult?, message: String?) {
                    if (code == 0 && result?.result != null && result.result?.songs?.size!! > 0){
                        getLrcOnlineByFromNetease(result.result?.songs?.get(0)?.id!!,path,listener)
                    }else{
                        listener.onFailed()
                    }
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            listener.onFailed()
        }
    }


    private fun getLrcOnlineFromBaidu(songId: String, path: String, listener: OnLoadLrcListener) {
        BaiduMusicApi.searchLrcById(songId, object : BaiduMusicApi.OnLrcSearchListener {
            override fun onResult(code: Int, lrcString: String?, message: String?) {
                if (code != 0 || TextUtils.isEmpty(lrcString)) {
                    listener.onFailed()
                    return
                }
                if (!saveLrc(path, lrcString!!)) {
                    listener.onFailed()
                } else {
                    listener.onSuccess(path)
                }
            }
        })
    }

    private fun getLrcOnlineByFromNetease(songId: Long, path: String, listener: OnLoadLrcListener) {
        NeteaseMusicApi.searchLrcById(songId, object : NeteaseMusicApi.OnLrcSearchListener {
            override fun onResult(code: Int, result: NLrcSearchResult?, message: String?) {
                if (code != 0 || result == null || result.lrc == null) {
                    listener.onFailed()
                    return
                }
                if (!saveLrc(path, result.lrc?.lyric!!)) {
                    listener.onFailed()
                } else {
                    listener.onSuccess(path)
                }
            }
        })
    }


    private fun saveLrc(path: String, lrcString: String): Boolean {
        try {
            val lrcFile = File(path)
            if (!lrcFile.createNewFile()) {
                return false
            }
            val fileWriter = FileWriter(lrcFile)
            fileWriter.write(lrcString)
            fileWriter.flush()
            fileWriter.close()
            return true
        } catch (e: IOException) {
            return false
        }
    }


    interface OnLoadLrcListener {
        fun onSuccess(lrcPath: String)
        fun onLoadOnline()
        fun onFailed()
    }
}



