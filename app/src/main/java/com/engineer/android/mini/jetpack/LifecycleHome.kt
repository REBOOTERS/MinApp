package com.engineer.android.mini.jetpack

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.lifecycle.*

//  https://mp.weixin.qq.com/s/SCNWCz9ZEIOwio9v-Tx0fA  lifecycle
val LIFECYCLE_TAG = "EasyObserver"

class EasyObserver : LifecycleObserver,DefaultLifecycleObserver, LifecycleEventObserver {

    @OnLifecycleEvent(Lifecycle.Event.ON_CREATE)
    fun foo() {}

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        Log.d(LIFECYCLE_TAG, "onCreate() called with: owner = $owner")
    }

    override fun onResume(owner: LifecycleOwner) {
        super.onResume(owner)
        val value = owner.lifecycle.currentState
        Log.d(LIFECYCLE_TAG, "onResume() called onResume, currentState =$value")
        if (value.isAtLeast(Lifecycle.State.STARTED)) {
            Log.d(LIFECYCLE_TAG, "atLeast started")
        }
    }

    override fun onPause(owner: LifecycleOwner) {
        super.onPause(owner)
        Log.d(LIFECYCLE_TAG, "onPause() called with: owner = $owner")
    }

    override fun onStateChanged(source: LifecycleOwner, event: Lifecycle.Event) {
        Log.d(LIFECYCLE_TAG, "onStateChanged() called with: source = $source, event = $event")
    }
}


class MyLifecycleAwareComponent
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null, style: Int = 0
) : View(context, attributeSet, style) {


    private var running = false

    fun init(provider: LifecycleOwner) {

        Thread {
            running = true
            var count = 0
            Log.d(LIFECYCLE_TAG, "init() currentState=${provider.lifecycle.currentState}")
            while (provider.lifecycle.currentState > Lifecycle.State.DESTROYED) {
                count++
                Thread.sleep(1000)
                if (count >= 1000) {
                    return@Thread
                }
                Log.d(LIFECYCLE_TAG, "init() count=$count")
            }
            Log.d(LIFECYCLE_TAG, "init() finish")
        }.start()

    }


}