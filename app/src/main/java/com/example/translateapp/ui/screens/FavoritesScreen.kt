package com.example.translateapp.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.translateapp.ui.theme.TranslateTheme
import com.google.firebase.auth.FirebaseAuth
import android.widget.Toast
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import com.example.translateapp.FavoritesManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment


@Composable
fun FavoritesScreen(
    favorites: MutableList<String> = mutableListOf(),
    translated: MutableList<String> = mutableListOf(),
    onBack: () -> Unit, // Callback to return to the previous screen
    onClearFavorites: () -> Unit
) {

    val context = LocalContext.current
    // Firebase
    val mAuth = FirebaseAuth.getInstance()
    val favoritesManager = FavoritesManager()

    // State for loading favorites from Cloud Firestore
    var isLoading by remember { mutableStateOf(true) }

    // Load favorites from Firestore on initial composition
    LaunchedEffect (Unit) {
        // Make sure the user is logged in before attempting to load favorites
        val userId = mAuth.currentUser?.uid
        if (userId != null) {
            //loadFavoritesFromFirebase(userId) { loadedFavorites ->
            favoritesManager.loadFavorites(userId) { loadedFavorites, loadedTranslations ->
                favorites.clear() // Clear existing favorites
                translated.clear() // Clear existing translated
                favorites.addAll(loadedFavorites) // Add loaded favorites
                translated.addAll(loadedTranslations) // Add loaded transalations
                isLoading = false
            }
        } else {
            isLoading = false // User is not logged in
        }
    }

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
        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        } else if (favorites.isEmpty()) {
            // If there are no favorites, display a message
            Text(
                text = "No favorites yet!",
                style = MaterialTheme.typography.bodyMedium
            )
        } else {
            // If there are favorites and translated words, display them in a scrollable list
            LazyColumn {
                // Iterate over the favorites and translated words list and display each item
                items(favorites.zip(translated)) { (favorite, translation) ->
                    // Display each favorite -> translation item as a Text widget
                    Text(
                        text = "$favorite -> $translation",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
            }
        }

        // Check if there are any favorites before displaying the "Clear Favorites" button
        if (favorites.isNotEmpty()) {
            // Button to clear all favorites
            Button(
                onClick = {
                    // Invoke the callback to clear favorites locally
                    onClearFavorites()
                    // Display a toast message confirming the action
                    Toast.makeText(context, "Favorites cleared", Toast.LENGTH_SHORT).show()
                    // Get the current user's ID to clear their favorites from Firestore
                    val userId = mAuth.currentUser?.uid
                    if (userId != null) {
                        // Clear the user's favorites using the FavoritesManager
                        favoritesManager.clearFavorites(userId)
                    }
                    // Navigate back to the previous screen
                    onBack()
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear Favorites")
            }
        }

        // Button to navigate back to the previous screen
        Button(
            onClick = onBack,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Back")
        }
    }
}

@SuppressLint("UnrememberedMutableState")
@Preview(showBackground = true)
@Composable
fun FavoritesScreenPreview() {
    // Previews the FavoritesScreen
    TranslateTheme {
        FavoritesScreen(
            favorites = mutableStateListOf("Hello -> Hola", "World -> Mundo"),
            onBack = { },
            onClearFavorites = { }
        )
    }
}
