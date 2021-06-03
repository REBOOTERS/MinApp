package com.engineer.android.mini.jetpack

import android.content.Context
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.lifecycle.*

//  https://mp.weixin.qq.com/s/SCNWCz9ZEIOwio9v-Tx0fA  lifecycle
val LIFECYCLE_TAG = "EasyObserver"

class EasyObserver : DefaultLifecycleObserver {

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
}


class MyComponent
@JvmOverloads constructor(
    context: Context,
    attributeSet: AttributeSet? = null, style: Int = 0
) : View(context, attributeSet, style), LifecycleOwner {

    private lateinit var lifecycleRegistry: LifecycleRegistry

    private var running = false

    fun init() {
        lifecycleRegistry = LifecycleRegistry(this)
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