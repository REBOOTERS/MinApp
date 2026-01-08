package com.engineer.third.internal

import android.content.Context
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import java.io.File
import java.util.concurrent.TimeUnit

object ProcessCppHelper {
    private val TAG = "ProcessCppHelper"


    private var process: Process? = null


    private const val EXECUTABLE_NAME = "server.so"


    fun startCommand(context: Context) {
        Log.d(TAG, "startCommand called")
        val nativeDir = context.applicationInfo.nativeLibraryDir
        val executableFile = File(nativeDir, EXECUTABLE_NAME)

        Log.d(TAG, "executable path = ${executableFile.absolutePath}")
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
        Log.d(TAG, "process = $process")
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

    @RequiresApi(Build.VERSION_CODES.O)
    fun stopBackend() {
        Log.i(TAG, "to stop backend")
        process?.let { proc ->
            try {
                proc.destroy()

                if (!proc.waitFor(5, TimeUnit.SECONDS)) {
                    proc.destroyForcibly()
                }

                Log.i(TAG, "process end, code: ${proc.exitValue()}")

            } catch (e: Exception) {
                Log.e(TAG, "error", e)
            } finally {
                process = null
            }
        }
    }
}