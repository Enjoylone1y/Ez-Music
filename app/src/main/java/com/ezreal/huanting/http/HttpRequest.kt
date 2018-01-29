package com.ezreal.huanting.http

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
 * 网络歌曲 网络请求类
 * Created by wudeng on 2018/1/23.
 */

object HttpRequest {

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
    // type: //1、新歌榜，2、热歌榜

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
     * 根据 关键词搜索歌词
     */
    fun searchLrcByKeyword(keyWord: String,listener: OnLrcSearchListener){
        Observable.create(ObservableOnSubscribe<String?> {emitter ->
            OkGo.get<KeywordSearchResult>(BASE_URL)
                    .params(PARAM_METHOD, METHOD_SEARCH_MUSIC)
                    .params(PARAM_QUERY, keyWord)
                    .execute(object : JsonCallBack<KeywordSearchResult>() {
                        override fun onSuccess(response: Response<KeywordSearchResult>?) {
                            if (response?.body() != null && response.body().song != null){
                                emitter.onNext(response.body().song?.get(0)?.songid!!)
                            }else{
                                emitter.onError(Throwable("empty result or failed"))
                            }
                        }
                    })
        }).map{ songId ->
            val lrc = LrcSearchResult()
            OkGo.get<LrcSearchResult>(BASE_URL)
                    .params(PARAM_METHOD, METHOD_LRC)
                    .params(PARAM_SONG_ID, songId)
                    .execute(object : JsonCallBack<LrcSearchResult>() {
                        override fun onSuccess(response: Response<LrcSearchResult>?) {
                            if (response?.body() != null){
                                lrc.title = response.body()?.title!!
                                lrc.lrcContent = response.body()?.lrcContent!!
                            }
                        }
                    })
            lrc
        }.subscribe({
                    listener.onResult(0,it.lrcContent,"success")
                },{
                    listener.onResult(-1,null,"failed")
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
                .execute(object :JsonCallBack<RecomSearchResult>(){
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

    /** 1.新歌榜
     * 2.热歌榜
     * #分类榜单
     * 20.华语金曲榜
     * 21.欧美金曲榜
     * 24.影视金曲榜
     * 23.情歌对唱榜
     * 25.网络歌曲榜
     * 22.经典老歌榜
     * 11.摇滚榜
     * #媒体榜单
     * 6.KTV热歌榜
     * 8.Billboard
     * 18.Hito中文榜
     * 7.叱咤歌曲榜*/

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
                        })
            }
        })
                .map {
                    val bean = RankBillBean()
                    bean.billId = it.billboard.billboard_no.toInt()
                    bean.billType = it.billboard.billboard_type.toInt()
                    bean.billCoverUrl = it.billboard.pic_s640
                    bean.update = it.billboard.update_date
                    bean.musicNun = it.billboard.billboard_songnum.toInt()
                    bean.musicFirst = it.song_list?.get(0)
                    bean.musicSecond = it.song_list?.get(1)
                    bean.musicThird = it.song_list?.get(2)
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
        fun onResult(code: Int, result: MusicBean?, message: String?)
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