package com.engineer.android.mini.ui.pure

import android.graphics.BitmapFactory
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.engineer.android.mini.R
import com.engineer.android.mini.jetpack.work.FilterActivity
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.common.widget.cropper.CropImageView
import java.io.File
import java.io.InputStream

class CropActivity : BaseActivity() {

    private var currentRect: Rect = Rect(0, 0, 0, 0)
    private var x = 0
    private var y = 0
    private var w = 0
    private var h = 0

    private var outPath =
        "/storage/emulated/0/DCIM/InsTakeDownloader/angieharmon_20200601_125317_${System.currentTimeMillis()}.jpg"

    private lateinit var cropImage: CropImageView

    private val pickPictureCallback =
        registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri == null) Log.e(TAG, "Invalid input image Uri.")
            else {
                Log.i(TAG, "uri = $uri")
                cropImage.setImageUriAsync(uri)
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_crop)

        cropImage = findViewById(R.id.cropImageView)
//        cropImage.imageResource = R.drawable.wallpaper_portrait
//        val bitmap =
//            BitmapFactory.decodeFile("/storage/emulated/0/DCIM/InsTakeDownloader/angieharmon_20200601_125317_2.jpg")
//        cropImage.setImageBitmap(bitmap)

        cropImage.setOnSetCropOverlayMovedListener {
            Log.d(TAG, "onCreate() called $it")
            x = it.left
            y = it.top
            w = it.right - it.left
            h = it.bottom - it.top
            Log.e(TAG, "x=$x,y=$y,w=$w,h=$h")
            currentRect = it
        }

        findViewById<View>(R.id.select_img).setOnClickListener {
            requestMediaPermission {
                pickPictureCallback.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }


//        RxFFmpegInvoke.getInstance().setDebug(true);
//
//        findViewById<View>(R.id.confirm).setOnClickListener {
//            val text = "ffmpeg -v"
////            val command = text.split(" ").toTypedArray()
//            val command = runDelogo()
//            RxFFmpegInvoke.getInstance()
//                .runCommand(command, object : RxFFmpegInvoke.IFFmpegListener {
//                    override fun onFinish() {
//                        Log.d(TAG, "onFinish() called ${Thread.currentThread().name}")
//
//                        val resultImg = findViewById<ImageView>(R.id.result_img)
//                        cropImage.visibility = View.GONE
//                        Glide.with(this@CropActivity).load(outPath).into(resultImg)
//
//                    }
//
//                    override fun onProgress(progress: Int, progressTime: Long) {
//                        Log.d(
//                            TAG,
//                            "onProgress() called with: progress = $progress, progressTime = $progressTime"
//                        )
//                    }
//
//                    override fun onCancel() {
//                        Log.d(TAG, "onCancel() called")
//                    }
//
//                    override fun onError(message: String?) {
//                        Log.d(TAG, "onError() called with: message = $message")
//                    }
//
//                });
//
//        }
    }


    // /ffmpeg -i InputVideo.mp4 -vf delogo=x=90:y=945:w=85:h=85 OutputVideo.mp4
//    private fun runDelogo(): Array<out String>? {
//        val path = "/storage/emulated/0/DCIM/InsTakeDownloader/angieharmon_20200601_125317_2.jpg"
//
//        val list = RxFFmpegCommandList()
//        list.append("-i")
//        list.append(path)
//        list.add("-vf")
//        list.add("delogo=x=$x:y=$y:w=$w:h=$h:show=1")
//        list.add(outPath)
//        return list.build()
//    }

    override fun onDestroy() {
        super.onDestroy()
        Thread {
            val file = File(outPath)
            if (file.exists()) {
                file.delete()
            }
        }.start()
    }
}