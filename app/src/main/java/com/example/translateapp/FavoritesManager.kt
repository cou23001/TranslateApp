package com.example.translateapp

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue
import android.util.Log

/**
 * Manages favorite translations for a user, including storing and loading data from Firestore.
 */
class FavoritesManager {

    // Firebase authentication instance for user management
    private val mAuth: FirebaseAuth = FirebaseAuth.getInstance()

    // Firestore database instance for managing favorites data
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()

    companion object {
        /**
         * Firestore field name for storing the user's favorite words.
         */
        const val FIELD_FAVORITE_WORD = "favoriteWord"

        /**
         * Firestore field name for storing the translated versions of the favorite words.
         */
        const val FIELD_TRANSLATED_WORD = "translatedWord"

        /**
         * Firestore field name for storing the timestamp of the last update.
         */
        const val FIELD_UPDATED_AT = "updatedAt"

        /**
         * Firestore field name for storing the unique identifier of the user.
         */
        const val FIELD_USER_ID = "userID"
    }

    /**
     * Function to store in Firestore lists of favorite and translated words
     */
    fun storeFavorites(favoriteWords: List<String>, translatedWords: List<String>) {
        val userId = mAuth.currentUser?.uid ?: run {
            Log.e("FavoritesManager", "User not logged in")
            return
        }
        Log.d("FavoritesManager", "Current User ID: $userId")
        val userDocRef = db.collection("userFavorites").document(userId)

        // Attempt to retrieve the user's document from Firestore
        userDocRef.get().addOnSuccessListener { document ->
            // Retrieve current favorite words and translated words from Firestore
            val currentFavorites = document.toObject(FavoritesData::class.java)

            // Combine current favorites with new ones, ensuring uniqueness with distinct()
            val updatedFavoriteWords = (currentFavorites?.favoriteWord ?: emptyList()) + favoriteWords
            val updatedTranslatedWords = (currentFavorites?.translatedWord ?: emptyList()) + translatedWords

            // Remove duplicates if needed, maintaining order
            val distinctFavoriteWords = updatedFavoriteWords.distinct()
            val distinctTranslatedWords = updatedTranslatedWords.distinct()

            // Create a map to store in Firestore
            val favoritesData = hashMapOf(
                FIELD_FAVORITE_WORD to distinctFavoriteWords,
                FIELD_TRANSLATED_WORD to distinctTranslatedWords,
                FIELD_UPDATED_AT to FieldValue.serverTimestamp(),
                FIELD_USER_ID to userId
            )

            // Save the updated favorites data to Firestore
            userDocRef.set(favoritesData).addOnSuccessListener {
                Log.d("FavoritesManager", "Favorites saved successfully!")
            }.addOnFailureListener { e ->
                Log.e("FavoritesManager", "Error saving favorites", e)
            }
        }.addOnFailureListener { e ->
            Log.e("FavoritesManager", "Error retrieving favorites", e)
        }
    }


    /**
     * Function to query user´s favorites and translated words from Firestore
     */
    fun loadFavorites(userId: String , onComplete: (List<String>, List<String>) -> Unit) {
        db.collection("userFavorites").document(userId).get().addOnSuccessListener { document ->
            val favoriteWords = document.get(FIELD_FAVORITE_WORD) as? List<String> ?: emptyList()
            val translatedWord = document.get(FIELD_TRANSLATED_WORD) as? List<String> ?: emptyList()
            onComplete(favoriteWords,translatedWord)
        }.addOnFailureListener { e ->
            Log.e("FavoritesManager", "Error loading favorites", e)
            onComplete(emptyList(),emptyList())
        }
    }

    /**
     * Function to delete user´s favorites and translated words from Friestore
     */
    fun clearFavorites(userId: String) {
        db.collection("userFavorites").document(userId).delete().addOnSuccessListener {
            Log.d("FavoritesManager", "Favorites cleared successfully!")
        }.addOnFailureListener { e ->
            Log.e("FavoritesManager", "Error clearing favorites", e)
        }
    }
}

/**
 * Data class representing the structure of a user's favorite words and their translations.
 * */
data class FavoritesData(
    val favoriteWord: List<String>? = emptyList(),
    val translatedWord: List<String>? = emptyList(),
    val updatedAt: com.google.firebase.Timestamp? = null,
    val userID: String? = null
)
