package com.example.translateapp.data

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject

object ApiClient {
    // API call to the translation service and returns the translated text
    suspend fun apiConnection(apiUrl: String, location: String, apiKey: String, inputWord: String): String {
        val client = OkHttpClient()

        // Create the JSON request body using JSONArray to wrap the input word
        val requestBodyJson = JSONArray().apply {
            put(JSONObject().put("Text", inputWord))
        }.toString()

        // Define the content type as "application/json"
        val mediaType = "application/json".toMediaType()
        // Create the request body with the JSON content and set the appropriate media type
        val requestBody = requestBodyJson.toRequestBody(mediaType)

        // Build the HTTP request with necessary headers and the request body
        val request = Request.Builder()
            .url(apiUrl) // The URL of the API endpoint
            .post(requestBody) // Post the request body
            .addHeader("Ocp-Apim-Subscription-Key", apiKey) // API subscription key header
            .addHeader("Ocp-Apim-Subscription-Region", location) // API subscription region header
            .addHeader("Content-Type", "application/json") // Content type header indicating JSON data
            .build()

        // Perform the API request on a background thread to avoid blocking the UI thread
        return withContext(Dispatchers.IO) {
            try {
                // Execute the request and obtain the response
                val response = client.newCall(request).execute()
                // Check if the response was successful (status code 200-299)
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        // Parse the response body to extract the translation
                        val translations = JSONArray(responseBody)
                            .getJSONObject(0) // Get the first object in the array
                            .getJSONArray("translations") // Get the "translations" array
                        val translatedText = translations.getJSONObject(0).getString("text") // Extract the translated text
                        translatedText // Return the translated text
                    } ?: "Empty response body" // Return message if the response body is null
                } else {
                    // Return a message with the error code and message if the response was unsuccessful
                    "Connection failed: ${response.code} - ${response.message}"
                }
            } catch (e: Exception) {
                // Catch any exceptions and return an error message
                "Connection error: ${e.message}"
            }
        }
    }
}
