package com.playground.screens.domain.model

data class ClassModel(
    val name: String,
    val time: String,
    val teacher: TeacherModel,
    val isHomeworkDone: Boolean
)
