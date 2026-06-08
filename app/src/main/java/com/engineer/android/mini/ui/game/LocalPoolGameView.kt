package com.engineer.android.mini.ui.game

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.Choreographer
import android.view.MotionEvent
import android.view.View
import android.os.SystemClock
import kotlin.math.exp
import kotlin.math.hypot
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class LocalPoolGameView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr), Choreographer.FrameCallback {

    data class PhysicsTuning(
        val linearDamping: Float = 1.65f,
        val railRestitution: Float = 0.95f,
        val cueBaseSpeed: Float = 520f,
        val cueSpeedRange: Float = 1620f,
        val pocketScale: Float = 1.0f
    )

    data class ShotResult(
        val pocketedBalls: List<Int>,
        val cuePocketed: Boolean,
        val firstObjectHit: Int?,
        val railTouched: Boolean,
        val blackPocketed: Boolean,
        val remainingObjectBalls: Int,
        val remainingSolids: Int,
        val remainingStripes: Int
    )

    interface GameEventListener {
        fun onStatusChanged(message: String)
        fun onShotResolved(result: ShotResult)
    }

    private data class Ball(
        val number: Int,
        var x: Float,
        var y: Float,
        var vx: Float,
        var vy: Float,
        var spinSide: Float,
        var spinTop: Float,
        val radius: Float,
        val color: Int,
        var active: Boolean = true
    )

    var eventListener: GameEventListener? = null

    private val tableRect = RectF()
    private val pockets = ArrayList<Pair<Float, Float>>(6)
    private val balls = ArrayList<Ball>(16)

    private var shotPower: Float = 0.55f
    private var sideSpin: Float = 0f
    private var topSpin: Float = 0f
    private var aimX = 1f
    private var aimY = 0f
    private var shotInProgress = false
    private var frameTimeNanos = 0L
    private var firstObjectHitThisShot: Int? = null
    private var cuePocketedThisShot = false
    private val pocketedThisShot = linkedSetOf<Int>()
    private var railTouchedThisShot = false
    private var ballInHand = false
    private var draggingCueBall = false
    private var ballInHandBehindHeadStringOnly = true
    private var tuning = PhysicsTuning()
    private var frameLoopActive = false
    private var shotStartTimeMs = 0L
    private var lastAimUpdateMs = 0L

    private companion object {
        const val MAX_SHOT_DURATION_MS = 6500L
        const val REST_VELOCITY_THRESHOLD = 9f
        const val AIM_UPDATE_INTERVAL_MS = 16L
    }

    private var ballRadius = 18f
    private var pocketBaseRadius = 28f
    private var pocketRadius = 28f

    private val bgPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#0d1a2f") }
    private val tablePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#165ba8") }
    private val railPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#23426b") }
    private val pocketPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { color = Color.parseColor("#111111") }
    private val linePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        style = Paint.Style.STROKE
        strokeWidth = 4f
        alpha = 220
    }
    private val ghostLinePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#80FFFFFF")
        style = Paint.Style.STROKE
        strokeWidth = 3f
    }
    private val headStringPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#66FFFFFF")
        style = Paint.Style.STROKE
        strokeWidth = 2f
    }
    private val cuePaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.parseColor("#d4b27a")
        style = Paint.Style.STROKE
        strokeWidth = 11f
    }
    private val ballPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply { style = Paint.Style.FILL }
    private val numberPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = Color.WHITE
        textAlign = Paint.Align.CENTER
        textSize = 20f
        isFakeBoldText = true
    }

    override fun onAttachedToWindow() {
        super.onAttachedToWindow()
        frameLoopActive = true
        scheduleNextFrame()
    }

    override fun onDetachedFromWindow() {
        frameLoopActive = false
        Choreographer.getInstance().removeFrameCallback(this)
        super.onDetachedFromWindow()
    }

    override fun doFrame(frameTimeNanos: Long) {
        if (!frameLoopActive) {
            return
        }
        if (this.frameTimeNanos > 0L) {
            val dt = ((frameTimeNanos - this.frameTimeNanos) / 1_000_000_000f).coerceIn(0f, 0.032f)
            updatePhysics(dt)
        }
        this.frameTimeNanos = frameTimeNanos
        invalidate()
        if (shotInProgress || ballInHand || draggingCueBall) {
            scheduleNextFrame()
        } else {
            frameLoopActive = false
        }
    }

    private fun scheduleNextFrame() {
        frameLoopActive = true
        Choreographer.getInstance().removeFrameCallback(this)
        Choreographer.getInstance().postFrameCallback(this)
    }

    fun setShotPower(value: Float) {
        shotPower = value.coerceIn(0.1f, 1f)
    }

    fun setSideSpin(value: Float) {
        sideSpin = value.coerceIn(-1f, 1f)
    }

    fun setTopSpin(value: Float) {
        topSpin = value.coerceIn(-1f, 1f)
    }

    fun setPhysicsTuning(value: PhysicsTuning) {
        tuning = value
        pocketRadius = pocketBaseRadius * tuning.pocketScale.coerceIn(0.85f, 1.3f)
    }

    fun setBallInHandEnabled(enabled: Boolean) {
        ballInHand = enabled
        draggingCueBall = false
        if (enabled) {
            eventListener?.onStatusChanged("Ball in hand: drag cue ball to place")
        }
        scheduleNextFrame()
    }

    fun setBallInHandBehindHeadStringOnly(enabled: Boolean) {
        ballInHandBehindHeadStringOnly = enabled
    }

    fun resetGame() {
        if (width == 0 || height == 0) {
            return
        }
        buildTableLayout(width, height)
        buildRack()
        shotInProgress = false
        pocketedThisShot.clear()
        firstObjectHitThisShot = null
        cuePocketedThisShot = false
        railTouchedThisShot = false
        ballInHand = false
        draggingCueBall = false
        eventListener?.onStatusChanged("Drag to aim and release to shoot")
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        if (w == 0 || h == 0) {
            return
        }
        buildTableLayout(w, h)
        buildRack()
    }

    private fun buildTableLayout(w: Int, h: Int) {
        val horizontalMargin = w * 0.055f
        val verticalMargin = h * 0.08f
        tableRect.set(
            horizontalMargin,
            verticalMargin,
            w - horizontalMargin,
            h - verticalMargin
        )

        ballRadius = min(tableRect.width(), tableRect.height()) / 34f
        pocketBaseRadius = ballRadius * 1.55f
        pocketRadius = pocketBaseRadius * tuning.pocketScale.coerceIn(0.85f, 1.3f)

        pockets.clear()
        pockets.add(tableRect.left to tableRect.top)
        pockets.add(tableRect.centerX() to tableRect.top)
        pockets.add(tableRect.right to tableRect.top)
        pockets.add(tableRect.left to tableRect.bottom)
        pockets.add(tableRect.centerX() to tableRect.bottom)
        pockets.add(tableRect.right to tableRect.bottom)

        numberPaint.textSize = ballRadius * 0.9f
        linePaint.strokeWidth = max(2f, ballRadius * 0.14f)
        cuePaint.strokeWidth = max(7f, ballRadius * 0.5f)
    }

    private fun buildRack() {
        balls.clear()

        val cueX = tableRect.left + tableRect.width() * 0.23f
        val cueY = tableRect.centerY()
        balls.add(Ball(0, cueX, cueY, 0f, 0f, 0f, 0f, ballRadius, Color.WHITE, true))

        val rackStartX = tableRect.left + tableRect.width() * 0.74f
        val rackStartY = tableRect.centerY()
        val stepX = ballRadius * 1.82f

        val colors = listOf(
            Color.parseColor("#f7c200"),
            Color.parseColor("#0b66cc"),
            Color.parseColor("#cf2a27"),
            Color.parseColor("#642d92"),
            Color.parseColor("#f28c28"),
            Color.parseColor("#1f8f4a"),
            Color.parseColor("#5b2812"),
            Color.parseColor("#111111")
        )

        var number = 1
        for (row in 0 until 5) {
            for (col in 0..row) {
                val x = rackStartX + row * stepX
                val y = rackStartY + (col - row / 2f) * (ballRadius * 2.1f)
                val color = colors[(number - 1) % colors.size]
                balls.add(Ball(number, x, y, 0f, 0f, 0f, 0f, ballRadius, color, true))
                number++
            }
        }

    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        val cueBall = balls.firstOrNull { it.number == 0 && it.active } ?: return true

        if (ballInHand) {
            handleBallInHandTouch(event, cueBall)
            return true
        }

        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN,
            MotionEvent.ACTION_MOVE -> {
                if (!canAim()) {
                    return true
                }
                if (event.actionMasked == MotionEvent.ACTION_MOVE) {
                    val now = SystemClock.uptimeMillis()
                    if (now - lastAimUpdateMs < AIM_UPDATE_INTERVAL_MS) {
                        return true
                    }
                    lastAimUpdateMs = now
                }
                val dx = event.x - cueBall.x
                val dy = event.y - cueBall.y
                val distance = hypot(dx, dy)
                if (distance > 2f) {
                    val newAimX = dx / distance
                    val newAimY = dy / distance
                    val aimChanged = kotlin.math.abs(newAimX - aimX) > 0.0025f || kotlin.math.abs(newAimY - aimY) > 0.0025f
                    if (aimChanged) {
                        aimX = newAimX
                        aimY = newAimY
                        postInvalidateOnAnimation()
                    }
                }
            }

            MotionEvent.ACTION_UP -> {
                performClick()
                if (canAim()) {
                    requestShot()
                }
            }
        }
        return true
    }

    fun requestShot(): Boolean {
        val cueBall = balls.firstOrNull { it.number == 0 && it.active } ?: return false
        if (ballInHand) {
            eventListener?.onStatusChanged("Place cue ball first")
            return false
        }
        if (!canShoot()) {
            eventListener?.onStatusChanged("Wait for balls to stop")
            return false
        }
        shootCueBall(cueBall)
        scheduleNextFrame()
        return true
    }

    private fun handleBallInHandTouch(event: MotionEvent, cueBall: Ball) {
        when (event.actionMasked) {
            MotionEvent.ACTION_DOWN -> {
                val distance = hypot(event.x - cueBall.x, event.y - cueBall.y)
                draggingCueBall = distance <= cueBall.radius * 2.1f
            }

            MotionEvent.ACTION_MOVE -> {
                if (!draggingCueBall) {
                    return
                }
                val x = event.x.coerceIn(tableRect.left + cueBall.radius, tableRect.right - cueBall.radius)
                val y = event.y.coerceIn(tableRect.top + cueBall.radius, tableRect.bottom - cueBall.radius)
                if (isCuePositionLegal(cueBall, x, y)) {
                    cueBall.x = x
                    cueBall.y = y
                    cueBall.vx = 0f
                    cueBall.vy = 0f
                }
            }

            MotionEvent.ACTION_UP -> {
                if (!draggingCueBall) {
                    return
                }
                draggingCueBall = false
                val isValid = isCuePositionLegal(cueBall, cueBall.x, cueBall.y)
                if (isValid) {
                    ballInHand = false
                    eventListener?.onStatusChanged("Cue ball placed")
                } else {
                    eventListener?.onStatusChanged("Invalid position: avoid overlap")
                }
            }
        }
    }

    private fun isCuePositionLegal(cueBall: Ball, x: Float, y: Float): Boolean {
        val headStringX = tableRect.left + tableRect.width() * 0.25f
        if (x - cueBall.radius < tableRect.left || x + cueBall.radius > tableRect.right) {
            return false
        }
        if (y - cueBall.radius < tableRect.top || y + cueBall.radius > tableRect.bottom) {
            return false
        }
        if (ballInHandBehindHeadStringOnly && x > headStringX - cueBall.radius) {
            return false
        }
        return balls.none {
            it.active && it.number != 0 && hypot(it.x - x, it.y - y) < (it.radius + cueBall.radius + 1f)
        }
    }

    override fun performClick(): Boolean {
        super.performClick()
        return true
    }

    private fun shootCueBall(cueBall: Ball) {
        val curvedPower = shotPower.pow(1.35f)
        val speed = tuning.cueBaseSpeed + curvedPower * tuning.cueSpeedRange
        cueBall.vx = aimX * speed
        cueBall.vy = aimY * speed
        cueBall.spinSide = sideSpin * (220f + 180f * shotPower)
        cueBall.spinTop = topSpin * (180f + 150f * shotPower)
        shotInProgress = true
        shotStartTimeMs = SystemClock.uptimeMillis()
        pocketedThisShot.clear()
        firstObjectHitThisShot = null
        cuePocketedThisShot = false
        railTouchedThisShot = false
        eventListener?.onStatusChanged("Shot fired")
    }

    private fun canAim(): Boolean {
        return !shotInProgress && !ballInHand
    }

    private fun canShoot(): Boolean {
        return balls.any { it.number == 0 && it.active } && isAllBallsResting()
    }

    fun canShootNow(): Boolean {
        return !ballInHand && canShoot() && !shotInProgress
    }

    private fun updatePhysics(dt: Float) {
        if (balls.isEmpty()) {
            return
        }

        balls.forEach { ball ->
            if (!ball.active) {
                return@forEach
            }

            ball.x += ball.vx * dt
            ball.y += ball.vy * dt

            if (ball.number == 0) {
                val speed = hypot(ball.vx, ball.vy)
                if (speed > 0.01f) {
                    val ux = ball.vx / speed
                    val uy = ball.vy / speed
                    val sideForce = ball.spinSide * 0.20f
                    ball.vx += -uy * sideForce * dt
                    ball.vy += ux * sideForce * dt
                }
                ball.spinSide *= exp(-3.2f * dt)
                ball.spinTop *= exp(-2.8f * dt)
            }

            val topSpinBoost = if (ball.number == 0) (-ball.spinTop / 240f) else 0f
            val damping = exp(-(tuning.linearDamping + topSpinBoost).coerceIn(1.1f, 2.6f) * dt)
            ball.vx *= damping
            ball.vy *= damping
            if (hypot(ball.vx, ball.vy) < REST_VELOCITY_THRESHOLD) {
                ball.vx = 0f
                ball.vy = 0f
            }

            if (ball.x - ball.radius < tableRect.left) {
                ball.x = tableRect.left + ball.radius
                ball.vx = -ball.vx * tuning.railRestitution
                railTouchedThisShot = true
                if (ball.number == 0) {
                    ball.spinSide *= -0.46f
                }
            } else if (ball.x + ball.radius > tableRect.right) {
                ball.x = tableRect.right - ball.radius
                ball.vx = -ball.vx * tuning.railRestitution
                railTouchedThisShot = true
                if (ball.number == 0) {
                    ball.spinSide *= -0.46f
                }
            }

            if (ball.y - ball.radius < tableRect.top) {
                ball.y = tableRect.top + ball.radius
                ball.vy = -ball.vy * tuning.railRestitution
                railTouchedThisShot = true
                if (ball.number == 0) {
                    ball.spinSide *= -0.46f
                }
            } else if (ball.y + ball.radius > tableRect.bottom) {
                ball.y = tableRect.bottom - ball.radius
                ball.vy = -ball.vy * tuning.railRestitution
                railTouchedThisShot = true
                if (ball.number == 0) {
                    ball.spinSide *= -0.46f
                }
            }
        }

        resolveBallCollisions()
        resolvePockets()

        if (shotInProgress && SystemClock.uptimeMillis() - shotStartTimeMs > MAX_SHOT_DURATION_MS) {
            balls.forEach { ball ->
                if (ball.active) {
                    ball.vx = 0f
                    ball.vy = 0f
                }
            }
        }

        if (shotInProgress && isAllBallsResting()) {
            shotInProgress = false
            val result = ShotResult(
                pocketedBalls = pocketedThisShot.toList(),
                cuePocketed = cuePocketedThisShot,
                firstObjectHit = firstObjectHitThisShot,
                railTouched = railTouchedThisShot,
                blackPocketed = pocketedThisShot.contains(8),
                remainingObjectBalls = balls.count { it.active && it.number != 0 },
                remainingSolids = balls.count { it.active && it.number in 1..7 },
                remainingStripes = balls.count { it.active && it.number in 9..15 }
            )
            eventListener?.onShotResolved(result)
            eventListener?.onStatusChanged("Round ended")
        }
    }

    private fun resolveBallCollisions() {
        val restitution = 0.97f
        for (i in 0 until balls.size) {
            val a = balls[i]
            if (!a.active) {
                continue
            }
            for (j in i + 1 until balls.size) {
                val b = balls[j]
                if (!b.active) {
                    continue
                }
                val dx = b.x - a.x
                val dy = b.y - a.y
                val minDistance = a.radius + b.radius
                val distanceSquared = dx * dx + dy * dy
                if (distanceSquared <= 0.0001f || distanceSquared >= minDistance * minDistance) {
                    continue
                }

                val distance = sqrt(distanceSquared)
                val nx = dx / distance
                val ny = dy / distance

                val rvx = b.vx - a.vx
                val rvy = b.vy - a.vy
                val velocityAlongNormal = rvx * nx + rvy * ny
                if (velocityAlongNormal < 0f) {
                    val impulse = -(1f + restitution) * velocityAlongNormal / 2f
                    val impulseX = impulse * nx
                    val impulseY = impulse * ny
                    a.vx -= impulseX
                    a.vy -= impulseY
                    b.vx += impulseX
                    b.vy += impulseY

                    if (shotInProgress && firstObjectHitThisShot == null) {
                        if (a.number == 0 && b.number != 0) {
                            firstObjectHitThisShot = b.number
                        } else if (b.number == 0 && a.number != 0) {
                            firstObjectHitThisShot = a.number
                        }
                    }
                }

                val overlap = minDistance - distance
                val correction = overlap * 0.5f + 0.01f
                a.x -= correction * nx
                a.y -= correction * ny
                b.x += correction * nx
                b.y += correction * ny
            }
        }
    }

    private fun resolvePockets() {
        val cueResetX = tableRect.left + tableRect.width() * 0.23f
        val cueResetY = tableRect.centerY()

        balls.forEach { ball ->
            if (!ball.active) {
                return@forEach
            }
            pockets.forEach { pocket ->
                val dx = pocket.first - ball.x
                val dy = pocket.second - ball.y
                if (hypot(dx, dy) <= pocketRadius - ball.radius * 0.2f) {
                    if (ball.number == 0) {
                        ball.x = cueResetX
                        ball.y = cueResetY
                        ball.vx = 0f
                        ball.vy = 0f
                        ball.spinSide = 0f
                        ball.spinTop = 0f
                        cuePocketedThisShot = true
                        eventListener?.onStatusChanged("Cue ball pocketed, reset")
                    } else {
                        ball.active = false
                        ball.vx = 0f
                        ball.vy = 0f
                        pocketedThisShot.add(ball.number)
                    }
                }
            }
        }
    }

    private fun isAllBallsResting(): Boolean {
        return balls.none { it.active && hypot(it.vx, it.vy) > REST_VELOCITY_THRESHOLD }
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)
        canvas.drawRect(
            tableRect.left - ballRadius,
            tableRect.top - ballRadius,
            tableRect.right + ballRadius,
            tableRect.bottom + ballRadius,
            railPaint
        )
        canvas.drawRoundRect(tableRect, ballRadius, ballRadius, tablePaint)

        pockets.forEach {
            canvas.drawCircle(it.first, it.second, pocketRadius, pocketPaint)
        }

        balls.forEach { ball ->
            if (!ball.active) {
                return@forEach
            }
            ballPaint.color = ball.color
            canvas.drawCircle(ball.x, ball.y, ball.radius, ballPaint)
            if (ball.number != 0) {
                canvas.drawText(
                    ball.number.toString(),
                    ball.x,
                    ball.y + numberPaint.textSize * 0.3f,
                    numberPaint
                )
            }
        }

        val cueBall = balls.firstOrNull { it.active && it.number == 0 } ?: return
        if (ballInHand) {
            val headStringX = tableRect.left + tableRect.width() * 0.25f
            canvas.drawLine(headStringX, tableRect.top, headStringX, tableRect.bottom, headStringPaint)
        }
        if (canShoot()) {
            val preview = computeAimPreview(cueBall.x, cueBall.y, aimX, aimY)
            val lineEnd = preview?.let { it.hitX to it.hitY } ?: projectedLineEnd(cueBall.x, cueBall.y, aimX, aimY)
            canvas.drawLine(cueBall.x, cueBall.y, lineEnd.first, lineEnd.second, linePaint)

            if (preview != null) {
                val ghostLen = ballRadius * 5.2f
                val ghostEndX = preview.hitX + preview.normalX * ghostLen
                val ghostEndY = preview.hitY + preview.normalY * ghostLen
                canvas.drawLine(preview.hitX, preview.hitY, ghostEndX, ghostEndY, ghostLinePaint)
            }

            val cueLength = ballRadius * 6f
            val cueStartX = cueBall.x - aimX * (cueLength + ballRadius * 1.8f)
            val cueStartY = cueBall.y - aimY * (cueLength + ballRadius * 1.8f)
            val cueEndX = cueBall.x - aimX * (ballRadius * 1.9f)
            val cueEndY = cueBall.y - aimY * (ballRadius * 1.9f)
            canvas.drawLine(cueStartX, cueStartY, cueEndX, cueEndY, cuePaint)
        }
    }

    private data class AimPreview(
        val hitX: Float,
        val hitY: Float,
        val normalX: Float,
        val normalY: Float,
        val distance: Float
    )

    private fun computeAimPreview(
        cueX: Float,
        cueY: Float,
        dirX: Float,
        dirY: Float
    ): AimPreview? {
        var minDistance = Float.MAX_VALUE
        var preview: AimPreview? = null
        balls.forEach { target ->
            if (!target.active || target.number == 0) {
                return@forEach
            }
            val relX = target.x - cueX
            val relY = target.y - cueY
            val along = relX * dirX + relY * dirY
            if (along <= ballRadius) {
                return@forEach
            }
            val projX = cueX + dirX * along
            val projY = cueY + dirY * along
            val distToLine = hypot(target.x - projX, target.y - projY)
            val collideDistance = ballRadius * 2f
            if (distToLine >= collideDistance) {
                return@forEach
            }
            val backOff = sqrt(collideDistance * collideDistance - distToLine * distToLine)
            val hitDistance = along - backOff
            if (hitDistance < minDistance) {
                minDistance = hitDistance
                val hitX = cueX + dirX * hitDistance
                val hitY = cueY + dirY * hitDistance
                val nx = (target.x - hitX)
                val ny = (target.y - hitY)
                val nd = hypot(nx, ny).coerceAtLeast(0.0001f)
                preview = AimPreview(hitX, hitY, nx / nd, ny / nd, hitDistance)
            }
        }

        if (preview == null) {
            return null
        }
        val tableEnd = projectedLineEnd(cueX, cueY, dirX, dirY)
        val maxDistance = hypot(tableEnd.first - cueX, tableEnd.second - cueY)
        return if (preview.distance <= maxDistance) preview else null
    }

    private fun projectedLineEnd(x: Float, y: Float, dx: Float, dy: Float): Pair<Float, Float> {
        var t = Float.MAX_VALUE
        if (dx > 0f) {
            t = min(t, (tableRect.right - x) / dx)
        } else if (dx < 0f) {
            t = min(t, (tableRect.left - x) / dx)
        }

        if (dy > 0f) {
            t = min(t, (tableRect.bottom - y) / dy)
        } else if (dy < 0f) {
            t = min(t, (tableRect.top - y) / dy)
        }
        val lineDistance = max(0f, t - ballRadius * 0.35f)
        return (x + dx * lineDistance) to (y + dy * lineDistance)
    }
}