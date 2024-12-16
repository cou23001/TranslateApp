package com.example.translateapp

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.example.translateapp.ui.theme.TranslateTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.example.translateapp.ui.screens.ApiScreen
import com.example.translateapp.ui.screens.FavoritesScreen
import com.google.firebase.auth.FirebaseAuth
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import com.example.translateapp.ui.components.AppHeader

    class MainActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                AppNavigation()
            }
        }
    }

    @Composable
    fun AppNavigation() {
        var currentScreen by remember { mutableStateOf("login") }

        when (currentScreen) {
            "login" -> LoginScreen(
                onSignIn = { email, password, context ->
                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Signed in successfully", Toast.LENGTH_SHORT).show()
                                currentScreen = "Main" // Navigate on success
                            } else {
                                Toast.makeText(
                                    context,
                                    "Sign in failed: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                },
                onSignUp = { email, password, context ->
                    FirebaseAuth.getInstance()
                        .createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(context, "Account created successfully", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(
                                    context,
                                    "Sign up failed: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
            )
            "Main" -> TranslateApp(onLogout = {})
        }
    }


    @SuppressLint("MutableCollectionMutableState")
    @Composable
    fun TranslateApp(onLogout: () -> Unit) {
        val favoritesManager = FavoritesManager()

        var favorites by remember { mutableStateOf<MutableList<String>>(mutableListOf()) }
        var translations by remember { mutableStateOf<MutableList<String>>(mutableListOf()) }

        //var favorites by remember { mutableStateOf<MutableList<String>>(mutableListOf()) }
        var currentScreen by remember { mutableStateOf("Main") } // Tracks the current screen

        // A conditional block to switch between the "Main" screen and the "Favorites" screen
        when (currentScreen) {
            // When the current screen is "Main", show the ApiScreen
            "Main" -> ApiScreen (
                onNavigateToFavorites = { currentScreen = "Favorites" },
                // onAddFavorite callback adds a new favorite translation to the list
                onAddFavorite = { favoriteWord, translatedWord ->
                    //val favoriteWord = inputWord // This is the original word entered by the user
                    //val translatedWord = translation // This is the translated word

                    // Add the favoriteWord and translatedWord to their respective lists
                    val updatedFavoriteWords = favorites + favoriteWord
                    val updatedTranslatedWords = translations + translatedWord

                    favoritesManager.storeFavorites(updatedFavoriteWords, updatedTranslatedWords) // Store both favoriteWord and translatedWord
                    favorites = updatedFavoriteWords as MutableList<String>
                    translations = updatedTranslatedWords as MutableList<String>
                },

                // onToExit
                onLogout = {
                    performLogout()
                    onLogout()
                    currentScreen = "Login"
                }
            )
            // When the current screen is "Favorites", show the FavoritesScreen composable
            "Favorites" -> FavoritesScreen(
                favorites = favorites,
                // onBack callback switches the current screen back to "Main"
                onBack = { currentScreen = "Main" },
                onClearFavorites = {
                    //favoritesManager.clearFavorites() // Clear favorites from Firestore
                    favorites.clear() // Clear the local list as well
                }
            )
            "Login" -> LoginScreen(
                onSignIn = { email, password, context ->
                    // Handle sign-in logic here
                    // For example, you can call Firebase sign-in
                    FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Sign-in successful, navigate to the main screen
                                currentScreen = "Main"
                            } else {
                                // Sign-in failed, show an error message
                                Toast.makeText(context, "Sign-in failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                },
                onSignUp = { email, password, context ->
                    // Handle sign-up logic here
                    // For example, you can call Firebase sign-up
                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Sign-up successful, navigate to the main screen
                                currentScreen = "Main"
                            } else {
                                // Sign-up failed, show an error message
                                Toast.makeText(context, "Sign-up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                            }
                        }
                }
            )
        }
    }

    fun performLogout() {
        try {
            FirebaseAuth.getInstance().signOut()
        } catch (e: Exception) {
            // Log error or show user-friendly message
            Log.e("Authentication", "Sign-out failed", e)
        }
    }

@Composable
fun LoginScreen(
    onSignIn: (String, String, Context) -> Unit,
    onSignUp: (String, String, Context) -> Unit
) {
    val context = LocalContext.current // Get context here
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header displaying the application name
        AppHeader()
        Spacer(modifier = Modifier.height(32.dp))
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    onSignIn(email, password, context)
                } else {
                    Toast.makeText(context, "Please enter an email and password", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In")
        }

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = {
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    onSignUp(email, password, context)
                } else {
                    Toast.makeText(context, "Please enter an email and password", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up")
        }
    }
}


@Preview(showBackground = true)
@Composable
fun TranslateAppPreview() {
    // Preview of the TranslateApp
    TranslateTheme {
        TranslateApp(
            onLogout = {}
        )
    }
}
