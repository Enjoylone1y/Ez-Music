package com.ezreal.huanting.model.network

import com.ezreal.huanting.model.entities.DiscoverBannerInfo
import retrofit2.http.GET

data class BaseResp <T>(val code:Int = 0,val errorMsg:String = "",val data:T? = null)

interface RemoteApi {

    @GET("banner")
    suspend fun getHomeBanner(): BaseResp<List<DiscoverBannerInfo>>
}


