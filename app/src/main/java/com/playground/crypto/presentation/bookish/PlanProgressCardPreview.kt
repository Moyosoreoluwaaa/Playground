package com.playground.crypto.presentation.bookish

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Modifier

@Preview(name = "SearchInput", showBackground = true)
@Composable
fun SearchInputPreview() {
    MaterialTheme {
        SearchInput(
            onSearchClicked = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "PlanProgressCard - 46%", showBackground = true)
@Composable
fun PlanProgressCardPreview() {
    MaterialTheme {
        PlanProgressCard(
            plan = Plan(
                title = "Yearly plan",
                details = "15 books",
                progress = 0.46f
            ),
            onCardClicked = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "PlanProgressCard - 85%", showBackground = true)
@Composable
fun PlanProgressCardHighProgressPreview() {
    MaterialTheme {
        PlanProgressCard(
            plan = Plan(
                title = "Yearly plan",
                details = "15 books",
                progress = 0.85f
            ),
            onCardClicked = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}