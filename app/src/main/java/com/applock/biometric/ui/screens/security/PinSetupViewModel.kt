package com.applock.biometric.ui.screens.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applock.biometric.common.SharedPrefsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class PinSetupViewModel : ViewModel() {
    
    private val _uiState = MutableStateFlow(PinSetupUiState())
    val uiState: StateFlow<PinSetupUiState> = _uiState.asStateFlow()
    
    private var firstPin = ""
    private var secondPin = ""
    
    fun onPinInput(pin: String) {
        if (_uiState.value.isConfirming) {
            secondPin = pin
            _uiState.value = _uiState.value.copy(
                confirmPin = pin,
                isError = false,
                errorMessage = null
            )
        } else {
            firstPin = pin
            _uiState.value = _uiState.value.copy(
                initialPin = pin,
                isError = false,
                errorMessage = null
            )
        }
    }
    
    fun onPinComplete(pin: String) {
        if (_uiState.value.isConfirming) {
            secondPin = pin
            _uiState.value = _uiState.value.copy(confirmPin = pin)
            verifyPins()
        } else {
            firstPin = pin
            _uiState.value = _uiState.value.copy(
                initialPin = pin,
                isConfirming = true
            )
        }
    }
    
    fun onDeleteClick() {
        if (_uiState.value.isConfirming) {
            if (secondPin.isNotEmpty()) {
                secondPin = secondPin.dropLast(1)
                _uiState.value = _uiState.value.copy(
                    confirmPin = secondPin,
                    isError = false,
                    errorMessage = null
                )
            }
        } else {
            if (firstPin.isNotEmpty()) {
                firstPin = firstPin.dropLast(1)
                _uiState.value = _uiState.value.copy(
                    initialPin = firstPin,
                    isError = false,
                    errorMessage = null
                )
            }
        }
    }
    
    fun onBackClick() {
        if (_uiState.value.isConfirming) {
            // Go back to initial pin entry
            secondPin = ""
            _uiState.value = _uiState.value.copy(
                isConfirming = false,
                confirmPin = "",
                isError = false,
                errorMessage = null
            )
        } else {
            // Exit setup
            _uiState.value = _uiState.value.copy(
                shouldExit = true
            )
        }
    }
    
    fun onSkipClick() {
        _uiState.value = _uiState.value.copy(
            shouldExit = true
        )
    }
    
    private fun verifyPins() {
        if (firstPin == secondPin) {
            // Pins match, save and complete setup
            viewModelScope.launch {
                SharedPrefsHelper.saveString(SharedPrefsHelper.KEY_APP_PASSWORD, firstPin, true)
                SharedPrefsHelper.saveBoolean(SharedPrefsHelper.KEY_IS_AUTH, true, true)
                _uiState.value = _uiState.value.copy(
                    isPinSet = true,
                    isError = false,
                    errorMessage = null
                )
            }
        } else {
            // Pins don't match, show error and reset
            _uiState.value = _uiState.value.copy(
                isError = true,
                errorMessage = "PINs don't match. Please try again.",
                isConfirming = false,
                confirmPin = "",
                initialPin = ""
            )
            firstPin = ""
            secondPin = ""
        }
    }
    
    fun clearError() {
        _uiState.value = _uiState.value.copy(
            isError = false,
            errorMessage = null
        )
    }
    
    fun resetState() {
        firstPin = ""
        secondPin = ""
        _uiState.value = PinSetupUiState()
    }
}

data class PinSetupUiState(
    val initialPin: String = "",
    val confirmPin: String = "",
    val isConfirming: Boolean = false,
    val isPinSet: Boolean = false,
    val isError: Boolean = false,
    val errorMessage: String? = null,
    val shouldExit: Boolean = false
)
