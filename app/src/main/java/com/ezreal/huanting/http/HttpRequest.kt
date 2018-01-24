package com.ezreal.huanting.http

import android.text.TextUtils
import com.lzy.okgo.OkGo
import com.lzy.okgo.model.Response

/**
 * Created by wudeng on 2018/1/23.
 */

object HttpRequest {

    private val BASE_URL = "http://tingapi.ting.baidu.com/v1/restserver/ting"
    private val METHOD_GET_MUSIC_LIST = "baidu.ting.billboard.billList"
    private val METHOD_DOWNLOAD_MUSIC = "baidu.ting.song.play"
    private val METHOD_ARTIST_INFO = "baidu.ting.artist.getInfo"
    private val METHOD_SEARCH_MUSIC = "baidu.ting.search.catalogSug"
    private val METHOD_LRC = "baidu.ting.song.lry"
    private val PARAM_METHOD = "method"
    private val PARAM_TYPE = "type"
    private val PARAM_SIZE = "size"
    private val PARAM_OFFSET = "offset"
    private val PARAM_SONG_ID = "songid"
    private val PARAM_TING_UID = "tinguid"
    private val PARAM_QUERY = "query"

    fun searchMusicByKey(keyWord: String, listener: OnMusicSearchListener) {
        OkGo.get<MusicRearchResult>(BASE_URL)
                .params(PARAM_METHOD, METHOD_SEARCH_MUSIC)
                .params(PARAM_QUERY, keyWord)
                .execute(object : JsonCallBack<MusicRearchResult>() {
                    override fun onSuccess(response: Response<MusicRearchResult>?) {
                        if (response?.body() != null && response.body()?.song != null) {
                            listener.onResult(0, response.body().song?.get(0), "onSuccess")
                        } else {
                            listener.onResult(-1, null, "未找到歌曲")
                        }
                    }

                    override fun onError(response: Response<MusicRearchResult>?) {
                        super.onError(response)
                        listener.onResult(-1, null, response?.exception?.message)
                    }
                })
    }

    fun searchLrcById(songId: String, listener: OnLrcSearchListener) {
        OkGo.get<LrcSearchResult>(BASE_URL)
                .params(PARAM_METHOD, METHOD_LRC)
                .params(PARAM_SONG_ID, songId)
                .execute(object : JsonCallBack<LrcSearchResult>() {
                    override fun onSuccess(response: Response<LrcSearchResult>?) {
                        if (response?.body() != null
                                && !TextUtils.isEmpty(response.body().lrcContent)) {
                            listener.onResult(0, response.body().lrcContent, "onSuccess")
                        } else {
                            listener.onResult(-1, null, "未找到歌词")
                        }
                    }

                    override fun onError(response: Response<LrcSearchResult>?) {
                        super.onError(response)
                        listener.onResult(-1, null, response?.exception?.message)
                    }
                })
    }


    interface OnMusicSearchListener {
        fun onResult(code: Int, result: MusicRearchResult.SongBean?, message: String?)
    }

    interface OnLrcSearchListener {
        fun onResult(code: Int, lrcString: String?, message: String?)
    }

}