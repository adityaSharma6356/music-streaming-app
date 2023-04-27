package com.example.globalmonitor.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.globalmonitor.presentation.main.MainViewModel
import kotlinx.coroutines.cancel

@Composable
fun SongInfoScreen(mainViewModel: MainViewModel, modifier: Modifier) {
    val configuration = LocalConfiguration.current
    val viewModel = viewModel<SongViewModel>()
    BackHandler(mainViewModel.expandedSongScreen) {
        mainViewModel.expandedSongScreen = false
        viewModel.scope?.cancel()
    }
    if(mainViewModel.expandedSongScreen){
        viewModel.heigt = configuration.screenHeightDp.dp
    } else {
        viewModel.heigt = 0.dp
    }
    val ht by animateDpAsState(targetValue = viewModel.heigt, animationSpec = TweenSpec(300))

    ConstraintLayout(modifier = modifier
        .fillMaxWidth()
        .background(Color.Gray)
        .height(ht)) {
        val (songImage, songTitle, songArtist, seekBar, playPauseButton, nextButton, previousButton) = createRefs()
        if(mainViewModel.expandedSongScreen){
            Column(modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = rememberAsyncImagePainter(model = mainViewModel.state.currentPlayingSong.imageUri, filterQuality = FilterQuality.High),
                    contentDescription = "",
                    modifier = Modifier.size(250.dp)
                )
            }
        }
    }

}