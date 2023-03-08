package com.engineer.android.mini.ocr

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.result.contract.ActivityResultContract
import com.bumptech.glide.Glide
import com.engineer.android.mini.databinding.ActivityOcrBinding
import com.engineer.android.mini.ui.BaseActivity
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.chinese.ChineseTextRecognizerOptions

/**
 * https://developers.google.com/ml-kit/vision/text-recognition/v2/android?hl=zh-cn
 */
class OcrActivity : BaseActivity() {

    private lateinit var viewBinding: ActivityOcrBinding
    private val recognizer = TextRecognition.getClient(ChineseTextRecognizerOptions.Builder().build())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityOcrBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        viewBinding.selectImage.setOnClickListener {
            chooserLauncher.launch("ocr")
        }

    }


    private val chooserLauncher = registerForActivityResult(ChooserResultContract()) { result ->
        if (result != null) {
            Log.e(TAG, result.toString())
            val uri = result[0]
            Glide.with(this).load(uri).into(viewBinding.inputImage)
            val image = InputImage.fromFilePath(this, uri)
            val result = recognizer.process(image).addOnSuccessListener {
                Log.e(TAG, "success $it")
                Log.e(TAG, "success ${it.text}")
                viewBinding.ocrResult.text = it.text

            }.addOnFailureListener {
                Log.e(TAG, "fail " + it.stackTraceToString())
            }
        }
    }

    inner class ChooserResultContract : ActivityResultContract<String, List<Uri>>() {
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
                            results.add(data.getItemAt(i).uri)
                        }
                    } else if (it.data != null) {
                        val data = it.data
                        Log.i(TAG, "data is $data")
                        results.add(data!!)
                    }
                }
            }
            return results
        }
    }
}