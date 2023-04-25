package com.example.globalmonitor.presentation.main

import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.globalmonitor.data.entities.SongModel
import com.example.globalmonitor.exoplayer.MusicServiceConnection
import com.example.globalmonitor.exoplayer.isPlayEnabled
import com.example.globalmonitor.exoplayer.isPlaying
import com.example.globalmonitor.exoplayer.isPrepared
import com.example.globalmonitor.other.Constants.MEDIA_ROOT_ID
import com.example.globalmonitor.presentation.SongsListState
import com.example.globalmonitor.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection : MusicServiceConnection,
) : ViewModel() {

    var state by mutableStateOf(SongsListState())
    var currentSongIndex by mutableStateOf(0)
    var progress by mutableStateOf(0f)

    fun change(){
        progress = if(progress==0f) 1f else 0f
    }

    val isConnected = musicServiceConnection.isConnected
    val networkError = musicServiceConnection.networkError
    val curPlayingSong = musicServiceConnection.curPlayingSong
    val playbackState = musicServiceConnection.playbackState


    init {
        getSongsList()
    }

    fun getSongsList(){
        viewModelScope.launch {
            getSongs()
                .collect{ result ->
                    when(result){
                        is Resource.Success -> {
                            result.data?.let {
                                state = state.copy(songsList = it)
                            }
                        }
                        is Resource.Error -> Unit
                        is Resource.Loading -> {
                            state = state.copy(isLoading = result.isLoading)
                        }
                    }
                }
        }
    }


    fun skipToNextSong() {
        musicServiceConnection.transportControls.skipToNext()
    }

    fun skipToPreviousSong() {
        musicServiceConnection.transportControls.skipToPrevious()
    }

    fun seekTo(pos: Long) {
        musicServiceConnection.transportControls.seekTo(pos)
    }


    fun playOrToggleSong(mediaItem: SongModel, toggle: Boolean = false) {
        val isPrepared = playbackState.value?.isPrepared ?: false
        if(isPrepared && mediaItem.mediaid ==
            curPlayingSong.value?.getString(METADATA_KEY_MEDIA_ID)) {
            playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> if(toggle) musicServiceConnection.transportControls.pause()
                    playbackState.isPlayEnabled -> musicServiceConnection.transportControls.play()
                    else -> Unit
                }
            }
        } else {
            musicServiceConnection.transportControls.playFromMediaId(mediaItem.mediaid, null)
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(MEDIA_ROOT_ID, object: MediaBrowserCompat.SubscriptionCallback() {})
    }

    private suspend fun getSongs(): Flow<Resource<List<SongModel>>> {
        return flow {
            val deferred = CompletableDeferred<List<SongModel>>()
            emit(Resource.Loading(null))
            musicServiceConnection.subscribe(
                MEDIA_ROOT_ID,
                object : MediaBrowserCompat.SubscriptionCallback() {
                    override fun onChildrenLoaded(
                        parentId: String,
                        children: MutableList<MediaBrowserCompat.MediaItem>
                    ) {
                        super.onChildrenLoaded(parentId, children)
                        val items = children.map {
                            SongModel(
                                it.description.iconUri.toString(),
                                it.description.mediaId.toString(),
                                it.description.mediaUri.toString(),
                                it.description.subtitle.toString(),
                                it.description.title.toString()
                            )
                        }
                        deferred.complete(items)
                    }
                })
            try {
                val songs = deferred.await()
                emit(Resource.Loading(null, false))
                emit(Resource.Success(songs))
            } finally {
            }
        }
    }
}















