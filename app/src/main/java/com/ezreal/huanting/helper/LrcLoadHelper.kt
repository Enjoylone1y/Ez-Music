package com.ezreal.huanting.helper

import android.text.TextUtils
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.http.HttpRequest
import com.ezreal.huanting.utils.Constant
import java.io.File
import java.io.FileWriter
import java.io.IOException

/**
 * 歌词装载工具类
 * Created by wudeng on 2018/1/24.
 */
object LrcLoadHelper {

    fun loadLrcFile(musicBean: MusicBean, listener: OnLoadLrcListener) {
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
            HttpRequest.searchLrcByKeyword(keyWord, object : HttpRequest.OnLrcSearchListener {
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
        } catch (e: Exception) {
            e.printStackTrace()
            listener.onFailed()
        }
    }

    private fun getLrcOnlineById(songId: String, path: String, listener: OnLoadLrcListener) {
        HttpRequest.searchLrcById(songId, object : HttpRequest.OnLrcSearchListener {
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
        } catch (e: IOException) {
            return false
        }
        return false
    }


    interface OnLoadLrcListener {
        fun onSuccess(lrcPath: String)
        fun onLoadOnline()
        fun onFailed()
    }
}



