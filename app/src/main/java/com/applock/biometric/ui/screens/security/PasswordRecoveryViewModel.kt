package com.applock.biometric.ui.screens.security

import androidx.lifecycle.ViewModel
import com.applock.biometric.common.SharedPrefsHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

data class PasswordRecoveryUiState(
    val question: String = "",
    val answer: String = "",
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class PasswordRecoveryViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(PasswordRecoveryUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadSecurityQuestion()
    }

    private fun loadSecurityQuestion() {
        val question = SharedPrefsHelper.getSecurityQuestion()
        if (question != null) {
            _uiState.update { it.copy(question = question) }
        } else {
             _uiState.update { it.copy(errorMessage = "Secure setup not completed.") }
        }
    }

    fun onAnswerChange(answer: String) {
        _uiState.update { it.copy(answer = answer, errorMessage = null) }
    }

    fun verifyAnswer() {
        val savedAnswer = SharedPrefsHelper.getSecurityAnswer()
        val inputAnswer = _uiState.value.answer.trim()
        
        if (savedAnswer != null && savedAnswer.equals(inputAnswer, ignoreCase = true)) {
            SharedPrefsHelper.clearPin() // Clear old PIN so user can set new one
            _uiState.update { it.copy(isSuccess = true) }
        } else {
            _uiState.update { it.copy(errorMessage = "Incorrect answer") }
        }
    }
}
