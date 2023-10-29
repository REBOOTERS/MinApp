/*
 * Copyright (C) 2019 Google LLC
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

package com.engineer.android.mini.coroutines.old

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.engineer.android.mini.R
import com.engineer.android.mini.databinding.ActivityOldMainBinding
import com.engineer.android.mini.ext.toast
import com.engineer.android.mini.ui.BaseActivity
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.random.Random

/**
 * Show layout.activity_main and setup data binding.
 */
private const val TAG = "Coroutines"

@AndroidEntryPoint
class DetailActivity : BaseActivity() {
    private lateinit var viewBinding: ActivityOldMainBinding

    private var mainScope: CoroutineScope? = null

    private val viewModel: MainViewModel by viewModels()


    /**
     * Inflate layout.activity_main and setup data binding.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityOldMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)
        mainScope = MainScope()


        val errorHandler = CoroutineExceptionHandler { coroutineContext, throwable ->
            throwable.message.toast()
            Log.e(TAG, "errorHandler -> " + throwable.stackTraceToString())
        }

        var job: Job? = null
        viewBinding.handle.setOnClickListener {
            job = mainScope?.launch(errorHandler) {
                job?.let {
                    Log.e(TAG, "job is ${it.isActive}")
                }
                val start = System.currentTimeMillis()
                printThreadName()
                val x = mockNet(10)
                lg(x)
                val y = mockNet(30)
                lg(y)
//                val z = mockNet(0)
//                lg(z)
                val result = x + y
                printThreadName()
                printMethodCost(start)
                "result is $result".toast()
                job?.let {
                    Log.e(TAG, "job is ${it.isActive}")
                }
            }

        }

        viewBinding.useAwait.setOnClickListener {
            mainScope?.launch(Dispatchers.Unconfined) {
                val start = System.currentTimeMillis()
                printThreadName("1")
                val x = async { mockNet(10) }
                val y = async { mockNet(30) }
                val result = x.await() + y.await()
                printThreadName("2")
                withContext(Dispatchers.Main) {
                    printThreadName("3")
                    "result is $result".toast()
                    printMethodCost(start)
                }

            }
        }

        viewBinding.useFlow.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                createFlow().flowOn(Dispatchers.IO)
//                    .map {
//                        if (it > 5) {
//                            it % 0
//                        }
//                    }
                    .catch {
                        Log.e(TAG, this.toString())
                    }.onCompletion {
                        Log.e(TAG, "onCompletion")
                    }.collect {
                        if (it > 5) {
                            it / 0
                        }
                        Log.e(TAG, "collect ,it = $it")
                    }
            }
        }

        coroutinesSimple()
    }


    private fun createFlow(): Flow<Int> {
        return (1..10).asFlow()
    }

    private suspend fun mockNet(input: Int): Int {
        withContext(Dispatchers.Default) {
            delay(1000)
            printThreadName("mockNet")
        }
        return Random.nextInt(input)
    }

    private fun coroutinesSimple() {
        val title: TextView = findViewById(R.id.title)
        val taps: Button = findViewById(R.id.taps)
        val spinner: ProgressBar = findViewById(R.id.spinner)

        // Get MainViewModel by passing a database to the factory
//        val database = getDatabase(this)
//        val repository = TitleRepository(getNetworkService(), database.titleDao)
//        val viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        // When rootLayout is clicked call onMainViewClicked in ViewModel
        taps.setOnClickListener {
            viewModel.onMainViewClicked()
        }

        // update the title when the [MainViewModel.title] changes
        viewModel.title.observe(this) { value ->
            value?.let {
                title.text = it
            }
        }

        viewModel.taps.observe(this) { value ->
            taps.text = value
        }

        // show the spinner when [MainViewModel.spinner] is true
        viewModel.spinner.observe(this) { value ->
            value.let { show ->
                spinner.visibility = if (show) View.VISIBLE else View.GONE
            }
        }

        // Show a snackbar whenever the [ViewModel.snackbar] is updated a non-null value
        viewModel.snackbar.observe(this) { text ->
            text?.let {
                Snackbar.make(viewBinding.rootLayout, text, Snackbar.LENGTH_SHORT).show()
                viewModel.onSnackbarShown()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope?.cancel()
    }

    private fun printThreadName(method: String? = "main") {
        Log.e(TAG, "in method $method , thread == ${Thread.currentThread().name}")
    }

    private fun lg(value: Int) {
        Log.e(TAG, "value == $value")
    }

    private fun printMethodCost(start: Long) {
        Log.e(TAG, "cost time = ${System.currentTimeMillis() - start}")
    }
}
