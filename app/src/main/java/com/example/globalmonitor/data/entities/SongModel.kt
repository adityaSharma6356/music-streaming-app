package com.example.globalmonitor.data.entities

import androidx.compose.ui.graphics.Color

data class SongModel (
    val imageUri: String = "",
    var mediaid: String = "",
    val songUri: String = "",
    val subtitle: String = "",
    val title: String = "",
    val duration: Long = 0L,
    val dateAdded : String  = "",
    val local : Boolean = false,
    val lightColor : Color = Color(255, 255, 255, 255),
    val darkColor : Color = Color(0, 0, 0, 255),
)