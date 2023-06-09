package com.example.globalmonitor.presentation

import com.example.globalmonitor.data.entities.SongModel

data class SongsListState (
    var currentPlayingSong: SongModel = SongModel("","","","",""),
    var songsList: List<SongModel> = emptyList(),
    var isLoading: Boolean = false
)