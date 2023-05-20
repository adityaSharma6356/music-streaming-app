package com.example.globalmonitor.exoplayer

import android.net.Uri
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_URI
import androidx.core.net.toUri
import com.example.globalmonitor.data.entities.SongModel
import com.example.globalmonitor.data.local.LocalMediaSource
import com.example.globalmonitor.data.mapper.toMediaBrowserCompatMediaItem
import com.example.globalmonitor.data.mapper.toMediaMetaDataCompat
import com.example.globalmonitor.data.remote.MusicDatabase
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.source.ConcatenatingMediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject

class FirebaseMusicSource @Inject constructor(
    private val musicDatabase: MusicDatabase,
    private val localDatabase: LocalMediaSource
) {

    var songs = emptyList<MediaMetadataCompat>()

    suspend fun fetchMediaData() = withContext(Dispatchers.Main){
        state = State.STATE_INITIALIZING
        val allSongs = musicDatabase.getSongsList()
        val finalList = mutableListOf<SongModel>()
        finalList.addAll(allSongs)
//        val allLocalSongs = localDatabase.getLocalSongs(0)
//        finalList.addAll(allLocalSongs)
        songs = finalList.map { it.toMediaMetaDataCompat() }
        state = State.STATE_INITIALIZED
    }

    fun asMediaSource(dataSourceFactory: DefaultDataSourceFactory): ConcatenatingMediaSource {
        val concatenatingMediaSource = ConcatenatingMediaSource()
        songs.forEach {
            val mediaSource = ProgressiveMediaSource.Factory(dataSourceFactory)
                .createMediaSource(MediaItem.fromUri(
                    if(it.getString(METADATA_KEY_MEDIA_ID).toInt() < 1000){
                        it.getString(METADATA_KEY_MEDIA_URI).toUri()
                    }
                    else {
                        Uri.fromFile(File(it.getString(METADATA_KEY_MEDIA_URI)))
                    }))
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