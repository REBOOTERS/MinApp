package com.engineer.android.mini.ui.tabs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ComputableLiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.map
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.tabs.ui.fragments.placeholder.PlaceholderContent

class ListViewModel(application: Application) : AndroidViewModel(application) {

    val items = MutableLiveData<PlaceholderContent>()
    val count = items.map {
        val sum = it.ITEMS.size
        sum
    }


    init {
        items.value = PlaceholderContent
    }

    fun onItemClicked(item: PlaceholderContent.PlaceholderItem) {
        item.toString().toast()
        val temp = PlaceholderContent.PlaceholderItem(
            System.currentTimeMillis().toString(), hashCode().toString(), "self add"
        )
        val copy = items.value
        copy?.ITEMS?.add(temp)
        copy?.let { items.postValue(it) }
    }
}