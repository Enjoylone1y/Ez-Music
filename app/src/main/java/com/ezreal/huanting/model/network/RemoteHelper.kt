package com.ezreal.huanting.model.network

import com.lzy.okgo.interceptor.HttpLoggingInterceptor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import java.util.concurrent.TimeUnit



object RemoteHelper {

    private const val LOG_TAG = "Remote"
    private const val BASE_URL = "http://121.4.85.55:3000/"

    private val okHttpClient = OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor(LOG_TAG).also {
                it.setPrintLevel(HttpLoggingInterceptor.Level.BODY) })
            .connectTimeout(10,TimeUnit.SECONDS)
            .writeTimeout(20,TimeUnit.SECONDS)
            .readTimeout(30,TimeUnit.SECONDS)
            .build()

    private val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .build()

    private val remoteApi:RemoteApi = retrofit.create(RemoteApi::class.java)


    fun loadHomeBanner() {
        GlobalScope.launch {
            withContext(Dispatchers.IO){
                val homeBanner = remoteApi.getHomeBanner()
                withContext(Dispatchers.Main){
                    if (homeBanner.code == 200){

                    }
                }
            }
        }
    }

}