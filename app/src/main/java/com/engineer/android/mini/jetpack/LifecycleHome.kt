package com.engineer.android.mini.jetpack

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.lifecycle.*

//  https://mp.weixin.qq.com/s/SCNWCz9ZEIOwio9v-Tx0fA  lifecycle
val LIFECYCLE_TAG = "EasyObserver"

class EasyObserver : LifecycleObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun fun0() {
        Log.d(LIFECYCLE_TAG, "fun0() called")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    fun fun1(owner: LifecycleOwner) {
        val value = owner.lifecycle.currentState
        Log.d(LIFECYCLE_TAG, "fun1() called onResume, currentState =$value")
        if (value.isAtLeast(Lifecycle.State.STARTED)) {
            Log.d(LIFECYCLE_TAG, "atLeast started")
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    fun fun2() {
        Log.d(LIFECYCLE_TAG, "fun2() called onPause")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_ANY)
    fun funx(owner: LifecycleOwner,event: Lifecycle.Event) {
        Log.d(LIFECYCLE_TAG, "funx() called with: owner = $owner, event = $event")
    }
}


class MyComponent
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null, style: Int = 0
) : View(context, attributeSet, style), LifecycleOwner {

    private lateinit var lifecycleRegistry: LifecycleRegistry

    private var running = false

    fun init(owner: LifecycleOwner) {
        lifecycleRegistry = LifecycleRegistry(owner)
        lifecycleRegistry.currentState = Lifecycle.State.CREATED
        Thread {
            running = true
            var count = 0
            while (running) {
                count++
                Thread.sleep(1000)
                if (count >= 10) {
                    return@Thread
                }
                Log.d(LIFECYCLE_TAG, "init() count=$count")
            }
            Log.d(LIFECYCLE_TAG, "init() finish")
        }.start()

    }

    fun release() {
        lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        running = false
    }

    override fun getLifecycle(): Lifecycle {
        return lifecycleRegistry
    }

}