package com.applock.biometric.components

import android.view.Window
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.dropShadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.shadow.Shadow
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.compose.ui.window.DialogWindowProvider
import kotlinx.coroutines.delay

@Composable
fun CustomDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    content: @Composable () -> Unit,
) {
    val showAnimatedDialog by rememberUpdatedState(showDialog)
    if (showAnimatedDialog) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(
                usePlatformDefaultWidth = false
            )
        ) {
            val dialogWindow = getDialogWindow()

            SideEffect {
                dialogWindow.let { window ->
                    window?.setDimAmount(0f)
                    window?.setWindowAnimations(-1)
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                var animateIn by remember { mutableStateOf(false) }
                LaunchedEffect(Unit) { animateIn = true }
                AnimatedVisibility(
                    visible = animateIn && showDialog,
                    enter = fadeIn(),
                    exit = fadeOut(),
                ) {
                    Box(
                        modifier = Modifier
                            .pointerInput(Unit) { detectTapGestures { onDismissRequest() } }
                            .background(Color.Black.copy(alpha = .56f))
                            .fillMaxSize()
                    )
                }
                AnimatedVisibility(
                    visible = animateIn && showDialog,
                    enter = fadeIn(spring(stiffness = Spring.StiffnessHigh)) + scaleIn(
                        initialScale = .8f,
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioMediumBouncy,
                            stiffness = Spring.StiffnessMediumLow
                        )
                    ),
                    exit = slideOutVertically { it / 8 } + fadeOut() + scaleOut(targetScale = .95f)
                ) {
                    Box(
                        Modifier
                            .pointerInput(Unit) { detectTapGestures { } }
                            .dropShadow(
                                RoundedCornerShape(16.dp),
                                shadow = Shadow(
                                    10.dp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            )
                            .fillMaxWidth(0.8f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                MaterialTheme.colorScheme.surface,
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        content()
                    }
                }
            }
        }
    }
}

@Composable
fun BottomAlignedDialog(
    showDialog: Boolean,
    onDismissRequest: () -> Unit,
    contentMaxWidthFraction: Float = 1f,
    topCornerRadius: Dp = 16.dp,
    enterDurationMs: Int = 420,
    exitDurationMs: Int = 300,
    content: @Composable () -> Unit
) {
    var internalDialogVisible by remember { mutableStateOf(showDialog) }

    val transitionState = remember {
        MutableTransitionState(false).apply { targetState = false }
    }

    LaunchedEffect(showDialog) {
        if (showDialog) {
            internalDialogVisible = true
            transitionState.targetState = true
        } else {
            transitionState.targetState = false
            delay(exitDurationMs.toLong())
            internalDialogVisible = false
        }
    }

    if (internalDialogVisible) {
        Dialog(
            onDismissRequest = onDismissRequest,
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            val dialogWindow = getDialogWindow()
            SideEffect {
                dialogWindow?.let { window ->
                    window.setDimAmount(0f)
                    window.setWindowAnimations(-1)
                }
            }

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                // scrim with fade in/out
                AnimatedVisibility(
                    visibleState = transitionState,
                    enter = fadeIn(animationSpec = tween(durationMillis = enterDurationMs)),
                    exit = fadeOut(animationSpec = tween(durationMillis = exitDurationMs))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color.Black.copy(alpha = 0.56f))
                            .pointerInput(Unit) {
                                detectTapGestures { onDismissRequest() }
                            }
                    )
                }

                AnimatedVisibility(
                    visibleState = transitionState,
                    enter = slideInVertically(
                        initialOffsetY = { fullHeight -> fullHeight },
                        animationSpec = spring(
                            dampingRatio = Spring.DampingRatioLowBouncy,
                            stiffness = Spring.StiffnessLow
                        )
                    ) + fadeIn(animationSpec = tween(enterDurationMs)),
                    exit = slideOutVertically(
                        targetOffsetY = { fullHeight -> fullHeight / 1 },
                        animationSpec = tween(exitDurationMs, easing = FastOutSlowInEasing)
                    ) + fadeOut(animationSpec = tween(exitDurationMs))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(contentMaxWidthFraction)
                            .wrapContentHeight()
                            .clip(RoundedCornerShape(topStart = topCornerRadius, topEnd = topCornerRadius))
                            .background(MaterialTheme.colorScheme.surface)
                            .pointerInput(Unit) {

                            }
                    ) {
                        content()
                    }
                }
            }
        }
    }
}

@ReadOnlyComposable
@Composable
fun getDialogWindow(): Window? = (LocalView.current.parent as? DialogWindowProvider)?.window