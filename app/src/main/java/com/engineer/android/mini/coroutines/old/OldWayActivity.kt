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
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.engineer.android.mini.R
import com.engineer.android.mini.ext.toast
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_old_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*
import java.lang.Exception
import kotlin.random.Random

/**
 * Show layout.activity_main and setup data binding.
 */
private const val TAG = "Coroutines"

class OldWayActivity : AppCompatActivity() {


    private var mainScope: CoroutineScope? = null

    /**
     * Inflate layout.activity_main and setup data binding.
     */
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_old_main)
        mainScope = MainScope()
        val title: TextView = findViewById(R.id.title)
        val taps: Button = findViewById(R.id.taps)
        val spinner: ProgressBar = findViewById(R.id.spinner)

        // Get MainViewModel by passing a database to the factory
        val database = getDatabase(this)
        val repository = TitleRepository(getNetworkService(), database.titleDao)
        val viewModel = ViewModelProvider(this, MainViewModel.FACTORY(repository))
            .get(MainViewModel::class.java)

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
                Snackbar.make(rootLayout, text, Snackbar.LENGTH_SHORT).show()
                viewModel.onSnackbarShown()
            }
        }

        handle.setOnClickListener {
            try {
                mainScope?.launch {
                    val start = System.currentTimeMillis()
                    printThreadName()
                    val x = mockNet(10)
                    lg(x)
                    val y = mockNet(30)
                    lg(y)
                    val z = mockNet(-1)
//                lg(z)
                    val result = x + y
                    printThreadName()
                    printMethodCost(start)
                    "result is $result".toast()
                }
            } catch (e: Exception) {
                Log.e(TAG, "catch a coroutine ex $e")
            }

        }

        useAwait.setOnClickListener {
            mainScope?.launch(Dispatchers.Unconfined) {
                val start = System.currentTimeMillis()
                printThreadName()
                val x = async { mockNet(10) }
                val y = async { mockNet(30) }
                val result = x.await() + y.await()
                printThreadName()
                printMethodCost(start)
                withContext(Dispatchers.Main) {
                    "result is $result".toast()
                }

            }
        }

        useFlow.setOnClickListener {
            lifecycleScope.launchWhenResumed {
                createFlow()
                    .flowOn(Dispatchers.IO)
                    .catch {

                    }
                    .onCompletion {
                        Log.e(TAG, "onCompletion")
                    }
                    .collect {
                        Log.e(TAG, "collect ,it = $it")
                    }
            }
        }
    }

    private fun createFlow(): Flow<Int> {
        return (1..10).asFlow()
    }

    private suspend fun mockNet(input: Int): Int {
        withContext(Dispatchers.Default) {
            delay(1000)
            printThreadName()
        }
        if (input > 0) {
            return Random(input).nextInt()
        } else {
            throw Exception("error")
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mainScope?.cancel()
    }

    private fun printThreadName() {
        Log.e(TAG, "thread == ${Thread.currentThread().name}")
    }

    private fun lg(value: Int) {
        Log.e(TAG, "value == $value")
    }

    private fun printMethodCost(start: Long) {
        Log.e(TAG, "cost time = ${System.currentTimeMillis() - start}")
    }
}
