package com.playground.freelancer.domain.model

enum class FreelancerSearchTabType {
    SEARCH, PROFILE
}

enum class FreelancerProfileTabType {
    RESPONSIBILITIES, EXPERIENCE, EDUCATION
}

enum class ProfileTab {
    RESPONSIBILITIES, EXPERIENCE, EDUCATION
}

enum class LoadStatus { LOADING, LOADED, ERROR }

enum class PlayFreelancerJobLevel {
    JUNIOR, MID, SENIOR, LEAD
}

enum class PlayFreelancerSwipeDirection {
    RIGHT, LEFT
}

enum class CardDepth {
    TOP, MIDDLE, BOTTOM
}
