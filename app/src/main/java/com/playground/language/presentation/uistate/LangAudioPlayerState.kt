package com.playground.language.presentation.uistate

import com.playground.language.domain.LangAnswer

// Internal state for the Audio Player Component
data class LangAudioPlayerState(
    val isPlaying: Boolean = false,
    val currentTime: Long = 45_000, // in milliseconds
    val totalDuration: Long = 150_000, // 2:30 in milliseconds
    val volumeLevel: Float = 0.5f, // 0.0f to 1.0f
    val isSegmentComplete: Boolean = false // Checkmark icon status
)

data class LangListeningLessonUiState(
    val isLoading: Boolean = false,
    val audioState: LangAudioPlayerState = LangAudioPlayerState(),
    val lessonTitle: String = "Listening Lesson",
    val lessonText: String = "What Hunt the dog doing?", // The question/prompt
    val transcriptionTitle: String = "Trancprat",
    val choices: List<LangAnswer> = emptyList(),
    val selectedAnswerId: String? = null,
    val submitButtonEnabled: Boolean = false,
    val errorMessage: String? = null
)

sealed class LangAudioPlayerEvent {
    data object OnPlayPauseClick : LangAudioPlayerEvent()
    data class OnSeek(val positionMs: Long) : LangAudioPlayerEvent()
    data object OnVolumeControlClick : LangAudioPlayerEvent()
}

sealed class LangListeningLessonUiEvent {
    data object OnBackClick : LangListeningLessonUiEvent()
    data object OnSubmitAnswer : LangListeningLessonUiEvent()
    data class OnAnswerSelected(val answer: LangAnswer) : LangListeningLessonUiEvent()
    data class OnAudioEvent(val event: LangAudioPlayerEvent) : LangListeningLessonUiEvent()
}