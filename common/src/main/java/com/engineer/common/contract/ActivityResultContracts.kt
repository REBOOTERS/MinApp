package com.engineer.common.contract

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract

class PickGifResultContract : ActivityResultContract<String, Uri?>() {
    override fun createIntent(context: Context, input: String): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "image/gif"
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode == Activity.RESULT_OK && intent != null) {
            return intent.data
        }
        return null
    }
}

class PickMp4ResultContract : ActivityResultContract<String, Uri?>() {
    override fun createIntent(context: Context, input: String): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "video/mp4"
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode == Activity.RESULT_OK && intent != null) {
            return intent.data
        }
        return null
    }
}

class PickFileResultContract : ActivityResultContract<String, Uri?>() {
    override fun createIntent(context: Context, input: String): Intent {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        return intent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
        if (resultCode == Activity.RESULT_OK && intent != null) {
            return intent.data
        }
        return null
    }

}

class ChooserResultContract : ActivityResultContract<String, List<Uri>>() {
    private val TAG = "ActivityResultContracts"
    override fun createIntent(context: Context, input: String): Intent {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        intent.type = "image/*"
        val finalIntent = Intent(Intent.ACTION_CHOOSER)
        finalIntent.putExtra(Intent.EXTRA_INTENT, intent)
        finalIntent.putExtra(Intent.EXTRA_TITLE, "Image Chooser")
        return finalIntent
    }

    override fun parseResult(resultCode: Int, intent: Intent?): List<Uri> {
        val results = ArrayList<Uri>()
        intent?.let {
            if (resultCode == Activity.RESULT_OK) {
                if (it.clipData != null) {
                    val data = it.clipData!!
                    Log.i(TAG, "clip is ${data.itemCount}")
                    for (i in 0 until data.itemCount) {
                        Log.i(TAG, "clip $i is ${data.getItemAt(i).uri}")
                    }
                } else if (it.data != null) {
                    val data = it.data
                    Log.i(TAG, "data is $data")
                }
            }
        }
        return results
    }
}