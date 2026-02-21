package com.devsky.videorecorder.screens.security

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.applock.biometric.R
import com.applock.biometric.components.PinInputField
import com.applock.biometric.components.PinKeypad


@Composable
fun PinEntryScreen(
    onPinValidated: () -> Unit,
    onForgotPin: () -> Unit,
    viewModel: PinEntryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(uiState.isAuthenticated) {
        if (uiState.isAuthenticated) {
            onPinValidated()
        }
    }
    
//    LaunchedEffect(uiState.isPinReset) {
//        if (uiState.isPinReset) {
//            onForgotPin()
//        }
//    }
    
    LaunchedEffect(Unit) {
        viewModel.refreshBiometricSettings()
    }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = Icons.Rounded.Lock,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.primary
            )
            
            // Title
            Text(
                text = stringResource(R.string.enter_pin),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Subtitle
            Text(
                text = stringResource(R.string.pin_instructions),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            PinInputField(
                pinLength = 4,
                currentPin = uiState.currentPin,
                onPinComplete = viewModel::onPinComplete,
                isError = uiState.isError
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (uiState.isError && uiState.errorMessage != null) {
                Text(
                    text = uiState.errorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }
            
            // Lockout Message
            if (uiState.isLockedOut) {
                val remainingTime = if (uiState.lockoutEndTime > 0) {
                    val remaining = (uiState.lockoutEndTime - System.currentTimeMillis()) / 1000
                    if (remaining > 0) "${remaining / 60}:${(remaining % 60).toString().padStart(2, '0')}" else "0:00"
                } else "0:00"
                
                Text(
                    text = stringResource(R.string.lockout_message, remainingTime),
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
            }

            if (!uiState.isLockedOut) {
                PinKeypad(
                    onNumberClick = { number ->
                        val newPin = uiState.currentPin + number
                        if (newPin.length <= 4) {
                            viewModel.onPinInput(newPin)
                        }
                    },
                    onDeleteClick = viewModel::onDeleteClick,
                    onBiometricClick = if (uiState.isBiometricEnabled) {
                        viewModel::onBiometricSuccess
                    } else null,
                    onBiometricError = if (uiState.isBiometricEnabled) {
                        viewModel::onBiometricError
                    } else null,
                    isEnabled = true
                )
            }
            
            Spacer(modifier = Modifier.height(32.dp))
            
            // Forgot PIN Button
            if (!uiState.isLockedOut) {
                TextButton(
                    onClick = onForgotPin
                ) {
                    Text(
                        text = stringResource(R.string.forgot_pin),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
        
        // Loading Indicator
        if (uiState.isAuthenticated) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
    
//    // Forgot PIN Dialog
//    if (uiState.showForgotPinDialog) {
//        ForgotPinDialog(
//            onConfirm = viewModel::resetPin,
//            onDismiss = viewModel::dismissForgotPinDialog
//        )
//    }
}

@Composable
private fun ForgotPinDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = stringResource(R.string.forgot_pin_dialog_title),
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Text(
                text = stringResource(R.string.forgot_pin_dialog_message),
                style = MaterialTheme.typography.bodyMedium
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = androidx.compose.material3.ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            OutlinedButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
