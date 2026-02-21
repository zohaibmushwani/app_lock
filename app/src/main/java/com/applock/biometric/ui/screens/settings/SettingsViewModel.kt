package com.devsky.videorecorder.screens.settings

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update

class SettingsViewModel : ViewModel() {
    data class UiState(
        val saveLocation: String? = null,
        val showRatingDialog: Boolean = false,
        val showAudioDialog: Boolean = false,
        val rating: Float = 0f,
    )

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState





    fun setShowRatingDialog(show: Boolean) { _uiState.update { it.copy(showRatingDialog = show) } }

    fun setShowAudioDialog(show: Boolean) { _uiState.update { it.copy(showAudioDialog = show) } }
    fun setRating(value: Float) { _uiState.update { it.copy(rating = value) } }



}


