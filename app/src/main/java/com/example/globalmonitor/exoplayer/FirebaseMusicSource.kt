package com.example.globalmonitor.exoplayer

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.*
import com.example.globalmonitor.data.remote.MusicDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseMusicSource @Inject constructor(
    private val musicDatabase: MusicDatabase
) {
    var songs = emptyList<MediaMetadataCompat>()

    suspend fun fetchMediaData() = withContext(Dispatchers.IO){
        state = State.STATE_INITIALIZING
        val allSongs = musicDatabase.getSongsList()
        songs = allSongs.map { song ->
            MediaMetadataCompat.Builder()
                .putString(METADATA_KEY_ARTIST, song.subtitle)
                .putString(METADATA_KEY_MEDIA_ID, song.mediaid)
                .putString(METADATA_KEY_TITLE, song.title)
                .putString(METADATA_KEY_DISPLAY_TITLE, song.title)
                .putString(METADATA_KEY_DISPLAY_ICON_URI, song.imageUri)
                .putString(METADATA_KEY_MEDIA_URI, song.songUri)
                .putString(METADATA_KEY_ALBUM_ART_URI, song.imageUri)
                .putString(METADATA_KEY_DISPLAY_SUBTITLE, song.subtitle)
                .putString(METADATA_KEY_DISPLAY_DESCRIPTION, song.subtitle)
                .build()
        }
    }

    private val onReadyListners = mutableListOf<(Boolean) -> Unit> ()

    private var state: State = State.STATE_CREATED
        set(value) {
            if (value == State.STATE_INITIALIZED || value == State.STATE_ERROR){
                synchronized(onReadyListners) {
                    field = value
                    onReadyListners.forEach { listner ->
                        listner(state == State.STATE_INITIALIZED)
                    }
                }
            } else {
                field = value
            }
        }
    fun whenReady(action: (Boolean) -> Unit): Boolean{
        return if(state == State.STATE_CREATED || state == State.STATE_INITIALIZING){
            onReadyListners += action
            false
        } else {
            action(state == State.STATE_INITIALIZED)
            true
        }
    }
}

enum class State{
    STATE_CREATED,
    STATE_INITIALIZING,
    STATE_INITIALIZED,
    STATE_ERROR

}