package com.example.translateapp.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.example.translateapp.BuildConfig
import kotlinx.coroutines.*
import com.example.translateapp.data.ApiClient

@Composable
fun ApiScreen(
    onNavigateToFavorites: () -> Unit,
    onAddFavorite: (String) -> Unit
) {
    var inputWord by remember { mutableStateOf("") }
    var translationResult by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        val location = BuildConfig.API_LOC
        val apiKey = BuildConfig.API_KEY

        // Header
        AppHeader()

        // Input field for the word to translate
        OutlinedTextField(
            value = inputWord,
            onValueChange = { inputWord = it },
            label = { Text("Enter a word or phrase to translate") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Helper function for translation
            fun translate(from: String, to: String) {
                coroutineScope.launch {
                    translationResult = ApiClient.apiConnection(
                        buildApiUrl(from, to),
                        location,
                        apiKey,
                        inputWord
                    )
                }
            }

            Button(
                onClick = { translate("en", "es") },
                modifier = Modifier.wrapContentWidth()
            ) {
                Text("English to Spanish")
            }
            Button(
                onClick = { translate("es", "en") },
                modifier = Modifier.wrapContentWidth()
            ) {
                Text("Español a Inglés")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (translationResult.isNotEmpty()) {
            Text(
                text = translationResult,
                style = MaterialTheme.typography.bodyMedium
            )

            Button(
                onClick = { onAddFavorite(translationResult) },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Add to Favorites")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = onNavigateToFavorites,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Go to Favorites")
        }
    }
}

@Composable
fun AppHeader() {
    Text(
        text = "AI Translate",
        style = MaterialTheme.typography.displayMedium,
        modifier = Modifier.padding(16.dp)
    )
}

// Helper function to build the API URL
fun buildApiUrl(from: String, to: String): String {
    val apiUrl = BuildConfig.API_URL
    return "$apiUrl?from=$from&to=$to"
}

@Preview(showBackground = true)
@Composable
fun ApiScreenPreview() {
    ApiScreen(
        onNavigateToFavorites = {},
        onAddFavorite = {}
    )
}
