package com.applock.biometric.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun PinInputField(
    modifier: Modifier = Modifier,
    pinLength: Int = 4,
    currentPin: String = "",
    onPinComplete: (String) -> Unit,
    isError: Boolean = false
) {
    LaunchedEffect(currentPin) {
        if (currentPin.length == pinLength) {
            onPinComplete(currentPin)
        }
    }
    
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(pinLength) { index ->
            PinCircle(
                isFilled = index < currentPin.length,
                isError = isError,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
private fun PinCircle(
    isFilled: Boolean,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    val scale by animateFloatAsState(
        targetValue = if (isFilled) 1.1f else 1f,
        animationSpec = tween(200),
        label = "pin_scale"
    )
    
    val backgroundColor = when {
        isError -> MaterialTheme.colorScheme.error
        isFilled -> MaterialTheme.colorScheme.primary
        else -> Color.Transparent
    }

    Box(
        modifier = modifier
            .size(24.dp)
            .scale(scale)
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
    )
}

@Composable
fun PinKeypad(
    modifier: Modifier = Modifier,
    onNumberClick: (String) -> Unit,
    onDeleteClick: () -> Unit,
    onBiometricClick: (() -> Unit)? = null,
    onBiometricError: ((String) -> Unit)? = null,
    isEnabled: Boolean = true
) {
    val numbers = listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")

    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // First row: 1, 2, 3
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(3) { index ->
                KeypadButton(
                    text = numbers[index],
                    onClick = { onNumberClick(numbers[index]) },
                    isEnabled = isEnabled
                )
            }
        }
        
        // Second row: 4, 5, 6
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(3) { index ->
                KeypadButton(
                    text = numbers[index + 3],
                    onClick = { onNumberClick(numbers[index + 3]) },
                    isEnabled = isEnabled
                )
            }
        }
        
        // Third row: 7, 8, 9
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            repeat(3) { index ->
                KeypadButton(
                    text = numbers[index + 6],
                    onClick = { onNumberClick(numbers[index + 6]) },
                    isEnabled = isEnabled
                )
            }
        }
        
        // Fourth row: Biometric (if available), 0, Delete
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            // Biometric button
            if (onBiometricClick != null) {
                BiometricAuthButton(
                    onBiometricSuccess = onBiometricClick,
                    onBiometricError = onBiometricError ?: { /* Handle error if needed */ },
                    modifier = Modifier.size(60.dp),
                    isEnabled = isEnabled
                )
            } else {
                Spacer(modifier = Modifier.size(60.dp))
            }
            
            // Zero button
            KeypadButton(
                text = "0",
                onClick = { onNumberClick("0") },
                isEnabled = isEnabled
            )
            
            // Delete button
            KeypadButton(
                text = "⌫",
                onClick = onDeleteClick,
                isEnabled = isEnabled
            )
        }
    }
}

@Composable
private fun KeypadButton(
    text: String,
    onClick: () -> Unit,
    isEnabled: Boolean,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (isEnabled) {
        MaterialTheme.colorScheme.surface
    } else {
        MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
    }
    
    Box(
        modifier = modifier
            .size(60.dp)
            .background(
                color = backgroundColor,
                shape = CircleShape
            )
            .clickable(enabled = isEnabled) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            fontSize = 24.sp,
            fontWeight = FontWeight.Medium,
            color = if (isEnabled) {
                MaterialTheme.colorScheme.onSurface
            } else {
                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            },
            textAlign = TextAlign.Center
        )
    }
}

@Composable
private fun Spacer(modifier: Modifier = Modifier) {
    Spacer(modifier = modifier)
}
