package com.playground.freelancer.domain.model

data class Freelancer(
    val id: String,
    val name: String,
    val title: String,
    val avatarUrl: String,
    val location: String,
    val yearsExperience: String,
    val contractType: String,
    val shortBio: String,
    val fullBio: String
)
