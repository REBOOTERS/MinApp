package com.engineer.android.mini.ui.game

import android.os.Bundle
import android.widget.SeekBar
import androidx.lifecycle.lifecycleScope
import com.engineer.android.mini.databinding.ActivityPoolGameBinding
import com.engineer.android.mini.ext.gotoActivity
import com.engineer.android.mini.ui.BaseActivity
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class PoolGameActivity : BaseActivity() {

    private enum class Group { NONE, SOLID, STRIPE }

    private data class Player(
        val name: String,
        var group: Group = Group.NONE
    )

    private lateinit var viewBinding: ActivityPoolGameBinding
    private val players = arrayOf(Player("You"), Player("Bot"))
    private var currentPlayerIndex = 0
    private var isGameOver = false
    private var remainingSolids = 7
    private var remainingStripes = 7
    private val tuningStore by lazy { PoolTuningStore(applicationContext) }

    private companion object {
        const val DEFAULT_POWER = 55
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityPoolGameBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        setupRuleState()
        setupGame()
        setupControls()
        loadPersistedSettings()
        refreshHud("Drag to aim, tap Shoot")
    }

    override fun onResume() {
        super.onResume()
        loadPersistedSettings()
    }

    private fun loadPersistedSettings() {
        lifecycleScope.launch {
            val snapshot = tuningStore.tuningFlow.first()
            applySettings(snapshot)
        }
    }

    private fun setupRuleState() {
        players[0].group = Group.NONE
        players[1].group = Group.NONE
        currentPlayerIndex = 0
        isGameOver = false
        remainingSolids = 7
        remainingStripes = 7
    }

    private fun setupGame() {
        viewBinding.poolGameView.eventListener = object : LocalPoolGameView.GameEventListener {
            override fun onStatusChanged(message: String) {
                viewBinding.statusInfo.text = message
            }

            override fun onShotResolved(result: LocalPoolGameView.ShotResult) {
                onShotResolved(result)
            }
        }

        viewBinding.poolGameView.setShotPower(viewBinding.powerBar.progress / 100f)
        applySettings(
            PoolTuningStore.TuningSnapshot(
                damping = 42,
                rail = 80,
                pocket = 50,
                headStringOnly = true
            )
        )
        viewBinding.poolGameView.resetGame()
    }

    private fun setupControls() {
        viewBinding.backButton.setOnClickListener { finish() }
        viewBinding.resetButton.setOnClickListener {
            setupRuleState()
            viewBinding.poolGameView.resetGame()
            viewBinding.powerBar.progress = DEFAULT_POWER
            viewBinding.poolGameView.setBallInHandEnabled(false)
            loadPersistedSettings()
            refreshHud("Drag to aim, tap Shoot")
        }
        viewBinding.shootButton.setOnClickListener {
            val shotTriggered = viewBinding.poolGameView.requestShot()
            if (!shotTriggered) {
                refreshHud("Can't shoot yet")
            }
        }
        viewBinding.settingsButton.setOnClickListener {
            gotoActivity(PoolSettingsActivity::class.java)
        }
        viewBinding.powerBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                val safeProgress = progress.coerceIn(10, 100)
                if (safeProgress != progress) {
                    seekBar?.progress = safeProgress
                }
                viewBinding.poolGameView.setShotPower(safeProgress / 100f)
                viewBinding.powerInfo.text = "Power ${safeProgress}%"
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) = Unit

            override fun onStopTrackingTouch(seekBar: SeekBar?) = Unit
        })
    }

    private fun applySettings(snapshot: PoolTuningStore.TuningSnapshot) {
        viewBinding.poolGameView.setBallInHandBehindHeadStringOnly(snapshot.headStringOnly)
        viewBinding.poolGameView.setPhysicsTuning(
            LocalPoolGameView.PhysicsTuning(
                linearDamping = 1.25f + (snapshot.damping / 100f) * 1.3f,
                railRestitution = 0.82f + (snapshot.rail / 100f) * 0.16f,
                pocketScale = 0.9f + (snapshot.pocket / 100f) * 0.34f
            )
        )
    }

    private fun onShotResolved(result: LocalPoolGameView.ShotResult) {
        if (isGameOver) {
            return
        }
        remainingSolids = result.remainingSolids
        remainingStripes = result.remainingStripes
        val current = players[currentPlayerIndex]
        val opponent = players[(currentPlayerIndex + 1) % 2]

        val legalHit = when (current.group) {
            Group.NONE -> result.firstObjectHit != null
            Group.SOLID -> result.firstObjectHit in 1..7 || result.firstObjectHit == 8
            Group.STRIPE -> result.firstObjectHit in 9..15 || result.firstObjectHit == 8
        }
        val noRailOrPocket = result.firstObjectHit != null &&
            !result.railTouched &&
            result.pocketedBalls.isEmpty() &&
            !result.cuePocketed
        val foul = result.cuePocketed || !legalHit || noRailOrPocket

        if (current.group == Group.NONE) {
            val firstScored = result.pocketedBalls.firstOrNull { it in 1..7 || it in 9..15 }
            if (firstScored != null) {
                current.group = if (firstScored in 1..7) Group.SOLID else Group.STRIPE
                opponent.group = if (current.group == Group.SOLID) Group.STRIPE else Group.SOLID
            }
        }

        if (result.blackPocketed) {
            val canWin = when (current.group) {
                Group.SOLID -> result.remainingSolids == 0
                Group.STRIPE -> result.remainingStripes == 0
                Group.NONE -> false
            }
            val winner = if (!foul && canWin) current else opponent
            isGameOver = true
            refreshHud("${winner.name} wins")
            return
        }

        val scoredOwnBall = when (current.group) {
            Group.NONE -> result.pocketedBalls.any { it in 1..7 || it in 9..15 }
            Group.SOLID -> result.pocketedBalls.any { it in 1..7 }
            Group.STRIPE -> result.pocketedBalls.any { it in 9..15 }
        }

        val keepTurn = !foul && scoredOwnBall
        if (!keepTurn) {
            currentPlayerIndex = (currentPlayerIndex + 1) % 2
        }

        if (foul) {
            viewBinding.poolGameView.setBallInHandEnabled(true)
        } else {
            viewBinding.poolGameView.setBallInHandEnabled(false)
        }

        val roundMessage = buildString {
            if (result.pocketedBalls.isNotEmpty()) {
                append("Pocketed: ${result.pocketedBalls.joinToString(",")}. ")
            }
            if (foul) {
                append("Foul. ")
            }
            if (noRailOrPocket) {
                append("No rail after contact. ")
            }
            if (foul) {
                append("Ball in hand. ")
            }
            append(if (keepTurn) "Keep turn" else "Turn switched")
        }
        refreshHud(roundMessage)
    }

    private fun refreshHud(message: String) {
        val p1 = players[0]
        val p2 = players[1]
        viewBinding.playerLeftName.text = "${p1.name} ${renderGroup(p1.group)}"
        viewBinding.playerRightName.text = "${p2.name} ${renderGroup(p2.group)}"

        val leftActive = currentPlayerIndex == 0 && !isGameOver
        val rightActive = currentPlayerIndex == 1 && !isGameOver
        viewBinding.playerLeftCard.alpha = if (leftActive) 1f else 0.65f
        viewBinding.playerRightCard.alpha = if (rightActive) 1f else 0.65f

        val turnLabel = if (isGameOver) "Game over" else "Turn: ${players[currentPlayerIndex].name}"
        viewBinding.turnInfo.text = turnLabel
        val remainText = "Solids: $remainingSolids  Stripes: $remainingStripes"
        viewBinding.ballInfo.text = remainText
        viewBinding.aimInfo.text = if (viewBinding.poolGameView.canShootNow()) {
            "Drag to aim, tap Shoot"
        } else {
            "Wait for balls to stop"
        }
        viewBinding.statusInfo.text = message
    }


    private fun renderGroup(group: Group): String {
        return when (group) {
            Group.NONE -> "(Open)"
            Group.SOLID -> "(Solid)"
            Group.STRIPE -> "(Stripe)"
        }
    }
}