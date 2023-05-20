package com.example.globalmonitor.data.entities

data class Songs (
    val imageUri: String = "",
    val mediaid: String = "",
    val songUri: String = "",
    val subtitle: String = "",
    val title: String = "",
    val duration: Long = 0L,
    val local : Boolean = false,
)