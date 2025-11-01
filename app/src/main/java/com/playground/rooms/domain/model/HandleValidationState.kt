package com.playground.rooms.domain.model

enum class HandleValidationState {
    IDLE, LOADING_CHECK, VALID, INVALID_LENGTH, INVALID_CHARS, UNAVAILABLE
}
