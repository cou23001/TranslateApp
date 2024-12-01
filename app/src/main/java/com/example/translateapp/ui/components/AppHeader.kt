package com.example.translateapp.ui.components

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.layout.padding

@Composable
fun AppHeader() {
    // Title
    Text(
        text = "Translate App",
        style = androidx.compose.material3.MaterialTheme.typography.displayMedium,
        modifier = Modifier.padding(16.dp)
    )
}
