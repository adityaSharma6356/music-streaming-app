package com.example.globalmonitor.data.remote

import com.example.globalmonitor.data.entities.SongModel
import com.example.globalmonitor.data.entities.Songs
import com.example.globalmonitor.data.mapper.toSongModel
import com.example.globalmonitor.other.Constants.SONG_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import retrofit2.HttpException
import java.io.IOException

class MusicDatabase {
    private val fireStore = FirebaseFirestore.getInstance()
    private val songCollection = fireStore.collection(SONG_COLLECTION)

    suspend fun getSongsList(): List<SongModel>{
        return try {
            songCollection.get().await().toObjects(Songs::class.java).map { it.toSongModel() }
        } catch (e: IOException){
            emptyList()
        } catch (e: HttpException){
            emptyList()
        }
    }
}