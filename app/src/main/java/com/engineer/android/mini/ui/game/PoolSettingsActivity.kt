package com.engineer.android.mini.ui.game

import android.os.Bundle
import android.widget.SeekBar
import androidx.lifecycle.lifecycleScope
import com.engineer.android.mini.databinding.ActivityPoolSettingsBinding
import com.engineer.android.mini.ui.BaseActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PoolSettingsActivity : BaseActivity() {

    private lateinit var viewBinding: ActivityPoolSettingsBinding
    private val tuningStore by lazy { PoolTuningStore(applicationContext) }
    private var applyingState = false

    private companion object {
        const val DEFAULT_DAMPING = 42
        const val DEFAULT_RAIL = 80
        const val DEFAULT_POCKET = 50
        const val DEFAULT_HEAD_STRING_ONLY = true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityPoolSettingsBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        setupControls()
        loadSettings()
    }

    private fun setupControls() {
        viewBinding.backButton.setOnClickListener { finish() }
        viewBinding.closeButton.setOnClickListener { finish() }
        viewBinding.resetButton.setOnClickListener {
            applySnapshot(
                PoolTuningStore.TuningSnapshot(
                    damping = DEFAULT_DAMPING,
                    rail = DEFAULT_RAIL,
                    pocket = DEFAULT_POCKET,
                    headStringOnly = DEFAULT_HEAD_STRING_ONLY
                ),
                persist = true
            )
        }

        val watcher = object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (applyingState) return
                persistCurrentSnapshot()
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit
            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        }
        viewBinding.dampingBar.setOnSeekBarChangeListener(watcher)
        viewBinding.railBar.setOnSeekBarChangeListener(watcher)
        viewBinding.pocketBar.setOnSeekBarChangeListener(watcher)
        viewBinding.headStringSwitch.setOnCheckedChangeListener { _, _ ->
            if (applyingState) return@setOnCheckedChangeListener
            persistCurrentSnapshot()
        }
    }

    private fun loadSettings() {
        lifecycleScope.launch {
            val snapshot = tuningStore.tuningFlow.first()
            applySnapshot(snapshot, persist = false)
        }
    }

    private fun applySnapshot(snapshot: PoolTuningStore.TuningSnapshot, persist: Boolean) {
        applyingState = true
        viewBinding.dampingBar.progress = snapshot.damping
        viewBinding.railBar.progress = snapshot.rail
        viewBinding.pocketBar.progress = snapshot.pocket
        viewBinding.headStringSwitch.isChecked = snapshot.headStringOnly
        applyingState = false
        updateLabels()
        if (persist) {
            persistCurrentSnapshot()
        }
    }

    private fun persistCurrentSnapshot() {
        if (applyingState) return
        val snapshot = currentSnapshot()
        lifecycleScope.launch {
            tuningStore.save(snapshot)
        }
        updateLabels()
    }

    private fun currentSnapshot(): PoolTuningStore.TuningSnapshot {
        return PoolTuningStore.TuningSnapshot(
            damping = viewBinding.dampingBar.progress,
            rail = viewBinding.railBar.progress,
            pocket = viewBinding.pocketBar.progress,
            headStringOnly = viewBinding.headStringSwitch.isChecked
        )
    }

    private fun updateLabels() {
        val damping = 1.25f + (viewBinding.dampingBar.progress / 100f) * 1.3f
        val rail = 0.82f + (viewBinding.railBar.progress / 100f) * 0.16f
        val pocketScale = 0.9f + (viewBinding.pocketBar.progress / 100f) * 0.34f
        viewBinding.dampingInfo.text = "Damping ${(damping * 100).toInt()}"
        viewBinding.railInfo.text = "Rail ${(rail * 100).toInt()}%"
        viewBinding.pocketInfo.text = "Pocket ${(pocketScale * 100).toInt()}%"
    }
}