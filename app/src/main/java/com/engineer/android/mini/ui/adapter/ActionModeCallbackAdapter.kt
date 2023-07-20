package com.engineer.android.mini.ui.adapter

import android.util.Log
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import androidx.core.view.get

/**
 * 自定义 ActionMode.Callback ，可以定制 EditText 选中文本的菜单
 */
class ActionModeCallbackAdapter : ActionMode.Callback {

    private val TAG = "ActionModeCallbackAdapt"

    override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        menu?.let {
            val size = it.size()
            for (i in size - 1 downTo 0) {
                val item = it[i]
                val itemId = item.itemId
                if (itemId != android.R.id.cut &&
                    itemId != android.R.id.copy &&
                    itemId != android.R.id.selectAll &&
                    itemId != android.R.id.paste
                ) {
                    it.removeItem(itemId)
                }
            }
        }
        return true
    }

    override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
        Log.d(TAG, "onPrepareActionMode() called with: mode = $mode, menu = $menu")
        return true
    }

    override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
        Log.d(TAG, "onActionItemClicked() called with: mode = $mode, item = $item")
        return true
    }

    override fun onDestroyActionMode(mode: ActionMode?) {
        Log.d(TAG, "onDestroyActionMode() called with: mode = $mode")
    }
}