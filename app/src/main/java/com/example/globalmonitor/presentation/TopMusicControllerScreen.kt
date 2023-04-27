package com.example.globalmonitor.presentation

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.globalmonitor.R
import com.example.globalmonitor.presentation.main.MainViewModel
import dev.chrisbanes.snapper.ExperimentalSnapperApi

@OptIn(ExperimentalSnapperApi::class, ExperimentalFoundationApi::class)
@Composable
fun TopMusicControllerScreen(viewModel: MainViewModel, modifier: Modifier = Modifier, context: Context) {
    Box(modifier = modifier
        .fillMaxWidth()
        .height(60.dp)
        .background(MaterialTheme.colors.background)) {
        Image(
            painter = rememberAsyncImagePainter(model = viewModel.state.currentPlayingSong.imageUri),
            contentDescription = "Song Image",
            modifier = Modifier
                .size(60.dp)
                .background(Color.White),
            contentScale = ContentScale.Crop
        )
        Row(modifier = Modifier.padding(70.dp, 0.dp, 0.dp, 0.dp).fillMaxSize(), verticalAlignment = Alignment.CenterVertically) {
            Column(
                Modifier.fillMaxHeight().weight(1f).clickable { viewModel.expandedSongScreen = true }
            ) {
                Text(
                    text = viewModel.state.currentPlayingSong.title,
                    fontSize = 16.sp,
                    modifier = Modifier
                        .padding(5.dp, 5.dp, 0.dp, 0.dp)
                        .fillMaxWidth(),
                    color = MaterialTheme.colors.onBackground,
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
                    color = MaterialTheme.colors.onBackground,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
            Icon(
                painter = painterResource(id = R.drawable.play_prev_song_icon),
                contentDescription = "previous",
                tint = MaterialTheme.colors.onBackground,
                modifier = Modifier
                    .size(45.dp)
                    .padding(0.dp, 0.dp, 15.dp, 0.dp)
                    .clickable {
                        viewModel.skipToPreviousSong()
                    }
            )
            Icon(
                painter = painterResource(id = viewModel.playIcon),
                contentDescription = "play/pause",
                tint = MaterialTheme.colors.onBackground,
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
                tint = MaterialTheme.colors.onBackground,
                modifier = Modifier
                    .size(45.dp)
                    .padding(0.dp, 0.dp, 15.dp, 0.dp)
                    .clickable {
                        viewModel.skipToNextSong()
                    }
            )
        }
    }
}












