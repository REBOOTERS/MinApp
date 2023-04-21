package com.engineer.android.mini.jetpack.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.switchMap
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import java.util.concurrent.TimeUnit

/**
 * Created on 2020/9/13.
 * @author rookie
 */
class FooViewModel(application: Application) : AndroidViewModel(application) {
    internal val foo = MutableLiveData<Int>()

    internal val test = MutableLiveData(true)


    internal val fooMap = foo.switchMap {
        MutableLiveData(it * it)
    }

    fun getFoo(): MutableLiveData<Int> {
        return foo
    }

    fun doFoo() {
        Handler(Looper.getMainLooper()).postDelayed({
            foo.value = 100
        }, 2000)
    }

    fun doFoo2() {
        Handler(Looper.getMainLooper()).postDelayed({
            foo.value = 2000
        }, 3000)
    }

    val mainValue = MutableLiveData(0L)

    val threadValue = MutableLiveData(0L)

    fun doUpdate() {
        Observable.intervalRange(1, 100, 1, 1, TimeUnit.MILLISECONDS).observeOn(AndroidSchedulers.mainThread())
            .doOnNext {
                mainValue.value = it
            }.subscribe()

        Observable.intervalRange(1, 100, 1, 1, TimeUnit.MILLISECONDS).doOnNext {
                threadValue.postValue(it)
            }.subscribe()
    }
}