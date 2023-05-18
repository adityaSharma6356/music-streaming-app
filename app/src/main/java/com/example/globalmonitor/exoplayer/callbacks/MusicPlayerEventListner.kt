package com.example.globalmonitor.exoplayer.callbacks

import android.app.Service.STOP_FOREGROUND_DETACH
import android.app.Service.STOP_FOREGROUND_LEGACY
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.globalmonitor.exoplayer.MusicService
import com.google.android.exoplayer2.ExoPlaybackException
import com.google.android.exoplayer2.Player

class MusicPlayerEventListner(
    private val musicService: MusicService
) : Player.Listener {
    override fun onPlaybackStateChanged(state: Int) {
        super.onPlaybackStateChanged(state)
        if (state == Player.STATE_READY){
            musicService.stopForeground(STOP_FOREGROUND_DETACH)
        }
    }

    override fun onPlayerError(error: ExoPlaybackException) {
        super.onPlayerError(error)
        Toast.makeText(musicService, "Unknown error occurred", Toast.LENGTH_SHORT).show()
    }
}