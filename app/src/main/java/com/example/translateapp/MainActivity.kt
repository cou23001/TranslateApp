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

    /**
     * MainActivity is the entry point of the application and handles the initialization of the composable navigation.
     */
    class MainActivity : ComponentActivity() {
        /**
         * Overrides the onCreate method to set the content of the activity.
         * The content is defined by the `AppNavigation` composable, which manages the navigation between screens.
         */
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContent {
                AppNavigation()  // Launches the app's navigation composable
            }
        }
    }

    /**
     * Composable function to handle navigation between different screens in the app.
     *
     * The navigation is managed using a `currentScreen` state variable that determines which screen to display.
     */
    @Composable
    fun AppNavigation() {
        // State variable to track the current screen ("login" or "Main")
        var currentScreen by remember { mutableStateOf("login") }

        // Conditional navigation logic based on the current screen
        when (currentScreen) {
            // Displays the LoginScreen composable, which allows the user to sign in or sign up.
            "login" -> LoginScreen(
                onSignIn = { email, password, context ->
                    FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                // Show success message on account creation
                                Toast.makeText(context, "Signed in successfully", Toast.LENGTH_SHORT).show()
                                currentScreen = "Main" // Navigate on success
                            } else {
                                // Show error message on failure
                                Toast.makeText(
                                    context,
                                    "Sign in failed: ${task.exception?.message}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                },
                onSignUp = { email, password, context ->
                    // Handle sign-up using Firebase Authentication
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
            /**
             * Displays the TranslateApp composable after a successful login.
             */
            "Main" -> TranslateApp(onLogout = {})
        }
    }


    /**
     * Composable function that represents the main structure of the Translate application.
     *
     * This function manages the navigation between different screens of the app: the main translation screen,
     * the favorites screen, and the login screen. It handles user actions, state management, and
     * interaction with the `FavoritesManager` for storing and clearing favorites.
     */
    @SuppressLint("MutableCollectionMutableState")
    @Composable
    fun TranslateApp(onLogout: () -> Unit) {
        // Initialize FavoritesManager for managing favorites
        val favoritesManager = FavoritesManager()

        // State to manage the list of favorite words
        var favorites by remember { mutableStateOf<MutableList<String>>(mutableListOf()) }

        // State to manage the list of translations corresponding to the favorite words
        var translations by remember { mutableStateOf<MutableList<String>>(mutableListOf()) }

        // State to track the currently displayed screen ("Main", "Favorites", or "Login")
        var currentScreen by remember { mutableStateOf("Main") } // Tracks the current screen

        // Screen navigation logic
        when (currentScreen) {
            // Main translation screen
            "Main" -> ApiScreen (
                onNavigateToFavorites = { currentScreen = "Favorites" },
                onAddFavorite = { favoriteWord, translatedWord ->
                    // Add new favorite word and its translation to the lists
                    val updatedFavoriteWords = favorites + favoriteWord
                    val updatedTranslatedWords = translations + translatedWord

                    // Store the updated favorites using FavoritesManager
                    favoritesManager.storeFavorites(updatedFavoriteWords, updatedTranslatedWords) // Store both favoriteWord and translatedWord

                    // Update the state with the new lists
                    favorites = updatedFavoriteWords as MutableList<String>
                    translations = updatedTranslatedWords as MutableList<String>
                },

                // To Exit
                onLogout = {
                    performLogout() // Perform any required logout operations
                    onLogout() // Trigger the onLogout callback
                    currentScreen = "Login" // Navigate to the login screen
                }
            )
            // Favorites screen
            "Favorites" -> FavoritesScreen(
                favorites = favorites, // Pass the list of favorite words
                onBack = { currentScreen = "Main" }, // Navigate back to the main screen
                onClearFavorites = {
                    favorites.clear() // Clear the local favorites list
                }
            )

            // Login screen
            "Login" -> LoginScreen(
                onSignIn = { email, password, context ->
                    // Handle sign-in with Firebase
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
                    // Handle sign-up with Firebase
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

/**
 * Function to logout from Firebase
 */
fun performLogout() {
        try {
            FirebaseAuth.getInstance().signOut()
        } catch (e: Exception) {
            // Log error or show user-friendly message
            Log.e("Authentication", "Sign-out failed", e)
        }
    }

/**
 * A Composable function that displays a login screen with options to sign in or sign up.
 *
 * Accepts the email, password, and the application context as parameters.
 */
@Composable
fun LoginScreen(
    onSignIn: (String, String, Context) -> Unit, // Callback for signing in
    onSignUp: (String, String, Context) -> Unit // Callback for signing up
) {
    // Local context from the Android environment
    val context = LocalContext.current
    // Holds the email input by the user
    var email by remember { mutableStateOf("") }
    // Holds the password input by the user
    var password by remember { mutableStateOf("") }

    // Layout container
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header displaying the application name
        AppHeader()

        // Spacer for vertical spacing between header and input fields
        Spacer(modifier = Modifier.height(32.dp))

        // Email input field
        TextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )

        // Spacer for spacing between email and password fields
        Spacer(modifier = Modifier.height(16.dp))

        // Password input field with visual transformation for hiding text
        TextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = PasswordVisualTransformation()
        )

        // Spacer for spacing between the password field and the Sign In button
        Spacer(modifier = Modifier.height(16.dp))

        // Sign In button
        Button(
            onClick = {
                // Checks if the email and password fields are not empty
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    onSignIn(email, password, context)
                } else {
                    // Displays a Toast message if fields are empty
                    Toast.makeText(context, "Please enter an email and password", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign In")
        }

        // Spacer for spacing between the Sign In and Sign Up buttons
        Spacer(modifier = Modifier.height(8.dp))

        // Sign Up button
        Button(
            onClick = {
                // Checks if the email and password fields are not empty
                if (email.isNotEmpty() && password.isNotEmpty()) {
                    onSignUp(email, password, context) // Calls the sign-up callback
                } else {
                    // Displays a Toast message if fields are empty
                    Toast.makeText(context, "Please enter an email and password", Toast.LENGTH_SHORT).show()
                }
            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Sign Up")
        }
    }
}


/**
 * A Composable function that provides a preview of the TranslateApp.
 */
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
