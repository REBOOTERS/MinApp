package com.engineer.android.mini.ipc.aidl

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.*
import android.text.method.ScrollingMovementMethod
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.engineer.android.mini.R
import io.reactivex.disposables.Disposable
import java.lang.ref.WeakReference

private const val TAG = "AIDLDemoActivity"

class AIDLDemoActivity : AppCompatActivity() {

    private var isBookServiceRegistered = false

    private var mIBookInterface: IBookInterface? = null
    private var myCallback: MyCallback? = null

    private var count = 0

    private var textView: TextView? = null
    private var button: Button? = null

    val mainHandler = Handler(Looper.getMainLooper())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aidldemo)

        findViewById<View>(R.id.aidl).setOnClickListener {
            startBookManger()
        }

        findViewById<View>(R.id.add_book).setOnClickListener {
            if (isBookServiceRegistered.not()) {
                return@setOnClickListener
            }
            count++
            val book =
                Book(count, "人类群星闪耀时$count")
            try {
                Log.e(TAG, "onViewClicked() called add book $book")
                mIBookInterface?.addBook(book)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
        findViewById<View>(R.id.get_book).setOnClickListener {
            if (isBookServiceRegistered.not()) {
                return@setOnClickListener
            }
            try {
                val books =
                    mIBookInterface?.bookList
                Log.e(TAG, "onViewClicked() called get book $books")
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
        findViewById<View>(R.id.del_book).setOnClickListener {
            if (isBookServiceRegistered.not()) {
                return@setOnClickListener
            }
            try {
                val book =
                    Book(count, "人类群星闪耀时$count")
                Log.e(TAG, "onViewClicked() called delete book $book")
                mIBookInterface?.deleteBook(book)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        button = findViewById(R.id.async_callback)
        button?.setOnClickListener {
            if (isBookServiceRegistered.not()) {
                return@setOnClickListener
            }
            count++
            val book =
                Book(count, "人类群星闪耀时$count")
            try {
                Log.e(
                    TAG,
                    "onViewClicked() called add book to repo $book"
                )
                mIBookInterface?.addBookToRepo(book)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
        textView = findViewById(R.id.async_result)
        textView?.movementMethod = ScrollingMovementMethod.getInstance()
    }

    private class MyCallback constructor(activity: AIDLDemoActivity) :
        IBookInfoCallback.Stub() {

        private val activityWeakReference: WeakReference<AIDLDemoActivity> = WeakReference(activity)

        override fun notifyBookInfo(books: List<Book>) {
            val activity: AIDLDemoActivity? = activityWeakReference.get()
            if (activity != null) {
                Log.e(
                    "MyCallback", "On Thread " + Thread.currentThread().name
                            + ",notifyBookInfo() called with: books = [" + books + "] "
                )
                activity.mainHandler.post {
                    activity.textView?.text = books.toString()
                    activity.button?.performClick()
                }
            }
        }

        override fun operationSuccess(action: String) {
            val activity = activityWeakReference.get()
            activity?.let {
                it.mainHandler.post {
                    Toast.makeText(it, "action = $action", Toast.LENGTH_SHORT).show()
                }
                Log.e(
                    "MyCallback", "On Thread " + Thread.currentThread().name
                            + ",operationSuccess() called with: action = [" + action + "]"
                )
            }

        }

    }

    private val mBookconn: ServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            mIBookInterface = IBookInterface.Stub.asInterface(service)
            Log.e(
                TAG, "onServiceConnected: thread on "
                        + Thread.currentThread().name
            )
            if (myCallback == null) {
                myCallback = MyCallback(this@AIDLDemoActivity)
            }
            try {
                mIBookInterface?.registerCallback(myCallback)
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.e(TAG, "onServiceDisconnected() called with: name = [$name]")
            releaseCallback()
        }
    }

    private fun startBookManger() {
        Log.e(TAG, "startBookManger() called")
        val intent = Intent(this, BookManagerService::class.java)
        isBookServiceRegistered = bindService(intent, mBookconn, BIND_AUTO_CREATE)
    }


    override fun onDestroy() {
        super.onDestroy()
        if (isBookServiceRegistered) {
            unbindService(mBookconn)
        }
        releaseCallback()
        Log.e(TAG, "onDestroy() called")
    }

    private fun releaseCallback() {
        if (mIBookInterface != null && myCallback != null) {
            try {
                mIBookInterface?.unregisterCallback(myCallback)
                myCallback = null
            } catch (e: RemoteException) {
                e.printStackTrace()
            }
        }
    }
}