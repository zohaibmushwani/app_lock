package com.applock.biometric.ui.screens.settings

import android.app.Application
import androidx.biometric.BiometricManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.applock.biometric.common.SharedPrefsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SecuritySettingsViewModel(private val app : Application) : AndroidViewModel(app) {
    
    private val _uiState = MutableStateFlow(SecuritySettingsUiState())
    val uiState: StateFlow<SecuritySettingsUiState> = _uiState.asStateFlow()

    fun loadSettings() {
        val biometricManager = BiometricManager.from(app)
        val status = when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> BiometricStatus.AVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> BiometricStatus.NO_HARDWARE
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> BiometricStatus.HW_UNAVAILABLE
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> BiometricStatus.NONE_ENROLLED
            else -> BiometricStatus.UNKNOWN
        }

        _uiState.value = _uiState.value.copy(
            isPinEnabled = SharedPrefsHelper.isPinSet(),
            isBiometricEnabled = SharedPrefsHelper.isBiometricEnabled(),
            biometricStatus = status
        )
    }

    fun toggleBiometric(enable: Boolean) {
        if (enable) {
            val biometricManager = BiometricManager.from(app)
            val canAuth = biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)
            if (canAuth == BiometricManager.BIOMETRIC_SUCCESS) {
                SharedPrefsHelper.setBiometricEnabled(true)
                _uiState.value = _uiState.value.copy(isBiometricEnabled = true)
            } else {
                _uiState.value = _uiState.value.copy(isBiometricEnabled = false)
            }
        } else {
            SharedPrefsHelper.setBiometricEnabled(false)
            _uiState.value = _uiState.value.copy(isBiometricEnabled = false)
        }
    }

    
    fun disablePin() {
        viewModelScope.launch {
            SharedPrefsHelper.clearPin()
            SharedPrefsHelper.setBiometricEnabled(false)
            _uiState.value = _uiState.value.copy(
                isPinEnabled = false,
                isBiometricEnabled = false
            )
        }
    }
}

data class SecuritySettingsUiState(
    val isPinEnabled: Boolean = false,
    val isBiometricEnabled: Boolean = false,
    val biometricStatus: BiometricStatus = BiometricStatus.UNKNOWN
)

enum class BiometricStatus {
    UNKNOWN,
    AVAILABLE,
    NO_HARDWARE,
    HW_UNAVAILABLE,
    NONE_ENROLLED
}
