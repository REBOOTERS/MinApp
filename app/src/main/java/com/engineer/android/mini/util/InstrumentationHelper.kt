package com.engineer.android.mini.util

import android.app.Instrumentation
import android.view.KeyEvent

object InstrumentationHelper {

    private val instrumentation = Instrumentation()

    fun sendBackKey() {
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_BACK)
    }

    fun openAppSwitch() {
        instrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_APP_SWITCH)
    }
}