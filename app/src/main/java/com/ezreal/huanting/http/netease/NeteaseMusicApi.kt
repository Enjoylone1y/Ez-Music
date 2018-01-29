package com.ezreal.huanting.http.netease

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.lzy.okgo.OkGo
import com.lzy.okgo.callback.AbsCallback
import com.lzy.okgo.model.Response
import java.lang.reflect.ParameterizedType

/**
 * 网易云音乐 在线音乐获取
 * Created by wudeng on 2018/1/29.
 */
object NeteaseMusicApi {


    fun searchMusicByKeyWord(keyword:String, size:Int, offset:Int, listener: OnKeywordSearchListener){
        OkGo.get<NKeySearchResult>("http://s.music.163.com/search/get/")
                .params("type",1)
                .params("s",keyword)
                .params("limit",size)
                .params("offset",offset)
                .execute(object :JsonCallBack<NKeySearchResult>(){
                    override fun onSuccess(response: Response<NKeySearchResult>?) {
                        if (response?.body() != null){
                            listener.onResult(0,response.body(),"success")
                        }else{
                            listener.onResult(-1,null,"failed")
                        }
                    }

                    override fun onError(response: Response<NKeySearchResult>?) {
                        super.onError(response)
                        listener.onResult(-1,null,"failed:" + response?.message())
                    }

                })
    }

    fun searchLrcById(id:Long,listener: OnLrcSearchListener){
        OkGo.get<NLrcSearchResult>("http://music.163.com/api/song/lyric")
                .params("id",id)
                .params("os","pc")
                .params("lv",-1)
                .params("kv",-1)
                .params("tv",-1)
                .execute(object :JsonCallBack<NLrcSearchResult>(){
                    override fun onSuccess(response: Response<NLrcSearchResult>?) {
                        if (response?.body() != null){
                            listener.onResult(0,response.body(),"success")
                        }else{
                            listener.onResult(-1,null,"failed"+ response?.message())
                        }
                    }

                    override fun onError(response: Response<NLrcSearchResult>?) {
                        super.onError(response)
                        listener.onResult(-1,null,"failed")
                    }

                })
    }

    interface OnKeywordSearchListener{
        fun onResult(code:Int, result: NKeySearchResult?, message:String?)
    }


    interface OnLrcSearchListener{
        fun onResult(code: Int, result: NLrcSearchResult?, message:String?)
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