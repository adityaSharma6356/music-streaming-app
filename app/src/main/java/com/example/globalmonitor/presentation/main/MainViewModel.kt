package com.example.globalmonitor.presentation.main

import android.app.LocaleConfig
import android.content.res.Configuration
import android.graphics.BitmapFactory
import android.icu.text.SimpleDateFormat
import android.icu.util.TimeZone
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_DURATION
import android.support.v4.media.MediaMetadataCompat.METADATA_KEY_MEDIA_ID
import android.util.Log
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.core.graphics.toColor
import androidx.lifecycle.*
import androidx.palette.graphics.Palette
import com.example.globalmonitor.R
import com.example.globalmonitor.data.entities.SongModel
import com.example.globalmonitor.data.mapper.toSongModel
import com.example.globalmonitor.exoplayer.*
import com.example.globalmonitor.other.Constants
import com.example.globalmonitor.other.Constants.MEDIA_ROOT_ID
import com.example.globalmonitor.presentation.SongsListState
import com.example.globalmonitor.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.net.URL
import java.util.*
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val musicServiceConnection : MusicServiceConnection,
) : ViewModel() {
    var currentposi = 0
    var finalposi by mutableStateOf(0f)
    var heigt by  mutableStateOf(1000.dp)
    var expandedSongScreen by mutableStateOf(false)
    var state by mutableStateOf(SongsListState())
    var currentSongIndex by mutableStateOf(0)
    var playIcon by mutableStateOf(R.drawable.play_arrow)
    var circlePlayIcon by mutableStateOf(R.drawable.circle_play)
    var someKindOfError by mutableStateOf(false)
    var errorStatement = "No Internet"
    var minute by mutableStateOf("00")
    var liveminute by mutableStateOf("00")
    var shouldUpdateSeekbar = true
    var dominantColors by mutableStateOf(listOf<android.graphics.Color>(android.graphics.Color.argb(255,0,0,0).toColor(),android.graphics.Color.argb(255,0,0,0).toColor() ))
    private val isConnected = musicServiceConnection.isConnected
    private val networkError = musicServiceConnection.networkError
    private val curPlayingSong = musicServiceConnection.curPlayingSong
    private val playbackState = musicServiceConnection.playbackState
    private val lightColor = Color(119, 118, 118, 255)
    var colorHome by mutableStateOf(Color.White)
    var colorSearch by mutableStateOf(lightColor)
    var colorPlay by mutableStateOf(lightColor)
    var colorProf by mutableStateOf(lightColor)
    var lazystate by mutableStateOf(LazyListState())
    var isSongEnding = false

    private var scope : Job? = null
    private var scope2 : Job? = null
    var scope3 : Job? = null

    private val _curPlayerPosition = MutableLiveData<Long>()
    private val curPlayerPosition: LiveData<Long> = _curPlayerPosition

    private fun setColors(uri: URL) {
        scope2?.cancel()
        scope2 = viewModelScope.launch(Dispatchers.IO) {
            delay(400)
                val bitmap = BitmapFactory.decodeStream(uri.openConnection().getInputStream())
                val pallete = Palette.from(bitmap).generate()
                dominantColors = listOf(
                    pallete.getDarkVibrantColor(Color.Black.toArgb()).toColor(),
                    pallete.getLightVibrantColor(Color.White.toArgb()).toColor(),
                )
        }
    }

    fun iconClick(i:Int){
        when(i){
            1-> {
                colorHome = Color.White
                colorSearch = lightColor
                colorPlay = lightColor
                colorProf =lightColor
            }
            2-> {
                colorSearch = Color.White
                colorHome = lightColor
                colorPlay = lightColor
                colorProf = lightColor
            }
            3-> {
                colorPlay = Color.White
                colorSearch = lightColor
                colorHome = lightColor
                colorProf = lightColor
            }
            4-> {
                colorProf = Color.White
                colorSearch = lightColor
                colorPlay = lightColor
                colorHome = lightColor
            }
        }
    }

    fun closeScope(){
        scope?.cancel()
    }
    init {
        getSongsList()
        updateCurrentPlayerPosition()
    }

    private fun updateCurrentPlayerPosition() {
        scope?.cancel()
        scope = viewModelScope.launch {
            while(true) {
                val pos = playbackState.value?.currentPlaybackPosition
                if(curPlayerPosition.value != pos) {
                    _curPlayerPosition.postValue(pos)
                }
                delay(Constants.UPDATE_PLAYER_POSITION_INTERVAL)
            }
        }
    }

    private fun setCurPlayerTimeToDuration(ms: Long) {
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        dateFormat.timeZone = TimeZone.GMT_ZONE
        minute = dateFormat.format(ms)
    }
    fun setCurPlayerTimeLive(ms: Long) {
        val dateFormat = SimpleDateFormat("mm:ss", Locale.getDefault())
        dateFormat.timeZone = TimeZone.GMT_ZONE
        liveminute = dateFormat.format(ms)
    }
    fun changeHeight(configuration: Configuration){
        heigt = if(!expandedSongScreen){
            (configuration.screenHeightDp+88).dp
        } else {
            0.dp
        }
    }
    fun startPlayBackLiveData(th: LifecycleOwner){
        curPlayerPosition.observe(th){
            it?.let {
                if(shouldUpdateSeekbar){
                    currentposi = it.toInt()
                    finalposi = currentposi.toFloat()/state.currentPlayingSong.duration.toFloat()
                    setCurPlayerTimeLive(it)
                    isSongEnding = finalposi>0.99
                }
            }
        }
        playbackState.observe(th){
            if(it?.isPlaying == true) {
                playIcon = R.drawable.pause_icon
                circlePlayIcon = R.drawable.circle_pause
            } else {
                playIcon = R.drawable.play_arrow
                circlePlayIcon = R.drawable.circle_play
            }
        }
        curPlayingSong.observe(th){
            it?.let {
                state = state.copy(currentPlayingSong = it.toSongModel())
                setCurPlayerTimeToDuration(state.currentPlayingSong.duration)
                setColors(URL(state.currentPlayingSong.imageUri))
                currentSongIndex = it.description.mediaId?.toInt()?.minus(1) ?: 0
            }
        }
        isConnected.observe(th){
            it?.getContentIfNotHandled()?.let { result ->
                when(result){
                    is Resource.Error -> {
                        someKindOfError = true
                        errorStatement = result.message.toString()
                    }
                    else -> someKindOfError = false
                }
            }
        }
        networkError.observe(th){
            it?.getContentIfNotHandled()?.let { result ->
                when(result){
                    is Resource.Error -> {
                        someKindOfError = true
                        errorStatement = result.message.toString()
                    }
                    else -> someKindOfError = false
                }
            }
        }
    }

    private fun getSongsList(){
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
//        currentSongIndex = state.songsList.indexOf(state.currentPlayingSong)
        if(state.songsList.lastIndex > currentSongIndex) {
            playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> {
                    }
                    playbackState.isPlayEnabled -> {
//                        musicServiceConnection.transportControls.play()
                        playIcon = R.drawable.pause_icon
                        circlePlayIcon = R.drawable.circle_pause
                    }
                    else -> Unit
                }
            }
            currentSongIndex = state.currentPlayingSong.mediaid.toInt().minus(1)
        }
    }

    fun skipToPreviousSong() {
        seekTo(0L)
        musicServiceConnection.transportControls.skipToPrevious()
//        currentSongIndex = state.songsList.indexOf(state.currentPlayingSong)
        if(currentSongIndex>0) {
            playbackState.value?.let { playbackState ->
                when {
                    playbackState.isPlaying -> {
                    }
                    playbackState.isPlayEnabled -> {
//                        musicServiceConnection.transportControls.play()
                        playIcon = R.drawable.pause_icon
                        circlePlayIcon = R.drawable.circle_pause
                    }
                    else -> Unit
                }
            }
            currentSongIndex = state.currentPlayingSong.mediaid.toInt().minus(1)
        }
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
                    playbackState.isPlaying -> if(toggle){
                        musicServiceConnection.transportControls.pause()
                        playIcon = R.drawable.play_arrow
                        circlePlayIcon = R.drawable.circle_play
                    }
                    playbackState.isPlayEnabled -> {
                        musicServiceConnection.transportControls.play()
                        playIcon = R.drawable.pause_icon
                        circlePlayIcon = R.drawable.circle_pause
                    }
                    else -> Unit
                }
            }
        } else {
            musicServiceConnection.transportControls.playFromMediaId(mediaItem.mediaid, null)
            playIcon = R.drawable.pause_icon
            circlePlayIcon = R.drawable.circle_pause
        }
    }

    override fun onCleared() {
        super.onCleared()
        musicServiceConnection.unsubscribe(MEDIA_ROOT_ID, object: MediaBrowserCompat.SubscriptionCallback() {})
        scope?.cancel()
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
                                it.description.title.toString(),
                                it.description.extras?.getLong(METADATA_KEY_DURATION)?: 0L
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















