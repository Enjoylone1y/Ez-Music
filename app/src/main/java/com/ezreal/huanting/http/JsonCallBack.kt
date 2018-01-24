package com.ezreal.huanting.http

import com.google.gson.Gson
import com.google.gson.stream.JsonReader
import com.lzy.okgo.callback.AbsCallback
import okhttp3.Response
import java.lang.reflect.ParameterizedType

/**
 * Created by wudeng on 2018/1/24.
 */

abstract class JsonCallBack<T> : AbsCallback<T>() {

    @Throws(Throwable::class)
    override fun convertResponse(response: Response): T? {
        val body = response.body() ?: return null
        val reader = JsonReader(body.charStream())
        val type = (javaClass.genericSuperclass as ParameterizedType).actualTypeArguments[0]
        return Gson().fromJson(reader,type)
    }
}
