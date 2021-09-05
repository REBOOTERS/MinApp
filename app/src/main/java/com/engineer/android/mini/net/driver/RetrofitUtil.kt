package com.engineer.android.mini.net.driver

import com.engineer.android.mini.net.WeChatCountList
import okhttp3.HttpUrl
import okhttp3.ResponseBody
import okhttp3.internal.platform.Platform
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

    val url = HttpUrl.get("https://image.baidu.com/search/albumsdetail?tn=albumsdetail&word=%E4%BA%BA%E7%89%A9&fr=albumslist&album_tab=%E4%BA%BA%E7%89%A9&album_id=45&rn=30")
    println(url.isHttps)
    println(url.pathSegments())
    println(url.encodedPath())
    println(url.queryParameterNames())
    println(url.query())
    println()

//    RetrofitUtil.go()
    RetrofitUtil.go2()

    val service = FakeRetrofit.getInstance().create(ApiService::class.java)
    val r1 = service.getAccountJson()
    val r2 = service.getAccountList()

    val dd = object :ApiService {
        override fun getAccountList(): Call<WeChatCountList> {
            TODO("Not yet implemented")
        }

        override fun getAccountJson(): Call<ResponseBody> {
            TODO("Not yet implemented")
        }

    }

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
        println(System.getProperty("java.vm.name"))
        println("platform ==${Platform.get()}")
        val retrofit = Retrofit.Builder()
            .baseUrl(baseUrl)
            .build()

        retrofit.callAdapterFactories().forEach {
            println(it)
        }
        println()
        retrofit.converterFactories().forEach {
            println(it)
        }
        println()
        println(retrofit.callbackExecutor())


        val service = retrofit.create(ApiService::class.java)
        println("service is $service")
        println("service is ${service.javaClass.name}")
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