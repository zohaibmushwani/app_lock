package com.applock.biometric.components

import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.unit.TextUnit

import kotlin.math.max
import kotlin.math.min
import kotlin.math.sin

internal const val SIZE_DECREASER = 0.9f

internal enum class SizeDecreasingStage(val value: Float) {
    Offense(0.5f),
    Defence(1.2f),
    Diplomacy(0.95f),
    Peace(Float.NaN);
}
fun singleClick(onClick: () -> Unit): () -> Unit {
    var latest: Long = 0
    return {
        val now = System.currentTimeMillis()
        if (now - latest >= 300) {
            onClick()
            latest = now
        }
    }
}

internal fun SizeDecreasingStage?.next(didOverflowHeight: Boolean) :SizeDecreasingStage {
    return when {
        this == null -> SizeDecreasingStage.Offense
        this == SizeDecreasingStage.Offense && didOverflowHeight -> SizeDecreasingStage.Offense
        this == SizeDecreasingStage.Offense -> SizeDecreasingStage.Defence
        this == SizeDecreasingStage.Defence && didOverflowHeight.not() -> SizeDecreasingStage.Defence
        this == SizeDecreasingStage.Defence -> SizeDecreasingStage.Diplomacy
        this == SizeDecreasingStage.Diplomacy && didOverflowHeight -> SizeDecreasingStage.Diplomacy
        else -> SizeDecreasingStage.Peace
    }
}

internal data class InnerMetrics(
    val fontSize: TextUnit,
    val lineHeight: TextUnit,
)

internal fun coerceTextUnit(
    expected: TextUnit,
    default: TextUnit
) = if (expected != TextUnit.Unspecified) expected else default

fun prepareSinePath(
    path: Path,
    size: Size,
    frequency: Int,
    amplitude: Float,
    phaseShift: Float,
    position: Float,
    step: Int
) {
    for (x in 0..size.width.toInt().plus(step) step step) {
        val y = position + amplitude * sin(x * frequency * Math.PI / size.width + phaseShift).toFloat()
        if (path.isEmpty)
            path.moveTo(x.toFloat(), max(0f, min(y, size.height)))
        else
            path.lineTo(x.toFloat(), max(0f, min(y, size.height)))
    }
}