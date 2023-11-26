/*
 * Copyright 2018 The Android Open Source Project
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

package com.engineer.android.mini.jetpack.work

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.work.WorkManager
import com.example.background.Constants
import com.example.background.ImageOperations

/**
 * A [ViewModel] for [FilterActivity].
 *
 * Keeps track of pending image filter operations.
 */
class FilterViewModel(application: Application) : AndroidViewModel(application) {

    private val workManager = WorkManager.getInstance(application)

    internal val workInfo = workManager.getWorkInfosByTagLiveData(Constants.TAG_OUTPUT)

    internal fun apply(imageOperations: ImageOperations) {
        imageOperations.continuation.enqueue()
    }

    internal fun cancel() {
        workManager.cancelUniqueWork(Constants.IMAGE_MANIPULATION_WORK_NAME)
    }
}
