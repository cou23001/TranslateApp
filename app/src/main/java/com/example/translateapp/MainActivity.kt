package com.example.translateapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
    val coroutineScope = rememberCoroutineScope()
    val favorites = remember { mutableStateListOf<String>() } // List to store favorites
    var currentScreen by remember { mutableStateOf("Main") } // Tracks the current screen

    when (currentScreen) {
        "Main" -> ApiScreen(
            onAddFavorite = { translation -> favorites.add(translation) },
            onNavigateToFavorites = { currentScreen = "Favorites" }
        )
        "Favorites" -> FavoritesScreen(
            favorites = favorites,
            onBack = { currentScreen = "Main" }
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TranslateAppPreview() {
    TranslateTheme {
        TranslateApp()
    }
}
