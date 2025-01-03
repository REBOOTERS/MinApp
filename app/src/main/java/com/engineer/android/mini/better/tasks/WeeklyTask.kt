package com.engineer.android.mini.better.tasks

import java.util.Calendar

class WeeklyTask(
    private val schedule: Map<Int, String>, // 每天对应的时间表，格式为 "HH:mm"
    private val task: () -> Unit
) {

    fun runTaskIfScheduled() {
        val calendar = Calendar.getInstance()
        val currentDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK) // 获取当前是星期几
        val currentTime = String.format(
            "%02d:%02d", calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE)
        )

        if (schedule.containsKey(currentDayOfWeek)) {
            val scheduledTime = schedule[currentDayOfWeek]
            if (currentTime == scheduledTime) {
                task() // 执行任务
            } else {
                println("现在是 $currentTime，不是执行任务的时间。")
            }
        } else {
            println("今天不是执行任务的日子。")
        }
    }
}
