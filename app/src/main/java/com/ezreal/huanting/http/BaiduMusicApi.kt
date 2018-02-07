package com.ezreal.huanting.http

import android.text.TextUtils
import com.ezreal.huanting.http.result.*
import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.AbsCallback
import com.lzy.okgo.model.Response
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
    private val METHOD_FOCCUS_PIC = "baidu.ting.plaza.getFocusPic"
    private val METHOD_HOT_BILL = "baidu.ting.diy.getHotGeDanAndOfficial"
    private val METHOD_RECOM_ALBUM = "baidu.ting.plaza.getRecommendAlbum"
    private val METHOD_RANK_BILL_LIST = "baidu.ting.billboard.billCategory"
    private val METHOD_EDITOR_RECOM = "baidu.ting.song.getEditorRecommend"
    private val METHOD_CATEGORY_LIST = "baidu.ting.diy.gedanCategory"
    private val METHOD_GEDAN_ALL = "baidu.ting.diy.gedan"
    private val METHOD_GEDAN_SEARCH = "baidu.ting.diy.search"
    private val METHOD_GEDAN_INFO = "baidu.ting.diy.gedanInfo"

    private val PARAM_FLAG = "kflag"
    private val PARAM_METHOD = "method"
    private val PARAM_FROM = "from"
    private val PARAM_FORMAT = "format"
    private val PARAM_VERSION = "version"
    private val PARAM_TYPE = "type"
    private val PARAM_SIZE = "size"
    private val PARAM_OFFSET = "offset"
    private val PARAM_NUM = "num"
    private val PARAM_SONG_ID = "songid"
    private val PARAM_RECOM_SONG_ID = "song_id"
    private val PARAM_TING_UID = "tinguid"
    private val PARAM_QUERY = "query"
    private val PARAM_LIMIT = "limit"
    private val PARAM_PAGE_NO = "page_no"
    private val PARAM_PAGE_SIZE = "page_size"
    private val PARAM_LIST_ID = "listid"

    private val VALUE_FROM = "android"
    private val VALUE_VERSION = "5.6.5.6"
    private val VALUE_FORMAT = "json"

    /** 获取轮播图 */
    fun loadImageUrls(num: Int, listener: OnUrlLoadListener) {
        OkGo.get<UrlLoadResult>(BASE_URL)
                .params(PARAM_FROM, VALUE_FROM)
                .params(PARAM_VERSION, VALUE_VERSION)
                .params(PARAM_FORMAT, VALUE_FORMAT)
                .params(PARAM_METHOD, METHOD_FOCCUS_PIC)
                .params(PARAM_NUM, num)
                .execute(object : JsonCallBack<UrlLoadResult>() {
                    override fun onSuccess(response: Response<UrlLoadResult>?) {
                        if (response?.body() != null) {
                            val urlList = ArrayList<String>()
                            response.body().pic.mapTo(urlList) { it.randpic }
                            listener.onResult(0, urlList, "success")
                        } else {
                            listener.onResult(-1, null, "failed")
                        }
                    }
                })
    }

    /** 获取推荐歌单 */
    fun loadHotBillList(num: Int, listener: BaiduMusicApi.OnHotBillLoadListener) {
        OkGo.get<HotGedanResult>(BASE_URL)
                .params(PARAM_FROM, VALUE_FROM)
                .params(PARAM_VERSION, VALUE_VERSION)
                .params(PARAM_FORMAT, VALUE_FORMAT)
                .params(PARAM_METHOD, METHOD_HOT_BILL)
                .params(PARAM_NUM, num)
                .execute(object : JsonCallBack<HotGedanResult>() {
                    override fun onSuccess(response: Response<HotGedanResult>?) {
                        if (response?.body() != null) {
                            listener.onResult(0, response.body().content.list, "success")
                        } else {
                            listener.onResult(-1, null, "failed")
                        }
                    }
                })

    }

    /** 获取推荐专辑 */
    fun loadRecomAlbum(offset: Int, limit: Int, listener: OnRecomAlbumListener) {
        OkGo.get<RecomAlbumResult>(BASE_URL)
                .params(PARAM_FROM, VALUE_FROM)
                .params(PARAM_VERSION, VALUE_VERSION)
                .params(PARAM_FORMAT, VALUE_FORMAT)
                .params(PARAM_METHOD, METHOD_RECOM_ALBUM)
                .params(PARAM_OFFSET, offset)
                .params(PARAM_LIMIT, limit)
                .execute(object : JsonCallBack<RecomAlbumResult>() {
                    override fun onSuccess(response: Response<RecomAlbumResult>?) {
                        if (response?.body()?.plaze?.rm?.album != null) {
                            listener.onResult(0, response.body()?.plaze?.rm?.album?.list,
                                    response.body()?.plaze?.rm?.album?.total!!, "success")
                        } else {
                            listener.onResult(-1, null, 0, "failed")
                        }
                    }

                })
    }

    /** 获取榜单列表  **/
    fun loadRankBillList(listener: OnRankBillListListener) {
        OkGo.get<RankBillListResult>(BASE_URL)
                .params(PARAM_FROM, VALUE_FROM)
                .params(PARAM_VERSION, VALUE_VERSION)
                .params(PARAM_FORMAT, VALUE_FORMAT)
                .params(PARAM_METHOD, METHOD_RANK_BILL_LIST)
                .params(PARAM_FLAG, 1)
                .execute(object : JsonCallBack<RankBillListResult>() {
                    override fun onSuccess(response: Response<RankBillListResult>?) {
                        if (response?.body()?.content != null) {
                            listener.onResult(0, response.body().content, "success")
                        } else {
                            listener.onResult(-1, null, "failed")
                        }
                    }

                })
    }

    /** 获取榜单数据（榜单信息和榜单歌曲列表） */
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

    /** 获取编辑推荐列表 */
    fun loadRecomMusicList(listener: OnRecomListListener) {
        OkGo.get<RecomListResult>(BASE_URL)
                .params(PARAM_FROM, VALUE_FROM)
                .params(PARAM_VERSION, VALUE_VERSION)
                .params(PARAM_FORMAT, VALUE_FORMAT)
                .params(PARAM_METHOD, METHOD_EDITOR_RECOM)
                .params(PARAM_NUM,25)
                .execute(object :JsonCallBack<RecomListResult>(){
                    override fun onSuccess(response: Response<RecomListResult>?) {
                        if (response?.body()?.content != null){
                            listener.onResult(0,response.body().content[0].list,"success")
                        }else{
                            listener.onResult(-1,null,"failed")
                        }
                    }
                })
    }

    /** 获取歌单标签 */
    fun loadGedanCategory(listener: OnCategoryListListener) {
        OkGo.get<CategoryListResult>(BASE_URL)
                .params(PARAM_FROM, VALUE_FROM)
                .params(PARAM_VERSION, VALUE_VERSION)
                .params(PARAM_FORMAT, VALUE_FORMAT)
                .params(PARAM_METHOD, METHOD_CATEGORY_LIST)
                .execute(object : JsonCallBack<CategoryListResult>() {
                    override fun onSuccess(response: Response<CategoryListResult>?) {
                        if (response?.body()?.content != null) {
                            listener.onResult(0, response.body().content, "success")
                        } else {
                            listener.onResult(-1, null, "failed")
                        }
                    }
                })

    }

    /** 获取所有歌单 */
    fun loadAllGedan(pageNo: Int, listener: OnGedanListListener) {
        OkGo.get<GedanListResult>(BASE_URL)
                .params(PARAM_FROM, VALUE_FROM)
                .params(PARAM_VERSION, VALUE_VERSION)
                .params(PARAM_FORMAT, VALUE_FORMAT)
                .params(PARAM_METHOD, METHOD_GEDAN_ALL)
                .params(PARAM_PAGE_NO, pageNo)
                .params(PARAM_PAGE_SIZE, 10)
                .execute(object : JsonCallBack<GedanListResult>() {
                    override fun onSuccess(response: Response<GedanListResult>?) {
                        if (response?.body() != null) {
                            listener.onResult(0, response.body().content,response.body().total,
                                    "success")
                        } else {
                            listener.onResult(-1, null, 0,"failed")
                        }
                    }
                })
    }

    /** 获取带标签的歌单 */
    fun loadGedanByTag(pageNo: Int, tag: String, listener: OnGedanListListener) {
        OkGo.get<GedanListResult>(BASE_URL)
                .params(PARAM_FROM, VALUE_FROM)
                .params(PARAM_VERSION, VALUE_VERSION)
                .params(PARAM_FORMAT, VALUE_FORMAT)
                .params(PARAM_METHOD, METHOD_GEDAN_SEARCH)
                .params(PARAM_PAGE_NO, pageNo)
                .params(PARAM_PAGE_SIZE, 10)
                .params(PARAM_QUERY, tag)
                .execute(object : JsonCallBack<GedanListResult>() {
                    override fun onSuccess(response: Response<GedanListResult>?) {
                        if (response?.body() != null) {
                            listener.onResult(0, response.body().content,response.body().total,
                                    "success")
                        } else {
                            listener.onResult(-1, null,0, "failed")
                        }
                    }
                })
    }

    /** 获取歌单数据 */
    fun loadGedanInfo(listId: Long, listener: OnGedanInfoListener) {
        OkGo.get<GedanInfoResult>(BASE_URL)
                .params(PARAM_FROM, VALUE_FROM)
                .params(PARAM_VERSION, VALUE_VERSION)
                .params(PARAM_FORMAT, VALUE_FORMAT)
                .params(PARAM_METHOD, METHOD_GEDAN_INFO)
                .params(PARAM_LIST_ID, listId)
                .execute(object : JsonCallBack<GedanInfoResult>() {
                    override fun onSuccess(response: Response<GedanInfoResult>?) {
                        if (response?.body() != null){
                            listener.onResult(0,response.body(),"success")
                        }else{
                            listener.onResult(-1,null,"failed")
                        }
                    }
                })
    }

    /**
     * 根据关键字（音乐名 歌手名）搜索百度音乐库， 可以获取得到歌曲 ID
     */
    fun searchMusicByKey(keyWord: String, listener: OnKeywordSearchListener) {
        OkGo.get<KeywordSearchResult>(BASE_URL)
                .params(PARAM_METHOD, METHOD_SEARCH_MUSIC)
                .params(PARAM_QUERY, keyWord)
                .execute(object : JsonCallBack<KeywordSearchResult>() {
                    override fun onSuccess(response: Response<KeywordSearchResult>?) {
                        if (response?.body() != null && response.body()?.song?.size!! > 0) {
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
     * 根据参考的音乐 ID ,获取相似推荐歌曲
     */

    fun searchRecomById(musicId: String, num: Int, listener: OnRecomSearchListener) {
        OkGo.get<RecomSearchResult>(BASE_URL)
                .params(PARAM_METHOD, METHOD_RECOM)
                .params(PARAM_RECOM_SONG_ID,musicId)
                .params(PARAM_NUM,num)
                .execute(object : JsonCallBack<RecomSearchResult>(){
                    override fun onSuccess(response: Response<RecomSearchResult>?) {
                        if (response?.body() != null && response.body()?.result != null){
                            listener.onResult(0, response.body().result.list, "success")
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
    fun searchMusicInfoById(musicId: String, listener: OnMusicInfoSearchListener) {
        OkGo.get<MusicSearchResult>(BASE_URL)
                .params(PARAM_METHOD, METHOD_PLAY)
                .params(PARAM_SONG_ID, musicId)
                .execute(object : JsonCallBack<MusicSearchResult>() {
                    override fun onSuccess(response: Response<MusicSearchResult>?) {
                        if (response?.body() != null){
                            listener.onResult(0,response.body(),"success")
                        }else{
                            listener.onResult(-1,null,"failed")
                        }
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


    interface OnUrlLoadListener {
        fun onResult(code: Int, result: List<String>?, message: String?)
    }

    interface OnHotBillLoadListener {
        fun onResult(code: Int, result: List<HotGedanResult.ContentBean.ListBean>?,
                     message: String?)
    }

    interface OnRecomAlbumListener {
        fun onResult(code: Int, result: List<RecomAlbumResult.Plaze.RMBean.Album.RecomAlbumBean>?,
                     total: Int, message: String?)
    }

    interface OnRankBillListListener {
        fun onResult(code: Int, result: List<RankBillListResult.RankBillBean>?,
                     message: String?)
    }

    interface OnCategoryListListener {
        fun onResult(code: Int, result: List<CategoryListResult.Category>?,
                     message: String?)
    }

    interface OnGedanListListener {
        fun onResult(code: Int, result: List<GedanListResult.GedanBean>?,total: Int,
                     message: String?)
    }

    interface OnGedanInfoListener {
        fun onResult(code: Int, result: GedanInfoResult?, message: String?)
    }

    interface OnRecomListListener {
        fun onResult(code: Int, result: List<RecomListResult.ContentBean.SongListBean>?,
                     message: String?)
    }

    interface OnKeywordSearchListener {
        fun onResult(code: Int, result: KeywordSearchResult.SongBean?, message: String?)
    }

    interface OnLrcSearchListener {
        fun onResult(code: Int, lrcString: String?, message: String?)
    }

    interface OnRecomSearchListener{
        fun onResult(code: Int, result: List<RecomSearchResult.RecomSongBean>?,
                     message: String?)
    }

    interface OnArtistSearchListener {
        fun onResult(code: Int, result: ArtistSearchResult?, message: String?)
    }

    interface OnBillSearchListener {
        fun onResult(code: Int, result: RankBillSearchResult?, message: String?)
    }

    interface OnMusicInfoSearchListener{
        fun onResult(code: Int, result: MusicSearchResult?, message: String?)
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