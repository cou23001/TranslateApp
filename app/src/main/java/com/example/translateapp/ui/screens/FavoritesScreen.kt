package com.example.translateapp.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.translateapp.ui.theme.TranslateTheme

@Composable
fun FavoritesScreen(
    favorites: List<String>, // The list of favorite translations passed as a parameter
    onBack: () -> Unit // Callback to return to the previous screen
) {
    // Column layout that arranges child elements vertically
    Column(
        modifier = Modifier
            .fillMaxSize() // Makes the Column take up the full screen size
            .padding(16.dp), // Adds padding around the column
        verticalArrangement = Arrangement.Top // Aligns children at the top of the column
    ) {
        // Header displaying the title of the screen
        Text(
            text = "Favorites", // The title text
            style = MaterialTheme.typography.displayMedium, // Applies the display medium typography style
            modifier = Modifier.padding(bottom = 16.dp) // Adds space below the title
        )

        // Check if the favorites list is empty
        if (favorites.isEmpty()) {
            // If there are no favorites, display a message
            Text(
                text = "No favorites yet!",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            // If there are favorites, display them in a scrollable list
            LazyColumn {
                // Iterate over the favorites list and display each item
                items(favorites) { favorite ->
                    // Display each favorite item as a Text widget
                    Text(
                        text = favorite,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        // Back Button at the bottom of the screen to navigate back
        Button(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
            Text("Back")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    // Previews the FavoritesScreen
    TranslateTheme {
        FavoritesScreen(
            favorites = listOf("Hello -> Hola", "World -> Mundo"),
            onBack = {}
        )
    }
}
