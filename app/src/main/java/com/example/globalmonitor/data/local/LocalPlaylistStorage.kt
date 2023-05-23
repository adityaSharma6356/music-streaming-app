package com.example.globalmonitor.data.local

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import com.example.globalmonitor.data.entities.PlayListItem
import com.example.globalmonitor.data.entities.SongModel
import com.example.globalmonitor.presentation.viewmodels.MainViewModel
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

fun storeFavSong(context: Context,viewModel: MainViewModel) {
    val sharedpef = context.getSharedPreferences("SnowFlakeStorageFile" , Context.MODE_PRIVATE)
    val editor = sharedpef.edit()
    val gson = Gson()
    val it = viewModel.likedSongs
    val json = gson.toJson(it)
    editor.putString("FavouriteSongs" , json)
    editor.apply()
}

fun loadFavouriteSongs(viewModel: MainViewModel, context: Context){
    val shared = context.getSharedPreferences("SnowFlakeStorageFile" , Context.MODE_PRIVATE)

    val json = shared.getString("FavouriteSongs" , null)
    if(json!=null) {
        viewModel.likedSongs = try {
            val turnsType = Array<SongModel>::class.java
            Gson().fromJson(json, turnsType).toMutableList().toMutableStateList()
        } catch (e: JsonSyntaxException){
            e.printStackTrace()
            mutableStateListOf<SongModel>()
        }
    }
}
fun storePlaylists(context: Context,viewModel: MainViewModel) {
    val sharedpef = context.getSharedPreferences("SnowFlakeStoragePlaylists" , Context.MODE_PRIVATE)
    val editor = sharedpef.edit()
    val gson = Gson()
    var er = 0
    var found = false
    for(i in 0 until viewModel.playLists.size){
        if(viewModel.playLists[i].name==""){
            er = i
            found = true
        }
    }
    if(found) viewModel.playLists.removeAt(er)
    val it = viewModel.playLists
    val json = gson.toJson(it)
    editor.putString("FavouritePlaylists" , json)
    editor.apply()
}

fun loadPlaylists(viewModel: MainViewModel, context: Context){
    val shared = context.getSharedPreferences("SnowFlakeStoragePlaylists" , Context.MODE_PRIVATE)

    val json = shared.getString("FavouritePlaylists" , null)
    if(json!=null) {
        viewModel.playLists = try {
            val turnsType = Array<PlayListItem>::class.java
            Gson().fromJson(json, turnsType).toMutableList().toMutableStateList()
        } catch (e: JsonSyntaxException){
            e.printStackTrace()
            mutableStateListOf()
        }
    }
}

