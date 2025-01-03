package com.engineer.android.mini.better.tasks

import android.util.Log
import java.util.Calendar
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.TimeUnit

object SchedulerTaskManager {
    private const val TAG = "SchedulerTaskManager"

    private val executorList = ArrayList<ScheduledExecutorService?>()

    fun setUpTaskByConfig(timeConfigList: ArrayList<TimeConfig>) {
        Log.d(TAG, "setUpTaskByConfig() called with: timeConfigList = $timeConfigList")
        // 首先清空列表中已有的任务
        shutDownAllTask()
        timeConfigList.forEach {
            when (it.repeatModeConfig) {
                "none" -> {
                    createOneShotTask(it.startTime)
                }

                "daily" -> {
                    createDailyTask(it.startTime)
                }

                else -> {
                    val timeSet = it.repeatModeConfig.split(",").toSet()
                    createWeeklyTask(it.startTime, timeSet)
                }
            }
        }
    }

    /**
     * 每周固定天数，需要重复执行的任务
     */
    private fun createWeeklyTask(startTime: String, days: Set<String>) {
        Log.d(TAG, "createWeeklyTask() called with: startTime = $startTime, days = $days")
        // 创建一个调度服务，定期执行任务检查
        val executor = Executors.newSingleThreadScheduledExecutor()
        val schedule = HashMap<Int, String>()

        for (day in days) {
            when (day) {
                "1" -> {
                    schedule[Calendar.MONDAY] = startTime
                }

                "2" -> {
                    schedule[Calendar.TUESDAY] = startTime
                }

                "3" -> {
                    schedule[Calendar.WEDNESDAY] = startTime
                }

                "4" -> {
                    schedule[Calendar.THURSDAY] = startTime
                }

                "5" -> {
                    schedule[Calendar.FRIDAY] = startTime
                }

                "6" -> {
                    schedule[Calendar.SATURDAY] = startTime
                }

                "7" -> {
                    schedule[Calendar.SUNDAY] = startTime
                }
            }
        }


        // 定义要执行的任务
        val task: () -> Unit = {
            println("执行任务：记录日志-weekly")
        }

        // 创建任务
        val scheduler = WeeklyTask(schedule, task)
        // 每分钟检查一次
        executor.scheduleWithFixedDelay({ scheduler.runTaskIfScheduled() }, 0, 1, TimeUnit.MINUTES)
        executorList.add(executor)
    }

    /**
     * 每天重复执行的任务
     */
    private fun createDailyTask(startTime: String) {
        Log.d(TAG, "createDailyTask() called with: startTime = $startTime")
        // 创建一个调度服务，定期执行任务检查
        val executor = Executors.newSingleThreadScheduledExecutor()

        // 定义要执行的任务
        val task: () -> Unit = {
            println("执行任务：记录日志-daily")
        }
        val scheduler = DailyTask(startTime, task)
        // 每分钟检查一次
        executor.scheduleWithFixedDelay({ scheduler.runTaskIfScheduled() }, 0, 1, TimeUnit.MINUTES)
        executorList.add(executor)
    }

    /**
     * 只执行一次的任务
     */
    private fun createOneShotTask(startTime: String) {
        Log.d(TAG, "createOneShotTask() called with: startTime = $startTime")
        // 创建一个调度服务，定期执行任务检查
        val executor = Executors.newSingleThreadScheduledExecutor()
        // 定义要执行的任务
        val task: () -> Unit = {
            println("执行任务：记录日志-oneshot")
        }
        val timeData = startTime.split(":")
        if (timeData.size < 2) {
            return
        }
        val hour = timeData[0].toInt()
        val minute = timeData[1].toInt()
        val targetTime = Calendar.getInstance()
        targetTime.set(Calendar.HOUR_OF_DAY, hour)
        targetTime.set(Calendar.MINUTE, minute)

        Log.i(TAG, "targetTime is ${targetTime.time}")
        val now = Calendar.getInstance()
        val diff = targetTime.timeInMillis - now.timeInMillis
        Log.i(TAG, "task will run ${targetTime.time} ,waiting $diff ms")
        executor.schedule(task, diff, TimeUnit.MILLISECONDS)
        executorList.add(executor)
    }

    fun shutDownAllTask() {
        for (service in executorList) {
            service?.shutdown()
        }
        executorList.clear()
    }
}