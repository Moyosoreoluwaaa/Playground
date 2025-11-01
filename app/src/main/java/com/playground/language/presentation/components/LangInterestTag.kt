package com.playground.language.presentation.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.horizontalScroll
import com.playground.language.presentation.uistate.LangInterest

@Composable
fun LangInterestTag(
    interest: LangInterest,
    onClick: (LangInterest) -> Unit,
    modifier: Modifier = Modifier
) {
    // The colors in the image are diverse. For simplicity, we'll use a fixed color
    // for selected state and a neutral one for unselected, but allow for variable
    // tint if the model provided a color.
    val selectedBackgroundColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.8f)
    val unselectedBackgroundColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)

    val backgroundColor by animateColorAsState(
        targetValue = if (interest.isSelected) selectedBackgroundColor else unselectedBackgroundColor,
        label = "tag_bg_animation"
    )
    val textColor by animateColorAsState(
        targetValue = if (interest.isSelected) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface,
        label = "tag_text_animation"
    )

    Surface(
        shape = RoundedCornerShape(percent = 50), // Pill shape
        color = backgroundColor,
        modifier = modifier
            .padding(4.dp) // Spacing to mimic cloud structure
            .clickable { onClick(interest) }
    ) {
        Text(
            text = interest.name,
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
            color = textColor,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}

// A simplified FlowLayout replacement using standard Row/Wrap for the tag cloud
@Composable
fun LangInterestCloud(
    interests: List<LangInterest>,
    onInterestToggled: (LangInterest) -> Unit,
    modifier: Modifier = Modifier
) {
    // NOTE: Achieving the exact heart shape requires a Custom Layout.
    // Here we use a standard Column + FlowRow logic for a visually engaging, wrapping layout.
    Column(modifier = modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
        // Use a simple Column of Rows or FlowRow (not native Compose, so simulating with a fixed number of rows/items)
        // Given the constraints, a single Column of items with staggered padding gives a cloud effect.

        val rowSizes = listOf(4, 5, 5, 5, 4, 3) // Simulating flow pattern from image

        var startIndex = 0
        rowSizes.forEachIndexed { rowIndex, rowSize ->
            if (startIndex < interests.size) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = (rowIndex * 8).dp), // Staggered padding to force a shape
                    horizontalArrangement = Arrangement.Center
                ) {
                    val rowItems = interests.subList(startIndex, (startIndex + rowSize).coerceAtMost(interests.size))
                    rowItems.forEach { interest ->
                        LangInterestTag(
                            interest = interest,
                            onClick = onInterestToggled
                        )
                    }
                }
                startIndex += rowSize
            }
        }
    }
}