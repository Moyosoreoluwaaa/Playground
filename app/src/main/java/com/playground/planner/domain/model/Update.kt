package com.playground.planner.domain.model

data class Update(
    val id: String,
    val body: String,
    val timestamp: Long // Using Long for internal timestamp representation
)
