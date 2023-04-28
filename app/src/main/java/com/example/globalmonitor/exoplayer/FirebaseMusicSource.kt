package com.example.globalmonitor.exoplayer

import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_URI
import android.util.Log
import androidx.core.net.toUri
import com.example.globalmonitor.data.mapper.toMediaBrowserCompatMediaItem
import com.example.globalmonitor.data.mapper.toMediaMetaDataCompat
import com.example.globalmonitor.data.remote.MusicDatabase
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FirebaseMusicSource @Inject constructor(
    private val musicDatabase: MusicDatabase
) {
    var songs = emptyList<MediaMetadataCompat>()

    suspend fun fetchMediaData() = withContext(Dispatchers.Main){
        state = State.STATE_INITIALIZING
        val allSongs = musicDatabase.getSongsList()
//        Log.d("somelog", allSongs.toString())
        songs = allSongs.map { it.toMediaMetaDataCompat() }
        state = State.STATE_INITIALIZED
    }

    fun asMediaSource(dataSourceFactory: DefaultDataSourceFactory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach {
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(it.getString(METADATA_KEY_MEDIA_URI).toUri()))
            concatenatingMediaSource.addMediaSource(mediaSource)
        }
        return concatenatingMediaSource
    }
    fun asMediaItems() = songs.map { it.toMediaBrowserCompatMediaItem() }.toMutableList()

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