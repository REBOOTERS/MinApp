package com.engineer.android.mini.ui.tabs

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.tabs.ui.fragments.placeholder.PlaceholderContent

class ListViewModel(application: Application) : AndroidViewModel(application) {

    val items = MutableLiveData<PlaceholderContent>()

    init {
        items.value = PlaceholderContent
    }

    fun onItemClicked(item: PlaceholderContent.PlaceholderItem) {
        item.toString().toast()
    }
}