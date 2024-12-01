package com.example.translateapp.data

import com.example.translateapp.BuildConfig

object ApiHelper {
    fun buildApiUrl(from: String, to: String): String {
        val apiUrl = BuildConfig.API_URL
        return "$apiUrl?from=$from&to=$to"
    }
}
