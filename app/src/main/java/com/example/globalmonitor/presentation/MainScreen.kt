package com.example.globalmonitor.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.globalmonitor.presentation.main.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel, modifier: Modifier = Modifier){
    Box(modifier = modifier.fillMaxSize()
    ) {
        if(viewModel.state.isLoading){
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        }
        LazyColumn(modifier = Modifier.fillMaxSize()){
            itemsIndexed(viewModel.state.songsList) {index,song->
                Box(modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp, 5.dp)
                    .height(50.dp)
                    .background(Color(219, 229, 255, 255), RoundedCornerShape(15.dp)),
                    contentAlignment = Alignment.CenterStart) {
                    Text(
                        text = song.title,
                        color = Color.Black,
                        fontSize = 15.sp,
                        textAlign = TextAlign.Start,
                        modifier = Modifier
                            .padding(10.dp, 0.dp, 0.dp, 0.dp)
                            .fillMaxWidth()
                            .clickable {
                                viewModel.playOrToggleSong(song)
                                viewModel.currentSongIndex = index
                            }
                    )
                }
            }
        }
    }
}