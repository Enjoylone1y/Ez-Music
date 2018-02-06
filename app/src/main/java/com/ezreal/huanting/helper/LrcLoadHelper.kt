package com.ezreal.huanting.helper

import android.text.TextUtils
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.http.BaiduMusicApi
import com.ezreal.huanting.http.result.KeywordSearchResult
import com.ezreal.huanting.utils.Constant
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * 本地歌曲歌词搜索工具类
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



