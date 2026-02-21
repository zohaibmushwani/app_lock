package com.devsky.videorecorder.screens.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applock.biometric.common.SharedPrefsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PinEntryViewModel: ViewModel() {
    
    private val _uiState = MutableStateFlow(PinEntryUiState())
    val uiState: StateFlow<PinEntryUiState> = _uiState.asStateFlow()
    
    private var currentPin = ""
    private var failedAttempts = 0
    private val maxAttempts = 5
    private val lockoutDuration = 5 * 60 * 1000L
    
    init {
        loadFailedAttempts()
        checkBiometricSettings()
    }
    
    fun onPinInput(pin: String) {
        if (_uiState.value.isLockedOut) return
        
        currentPin = pin
        _uiState.value = _uiState.value.copy(
            currentPin = pin,
            isError = false,
            errorMessage = null
        )
    }
    
    fun onPinComplete(pin: String) {
        if (_uiState.value.isLockedOut) return
        
        viewModelScope.launch {
            val storedPin = SharedPrefsHelper.getString(SharedPrefsHelper.KEY_APP_PASSWORD, "")
            
            if (pin == storedPin) {
                SharedPrefsHelper.saveBoolean(SharedPrefsHelper.KEY_IS_AUTH, true, true)
                resetFailedAttempts()
                _uiState.value = _uiState.value.copy(
                    isAuthenticated = true,
                    isError = false,
                    errorMessage = null
                )
            } else {
                handleAuthenticationFailure()
            }
        }
    }
    
    fun onBiometricSuccess() {
        viewModelScope.launch {
            SharedPrefsHelper.saveBoolean(SharedPrefsHelper.KEY_IS_AUTH, true, true)
            resetFailedAttempts()
            _uiState.value = _uiState.value.copy(
                isAuthenticated = true,
                isError = false,
                errorMessage = null
            )
        }
    }
    
    fun onBiometricError(error: String) {
        _uiState.value = _uiState.value.copy(
            isError = true,
            errorMessage = error
        )
    }
    
    fun onDeleteClick() {
        if (currentPin.isNotEmpty()) {
            currentPin = currentPin.dropLast(1)
            _uiState.value = _uiState.value.copy(
                currentPin = currentPin,
                isError = false,
                errorMessage = null
            )
        }
    }
    
    fun onForgotPin() {
        _uiState.value = _uiState.value.copy(
            showForgotPinDialog = true
        )
    }
    
    fun dismissForgotPinDialog() {
        _uiState.value = _uiState.value.copy(
            showForgotPinDialog = false
        )
    }
    
    fun resetPin() {
        viewModelScope.launch {
            SharedPrefsHelper.remove(SharedPrefsHelper.KEY_APP_PASSWORD, true)
            SharedPrefsHelper.saveBoolean(SharedPrefsHelper.KEY_IS_AUTH, false, true)
            resetFailedAttempts()
            _uiState.value = _uiState.value.copy(
                isPinReset = true,
                showForgotPinDialog = false
            )
        }
    }
    
    private fun handleAuthenticationFailure() {
        failedAttempts++
        SharedPrefsHelper.saveInt("failed_attempts", failedAttempts, true)
        
        if (failedAttempts >= maxAttempts) {
            val lockoutEndTime = System.currentTimeMillis() + lockoutDuration
            SharedPrefsHelper.saveLong("lockout_end_time", lockoutEndTime, true)
            _uiState.value = _uiState.value.copy(
                isLockedOut = true,
                lockoutEndTime = lockoutEndTime,
                isError = true,
                errorMessage = "Too many failed attempts. Try again in 5 minutes."
            )
        } else {
            _uiState.value = _uiState.value.copy(
                isError = true,
                errorMessage = "Incorrect PIN. ${maxAttempts - failedAttempts} attempts remaining.",
                currentPin = "",
                failedAttempts = failedAttempts
            )
        }
    }
    
    private fun resetFailedAttempts() {
        failedAttempts = 0
        SharedPrefsHelper.remove("failed_attempts", true)
        SharedPrefsHelper.remove("lockout_end_time", true)
    }
    
    private fun loadFailedAttempts() {
        failedAttempts = SharedPrefsHelper.getInt("failed_attempts", 0)
        val lockoutEndTime = SharedPrefsHelper.getLong("lockout_end_time", 0)
        
        val isLockedOut = lockoutEndTime > 0 && System.currentTimeMillis() < lockoutEndTime
        
        _uiState.value = _uiState.value.copy(
            failedAttempts = failedAttempts,
            isLockedOut = isLockedOut,
            lockoutEndTime = lockoutEndTime
        )
    }
    
    private fun checkBiometricSettings() {
        val isBiometricEnabled = SharedPrefsHelper.isBiometricEnabled()
        val isPinSet = SharedPrefsHelper.isPinSet()
        
        _uiState.value = _uiState.value.copy(
            isBiometricEnabled = isBiometricEnabled && isPinSet
        )
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(
            isError = false,
            errorMessage = null
        )
    }
    
    fun refreshBiometricSettings() {
        checkBiometricSettings()
    }
}

data class PinEntryUiState(
    val currentPin: String = "",
    val isAuthenticated: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val isLockedOut: Boolean = false,
    val lockoutEndTime: Long = 0,
    val failedAttempts: Int = 0,
    val showForgotPinDialog: Boolean = false,
    val isPinReset: Boolean = false,
    val isBiometricEnabled: Boolean = false
)
