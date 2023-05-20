package com.example.globalmonitor.data.local

import android.content.ContentUris
import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import com.example.globalmonitor.data.entities.SongModel

class LocalMediaSource (
    context: Context
) {
    private var contextOP: Context? = null
    init {
        contextOP = context
    }
    fun getLocalSongs(index: Int) : List<SongModel> {
        Log.d("loadingLog", "local song fetcher Started...")
        val musicList = mutableListOf<SongModel>()
        var mediaIdSetter = index
        val projection = arrayOf(
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.DATE_ADDED,
            MediaStore.Audio.Media.ALBUM_ID,
            MediaStore.Audio.Media.DATA
        )
        val selection = "${MediaStore.Audio.Media.IS_MUSIC} != 0"
        val sortOrder = "${MediaStore.Audio.Media.DATE_ADDED} ASC"
        val cursor = contextOP?.contentResolver?.query(
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
            projection,
            selection,
            null,
            sortOrder
        )
        Log.d("loadingLog", "local song fetcher cursor created...")
        if (cursor != null) {
            Log.d("loadingLog", "local song fetcher cursor NOT NULL...")
            while (cursor.moveToNext()) {
                val title = cursor.getString(0)
                val artist = cursor.getString(1)
                val album = cursor.getString(2)
                val duration = cursor.getInt(3)
                val dateAdded = cursor.getString(4)
                val albumId = cursor.getLong(5)
                val filepath = cursor.getString(6)
                var albumArtUri : Uri?
                albumArtUri = ContentUris.withAppendedId(
                    Uri.parse("content://media/external/audio/albumart"),
                    albumId
                )
                val musicFile = SongModel(
                    imageUri = albumArtUri.toString(),
                    mediaid = (mediaIdSetter).toString(),
                    title = title,
                    subtitle = artist,
                    duration = duration.toLong(),
                    dateAdded = dateAdded,
                    songUri = filepath
                )
                musicList.add(musicFile)
                mediaIdSetter++
            }
            Log.d("loadingLog", "local song fetcher Closing cursor...")
            cursor.close()
            for((temp, i) in musicList.withIndex()){
                i.mediaid = temp.toString()
            }
            musicList.sortByDescending { it.mediaid }
            Log.d("loadingLog", "local song fetcher Closing...")
            Log.d("loadingLog", musicList.toString())
        }
        return musicList
    }
}