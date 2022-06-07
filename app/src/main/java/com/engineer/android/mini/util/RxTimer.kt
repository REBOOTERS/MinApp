package com.engineer.android.mini.util

import io.reactivex.Observable
import io.reactivex.Observer
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import java.util.concurrent.TimeUnit

class RxTimer {
    private var disposable: Disposable? = null

    val action: ((Long) -> Unit)? = null

    @SuppressWarnings("unused")
    fun timer(milliSeconds: Long, action: ((Long) -> Unit)?): RxTimer {
        return timer(milliSeconds, action, AndroidSchedulers.mainThread())
    }

    @SuppressWarnings("unused")
    fun timer(milliSeconds: Long, action: ((Long) -> Unit)?, scheduler: Scheduler): RxTimer {
        Observable.timer(milliSeconds, TimeUnit.MILLISECONDS)
            .observeOn(scheduler)
            .subscribe(object : Observer<Long> {
                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onNext(t: Long) {
                    action?.let { it(t) }
                }

                override fun onError(e: Throwable) {
                    cancel()
                }

                override fun onComplete() {
                    cancel()
                }

            })
        return this
    }

    fun interval(milliSeconds: Long, action: ((Long) -> Unit)?): RxTimer {
        return interval(milliSeconds, action, AndroidSchedulers.mainThread())
    }

    @SuppressWarnings("unused")
    fun interval(milliSeconds: Long, action: ((Long) -> Unit)?, scheduler: Scheduler): RxTimer {
        Observable.interval(0L, milliSeconds, TimeUnit.MILLISECONDS, scheduler)
            .subscribe(object : Observer<Long> {
                override fun onSubscribe(d: Disposable) {
                    disposable = d
                }

                override fun onNext(t: Long) {
                    action?.let { it(t) }
                }

                override fun onError(e: Throwable) {
                    cancel()
                }

                override fun onComplete() {
                    cancel()
                }
            })
        return this
    }

    fun cancel() {
        disposable?.let {
            if (it.isDisposed.not()) {
                it.dispose()
            }
        }
    }

    @SuppressWarnings("unused")
    fun isRunning() = disposable != null && disposable!!.isDisposed.not()
}