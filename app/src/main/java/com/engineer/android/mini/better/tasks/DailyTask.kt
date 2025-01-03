package com.engineer.android.mini.better.tasks

import java.util.Calendar

class DailyTask(
    private val schedule: String, // 每天对应的时间表，格式为 "HH:mm"
    private val task: () -> Unit
) {

    fun runTaskIfScheduled() {
        val calendar = Calendar.getInstance()
        val currentTime = String.format(
            "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)
        )

        if (currentTime == schedule) {
            task() // 执行任务
        } else {
            println("现在是 $currentTime，不是执行任务的时间。")
        }
    }
}


