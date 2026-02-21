package com.applock.biometric.ui.screens.security

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.applock.biometric.common.SharedPrefsHelper
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class SecurityQuestionUiState(
    val selectedQuestion: String = "",
    val answer: String = "",
    val errorMessage: String? = null,
    val isSuccess: Boolean = false
)

class SecurityQuestionViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(SecurityQuestionUiState())
    val uiState = _uiState.asStateFlow()

    val questions = listOf(
        "What is your mother's maiden name?",
        "What was the name of your first pet?",
        "What is the name of the city you were born in?",
        "What is your favorite food?",
        "What is the name of your first school?"
    )

    fun selectQuestion(question: String) {
        _uiState.update { it.copy(selectedQuestion = question, errorMessage = null) }
    }

    fun onAnswerChange(answer: String) {
        _uiState.update { it.copy(answer = answer, errorMessage = null) }
    }

    fun saveSecurityQuestion() {
        if (_uiState.value.selectedQuestion.isBlank() || _uiState.value.answer.isBlank()) {
            _uiState.update { it.copy(errorMessage = "Please fill in all fields") }
            return
        }
        
        // Save to preferences
        SharedPrefsHelper.saveSecurityQuestion(_uiState.value.selectedQuestion, _uiState.value.answer)
        
        _uiState.update { it.copy(isSuccess = true) }
    }
}
