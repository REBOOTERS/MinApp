package com.engineer.android.mini.net

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Html
import android.text.TextUtils
import android.util.Log
import android.util.SparseArray
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityRxCacheBinding
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.net.driver.createFlowUp
import com.engineer.android.mini.net.driver.parseUri
import com.engineer.android.mini.net.driver.registerFlow
import com.engineer.android.mini.net.hilt.AnalyticsService
import com.engineer.android.mini.net.hilt.MiniEntryHelper
import com.engineer.android.mini.net.model.JsonSerializeTest
import com.engineer.android.mini.util.JsonUtil
import com.zchu.rxcache.RxCache
import com.zchu.rxcache.data.CacheResult
import com.zchu.rxcache.data.ResultFrom
import com.zchu.rxcache.kotlin.rxCache
import com.zchu.rxcache.stategy.FirstCacheTimeoutStrategy
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import java.io.File
import java.net.InetAddress
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import kotlin.random.Random

private const val TAG = "RxCacheActivity"

@AndroidEntryPoint
class RxCacheActivity : AppCompatActivity() {


    lateinit var viewBinding: ActivityRxCacheBinding

    @Inject
    lateinit var rxCache: RxCache

    @Inject
    lateinit var service: AnalyticsService

    private val arrayLiveData = MutableLiveData<ArrayList<String>>()
    private val mapLiveData = MutableLiveData<HashMap<String, Int>>()

    private val array = ArrayList<String>()
    private val map = HashMap<String, Int>()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityRxCacheBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)


        lg("dir 1 is ${getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)}")
        lg("dir 2 is ${cacheDir.toString() + File.separator + getString(R.string.app_name)}")

        service.analyticsMethods()
        MiniEntryHelper.entryPoint.flyMachine().start()
        MiniEntryHelper.entryPoint.videoPlayer().play()
        loadData()

        arrayLiveData.observe(this) {
            viewBinding.tvArray.text = "array size is ${it.size} \n $it"
        }
        mapLiveData.observe(this) {
            viewBinding.tvMap.text = "map size is ${it.size} \n $it"
        }

        viewBinding.tvArray.setOnClickListener {
            array.add(System.currentTimeMillis().toString())
            arrayLiveData.value = array

            JsonUtil.parseSpecialJson(this)
            JsonUtil.parseSpecialJson1(this)
            JsonUtil.parseSpecialJson2(this)

            SimpleDialog().show(supportFragmentManager, TAG)
        }
        viewBinding.tvArray.setOnLongClickListener {
            array.clear()
            arrayLiveData.value = array
            true
        }
        viewBinding.tvMap.setOnClickListener {
            testSparseArray()
            map[System.currentTimeMillis().toString()] = Random(100).nextInt()
            mapLiveData.value = map
        }
        viewBinding.tvMap.setOnLongClickListener {
            map.clear()
            mapLiveData.value = map
            true
        }


        createFlowUp()
        registerFlow()
//        JsonSerializeTest.jsonSerialize()
        JsonSerializeTest.jsonDeserialize(this)

        Observable.create<String> {
            val address = InetAddress.getByName("www.qq.com")
            val ip = address.hostAddress
            if (TextUtils.isEmpty(ip)) it.onError(Throwable("ip is null")) else it.onNext(ip)
            it.onComplete()
        }.subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).doOnNext {
            println("ip is $it")
        }.doOnError {
            it.printStackTrace()
        }.subscribe()

        val url =
            "https://search.jd.com/Search?keyword=%E5%8F%B0%E5%BC%8F%E6%9C%BA&enc=utf-8&suggest=2.his.0.0&wq=&pvid=e33792a4dc654b29af0caae2c63bce53"
        parseUri(Uri.parse(url))

        val sb = StringBuilder()
        sb.append("这是一段文本，<b>部分文本</b>将会加粗显示、")
//        sb.append("打开 <a href=\"http://www.baidu.com\">baidu</a> 首页").append("、")
        var s = sb.toString()
        Log.i(TAG, "s length ${s.length}")
        s = s.substring(0, s.length - 1)
        val content = Html.fromHtml(s)
        viewBinding.fullTv.text = content

    }


    private fun testSparseArray() {
        val sa = SparseArray<Char>()

        for (i in 0..10) {
            sa.put(i, 'A' + i)
        }

        Log.e("sparseArray", sa.toString())
        Log.e("sparseArray", sa.indexOfKey(1).toString())
        Log.e("sparseArray", sa.indexOfValue('K').toString())

        reflectValue(sa)
    }

    private fun reflectValue(sa: SparseArray<Char>) {
        try {
            val array = ReflectUtil.getFiled(sa, "mKeys")
            if (array is IntArray) {
                Log.e("sparseArray", Arrays.toString(array))
            }

            Log.e("sparseArray", "========================")

            val values = ReflectUtil.getFiled(sa, "mValues")
            val list = values as Array<*>
            Log.e("sparseArray", list.contentToString())
        } catch (e: Exception) {
            Log.e("sparseArray", e.message ?: "")
        }

    }


    @SuppressLint("CheckResult")
    private fun loadData() {

        val millis = TimeUnit.DAYS.toMillis(1)
        val second = TimeUnit.DAYS.toSeconds(1)
        Log.e("zyq", "millis = $millis")
        Log.e("zyq", "second = $second")
        Net.createService(WanAndroidService::class.java).getWeChatAccountList()
            .rxCache(rxCache, "all", FirstCacheTimeoutStrategy(millis)).compose(ThreadExTransform()).subscribe({
                handleCache(it)
//                parseData(it)
            }, {
                it.message.toast()
                lg(it.message)
            })

        Net.createService(WanAndroidService::class.java).getNav()
            .rxCache(rxCache, "nav", FirstCacheTimeoutStrategy(millis)).compose(ThreadExTransform()).subscribe({}, {})
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
                viewBinding.tv.text = sb.toString()
            }
        }
    }

    private fun lg(msg: String?) {
        Log.e(TAG, "msg ====>  $msg")
    }

    override fun onStop() {
        super.onStop()
        MiniEntryHelper.entryPoint.flyMachine().stop()
        MiniEntryHelper.entryPoint.videoPlayer().stop()
    }
}
