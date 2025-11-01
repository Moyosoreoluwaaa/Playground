package com.playground.freelancer.data.source

import com.playground.freelancer.domain.model.Freelancer
import com.playground.freelancer.domain.model.FreelancerRole
import com.playground.freelancer.domain.model.PlayFreelancerJobLevel
import com.playground.freelancer.presentation.uistate.FreelancerRoleSelectionUiState
import com.playground.freelancer.presentation.uistate.PlayFreelancerProfileCardUiState
import com.playground.freelancer.presentation.uistate.FreelancerSearchResult

object MockFreelancerData {
    private val names = listOf(
        "Jasmin Lowery", "Alex Ray", "Morgan Chase", "Jamie Rivera", "Casey Jordan",
        "Taylor Smith", "Riley Green", "Cameron Diaz", "Drew Barrymore", "Lucy Liu"
    )

    private val titles = listOf(
        "Hardware Engineer", "Android Developer", "Cloud Architect",
        "UX/UI Designer", "Product Manager", "Data Scientist", "DevOps Specialist",
        "QA Engineer", "Frontend Developer", "iOS Engineer"
    )

    fun generateProfiles(): List<PlayFreelancerProfileCardUiState> {
        return List(10) { index ->
            PlayFreelancerProfileCardUiState(
                id = "freelancer_$index",
                name = names[index],
                imageUrl = "https://i.pravatar.cc/300?img=${index + 1}", // Placeholder image URL
                jobLevel = PlayFreelancerJobLevel.entries[index % 4],
                jobTitle = titles[index],
                salary = "${2400 + index * 125} / month",
                hasUnreadMessages = index % 3 == 0
            )
        }
    }
}

val mockFreelancer = Freelancer(
    id = "1",
    name = "Chelsea Knight",
    title = "Backend Developer",
    avatarUrl = "", // Mock
    location = "New York",
    yearsExperience = "3+ year",
    contractType = "Full-time",
    shortBio = "Hey there! I'm your perfect freelancer for all your hardware engineering projects. 🧑‍💻 Working with me is easy and enjoyable: I'm always ready to dive into the details of...",
    fullBio = "Hey there! I'm your perfect freelancer for all your hardware engineering projects. 🧑‍💻 Working with me is easy and enjoyable: I'm always ready to dive into the details of the project and collaborate closely with your team to deliver high-quality, reliable solutions on time and within budget."
)

val mockFreelancers = listOf(
    FreelancerSearchResult("1", "Jane Doe", "DevOps Engineer", 65.0, 4.9f, "", false),
    FreelancerSearchResult("2", "John Smith", "Cloud Architect", 75.0, 4.7f, "", true),
    FreelancerSearchResult("3", "Alice Johnson", "Site Reliability", 55.0, 5.0f, "", false)
)

val mockFreelancerRoles = listOf(
    FreelancerRole("1", "Product Designer"),
    FreelancerRole("2", "Business Analyst"),
    FreelancerRole("3", "Web Design"),
    FreelancerRole("4", "DevOps Engineer"),
    FreelancerRole("5", "Frontend Developer"),
    FreelancerRole("6", "Cloud Architect"),
)

val mockFreelancerUiState = FreelancerRoleSelectionUiState(
    roles = mockFreelancerRoles,
    selectedRoleIds = setOf("2", "4"), // Business Analyst, DevOps Engineer selected
    isSelectAllActive = false
)