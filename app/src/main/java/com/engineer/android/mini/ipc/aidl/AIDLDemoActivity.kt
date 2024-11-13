package com.engineer.android.mini.ipc.aidl

import android.content.Intent
import android.os.*
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.engineer.android.mini.R
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ipc.aidl.impls.IResponseListenerImpl
import com.engineer.android.mini.ipc.manager.BindServiceCallback
import com.engineer.android.mini.ipc.manager.BinderBridge
import com.engineer.android.mini.ipc.manager.ServiceHelper

private const val TAG = "AIDLDemoActivity"

// debug log-tag  BookManagerService|ServiceBinder|AIDLDemoActivity|MyCallback
class AIDLDemoActivity : AppCompatActivity() {

    private var isBookServiceRegistered = false

    private var count = 0

    private var textView: TextView? = null
    private var button: Button? = null

    val mainHandler = Handler(Looper.getMainLooper())
    private var remoteIntent: Intent? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.e(TAG, "onCreate() called on thread :${Process.myPid()}-${Thread.currentThread().id}")
        setContentView(R.layout.activity_aidldemo)

        findViewById<View>(R.id.aidl).setOnClickListener {
            startBookManger()
        }

        findViewById<View>(R.id.startRequest).setOnClickListener {
            if (isBookServiceRegistered.not()) {
                "service 未启动".toast()
                return@setOnClickListener
            }
            val bookInterface = getIBookInterface()
            bookInterface?.let {
                it.unRegisterIResponseListener(IResponseListenerImpl)
                it.registerIResponseListener(IResponseListenerImpl)
                it.startRequest(System.currentTimeMillis().toString())
            }
        }

        findViewById<View>(R.id.start_other_app_service).setOnClickListener {
            if (isBookServiceRegistered.not()) {
                "service 未启动".toast()
                return@setOnClickListener
            }
            getIBookInterface()?.startOtherAppService()
        }

        findViewById<View>(R.id.start_other_app).setOnClickListener {
            if (isBookServiceRegistered.not()) {
                "service 未启动".toast()
                return@setOnClickListener
            }
            getIBookInterface()?.startOtherApp()
        }

        findViewById<View>(R.id.add_book).setOnClickListener {
            if (isBookServiceRegistered.not()) {
                "service 未启动".toast()
                return@setOnClickListener
            }
            count++
            val book = Book(count, "人类群星闪耀时$count")
            try {
                Log.e(TAG, "onViewClicked() called add book $book")
                getIBookInterface()?.addBook(book)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
        findViewById<View>(R.id.get_book).setOnClickListener {
            if (isBookServiceRegistered.not()) {
                "service 未启动".toast()
                return@setOnClickListener
            }
            try {
                val books = getIBookInterface()?.bookList
                Log.e(TAG, "onViewClicked() called get book $books")
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
        findViewById<View>(R.id.del_book).setOnClickListener {
            if (isBookServiceRegistered.not()) {
                "service 未启动".toast()
                return@setOnClickListener
            }
            try {
                val book = Book(count, "人类群星闪耀时$count")
                Log.e(TAG, "onViewClicked() called delete book $book")
                getIBookInterface()?.deleteBook(book)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        button = findViewById(R.id.async_callback)
        button?.setOnClickListener {
            if (isBookServiceRegistered.not()) {
                "service 未启动".toast()
                return@setOnClickListener
            }
            registerAsyncCallback()
            count++
            val book = Book(count, "人类群星闪耀时$count")
            try {
                Log.e(
                    TAG, "onViewClicked() called add book to repo $book"
                )
                getIBookInterface()?.addBookToRepo(book)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
        findViewById<View>(R.id.stop_remote).setOnClickListener {
            stopRemote()
        }
        textView = findViewById(R.id.async_result)
        textView?.movementMethod = ScrollingMovementMethod.getInstance()
    }


    private fun startBookManger() {
        Log.e(TAG, "startBookManger() called")

        ServiceHelper.registerBindServiceCallback(object : BindServiceCallback {
            override fun bindSuccess() {
                Log.d(TAG, "bindSuccess() called")
                isBookServiceRegistered = true
            }

            override fun bindFail(e: String) {
                Log.d(TAG, "bindFail() called with: e = $e")
                isBookServiceRegistered = false
            }

        })
        ServiceHelper.bindService(
            this,
            "com.engineer.android.mini",
            "com.engineer.android.mini.ipc.aidl.BookManagerService"
        )
    }

    private fun registerAsyncCallback() {
        ServiceHelper.provideConnection().asyncCallback =
            object : BinderBridge.AsyncProcessCallback {
                override fun notifyBookInfo(books: List<Book>) {
                    Log.e(
                        "MyCallback",
                        "On Thread " + Thread.currentThread().name + ",notifyBookInfo() called with: books = [" + books + "] "
                    )
                    mainHandler.post {
                        textView?.text = books.toString()
                        button?.performClick()

                        Log.e(
                            "MyCallback",
                            "**************************************************************"
                        )
                    }

                }

                override fun operationSuccess(action: String) {
                    Log.e(
                        "MyCallback",
                        "On Thread " + Thread.currentThread().name + ",operationSuccess() called with: action = [" + action + "]"
                    )
                }
            }
    }


    override fun onDestroy() {
        super.onDestroy()
        if (isBookServiceRegistered) {
            unbindService(ServiceHelper.provideConnection())
            isBookServiceRegistered = false
        }
        ServiceHelper.releaseCallback()
        Log.e(TAG, "onDestroy() called")
    }


    private fun getIBookInterface(): IBookInterface? {
        return ServiceHelper.provideIBookInterface()
    }

    private fun stopRemote() {
        Log.e(TAG, "stopRemote() called")
        remoteIntent?.let {
            stopService(it)
        }
        if (isBookServiceRegistered) {
            unbindService(ServiceHelper.provideConnection())
            isBookServiceRegistered = false
        }
        ServiceHelper.releaseCallback()

    }
}