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
    onNavigateToFavorites: () -> Unit, // Callback to navigate to the Favorites screen
    onAddFavorite: (String) -> Unit // Callback to add a translation to the favorites list
) {
    var inputWord by remember { mutableStateOf("") } // Stores the word or phrase entered by the user
    var translationResult by remember { mutableStateOf("") } // Stores the result of the translation
    val coroutineScope = rememberCoroutineScope() // Scope for launching coroutines

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top
    ) {
        // Retrieves the API location and key from the BuildConfig
        val location = BuildConfig.API_LOC
        val apiKey = BuildConfig.API_KEY

        // Header displaying the application name
        AppHeader()

        // Input field for the word/phrase to be translated
        OutlinedTextField(
            value = inputWord, // The current input value
            onValueChange = { inputWord = it }, // Updates inputWord when the user types
            label = { Text("Enter a word or phrase to translate") }, // Placeholder text
            modifier = Modifier.fillMaxWidth() // Makes the input field take up full width
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Row layout for translation buttons
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

            // Button to translate from English to Spanish
            Button(
                onClick = { translate("en", "es") },
                modifier = Modifier.wrapContentWidth()
            ) {
                Text("English to Spanish")
            }
            // Button to translate from Spanish to English
            Button(
                onClick = { translate("es", "en") },
                modifier = Modifier.wrapContentWidth()
            ) {
                Text("Español a Inglés")
            }
        }

        Spacer(modifier = Modifier.height(16.dp)) // Adds space between the translation buttons and result

        // If the translation result is not empty, display the result and options
        if (translationResult.isNotEmpty()) {
            // Displays the translation result
            Text(
                text = translationResult,
                style = MaterialTheme.typography.bodyMedium
            )

            // Button to add the translation to favorites
            Button(
                onClick = { onAddFavorite(translationResult) },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Add to Favorites")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Button to navigate to the Favorites screen
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
    // Displays the application header (title)
    Text(
        text = "Translate App",
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
    // Previews the ApiScreen
    ApiScreen(
        onNavigateToFavorites = {},
        onAddFavorite = {}
    )
}
