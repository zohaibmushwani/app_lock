package com.applock.biometric.components

import android.content.Context
import android.os.Build
import androidx.activity.compose.LocalActivity
import androidx.annotation.RequiresApi
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import com.applock.biometric.helpers.showToast
import com.applock.biometric.common.SharedPrefsHelper
import java.util.concurrent.Executor


@Composable
fun BiometricAuthButton(
    onBiometricSuccess: () -> Unit,
    onBiometricError: (String) -> Unit,
    modifier: Modifier = Modifier,
    isEnabled: Boolean = true
) {
    val context = LocalActivity.current as FragmentActivity
    var shouldShowBiometrics by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        shouldShowBiometrics = shouldShowBiometrics(context)
    }

    if (shouldShowBiometrics) {
        Box(
            modifier = modifier
                .size(64.dp)
                .clip(CircleShape)
                .background(
                    color = if (isEnabled) {
                        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)
                    } else {
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    }
                )
                .clickable(enabled = isEnabled) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        showBiometricPrompt(context, onBiometricSuccess, onBiometricError)
                    }else{
                        context.showToast("Biometric not supported")
                    }
                },
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Outlined.Fingerprint,
                contentDescription = "Biometric Authentication",
                tint = if (isEnabled) {
                    MaterialTheme.colorScheme.primary
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                },
                modifier = Modifier.size(28.dp)
            )
        }
    }
}

@Composable
fun BiometricAuthPrompt(
    onBiometricSuccess: () -> Unit,
    onBiometricError: (String) -> Unit,
    onUsePassword: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalActivity.current as FragmentActivity
    var shouldShowBiometrics by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        shouldShowBiometrics = shouldShowBiometrics(context)
        if (shouldShowBiometrics) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                showBiometricPrompt(context, onBiometricSuccess, onBiometricError)
            }
        }
    }
    if (shouldShowBiometrics) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .clickable {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            showBiometricPrompt(context, onBiometricSuccess, onBiometricError)
                        }else{
                            context.showToast("Biometric not supported")
                        }
                    },
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f),
                tonalElevation = 2.dp,
                shadowElevation = 2.dp
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = Icons.Outlined.Fingerprint,
                        contentDescription = "Biometric Authentication",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(36.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Touch the fingerprint sensor",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onUsePassword) {
                Text("Use password instead")
            }
        }
    }
}

// --- Helpers ---

private fun checkBiometricAvailability(context: Context): Boolean {
    val biometricManager = BiometricManager.from(context)
    return when (biometricManager.canAuthenticate(
        BiometricManager.Authenticators.BIOMETRIC_STRONG or
                BiometricManager.Authenticators.DEVICE_CREDENTIAL
    )) {
        BiometricManager.BIOMETRIC_SUCCESS -> true
        else -> false
    }
}

/**
 * Checks if biometrics should be shown to the user
 * This requires both hardware support and user enabling the feature
 */
private fun shouldShowBiometrics(context: Context): Boolean {
    val isBiometricsAvailable = checkBiometricAvailability(context)
    val isBiometricEnabled = SharedPrefsHelper.isBiometricEnabled()
    val isPinSet = SharedPrefsHelper.isPinSet()
    
    return isBiometricsAvailable && isBiometricEnabled && isPinSet
}

@RequiresApi(Build.VERSION_CODES.P)
private fun showBiometricPrompt(
    activity: FragmentActivity,
    onSuccess: () -> Unit,
    onError: (String) -> Unit
) {
    val executor: Executor = ContextCompat.getMainExecutor(activity)
    val biometricPrompt = BiometricPrompt(
        activity, executor,
        object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                onSuccess()
            }

            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                onError("Authentication failed")
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                // Don't show error for user cancellation
                if (errorCode != BiometricPrompt.ERROR_USER_CANCELED &&
                    errorCode != BiometricPrompt.ERROR_CANCELED) {
                    onError(errString.toString())
                }
            }
        })

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Biometric Authentication")
        .setSubtitle("Use fingerprint or device credentials")
        .setAllowedAuthenticators(
            BiometricManager.Authenticators.BIOMETRIC_STRONG or
                    BiometricManager.Authenticators.DEVICE_CREDENTIAL
        )
        .build()

    biometricPrompt.authenticate(promptInfo)
}
