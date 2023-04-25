package com.example.globalmonitor.presentation

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.globalmonitor.presentation.main.MainViewModel


@Composable
fun TopMusicControllerScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    Box(modifier = modifier
        .padding(10.dp)
        .height(60.dp)
        .clip(RoundedCornerShape(10.dp))
        .background(Color(255, 220, 220, 255))) {
        Image(
            painter = rememberAsyncImagePainter(model = viewModel.state.songsList[viewModel.currentSongIndex].imageUri),
            contentDescription = "",
            modifier = Modifier
                .padding(10.dp, 5.dp)
                .size(50.dp)
                .clip(RoundedCornerShape(3.dp))
        )
        Column(Modifier.fillMaxSize()) {
            Text(
                text = viewModel.state.songsList[viewModel.currentSongIndex].title,
                fontSize = 15.sp,
                modifier = Modifier
                    .padding(70.dp, 10.dp, 0.dp, 0.dp)
                    .fillMaxWidth(),
                color = Color.Black,
                overflow = TextOverflow.Ellipsis

            )
            Text(
                text = viewModel.state.songsList[viewModel.currentSongIndex].subtitle,
                fontSize = 10.sp,
                modifier = Modifier
                    .padding(70.dp, 0.dp, 0.dp, 0.dp)
                    .fillMaxWidth(),
                color = Color(1, 1, 1, 205),
                overflow = TextOverflow.Ellipsis
            )

        }
    }
}














