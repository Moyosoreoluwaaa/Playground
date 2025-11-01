package com.playground.rooms.domain.model

data class OnboardingSlideContent(
    val slide: OnboardingSlide,
    val headline: String,
    val body: String,
    val illustrationResId: Int // Placeholder for image resource
)
