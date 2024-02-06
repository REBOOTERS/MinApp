package com.engineer.android.mini.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData

class RecyclerViewModel(app: Application) : AndroidViewModel(app) {

    val datas = MutableLiveData<List<String>>()

    fun loadData() {
        val list = ArrayList<String>()
        for (i in 0..23) {
            list.add(i.toString())
        }
        datas.postValue(list)
    }

    fun insert(index: Int, value: String) {
        val list = datas.value?.toMutableList()
        list?.add(index, value)
        list?.let {
            datas.postValue(it)
        }
    }

    fun remove(index: Int) {
        val list = datas.value?.toMutableList()
        if(index < 0 || index >= (list?.size ?: 0)) return
        list?.removeAt(index)
        list?.let {
            datas.postValue(it)
        }
    }
}