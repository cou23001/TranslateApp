package com.example.translateapp.data

import com.example.translateapp.BuildConfig

object ApiHelper {
    // Helper function to build the API URL for translation
    fun buildApiUrl(from: String, to: String): String {
        val apiUrl = BuildConfig.API_URL
        return "$apiUrl?from=$from&to=$to"
    }
}
