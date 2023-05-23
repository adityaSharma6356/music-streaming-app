package com.example.globalmonitor.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.globalmonitor.data.entities.SongModel
import com.example.globalmonitor.presentation.viewmodels.MainViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(mainViewModel: MainViewModel, state: PagerState) {
    LaunchedEffect(key1 = state.currentPage){
        mainViewModel.iconClick(state.currentPage+1)
    }
    HorizontalPager(state = state, modifier = Modifier.fillMaxWidth(), pageCount = 3){
        when(it){
            0 ->{
                HomeScreen(mainViewModel, mainViewModel.state.songsList, "Latest Hits")
            }
            1 -> {
                PlaylistsScreen(mainViewModel, "Playlists")
            }
            2 -> {
                HomeScreen(mainViewModel, emptyList(), "Profile")
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: MainViewModel, songlist: List<SongModel>, title: String){
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
            SongList(viewModel = viewModel, songlist = songlist)
        }
    }
}


















