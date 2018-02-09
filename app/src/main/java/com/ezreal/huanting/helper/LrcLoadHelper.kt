package com.ezreal.huanting.helper

import android.text.TextUtils
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.http.BaiduMusicApi
import com.ezreal.huanting.http.FileCallBack
import com.ezreal.huanting.http.result.LrcPicSearchResult.SongInfoBean
import com.ezreal.huanting.utils.Constant
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response
import io.realm.Realm
import java.io.File

/**
 * 本地歌曲歌词搜索工具类
 * Created by wudeng on 2018/1/24.
 */
object LrcLoadHelper {

    fun loadLrcFileBaidu(musicBean: MusicBean, listener: OnLoadLrcListener) {
        try {
            BaiduMusicApi.searchLrcPicByKey(musicBean.musicTitle,musicBean.artistName,object :
                    BaiduMusicApi.OnLrcPicSearchListener{
                override fun onResult(code: Int, result: List<SongInfoBean>?, message: String?) {
                    if (code == 0 && result != null && result.isNotEmpty()){
                        val match = result.firstOrNull { musicBean.musicTitle == it.title
                                    && musicBean.artistName == it.author
                                    && !TextUtils.isEmpty(it.lrclink)
                        }
                        if (match != null){
                            loadLrc(musicBean,match,listener)
                        }else{
                            listener.onFailed()
                        }
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

    private fun loadLrc(musicBean: MusicBean,song:SongInfoBean, listener: OnLoadLrcListener){
        val url = song.lrclink
        val path = Constant.APP_LRC_PATH + File.separator + song.song_id + ".lrc"
        OkGo.get<File>(url).execute(object :FileCallBack(path){
            override fun onSuccess(response: Response<File>?) {
                if (response?.body() != null){
                    listener.onSuccess(path)
                    val realm = Realm.getDefaultInstance()
                    realm.beginTransaction()
                    musicBean.lrcPath = path
                    realm.commitTransaction()

                }else{
                    listener.onFailed()
                }
            }
        })
    }


    interface OnLoadLrcListener {
        fun onSuccess(lrcPath: String)
        fun onFailed()
    }
}



