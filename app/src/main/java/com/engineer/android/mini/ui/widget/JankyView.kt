/*
 * Copyright 2021 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.engineer.android.mini.ui.widget

import android.content.Context
import android.graphics.Canvas
import android.util.AttributeSet
import android.util.Log
import android.view.View
import androidx.tracing.trace
import kotlin.random.Random

/**
 * This custom view is used to inject an artificial, random delay during drawing, to simulate
 * jank on the UI thread.
 */
class JankyView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0,
    defStyleRes: Int = 0
) : View(context, attrs, defStyleAttr, defStyleRes) {

    override fun onDraw(canvas: Canvas) {
//        Log.e("JankStatsSample","JankyView onDraw")
        simulateJank()
        super.onDraw(canvas)
    }

    fun simulateJank(
        jankProbability: Double = 0.3,
        extremeJankProbability: Double = 0.02
    ) {
        val probability = Random.nextFloat()

        if (probability > 1 - jankProbability) {
            val delay = if (probability > 1 - extremeJankProbability) {
                Random.nextLong(500, 700)
            } else {
                Random.nextLong(32, 82)
            }

            try {
                // Make jank easier to spot in the profiler through tracing.
                trace("Jank Simulation") {
                    Thread.sleep(delay)
                }
            } catch (e: Exception) {
            }
        }
    }

}
