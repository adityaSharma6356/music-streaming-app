package com.example.globalmonitor.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.example.globalmonitor.data.entities.SongModel
import com.example.globalmonitor.presentation.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun MainScreen(viewModel: MainViewModel, modifier: Modifier = Modifier, state: LazyListState) {
    LazyRow(state = state, modifier = Modifier.fillMaxWidth(), userScrollEnabled = false){
        item {
            HomeScreen(viewModel, viewModel.state.songsList, "Latest Hits")
        }
        item {
            HomeScreen(viewModel, emptyList(), "Local Songs")
        }
        item {
            HomeScreen(viewModel, emptyList(), "Profile")
        }
    }
}

@Composable
fun HomeScreen(viewModel: MainViewModel, songlist: List<SongModel>, title: String){
    val lifecycleScope = rememberCoroutineScope()
    val config = LocalConfiguration.current
    Surface(color = Color.Transparent,modifier = Modifier
        .padding(0.dp, 50.dp)
        .zIndex(0f)
        .width(config.screenWidthDp.dp)
    ) {
        Column {
            Box(contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.Black)
            ) {
                Text(text = title, fontSize = 20.sp, color = Color.White, modifier = Modifier.padding(15.dp, 0.dp))
            }
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
                    }
                }
            }
        }
    }
}


















