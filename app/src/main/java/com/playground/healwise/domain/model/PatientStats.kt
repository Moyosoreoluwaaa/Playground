package com.playground.healwise.domain.model

import java.util.Locale

data class PatientStats(
    val title: String,
    val groupA: DiagramGroup,
    val groupB: DiagramGroup
) {
    val totalPatients: Float = groupA.floatValue + groupB.floatValue
    val totalCountFormatted: String = if (totalPatients >= 1f) "${
        String.format(Locale.ENGLISH, "%.1f", totalPatients).removeSuffix(".0")
    }M" else {
        String.format(Locale.ENGLISH, "%.1f", totalPatients)
    }
}
