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
    favorites: List<String>, // The list of favorite translations
    onBack: () -> Unit // Callback to return to the previous screen
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Header
        Text(
            text = "Favorites",
            style = MaterialTheme.typography.displayMedium,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        if (favorites.isEmpty()) {
            Text(
                text = "No favorites yet!",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            LazyColumn {
                items(favorites) { favorite ->
                    Text(
                        text = favorite,
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        // Back Button
        Button(onClick = onBack, modifier = Modifier.padding(top = 16.dp)) {
            Text("Back")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    TranslateTheme {
        FavoritesScreen(
            favorites = listOf("Hello -> Hola", "World -> Mundo"),
            onBack = {}
        )
    }
}
