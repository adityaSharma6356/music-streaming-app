package com.example.globalmonitor.data.entities

data class PlayListItem(
    var name: String = "",
    var list: MutableList<SongModel> = mutableListOf()
)
