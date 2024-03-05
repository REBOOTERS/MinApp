package com.engineer.android.mini.jetpack.work

import android.app.Application
import android.content.Context
import android.text.TextUtils
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.work.BackoffPolicy
import androidx.work.Constraints
import androidx.work.CoroutineWorker
import androidx.work.Data
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.ExistingWorkPolicy
import androidx.work.ForegroundInfo
import androidx.work.NetworkType
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.PeriodicWorkRequest
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.engineer.android.mini.R
import com.engineer.android.mini.util.JsonUtil
import com.engineer.android.mini.util.TimeUtil
import com.example.background.workers.createNotification
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.TimeUnit

// 这里我们定义一个需要周期性执行的工作，每天上传用户日志

private const val TAG = "WorkManagerPlayground"

const val INPUT_TAG = "input"
const val OUTPUT_TAG = "result"
const val WORK_TAG = "upload_user_log"
const val WORKER_LOG = "worker_log"

class UploadUserLogWork(appContext: Context, workerParameters: WorkerParameters) :
    CoroutineWorker(appContext, workerParameters) {
    override suspend fun doWork(): Result {
        val userId = inputData.getString(INPUT_TAG)

        if (TextUtils.isEmpty(userId)) {
            return Result.failure(Data.Builder().putString(OUTPUT_TAG, "userId is null").build())
        }

        val result = uploadLog(applicationContext, userId)
        return if (result != null) {
            val output = Data.Builder().putString(OUTPUT_TAG, result).build()
            Result.success(output)
        } else {
            Result.retry()
        }
    }

    override suspend fun getForegroundInfo(): ForegroundInfo {
        return ForegroundInfo(
            1, createNotification(
                applicationContext, id, applicationContext.getString(R.string.app_name)
            )
        )
    }
}

private suspend fun uploadLog(context: Context, userId: String?): String? {

    return withContext(Dispatchers.IO) {
        Log.i(TAG, "start upload")
        recordLog(context, "start upload")
        delay(20000)
        if (System.currentTimeMillis().toInt() % 2 == 0) {
            Log.i(TAG, "upload fail")
            recordLog(context, "upload fail")
            null
        } else {
            Log.i(TAG, "finish upload")
            recordLog(context, "finish upload")
            "${userId}_${System.currentTimeMillis()}"
        }
    }
}

fun createWorkRequest(userId: String?): PeriodicWorkRequest {
    val constraints = Constraints.Builder().setRequiredNetworkType(NetworkType.UNMETERED).setRequiresCharging(true)
        .setRequiresBatteryNotLow(true).build()


    return PeriodicWorkRequestBuilder<UploadUserLogWork>(25, TimeUnit.MINUTES).setInputData(
        workDataOf(
            INPUT_TAG to userId
        )
    ).setConstraints(constraints).setBackoffCriteria(BackoffPolicy.LINEAR, 3, TimeUnit.SECONDS).addTag(WORK_TAG).build()
}

fun triggerWork(application: Application) {
    val request = createWorkRequest("123")
    WorkManager.getInstance(application).enqueueUniquePeriodicWork(
        WORK_TAG, ExistingPeriodicWorkPolicy.KEEP, request
    )
}

class WorkManagerViewModel(app: Application) : AndroidViewModel(app) {
    private val workManager = WorkManager.getInstance(app)

    val cleanWorkInfo = workManager.getWorkInfosByTagLiveData("cleanWork")
}

fun recordLog(context: Context, msg: String) {
    val key = "recordLog"
    val json = context.getSharedPreferences(WORKER_LOG, Context.MODE_PRIVATE).getString(key, "")
    val records: ArrayList<LogRecord>
    if (TextUtils.isEmpty(json)) {
        records = ArrayList()
    } else {
        records = ArrayList(JsonUtil.strToObjArray(json!!, LogRecord::class.java))
    }
    val record = LogRecord(TimeUtil.getTime(System.currentTimeMillis()), msg)
    records.add(record)
    val list = JsonUtil.jsonToString(records)
    Log.d(TAG, "list is $list")
    context.getSharedPreferences(WORKER_LOG, Context.MODE_PRIVATE).edit().putString(key, list).apply()
}

data class LogRecord(val time: String, val tag: String)

object WorkUtil {

    fun clean(context: Context, path: String) {
        val request =
            OneTimeWorkRequestBuilder<CleanWork>().addTag("cleanWork").setInputData(workDataOf("path" to path)).build()

        WorkManager.getInstance(context).enqueueUniqueWork("clean", ExistingWorkPolicy.REPLACE, request)
    }

    private fun cleanOutDateFile(path: String): Boolean {
        Log.e("cleanWork", "cleanOutDateFile = $path")
        val file = File(path)
        Log.e("cleanWork", "cleanOutDateFile = ${file.exists()}")
        return if (file.exists()) {
            file.delete()
            true
        } else {
            false
        }
    }


    class CleanWork(appContext: Context, workerParameters: WorkerParameters) : Worker(appContext, workerParameters) {
        override fun doWork(): Result {
            val path = inputData.getString("path")
            Log.e("cleanWork", "path is $path")
            return if (TextUtils.isEmpty(path)) {
                Result.failure(Data.Builder().putString("result", "filepath is null").build())
            } else {
                if (cleanOutDateFile(path!!)) {
                    Result.success()
                } else {
                    Result.failure(Data.Builder().putString("result", "file not exist").build())
                }
            }
        }
    }
}
