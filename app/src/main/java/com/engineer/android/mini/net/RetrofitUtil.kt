package com.engineer.android.mini.net

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET

/**
 * Created on 2021/5/2.
 * @author rookie
 */

fun main() {
//    RetrofitUtil.go()
    RetrofitUtil.go2()
}

interface ApiService {

    @GET("wxarticle/chapters/json")
    fun getAccountList(): Call<WeChatCountList>

    @GET("wxarticle/chapters/json")
    fun getAccountJson(): Call<ResponseBody>
}

object RetrofitUtil {

    val baseUrl = "https://www.wanandroid.com/"



    fun go2() {
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .build()

        val service = retrofit.create(ApiService::class.java)

        val call = service.getAccountJson()

        call.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                println("onResponse() called with: call = $call, response = $response")
                println(response.body()?.string())
                println(response.code())
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                println("onFailure() called with: call = $call, t = $t")
            }

        })
    }



    fun go() {

        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(ApiService::class.java)
        val call = service.getAccountList()
        call.enqueue(object : retrofit2.Callback<WeChatCountList> {
            override fun onResponse(
                call: Call<WeChatCountList>,
                response: Response<WeChatCountList>
            ) {
                println("onResponse() called with: call = $call, response = $response")
                println(response.body())
            }

            override fun onFailure(call: Call<WeChatCountList>, t: Throwable) {
                println("onFailure() called with: call = $call, t = $t")
            }

        })
    }
}