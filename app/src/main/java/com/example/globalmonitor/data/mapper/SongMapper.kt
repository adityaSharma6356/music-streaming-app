package com.example.globalmonitor.data.mapper

import android.graphics.Bitmap
import android.graphics.Picture
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaBrowserCompat.MediaItem.FLAG_PLAYABLE
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import androidx.core.net.toUri
import com.example.globalmonitor.R
import com.example.globalmonitor.data.entities.SongModel

fun SongModel.toMediaMetaDataCompat() : MediaMetadataCompat{
    return  MediaMetadataCompat.Builder()
        .putString(MediaMetadataCompat.METADATA_KEY_ARTIST, subtitle)
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, mediaid)
        .putString(MediaMetadataCompat.METADATA_KEY_TITLE, title)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_TITLE, title)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI, imageUri)
        .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, songUri)
        .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI, imageUri)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE, subtitle)
        .putString(MediaMetadataCompat.METADATA_KEY_DISPLAY_DESCRIPTION, subtitle)
        .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
        .build()
}

fun MediaMetadataCompat.toMediaBrowserCompatMediaItem() : MediaBrowserCompat.MediaItem {
    val desc = MediaDescriptionCompat.Builder()
        .setMediaUri(getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI).toUri())
        .setTitle(description.title)
        .setSubtitle(description.subtitle)
        .setMediaId(description.mediaId)
        .setIconUri(description.iconUri)
        .build()
    return MediaBrowserCompat.MediaItem(desc, FLAG_PLAYABLE)
}

fun MediaMetadataCompat.toSongModel(): SongModel{
    return SongModel(
        imageUri = getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_ICON_URI)?: "",
        mediaid = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID)?: "",
        songUri = getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)?: "",
        subtitle = getString(MediaMetadataCompat.METADATA_KEY_DISPLAY_SUBTITLE)?: "",
        title = getString(MediaMetadataCompat.METADATA_KEY_TITLE)?: "",
        duration = getLong(MediaMetadataCompat.METADATA_KEY_DURATION)
    )
}