package com.engineer.android.mini.ui.behavior

import android.Manifest
import android.annotation.SuppressLint
import android.app.DownloadManager
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.*
import android.provider.MediaStore
import android.util.Log
import android.util.LogPrinter
import android.view.View
import android.view.WindowManager
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityBehaviorBinding
import com.engineer.android.mini.ext.gotoActivity
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.jetpack.work.FilterActivity
import com.engineer.android.mini.ui.BaseActivity
import com.engineer.android.mini.ui.fragments.GalleryType
import com.engineer.android.mini.ui.fragments.PictureBottomDialog
import com.engineer.android.mini.ui.pure.MessyActivity
import com.engineer.android.mini.util.ImageUtils
import com.engineer.common.contract.ChooserResultContract
import com.engineer.common.contract.PickFileResultContract
import com.engineer.common.utils.AndroidFileUtils
import com.engineer.common.utils.SystemTools
import com.google.android.material.snackbar.Snackbar
import com.permissionx.guolindev.PermissionX
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.io.BufferedInputStream
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.lang.reflect.Field
import java.net.HttpURLConnection
import java.net.URL
import kotlin.concurrent.thread

private const val TAG = "BehaviorActivity_TAG"


/**
 * https://mp.weixin.qq.com/s/jcnFN73d002OfRXRx6u3yA
 */

@AndroidEntryPoint
class BehaviorActivity : BaseActivity() {
    private lateinit var viewBinding: ActivityBehaviorBinding
    private val mainScope = MainScope()

    private val pickPictureCallback = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
        if (uri == null) Log.e(TAG, "Invalid input image Uri.")
        else startActivity(FilterActivity.newIntent(this, uri))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityBehaviorBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
//            setUpStorageInfo()
        }
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        Log.e(TAG, "onCreate: dir =$dir")

        viewBinding.markdown.setOnClickListener {
            gotoActivity(MDActivity::class.java)
        }
        viewBinding.snackBar.setOnClickListener {
            showSnackBar()
        }

        viewBinding.filterWork.setOnClickListener {
            requestMediaPermission {
                pickPictureCallback.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
            }
        }

        viewBinding.saveToBox.setOnClickListener {
            mainScope.launch(Dispatchers.IO) {
                val fileName = "current.txt"
                val result = AndroidFileUtils.saveFileToBox(
                    it.context, this::class.java.toString(), fileName
                )
                mainScope.launch {
                    Log.i(TAG, result)
                    result.toast()
                }
            }

        }

        viewBinding.screenCapture.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                window.addFlags(WindowManager.LayoutParams.FLAG_SECURE)
            } else {
                window.clearFlags(WindowManager.LayoutParams.FLAG_SECURE)
            }
        }

        viewBinding.tree.setOnClickListener { gotoActivity(TreeActivity::class.java) }
        viewBinding.fish.setOnClickListener { gotoActivity(FishActivity::class.java) }
        viewBinding.pictureQuery.setOnClickListener {
            requestMediaPermission {
                PictureBottomDialog(GalleryType.PHOTO).show(supportFragmentManager, "picture")
            }
        }
        viewBinding.gifQuery.setOnClickListener {
            requestMediaPermission {
                PictureBottomDialog(GalleryType.GIF).show(supportFragmentManager, "picture")
            }
        }

        viewBinding.audioRecord.setOnClickListener {
            gotoActivity(AudioRecorderActivity::class.java)
        }

        viewBinding.notificationCase.setOnClickListener {
            gotoActivity(NotifyActivity::class.java)
        }
        viewBinding.broadcastReceiverCase.setOnClickListener {
            gotoActivity(BroadcastPage::class.java)
        }
        viewBinding.contentProviderCase.setOnClickListener {
            gotoActivity(MessyActivity::class.java)
        }

        viewBinding.webViewCase.setOnClickListener {
            gotoActivity(WebViewActivity::class.java)
        }

        viewBinding.deeplink.setOnClickListener {
            startTheApp()
        }

        viewBinding.addImageToAlbum.setOnClickListener {
            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.phone)
            val displayName = "${System.currentTimeMillis()}.jpg"
            val mimeType = "image/jpeg"
            val compressFormat = Bitmap.CompressFormat.JPEG
            addBitmapToAlbum(bitmap, displayName, mimeType, compressFormat)
        }
        viewBinding.downloadFile.setOnClickListener {
            val fileUrl = "http://guolin.tech/android.txt"
            val fileName = "android.txt"
            downloadFile(fileUrl, fileName)
        }
        viewBinding.pickFile.setOnClickListener {
            pickFileAndCopyUriToExternalFilesDir()
        }

        viewBinding.pickGif.setOnClickListener {
            pickGifAndCopyUriToExternal()
        }

        viewBinding.pickChooser.setOnClickListener {
            chooserTest()
        }

        viewBinding.settings.setOnClickListener {
            gotoActivity(SettingsActivity::class.java)
        }

        viewBinding.useHideApi.setOnClickListener {
            val downloadManager: DownloadManager = getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            extracted(downloadManager)
        }

        viewBinding.handler.setOnClickListener {
            Log.e(TAG, "\n")
            val h = Handler(Looper.getMainLooper()) { msg ->
                Log.e(
                    TAG, "handleMessage() called in ${Thread.currentThread().name} with: msg = $msg" + ",${msg.target}"
                )
                true
            }

            val handlerThread = HandlerThread("subThread")
            handlerThread.start()
            handlerThread.looper.setMessageLogging(LogPrinter(Log.DEBUG, "ActivityThread"))
            val subHandler = Handler(handlerThread.looper) { msg ->
                Log.e(
                    TAG, "handleMessage() called in ${Thread.currentThread().name} with: msg = $msg " + ",${msg.target}"
                )
                // 为了方便调试多次方法，正常情况下，用完后记得立即关闭
//                handlerThread.quitSafely()
                true
            }

            Thread {
                val msg1 = Message.obtain()
                msg1.what = 1000
                msg1.obj = "1000"
                h.sendMessage(msg1)

                val msg2 = Message.obtain()
                msg2.what = 2000
                msg2.obj = "2000"
                subHandler.sendMessage(msg2)
            }.start()


            h.sendEmptyMessageDelayed(100, 1000)
            subHandler.sendEmptyMessageDelayed(200, 1000)
            System.currentTimeMillis()


//            viewBinding.handler.postDelayed({
//                "delay 3000".toast()
//            }, 3000)

        }

        viewBinding.threadLocal.setOnClickListener {
            val tag = "threadLocal"

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                ThreadLocal.withInitial { 0 }
            }
            val threadLocal = ThreadLocal<Int>()
            val threadLocal2 = ThreadLocal<String>()
            val threadLocal3 = ThreadLocal<Boolean>()

            threadLocal.set(-1)
            threadLocal2.set(this::class.java.name)

            fun printAll() {
                Log.e(tag, "${Thread.currentThread().name} : threadLocal = ${threadLocal.get()}")
                Log.e(tag, "${Thread.currentThread().name} : threadLocal2 = ${threadLocal2.get()}")
                Log.e(tag, "${Thread.currentThread().name} : threadLocal3 = ${threadLocal3.get()}")
            }

            val t1 = Thread {
                threadLocal.set(1)
                threadLocal2.set("22222")
                threadLocal3.set(true)
                printAll()
            }

            val t2 = Thread {
                threadLocal.set(2)
                threadLocal3.set(false)
                printAll()
            }

            t1.start()
            Thread.sleep(1000)
            t2.start()

            t1.join()
            t2.join()

            printAll()
//            Log.e(tag, "${Thread.currentThread().name} : threadLocal = ${threadLocal.get()}")

            val magicNumberStr = "0x61c88647"
            val magicNum = magicNumberStr.substring(2)
            val value = Integer.parseInt(magicNum, 16)
            val valueBin = Integer.toBinaryString(value)
        }
    }

    private fun showSnackBar() {
        Snackbar.make(viewBinding.rootScrollerView, getString(R.string.long_chinese_content), Snackbar.LENGTH_SHORT)
            .setAction("OPEN", object : View.OnClickListener {
                override fun onClick(v: View?) {
                    "OPEN CLICKED".toast()
                }
            }).show()
    }

    @SuppressLint("SoonBlockedPrivateApi")
    private fun extracted(downloadManager: DownloadManager) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {

            try {
                val fields: Array<Field> = downloadManager.javaClass.declaredFields
                fields.forEach {
                    it.isAccessible = true
                    Log.e(TAG, "it = ${it.name}: ${it.get(downloadManager)}")
                }
                val mAccessFilename: Field = downloadManager.javaClass.getDeclaredField("mAccessFilename")
                mAccessFilename.isAccessible = true
                Log.e(TAG, "before hack value is $mAccessFilename")

                val method = downloadManager.javaClass.getDeclaredMethod(
                    "setAccessFilename", Boolean::class.java.componentType
                )
                method.isAccessible = true
                method.invoke(downloadManager, true)

                Log.e(TAG, "after hack value is $mAccessFilename")
            } catch (e: Exception) {
                Log.e(TAG, "e = $e")
                e.message.toast()
            }

        }
    }

    private fun chooserTest() {
        chooserLauncher.launch("hhh")
    }

    /**
     *  deep link 的方式，打开另外一个 App
     */
    private fun startTheApp() {
        val intent = Intent()
        intent.data = Uri.parse("phoenix://easy_link")
        intent.putExtra("msg", "from_mini_app")
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        val resolveInfo = packageManager.resolveActivity(intent, PackageManager.GET_RESOLVED_FILTER)
        startActivity(intent)
//        if (resolveInfo?.activityInfo?.exported == true) {
//            startActivity(intent)
//        } else {
//            "can't open ".toast()
//        }
    }


    private fun addBitmapToAlbum(
        bitmap: Bitmap, displayName: String, mimeType: String, compressFormat: Bitmap.CompressFormat
    ) {
        val values = ContentValues()
        values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
        values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
        } else {
            values.put(
                MediaStore.MediaColumns.DATA,
                "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}/$displayName"
            )
        }
        val uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            val outputStream = contentResolver.openOutputStream(uri)
            if (outputStream != null) {
                bitmap.compress(compressFormat, 100, outputStream)
                outputStream.close()
                Toast.makeText(this, "Add bitmap to album succeeded.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun downloadFile(fileUrl: String, fileName: String) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            Toast.makeText(
                this, "You must use device running Android 10 or higher", Toast.LENGTH_SHORT
            ).show()
            return
        }
        thread {
            try {
                val url = URL(fileUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.requestMethod = "GET"
                connection.connectTimeout = 8000
                connection.readTimeout = 8000
                val inputStream = connection.inputStream
                val bis = BufferedInputStream(inputStream)
                val values = ContentValues()
                values.put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                val uri = contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values)
                if (uri != null) {
                    val outputStream = contentResolver.openOutputStream(uri)
                    if (outputStream != null) {
                        val bos = BufferedOutputStream(outputStream)
                        val buffer = ByteArray(1024)
                        var bytes = bis.read(buffer)
                        while (bytes >= 0) {
                            bos.write(buffer, 0, bytes)
                            bos.flush()
                            bytes = bis.read(buffer)
                        }
                        bos.close()
                        runOnUiThread {
                            Toast.makeText(
                                this, "$fileName is in Download directory now.", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
                bis.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun pickFileAndCopyUriToExternalFilesDir() {
        pickFileLauncher.launch("选择文件")
    }


    private fun pickGifAndCopyUriToExternal() {
        pickGifLauncher.launch("选择 Gif")
    }

    private val pickFileLauncher = registerForActivityResult(PickFileResultContract("*/*")) { result ->
        if (result != null) {
            val fileName = SystemTools.getFileNameByUri(this@BehaviorActivity, result)
//            copyUriToExternalFilesDir(result, fileName)
            fileName.toast()
            Log.d(TAG, "pick $fileName")
        }
    }

    private val pickGifLauncher = registerForActivityResult(PickFileResultContract("image/gif")) { result ->
        if (result != null) {
            val fileName = SystemTools.getFileNameByUri(this@BehaviorActivity, result)
//            copyUriToAlbumDir(this, result, fileName, "image/gif")
            fileName.toast()
        }
    }

    private val chooserLauncher = registerForActivityResult(ChooserResultContract()) { result ->
        if (result != null) {
            Log.e(TAG, result.toString())

        }
    }

    private fun copyUriToExternalFilesDir(uri: Uri, fileName: String) {
        thread {
            val inputStream = contentResolver.openInputStream(uri)
            val tempDir = getExternalFilesDir("temp")
            if (inputStream != null && tempDir != null) {
                val file = File("$tempDir/$fileName")
                val fos = FileOutputStream(file)
                val bis = BufferedInputStream(inputStream)
                val bos = BufferedOutputStream(fos)
                val byteArray = ByteArray(1024)
                var bytes = bis.read(byteArray)
                while (bytes > 0) {
                    bos.write(byteArray, 0, bytes)
                    bos.flush()
                    bytes = bis.read(byteArray)
                }
                bos.close()
                fos.close()
                runOnUiThread {
                    Toast.makeText(this, "Copy file into $tempDir succeeded.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun setUpStorageInfo() {
        val sb = StringBuilder()
        val codeCacheDir = this.codeCacheDir.absolutePath
        val cacheDir = this.cacheDir.absolutePath
        val dataDir = this.dataDir.absolutePath
        val filesDir = this.filesDir.absoluteFile
        val picDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        sb.append("codeCacheDir :$codeCacheDir").append("\n")
        sb.append("cacheDir :$cacheDir").append("\n")
        sb.append("dataDir :$dataDir").append("\n")
        sb.append("filesDir :$filesDir").append("\n")
        sb.append("picDir :$picDir").append("\n")
        viewBinding.storageInfo.text = sb
        Log.e(TAG, "setUpStorageInfo:\n $sb")
    }


    private fun copyUriToAlbumDir(
        context: Context, inputUri: Uri, displayName: String, mimeType: String
    ) {
        thread {
            val inputStream = context.contentResolver.openInputStream(inputUri)
            val values = ContentValues()
            values.put(MediaStore.MediaColumns.DISPLAY_NAME, displayName)
            values.put(MediaStore.MediaColumns.MIME_TYPE, mimeType)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                values.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DCIM)
            } else {
                values.put(
                    MediaStore.MediaColumns.DATA,
                    "${Environment.getExternalStorageDirectory().path}/${Environment.DIRECTORY_DCIM}/$displayName"
                )
            }
            val bis = BufferedInputStream(inputStream)
            val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
            if (uri != null) {
                val outputStream = context.contentResolver.openOutputStream(uri)
                if (outputStream != null) {
                    val bos = BufferedOutputStream(outputStream)
                    val buffer = ByteArray(1024)
                    var bytes = bis.read(buffer)
                    while (bytes >= 0) {
                        bos.write(buffer, 0, bytes)
                        bos.flush()
                        bytes = bis.read(buffer)
                    }
                    bos.close()
                }
            }
            bis.close()
            runOnUiThread {
                Toast.makeText(this, "Copy file into album succeeded.", Toast.LENGTH_LONG).show()
            }
        }


    }
}