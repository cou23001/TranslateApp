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
    suspend fun apiConnection(apiUrl: String, location: String, apiKey: String, inputWord: String): String {
        val client = OkHttpClient()

        // Create the JSON request body using JSONObject
        val requestBodyJson = JSONArray().apply {
            put(JSONObject().put("Text", inputWord))
        }.toString()

        val mediaType = "application/json".toMediaType()
        val requestBody = requestBodyJson.toRequestBody(mediaType)

        val request = Request.Builder()
            .url(apiUrl)
            .post(requestBody)
            .addHeader("Ocp-Apim-Subscription-Key", apiKey) // Microsoft Translator API key
            .addHeader("Ocp-Apim-Subscription-Region", location) // Region for the API
            .addHeader("Content-Type", "application/json")
            .build()

        return withContext(Dispatchers.IO) {
            try {
                val response = client.newCall(request).execute()
                if (response.isSuccessful) {
                    response.body?.string()?.let { responseBody ->
                        val translations = JSONArray(responseBody)
                            .getJSONObject(0)
                            .getJSONArray("translations")
                        val translatedText = translations.getJSONObject(0).getString("text")
                        "Translated text: $translatedText"
                    } ?: "Empty response body"
                } else {
                    "Connection failed: ${response.code} - ${response.message}"
                }
            } catch (e: Exception) {
                "Connection error: ${e.message}"
            }
        }
    }
}
