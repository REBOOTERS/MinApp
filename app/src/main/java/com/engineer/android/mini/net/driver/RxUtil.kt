package com.engineer.android.mini.net.driver

import android.annotation.SuppressLint
import android.util.Log
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.FlowableOnSubscribe
import io.reactivex.flowables.ConnectableFlowable
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

lateinit var flowableUp: ConnectableFlowable<Int>

private const val TAG = "RxUtil"
fun createFlowUp() {
    flowableUp = Flowable.create(FlowableOnSubscribe<Int> { emitter ->
        for (i in 0..20) {
            emitter.onNext(i)
        }
        emitter.onComplete()
    }, BackpressureStrategy.LATEST)
        .publish()

    flowableUp.connect()
}


@SuppressLint("CheckResult")
fun registerFlow() {
    flowableUp.subscribe({
        GlobalScope.launch(Dispatchers.IO) {
            delay(1000)
            Log.e(TAG, "onNext is $it")
        }
    }, {
        Log.e(TAG, "throwable is $it")
    }, {
        Log.e(TAG, "complete")
    }, {
        it.request(Long.MAX_VALUE)
        Log.e(TAG, "subscription $it")
    })
}