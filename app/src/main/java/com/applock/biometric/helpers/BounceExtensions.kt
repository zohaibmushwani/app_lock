package com.applock.biometric.helpers

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput

/**
 * Custom modifier for card bounce click that doesn't interfere with child clicks.
 * This is designed for cards that contain interactive elements like buttons.
 * 
 * @param scaleOutFactor The scale factor when pressed (default: 0.95f)
 * @param throttleDuration Minimum time between clicks in milliseconds (default: 1000L)
 * @param onClick The action to perform when the card is clicked
 */
fun Modifier.cardBounceClick(
    scaleOutFactor: Float = 0.95f,
    throttleDuration: Long = 1000L,
    onClick: () -> Unit = {}
) = composed {
    var isPressed by remember { mutableStateOf(false) }
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleOutFactor else 1f,
        label = "card_scale"
    )
    
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                },
                onTap = {
                    val now = System.currentTimeMillis()
                    if (now - lastClickTime > throttleDuration) {
                        lastClickTime = now
                        onClick()
                    }
                }
            )
        }
}

/**
 * Custom modifier for isolated button bounce click.
 * This is designed for buttons that should not interfere with parent click areas.
 * 
 * @param scaleOutFactor The scale factor when pressed (default: 0.8f)
 * @param throttleDuration Minimum time between clicks in milliseconds (default: 500L)
 * @param onClick The action to perform when the button is clicked
 */
fun Modifier.isolatedBounceClick(
    scaleOutFactor: Float = 0.8f,
    throttleDuration: Long = 500L,
    onClick: () -> Unit = {}
) = composed {
    var isPressed by remember { mutableStateOf(false) }
    var lastClickTime by remember { mutableLongStateOf(0L) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) scaleOutFactor else 1f,
        label = "button_scale"
    )
    
    this
        .graphicsLayer {
            scaleX = scale
            scaleY = scale
        }
        .pointerInput(Unit) {
            detectTapGestures(
                onPress = {
                    isPressed = true
                    tryAwaitRelease()
                    isPressed = false
                },
                onTap = {
                    val now = System.currentTimeMillis()
                    if (now - lastClickTime > throttleDuration) {
                        lastClickTime = now
                        onClick()
                    }
                }
            )
        }
} 