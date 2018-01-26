package com.ezreal.huanting.http

import android.text.TextUtils
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.AbsCallback
import com.lzy.okgo.model.Response
import java.lang.reflect.ParameterizedType

/**
 * 网络歌曲 网络请求类
 * Created by wudeng on 2018/1/23.
 */

object HttpRequest {

    private val BASE_URL = "http://tingapi.ting.baidu.com/v1/restserver/ting"
    private val METHOD_GET_MUSIC_LIST = "baidu.ting.billboard.billList"
    private val METHOD_DOWNLOAD_MUSIC = "baidu.ting.song.play"
    private val METHOD_ARTIST_INFO = "baidu.ting.artist.getInfo"
    private val METHOD_SEARCH_MUSIC = "baidu.ting.search.catalogSug"
    private val METHOD_LRC = "baidu.ting.song.lry"
    private val METHOD_RECOM = "baidu.ting.song.getRecommandSongList"
    private val METHOD_PLAY = "baidu.ting.song.play"
    private val PARAM_METHOD = "method"
    private val PARAM_TYPE = "type"
    private val PARAM_SIZE = "size"
    private val PARAM_OFFSET = "offset"
    private val PARAM_NUM = "num"
    private val PARAM_SONG_ID = "songid"
    private val PARAM_RECOM_SONG_ID = "song_id"
    private val PARAM_TING_UID = "tinguid"
    private val PARAM_QUERY = "query"

    //推荐列表
    //baidu.ting.song.getRecommandSongList  {song_id: id, num: 5 }

    //下载
    //baidu.ting.song.downWeb  {songid: id, bit:"24, 64, 128, 192, 256, 320, flac", _t: (new Date())}
    //songid: //歌曲id
    //bit: //码率
    //_t: //时间戳

    //获取歌手信息
    //baidu.ting.artist.getInfo  { tinguid: id }
    //tinguid: //歌手ting id

    //获取歌手歌曲列表
    //baidu.ting.artist.getSongList  { tinguid: id, limits:6, use_cluster:1, order:2}

    //播放
    //baidu.ting.song.play  {songid: id}
    //baidu.ting.song.playAAC  {songid: id}

    // baidu.ting.billboard.billList  {type:1,size:10, offset:0}
    // type: //1、新歌榜，2、热歌榜，6.KTV热歌榜,7.叱咤歌曲榜,8.Billboard, 11、摇滚榜，12、爵士，16、流行 ,
    // 18.Hito中文榜20.华语金曲榜,21、欧美金曲榜，22、经典老歌榜，23、情歌对唱榜，24、影视金曲榜，25、网络歌曲榜


    fun searchMusicByKey(keyWord: String, listener: OnKeywordSearchListener) {
        OkGo.get<KeywordSearchResult>(BASE_URL)
                .params(PARAM_METHOD, METHOD_SEARCH_MUSIC)
                .params(PARAM_QUERY, keyWord)
                .execute(object : JsonCallBack<KeywordSearchResult>() {
                    override fun onSuccess(response: Response<KeywordSearchResult>?) {
                        if (response?.body() != null && response.body()?.song != null) {
                            listener.onResult(0, response.body().song?.get(0), "onSuccess")
                        } else {
                            listener.onResult(-1, null, "未找到歌曲")
                        }
                    }

                    override fun onError(response: Response<KeywordSearchResult>?) {
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

    fun searchRecomMusic(musicId:String,num:Int,listener: OnRecomSearchListener){
        OkGo.get<RecomSearchResult>(BASE_URL)
                .params(PARAM_METHOD, METHOD_RECOM)
                .params(PARAM_RECOM_SONG_ID,musicId)
                .params(PARAM_NUM,num)
                .execute(object :JsonCallBack<RecomSearchResult>(){
                    override fun onSuccess(response: Response<RecomSearchResult>?) {
                        if (response?.body() != null
                                && response.body()?.result != null
                                && response.body().result?.list != null){
                            listener.onResult(0,response.body().result?.list,"onSuccess")
                        }else{
                            listener.onResult(-1, null, "搜索推荐歌曲失败")
                        }
                    }

                    override fun onError(response: Response<RecomSearchResult>?) {
                        super.onError(response)
                        listener.onResult(-1, null, response?.exception?.message)
                    }
                })
    }


    interface OnKeywordSearchListener {
        fun onResult(code: Int, result: KeywordSearchResult.SongBean?, message: String?)
    }

    interface OnLrcSearchListener {
        fun onResult(code: Int, lrcString: String?, message: String?)
    }

    interface OnRecomSearchListener{
        fun onResult(code: Int,result: List<RecomSongBean>?,message:String?)
    }

    abstract class JsonCallBack<T> : AbsCallback<T>() {
        @Throws(Throwable::class)
        override fun convertResponse(response: okhttp3.Response): T? {
            val body = response.body() ?: return null
            val reader = JsonReader(body.charStream())
            val type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
            return Gson().fromJson(reader,type)
        }
    }
}