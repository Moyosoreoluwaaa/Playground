package com.playground.crypto.presentation.bookish// In a new file, e.g., components/SavedBookCard.kt

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.playground.R

@Composable
fun SavedBookCard(
    book: SavedBook,
    onCardClicked: (bookId: String) -> Unit,
    onSaveToggled: (bookId: String) -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp) // Example fixed height
            .clip(RoundedCornerShape(16.dp))
            .clickable { onCardClicked(book.id) },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Book Cover Image
            AsyncImage(
                model = book.coverImageUri,
                contentDescription = "Cover for book ${book.title}",
                placeholder = painterResource(R.drawable.ic_launcher_background),
                error = painterResource(R.drawable.ic_launcher_background),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .width(100.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(12.dp))
            )

            // Content Column
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // "Top book" pill
                    if (book.isTopBook) {
                        Text(
                            text = "Top book",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onPrimary,
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    // Save Button
                    IconButton(onClick = { onSaveToggled(book.id) }) {
                        Icon(
                            imageVector = if (book.isSaved) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = if (book.isSaved) "Unsave book" else "Save book",
                            tint = if (book.isSaved) Color.Red else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Book Title
                Text(
                    text = book.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Author Row
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AsyncImage(
                        model = book.authorImageUri,
                        contentDescription = "Author's profile image",
                        placeholder = painterResource(R.drawable.ic_launcher_background),
                        error = painterResource(R.drawable.ic_launcher_background),
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = book.authorName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}


@Preview(name = "SavedBookCard - With 'Top book' label", showBackground = true)
@Composable
fun SavedBookCardWithLabelPreview() {
    MaterialTheme {
        SavedBookCard(
            book = SavedBook(
                id = "1",
                title = "Rays of the Earth: A guide to self-care and meditation",
                coverImageUri = "https://example.com/cover1.jpg",
                isTopBook = true,
                isSaved = true,
                authorName = "Lina Kostenko",
                authorImageUri = "https://example.com/author1.jpg"
            ),
            onCardClicked = {},
            onSaveToggled = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

@Preview(name = "SavedBookCard - Without 'Top book' label", showBackground = true)
@Composable
fun SavedBookCardWithoutLabelPreview() {
    MaterialTheme {
        SavedBookCard(
            book = SavedBook(
                id = "2",
                title = "The Art of Listening",
                coverImageUri = "https://example.com/cover2.jpg",
                isTopBook = false,
                isSaved = false,
                authorName = "Jordan Peterson",
                authorImageUri = "https://example.com/author2.jpg"
            ),
            onCardClicked = {},
            onSaveToggled = {},
            modifier = Modifier.padding(16.dp)
        )
    }
}

