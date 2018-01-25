package com.ezreal.huanting.helper

import android.text.TextUtils
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.http.HttpRequest
import com.ezreal.huanting.http.MusicRearchResult
import com.ezreal.huanting.utils.Constant
import java.io.File
import java.io.FileWriter

/**
 * 歌词装载工具类
 * Created by wudeng on 2018/1/24.
 */
object LrcLoadHelper {

    fun loadLrcFile(musicBean: MusicBean, listener: OnLoadLrcListener) {
        try {
            val name =  if (musicBean.musicTitle?.length!! < 50)  musicBean.musicTitle
                else  musicBean.musicTitle?.substring(0, 50)
            val artist = if (musicBean.artist?.length!! < 50)  musicBean.artist
                 else  musicBean.artist?.substring(0, 50)
            val path = Constant.APP_LRC_PATH + File.separator + name + "_" + artist + ".lrc"
            if (File(path).exists()) {
                listener.onSuccess(File(path))
                return
            }

            listener.onLoadOnline()
            val keyWord = musicBean.musicTitle + " " + musicBean.artist
            // 搜索歌曲 ID
            HttpRequest.searchMusicByKey(keyWord, object : HttpRequest.OnMusicSearchListener {
                override fun onResult(code: Int, result: MusicRearchResult.SongBean?, message: String?) {
                    if (code != 0 || TextUtils.isEmpty(result?.songid)) {
                        listener.onFailed()
                        return
                    }
                    // 用 ID 去搜索歌词
                    getLrcFromOnline(result?.songid!!,path,listener)
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            listener.onFailed()
        }
    }

    private fun getLrcFromOnline(songId: String, path: String, listener: OnLoadLrcListener) {
        HttpRequest.searchLrcById(songId, object : HttpRequest.OnLrcSearchListener {
            override fun onResult(code: Int, lrcString: String?, message: String?) {
                if (code != 0 || TextUtils.isEmpty(lrcString)) {
                    listener.onFailed()
                    return
                }
                val lrcFile = File(path)
                if (!lrcFile.createNewFile()){
                    listener.onFailed()
                    return
                }
                val fileWriter = FileWriter(lrcFile)
                fileWriter.write(lrcString)
                fileWriter.flush()
                fileWriter.close()
                listener.onSuccess(lrcFile)
            }
        })
    }


    interface OnLoadLrcListener {
        fun onSuccess(lrcFile: File)
        fun onLoadOnline()
        fun onFailed()
    }
}



