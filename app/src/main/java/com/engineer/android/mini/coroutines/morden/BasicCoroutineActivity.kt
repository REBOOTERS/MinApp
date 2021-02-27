package com.engineer.android.mini.coroutines.morden

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*

/**
 * Created on 2021/2/21.
 * @author rookie
 */
class BasicCoroutineActivity: AppCompatActivity(),CoroutineScope by MainScope() {

    fun launchFromMainScope() {
        launch {
            val deferred = async(Dispatchers.IO) {
                // network request
                delay(3000)
                "Get it"
            }
//            mainScope.text = deferred.await()
            Toast.makeText(applicationContext, "MainScope", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        cancel()
    }
}