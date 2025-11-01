package com.playground.visualizer.domain.model

data class AudioData(
    val waveform: List<Float>,
    val reactiveLines: List<List<Float>>
)
