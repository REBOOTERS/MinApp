package com.engineer.third.internal

import android.content.Context
import android.util.Log
import java.io.File

object ProcessCppHelper {
    private val TAG = "ProcessCppHelper"


    private var process: Process? = null


    private const val EXECUTABLE_NAME = "libdemo.so"


    fun startCommand(context: Context) {
        val nativeDir = context.applicationInfo.nativeLibraryDir
        val executableFile = File(nativeDir, EXECUTABLE_NAME)


        if (!executableFile.exists()) {
            Log.e(TAG, "error: executable does not exist: ${executableFile.absolutePath}")
            return
        }

        val command = listOf(
            executableFile.absolutePath
        )

        val processBuilder = ProcessBuilder(command).apply {
            directory(File(nativeDir))
            redirectErrorStream(true)

        }
        process = processBuilder.start()
        startMonitorThread()
    }

    private fun startMonitorThread() {
        Log.d(TAG,"process = $process")
        Thread {
            try {
                process?.let { proc ->
                    proc.inputStream.bufferedReader().use { reader ->
                        var line: String?
                        while (reader.readLine().also { line = it } != null) {
                            Log.i(TAG, "Backend: $line")
                        }
                    }

                    val exitCode = proc.waitFor()
                    Log.i(TAG, "Backend process exited with code: $exitCode")

                }
            } catch (e: Exception) {
                Log.e(TAG, "monitor error", e)

            }
        }.apply {
            isDaemon = true
            start()
        }
    }
}