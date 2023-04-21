package com.engineer.android.mini.jetpack.custom

import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer


class PureObserver<T>(
    val data: PureLiveData<T>,
    val stick: Boolean = false,
    val observer: Observer<in T>
) : Observer<T> {
    /**
     *  1. 这个值一定是在这里取，这里是这个类创建的时候，也就是添加观察者的时候
     *  2. mVersion 是PureLiveData 内部自己维护的 version
     */
    private var version = data.mVersion
    override fun onChanged(t: T) {
        if (version >= data.mVersion) {
            // 理论上 version == data.mVersion 就是在注册的时候
            if (stick && data.mPureData != null) {
                observer.onChanged(data.mPureData!!)
            }
            // 如果不支持 stick,那就直接返回吧
            return
        }
        version = data.mVersion
        observer.onChanged(data.mPureData!!)
    }
}

class PureLiveData<T> : LiveData<T>() {
    internal var mPureData: T? = null
    internal var mVersion = 0

    fun setData(t: T) {
        this.mPureData = t
        super.setValue(t)
    }

    fun postData(t: T) {
        this.mPureData = t
        super.postValue(t)
    }

    override fun setValue(value: T) {
        mVersion++
        super.setValue(value)
    }

    override fun postValue(value: T) {
        mVersion++
        super.postValue(value)
    }

    override fun observe(owner: LifecycleOwner, observer: Observer<in T>) {
        observePure(owner, observer, false)
    }

    fun observePure(owner: LifecycleOwner, observer: Observer<in T>, stick: Boolean) {
        super.observe(owner, PureObserver(this, stick, observer))
    }
}