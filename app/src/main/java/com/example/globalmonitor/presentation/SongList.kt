package com.example.globalmonitor.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.globalmonitor.R
import com.example.globalmonitor.data.entities.SongModel
import com.example.globalmonitor.data.local.storeFavSong
import com.example.globalmonitor.presentation.viewmodels.MainViewModel
import kotlinx.coroutines.launch


@Composable
fun SongList(viewModel: MainViewModel, songlist: List<SongModel>, show:Boolean = true) {
    val context = LocalContext.current
    val lifecycleScope = rememberCoroutineScope()
    LazyColumn(Modifier.fillMaxWidth(), contentPadding = PaddingValues(0.dp, 0.dp, 0.dp, 200.dp)) {
        itemsIndexed(songlist){_ , song ->
            Row(modifier = Modifier
                .fillMaxWidth()
                .clickable {
                    viewModel.playOrToggleSong(song)
                    lifecycleScope.launch {
                        viewModel.lazystate.scrollToItem(
                            viewModel.currentSongIndex
                        )
                    }
                }
                .height(80.dp)
                .padding(10.dp, 5.dp)) {
                Image(
                    painter = rememberAsyncImagePainter(model = song.imageUri),
                    contentDescription = "song image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .padding(0.dp, 0.dp, 10.dp, 0.dp)
                        .size(60.dp)
                )
                Column(Modifier.weight(1f)) {
                    Text(
                        text = song.title,
                        fontSize = 17.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(10.dp, 5.dp, 0.dp, 0.dp),
                    )
                    Text(
                        text = song.subtitle,
                        fontSize = 12.sp,
                        overflow = TextOverflow.Ellipsis,
                        color = Color.Black,
                        modifier = Modifier.padding(10.dp, 5.dp, 0.dp, 0.dp)
                    )
                }
                if(show){
                    var icon by remember { mutableStateOf(R.drawable.not_liked_icon) }
                    icon = if(viewModel.likedSongs.contains(song)) R.drawable.likded_icon else R.drawable.not_liked_icon
                    Icon(painter = painterResource(id = icon),
                        contentDescription = "liked",
                        modifier = Modifier.padding(5.dp, 15.dp).size(27.dp)
                            .clickable {
                                if(viewModel.likedSongs.contains(song)){
                                    icon = R.drawable.not_liked_icon
                                    viewModel.likedSongs.remove(song)
                                    storeFavSong(context,viewModel)
                                }
                                else{
                                    icon = R.drawable.likded_icon
                                    viewModel.likedSongs.add(song)
                                    storeFavSong(context,viewModel)
                                }

                            }
                        , tint = Color.Black)
                }
            }
        }
    }
}