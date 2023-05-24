package com.example.globalmonitor.presentation

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import coil.compose.rememberAsyncImagePainter
import com.example.globalmonitor.R
import com.example.globalmonitor.presentation.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun TopMusicControllerScreen(viewModel: MainViewModel, modifier: Modifier = Modifier, context: Context,) {
    var lifsco = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val color by animateColorAsState(targetValue = Color(viewModel.dominantColors[0].toArgb()), animationSpec = TweenSpec(500))
    Box(modifier = modifier
        .padding(5.dp)
        .fillMaxWidth()
        .height(60.dp)
        .background(color)) {
        Image(
            painter = rememberAsyncImagePainter(model = viewModel.state.currentPlayingSong.imageUri),
            contentDescription = "Song Image",
            modifier = Modifier
                .size(60.dp)
                .background(Color.White),
            contentScale = ContentScale.Crop
        )
        Row(modifier = Modifier
            .padding(70.dp, 0.dp, 0.dp, 0.dp)
            .fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            val interactionSource = remember { MutableInteractionSource() }
            Column(
                Modifier
                    .fillMaxHeight()
                    .weight(1f)
                    .clickable(
                        interactionSource = interactionSource,
                        indication = null
                    ) {
                        viewModel.expandedSongScreen = true
                        viewModel.changeHeight(configuration)
                    }
            ) {
                Text(
                    text = viewModel.state.currentPlayingSong.title,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(5.dp, 5.dp, 0.dp, 0.dp)
                        .fillMaxWidth()
                        .basicMarquee(),
                    color = Color.White,
                    overflow = TextOverflow.Ellipsis,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1
                )
                Text(
                    text = viewModel.state.currentPlayingSong.subtitle,
                    fontSize = 10.sp,
                    modifier = Modifier
                        .padding(5.dp, 5.dp, 0.dp, 0.dp)
                        .fillMaxWidth(),
                    color =Color.White,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.play_prev_song_icon),
                contentDescription = "previous",
                tint = Color.White,
                modifier = Modifier
                    .size(45.dp)
                    .padding(0.dp, 0.dp, 15.dp, 0.dp)
                    .clickable {
                        lifsco.launch {
                            viewModel.currentSongIndex = viewModel.state.currentPlayingSong.mediaid.toInt().minus(1)
                            if(viewModel.currentSongIndex>0){
                                viewModel.lazystate.scrollToItem(viewModel.currentSongIndex+1)
                            }
                        }
                        viewModel.skipToPreviousSong()
                    }
            )
            Icon(
                painter = painterResource(id = viewModel.playIcon),
                contentDescription = "play/pause",
                tint =Color.White,
                modifier = Modifier
                    .size(45.dp)
                    .padding(0.dp, 0.dp, 15.dp, 0.dp)
                    .clickable {
                        viewModel.playOrToggleSong(
                            viewModel.state.currentPlayingSong,
                            true
                        )
                    }
            )
            Icon(
                painter = painterResource(id = R.drawable.play_next_song_icon),
                contentDescription = "next",
                tint =Color.White,
                modifier = Modifier
                    .size(45.dp)
                    .padding(0.dp, 0.dp, 15.dp, 0.dp)
                    .clickable {
                        lifsco.launch {
                            viewModel.currentSongIndex = viewModel.state.currentPlayingSong.mediaid.toInt().minus(1)
                            if(viewModel.currentSongIndex!=viewModel.state.songsList.lastIndex){
                                viewModel.lazystate.scrollToItem(viewModel.currentSongIndex+1)
                            }
                        }
                        viewModel.skipToNextSong()
                    }
            )
        }
        Canvas(modifier = Modifier
            .fillMaxSize()
            .zIndex(2f)){
            drawLine(cap = StrokeCap.Round,strokeWidth = 5f, color = Color.Red, start = Offset(x = 0f, y = size.height), end = Offset(x = size.width*viewModel.finalposi, y = size.height))
        }
    }
}












