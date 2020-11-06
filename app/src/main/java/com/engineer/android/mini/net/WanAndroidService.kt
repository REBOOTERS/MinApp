package com.engineer.android.mini.net

import io.reactivex.Observable
import retrofit2.http.GET

interface WanAndroidService {

    @GET("wxarticle/chapters/json")
    fun getWeChatAccountList(): Observable<WeChatCountList>
}