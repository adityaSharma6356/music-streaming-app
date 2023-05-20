package com.example.globalmonitor.data.entities

import androidx.compose.ui.graphics.Color

data class SongModel (
    val imageUri: String = "",
    var mediaid: String = "",
    val songUri: String = "",
    val subtitle: String = "",
    val title: String = "",
    val duration: Long = 0L,
    val color: Color = Color(0,0,0),
    val dateAdded : String  = "",
    val local : Boolean = false
)