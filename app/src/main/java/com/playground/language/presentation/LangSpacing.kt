package com.playground.language.presentation

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.RamenDining
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter.State.Empty.painter
import com.playground.language.domain.LangGamifiedItemCategory
import com.playground.language.domain.LangGamifiedMetricItem
import com.playground.language.domain.LangMetric
import com.playground.language.presentation.components.LangSpacing
// Using android R for placeholder drawables
import android.R as R_android 

// --------------------------------------------------------------------------------
// 1. Custom Progress Bar (Simplified for brevity, using standard M3 indicator)
// --------------------------------------------------------------------------------

@Composable
fun LangLessonProgressIndicator(
    progress: Float, // 0.0f to 1.0f
    modifier: Modifier = Modifier
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress,
        label = "progress_animation"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Linear Progress Indicator (Styled for the rounded look)
        LinearProgressIndicator(
            progress = animatedProgress,
            modifier = Modifier
                .weight(1f)
                .height(8.dp)
                .clip(RoundedCornerShape(4.dp)), // Rounded corners on the track and bar
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
        )

        // Percentage Text
        Text(
            text = "${(progress * 100).toInt()}% Complete",
            style = MaterialTheme.typography.labelMedium.copy(fontSize = 12.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(start = 12.dp)
        )
    }
}

// --------------------------------------------------------------------------------
// 2. Progress Indicator Dots
// --------------------------------------------------------------------------------

@Composable
fun LangProgressIndicatorDots(
    count: Int,
    activeIndex: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth().padding(vertical = LangSpacing / 2),
        horizontalArrangement = Arrangement.Center
    ) {
        repeat(count) { index ->
            val isActive = index == activeIndex
            val color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f)
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(MaterialTheme.shapes.extraLarge) // Circle shape
                    .background(color)
                    .padding(horizontal = 4.dp)
            )
            if (index < count - 1) {
                Spacer(Modifier.width(4.dp))
            }
        }
    }
}

@Preview(name = "Gamified Metric Item Preview", showBackground = true)
@Composable
fun LangGamifiedMetricItemCardPreview() {
    MaterialTheme {
        LangGamifiedMetricItemCard(
            item = LangGamifiedMetricItem(
                id = "1",
                title = "Achievements",
                iconResId = R_android.drawable.star_on,
                category = LangGamifiedItemCategory.GRAMMAR
            ),
            onClick = {},
            modifier = Modifier.padding(LangSpacing)
        )
    }
}


// --------------------------------------------------------------------------------
// 3. Metric Card (15 Days / 3 Badges)
// --------------------------------------------------------------------------------

@Composable
fun LangMetricCard(
    metric: LangMetric,
    onClick: (LangMetric) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(LangSpacing),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp) // Light card background
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = modifier
            .heightIn(min = 90.dp)
            .clickable { onClick(metric) }
    ) {
        Row(
            modifier = Modifier.padding(LangSpacing),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = metric.iconResId),
                contentDescription = metric.label,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )

            Spacer(Modifier.width(LangSpacing / 2))

            Column {
                Text(
                    text = metric.value,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = metric.label,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Preview(name = "Metric Card Preview", showBackground = true)
@Composable
fun LangMetricCardPreview() {
    MaterialTheme {
        LangMetricCard(
            metric = LangMetric(
                id = "1",
                value = "15 Days",
                label = "Streak",
                iconResId = R_android.drawable.ic_menu_my_calendar
            ),
            onClick = {},
            modifier = Modifier.padding(LangSpacing)
        )
    }
}

// --------------------------------------------------------------------------------
// 4. Primary Button (Reusable)
// --------------------------------------------------------------------------------

@Composable
fun LangPrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier.fillMaxWidth().height(56.dp),
        shape = RoundedCornerShape(LangSpacing),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
        elevation = ButtonDefaults.buttonElevation(defaultElevation = 4.dp)
    ) {
        Text(text = text, style = MaterialTheme.typography.titleSmall)
    }
}

@Preview(name = "Primary Button Preview", showBackground = true)
@Composable
fun LangPrimaryButtonPreview() {
    MaterialTheme {
        LangPrimaryButton(
            text = "Click Me",
            onClick = {},
            modifier = Modifier.padding(LangSpacing)
        )
    }
}

// --------------------------------------------------------------------------------
// 5. Continue Learning Card
// --------------------------------------------------------------------------------

@Composable
fun LangContinueLearningCard(
    module: String,
    lesson: String,
    onContinueLearningClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(LangSpacing),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(2.dp)
        ),
        elevation = CardDefaults.cardElevation(2.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(LangSpacing * 1.5f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Continue your journey",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = "Module: $module",
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = lesson,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(Modifier.height(LangSpacing))
                LangPrimaryButton(
                    text = "Continue Learning",
                    onClick = onContinueLearningClick,
                    modifier = Modifier.fillMaxWidth(0.9f).height(48.dp)
                )
            }

            // Icon with background
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(MaterialTheme.shapes.medium)
                    .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    Icons.Filled.RamenDining, // Cutlery icon for Food & Drinks
                    contentDescription = "Lesson category icon",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

@Preview(name = "Continue Learning Card Preview", showBackground = true)
@Composable
fun LangContinueLearningCardPreview() {
    MaterialTheme {
        LangContinueLearningCard(
            module = "Food & Drinks",
            lesson = "Ordering at a Restaurant",
            onContinueLearningClick = {},
            modifier = Modifier.padding(LangSpacing)
        )
    }
}

// --------------------------------------------------------------------------------
// 6. Gamified Metric Item Card (LazyRow item)
// --------------------------------------------------------------------------------

@Composable
fun LangGamifiedMetricItemCard(
    item: LangGamifiedMetricItem,
    onClick: (LangGamifiedMetricItem) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(LangSpacing),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
        ),
        elevation = CardDefaults.cardElevation(0.dp),
        modifier = modifier
            .width(120.dp)
            .height(120.dp)
            .clickable { onClick(item) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(LangSpacing / 2),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                painter = painterResource(id = item.iconResId),
                contentDescription = item.title,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = item.title,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Medium),
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}