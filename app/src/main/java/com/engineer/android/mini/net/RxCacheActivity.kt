package com.engineer.android.mini.net

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import android.util.Log
import com.engineer.android.mini.R
import com.engineer.android.mini.ext.toast
import com.engineer.android.netcache.internal.Net
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_rx_cache.*

private const val TAG = "RxCacheActivity"

class RxCacheActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rx_cache)
        tv.movementMethod = ScrollingMovementMethod.getInstance()
        loadData()
    }

    @SuppressLint("CheckResult")
    private fun loadData() {
        Net.createService(WanAndroidService::class.java)
            .getWeChatAccountList()
            .compose(ThreadExTransform())
            .subscribe({
                if (it.isSuccessful) {
                    parseData(it.body())
                } else {
                    "net fail".toast()
                }
            }, {
                it.message.toast()
                lg(it.message)
            })
    }

    private fun parseData(list: WeChatCountList?) {
        list?.apply {
            if (errorCode == 0) {
                val sb = StringBuilder()
                data.forEach {
                    sb.append(it).append("\n")
                }
                tv.text = sb.toString()
            }
        }
    }

    private fun lg(msg: String?) {
        Log.e(TAG, "msg ====>  $msg")
    }

    class ThreadExTransform<T> : ObservableTransformer<T, T> {

        override fun apply(upstream: Observable<T>): ObservableSource<T> {
            return upstream
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap {
                    Observable.just(it)
                }
        }
    }
}