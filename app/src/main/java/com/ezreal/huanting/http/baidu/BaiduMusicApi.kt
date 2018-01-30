package com.ezreal.huanting.http.baidu

import android.text.TextUtils
import android.util.Log
import com.ezreal.huanting.bean.MusicBean
import com.ezreal.huanting.bean.RankBillBean
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.AbsCallback
import com.lzy.okgo.model.Response
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.lang.reflect.ParameterizedType

/**
 * 网络歌曲 网络请求类  百度 api
 * Created by wudeng on 2018/1/23.
 */

object BaiduMusicApi {

    private val BASE_URL = "http://tingapi.ting.baidu.com/v1/restserver/ting"
    private val METHOD_GET_MUSIC_LIST = "baidu.ting.billboard.billList"
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

    /**
     * 根据关键字（音乐名 歌手名）搜索百度音乐库， 可以获取得到歌曲 ID
     */
    fun searchMusicByKey(keyWord: String, listener: OnKeywordSearchListener) {
        OkGo.get<KeywordSearchResult>(BASE_URL)
                .params(PARAM_METHOD, METHOD_SEARCH_MUSIC)
                .params(PARAM_QUERY, keyWord)
                .execute(object : JsonCallBack<KeywordSearchResult>() {
                    override fun onSuccess(response: Response<KeywordSearchResult>?) {
                        if (response?.body() != null && response.body()?.song != null) {
                            listener.onResult(0,response.body()?.song?.get(0),"success")
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

    /**
     * 根据音乐id，搜索歌曲歌词
     */
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


    /**
     * 根据参考的音乐 ID ,获取推荐音乐列表
     */
    fun searchRecomMusic(musicId: String, num: Int, listener: OnRecomSearchListener) {
        OkGo.get<RecomSearchResult>(BASE_URL)
                .params(PARAM_METHOD, METHOD_RECOM)
                .params(PARAM_RECOM_SONG_ID,musicId)
                .params(PARAM_NUM,num)
                .execute(object : JsonCallBack<RecomSearchResult>(){
                    override fun onSuccess(response: Response<RecomSearchResult>?) {
                        if (response?.body() != null
                                && response.body()?.result != null
                                && response.body().result?.list != null){
                            listener.onResult(0, response.body().result?.list, "success")
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


    /**
     * 根据音乐 ID 获取音乐完整信息，并且得到播放在线连接
     */
    fun searchMusicInfoById(musicId: String, listener: OnKeywordSearchListener) {
        OkGo.get<MusicSearchResult>(BASE_URL)
                .params(PARAM_METHOD, METHOD_PLAY)
                .params(PARAM_SONG_ID, musicId)
                .execute(object : JsonCallBack<MusicSearchResult>() {
                    override fun onSuccess(response: Response<MusicSearchResult>?) {

                    }
                })
    }

    /**
     * 根据歌手 id 搜索歌手信息
     */
    fun searchArtistInfoById(artistId: String, listener: OnArtistSearchListener) {
        OkGo.get<ArtistSearchResult>(BASE_URL)
                .params(PARAM_METHOD, METHOD_ARTIST_INFO)
                .params(PARAM_TING_UID, artistId)
                .execute(object : JsonCallBack<ArtistSearchResult>() {
                    override fun onSuccess(response: Response<ArtistSearchResult>?) {

                    }
                })
    }

    fun searchRankBill(type: Int, size: Int, offset: Int, listener: OnBillSearchListener) {
        OkGo.get<RankBillSearchResult>(BASE_URL)
                .params(PARAM_METHOD, METHOD_GET_MUSIC_LIST)
                .params(PARAM_TYPE, type)
                .params(PARAM_SIZE, size)
                .params(PARAM_OFFSET, offset)
                .execute(object : JsonCallBack<RankBillSearchResult>() {
                    override fun onSuccess(response: Response<RankBillSearchResult>?) {
                        if (response?.body() != null){
                            listener.onResult(0,response.body(),"success")
                        }
                    }
                })
    }

    fun searchBillList(types: List<Int>, size: Int, offset: Int, listener: OnBillListSearchListener) {
        val billList = ArrayList<RankBillBean>()
        Observable.create(ObservableOnSubscribe<RankBillSearchResult> {
            for (type in types) {
                OkGo.get<RankBillSearchResult>(BASE_URL)
                        .params(PARAM_METHOD, METHOD_GET_MUSIC_LIST)
                        .params(PARAM_TYPE, type)
                        .params(PARAM_SIZE, size)
                        .params(PARAM_OFFSET, offset)
                        .execute(object : JsonCallBack<RankBillSearchResult>() {
                            override fun onSuccess(response: Response<RankBillSearchResult>?) {
                                if (response?.body() != null) {
                                    it.onNext(response.body())
                                }
                            }

                            override fun onError(response: Response<RankBillSearchResult>?) {
                                super.onError(response)
                                Log.e("searchBillList","onError type = " + type)
                            }
                        })
            }
        })
                .map {
                    val bean = RankBillBean()
                    bean.billId = it.billboard.billboard_type.toInt()
                    bean.billType = it.billboard.billboard_type.toInt()
                    bean.billCoverUrl = it.billboard.pic_s640
                    bean.update = it.billboard.update_date
                    if (it.song_list?.size!! >= 1){
                        bean.musicFirst = it.song_list?.get(0)
                        if (it.song_list?.size!! >= 2){
                            bean.musicSecond = it.song_list?.get(1)
                            if (it.song_list?.size!! >= 3){
                                bean.musicThird = it.song_list?.get(2)
                            }
                        }
                    }

                    bean
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    billList.add(it)
                    if (billList.size == types.size){
                        listener.onResult(0, billList, "success")
                    }
                }, {
                    Log.e("searchBillList","throwable = " + it.message)
                })
    }


    interface OnKeywordSearchListener {
        fun onResult(code: Int, result: KeywordSearchResult.SongBean?, message: String?)
    }

    interface OnLrcSearchListener {
        fun onResult(code: Int, lrcString: String?, message: String?)
    }

    interface OnRecomSearchListener{
        fun onResult(code: Int, result: List<RecomSearchResult.RecomSongBean>?, message: String?)
    }

    interface OnArtistSearchListener {
        fun onResult(code: Int, result: ArtistSearchResult?, message: String?)
    }

    interface OnBillSearchListener {
        fun onResult(code: Int, result: RankBillSearchResult?, message: String?)
    }

    interface OnBillListSearchListener {
        fun onResult(code: Int, result: List<RankBillBean>?, message: String?)
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