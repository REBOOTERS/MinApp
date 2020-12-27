package com.engineer.android.mini.net

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Environment
import android.text.method.ScrollingMovementMethod
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.engineer.android.mini.R
import com.engineer.android.mini.ext.toast
import com.zchu.rxcache.RxCache
import com.zchu.rxcache.data.CacheResult
import com.zchu.rxcache.data.ResultFrom
import com.zchu.rxcache.diskconverter.GsonDiskConverter
import com.zchu.rxcache.kotlin.rxCache
import com.zchu.rxcache.stategy.FirstCacheTimeoutStrategy
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_rx_cache.*
import java.io.File
import java.util.concurrent.TimeUnit
import kotlin.random.Random

private const val TAG = "RxCacheActivity"

class RxCacheActivity : AppCompatActivity() {

    private lateinit var rxCache: RxCache

    private val arrayLiveData = MutableLiveData<ArrayList<String>>()
    private val mapLiveData = MutableLiveData<HashMap<String, Int>>()

    private val array = ArrayList<String>()
    private val map = HashMap<String, Int>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_rx_cache)


        lg("dir 1 is ${getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}")
        lg("dir 2 is ${cacheDir.toString() + File.separator + getString(R.string.app_name)}")

        rxCache = RxCache.Builder()
            .appVersion(7)
            .diskConverter(GsonDiskConverter())
            .diskDir(File(cacheDir.toString() + File.separator + getString(R.string.app_name)))
            .setDebug(true)
            .build()


        loadData()

        arrayLiveData.observe(this) {
            tv_array.text = "array size is ${it.size} \n $it"
        }
        mapLiveData.observe(this) {
            tv_map.text = "map size is ${it.size} \n $it"
        }

        tv_array.setOnClickListener {
            array.add(System.currentTimeMillis().toString())
            arrayLiveData.value = array
        }
        tv_array.setOnLongClickListener {
            array.clear()
            arrayLiveData.value = array
            true
        }
        tv_map.setOnClickListener {
            map[System.currentTimeMillis().toString()] = Random(100).nextInt()
            mapLiveData.value = map
        }
        tv_map.setOnLongClickListener {
            map.clear()
            mapLiveData.value = map
            true
        }
    }


    @SuppressLint("CheckResult")
    private fun loadData() {

        val millis = TimeUnit.DAYS.toMillis(1)
        val second = TimeUnit.DAYS.toSeconds(1)
        Log.e("zyq", "millis =$millis")
        Log.e("zyq", "second =$second")
        Net.createService(WanAndroidService::class.java)
            .getWeChatAccountList()
            .rxCache(rxCache, "all", FirstCacheTimeoutStrategy(millis))
            .compose(ThreadExTransform())
            .subscribe({
                handleCache(it)
            }, {
                it.message.toast()
                lg(it.message)
            })
    }


    private fun handleCache(it: CacheResult<WeChatCountList>) {
        if (ResultFrom.ifFromCache(it.from)) {
            lg("cache")
            "from cache".toast()
            this.parseData(it.data)
        } else {
            lg("net")
            "from net".toast()
            this.parseData(it.data)
        }
    }

    private fun parseData(list: WeChatCountList?) {

        list?.apply {
            if (errorCode == 0) {
                val sb = StringBuilder()
                data.forEach {
                    sb.append(it).append("\n\n")
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