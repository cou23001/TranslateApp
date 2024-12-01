package com.example.translateapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.example.translateapp.ui.theme.TranslateTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.translateapp.ui.screens.ApiScreen
import com.example.translateapp.ui.screens.FavoritesScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TranslateApp()
        }
    }
}

@Composable
fun TranslateApp() {

    // Mutable state to hold the list of favorite translations
    val favorites = remember { mutableStateListOf<String>() }
    // Tracks the current screen ("Main" or "Favorites")
    var currentScreen by remember { mutableStateOf("Main") } // Tracks the current screen

    // A conditional block to switch between the "Main" screen and the "Favorites" screen
    when (currentScreen) {
        // When the current screen is "Main", show the ApiScreen
        "Main" -> ApiScreen(
            // onAddFavorite callback adds a new favorite translation to the list
            onAddFavorite = { translation -> favorites.add(translation) },
            // onNavigateToFavorites callback switches the current screen to "Favorites"
            onNavigateToFavorites = { currentScreen = "Favorites" }
        )
        // When the current screen is "Favorites", show the FavoritesScreen composable
        "Favorites" -> FavoritesScreen(
            // Pass the list of favorite translations to the FavoritesScreen
            favorites = favorites,
            // onBack callback switches the current screen back to "Main"
            onBack = { currentScreen = "Main" }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TranslateAppPreview() {
    // Preview of the TranslateApp
    TranslateTheme {
        TranslateApp()
    }
}
