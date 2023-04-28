package com.example.globalmonitor.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter.State.Empty.painter
import coil.compose.rememberAsyncImagePainter
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.globalmonitor.R
import com.example.globalmonitor.presentation.main.MainViewModel
import kotlinx.coroutines.cancel

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun SongInfoScreen(mainViewModel: MainViewModel, modifier: Modifier, viewModel: SongViewModel) {
    val configuration = LocalConfiguration.current
//    val viewModel = viewModel<SongViewModel>()
    val interactionSource = remember { MutableInteractionSource() }
    BackHandler(mainViewModel.expandedSongScreen) {
        mainViewModel.expandedSongScreen = false
    }
    ConstraintLayout(modifier = modifier
        .fillMaxWidth()
        .background(Color.White)
        .height(configuration.screenHeightDp.dp + 35.dp)) {
        val (songImage, songTitle, songArtist, seekBar, playPauseButton, nextButton, previousButton, backIcon, timeLapsed, timeTotal) = createRefs()
        if(mainViewModel.expandedSongScreen){
            Card(backgroundColor = Color.White,
                elevation = 50.dp,
                modifier = Modifier
                    .size((configuration.screenWidthDp * 0.7).dp)
                    .constrainAs(songImage) {
                        top.linkTo(parent.top, margin = 150.dp)
                        start.linkTo(parent.start,)
                        end.linkTo(parent.end,)
                    }){
                GlideImage(model = mainViewModel.state.currentPlayingSong.imageUri,
                    contentDescription = "", contentScale = ContentScale.Crop)
            }
            Icon(
                painter = painterResource(id = R.drawable.down_arrow),
                contentDescription = "next",
                tint = Color.Black,
                modifier = Modifier
                    .constrainAs(backIcon) {
                        top.linkTo(parent.top, margin = 35.dp)
                        start.linkTo(parent.start, margin = 15.dp)
                    }
                    .size(45.dp)
                    .padding(0.dp, 0.dp, 15.dp, 0.dp)
                    .clickable {
                        mainViewModel.expandedSongScreen = false
                    }
            )
            Icon(
                painter = painterResource(id = mainViewModel.circlePlayIcon),
                contentDescription = "play/pause",
                tint = Color.Black,
                modifier = Modifier
                    .constrainAs(playPauseButton) {
                        bottom.linkTo(parent.bottom, margin = 70.dp)
                        start.linkTo(parent.start,)
                        end.linkTo(parent.end,)
                    }
                    .size(70.dp)
                    .clickable(interactionSource = interactionSource, indication = null) {
                        mainViewModel.playOrToggleSong(
                            mainViewModel.state.currentPlayingSong,
                            true
                        )
                    }
            )
            Icon(
                painter = painterResource(id = R.drawable.play_prev_song_icon),
                contentDescription = "prev",
                tint = Color.Black,
                modifier = Modifier
                    .constrainAs(previousButton) {
                        top.linkTo(playPauseButton.top)
                        bottom.linkTo(playPauseButton.bottom)
                        end.linkTo(playPauseButton.start, margin = 40.dp)
                    }
                    .size(40.dp)
                    .clickable {
                        mainViewModel.skipToPreviousSong()
                    }
            )
            Icon(
                painter = painterResource(id = R.drawable.play_next_song_icon),
                contentDescription = "prev",
                tint = Color.Black,
                modifier = Modifier
                    .constrainAs(nextButton) {
                        top.linkTo(playPauseButton.top)
                        bottom.linkTo(playPauseButton.bottom)
                        start.linkTo(playPauseButton.end, margin = 40.dp)
                    }
                    .size(40.dp)
                    .clickable {
                        mainViewModel.skipToNextSong()
                    }
            )
            Canvas(modifier = Modifier.height(20.dp).fillMaxWidth(0.8f).constrainAs(seekBar){
                bottom.linkTo(playPauseButton.top, margin = 60.dp)
                start.linkTo(parent.start, )
                end.linkTo(parent.end,)
            }){
                drawLine(cap = StrokeCap.Round,strokeWidth = 5f, color = Color(182, 182, 182, 255), start = Offset(x = 0f, y = size.height), end = Offset(x = size.width, y = size.height))
                drawLine(cap = StrokeCap.Round,strokeWidth = 7f, color = Color.Black, start = Offset(x = 0f, y = size.height), end = Offset(x = size.width*mainViewModel.finalposi, y = size.height))
                drawCircle(color = Color.Black, radius = 10f, center = Offset(x = size.width*mainViewModel.finalposi, y = size.height))
            }
            Text(
                text = mainViewModel.state.currentPlayingSong.title,
                fontSize = 24.sp,
                color = Color.Black,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .constrainAs(songTitle) {
                    bottom.linkTo(songArtist.top, margin = 5.dp)
                    start.linkTo(parent.start, margin = 50.dp)
                })
            Text(
                text = mainViewModel.state.currentPlayingSong.subtitle,
                fontSize = 14.sp,
                color = Color.Black,
                modifier = Modifier.constrainAs(songArtist) {
                    bottom.linkTo(seekBar.top, margin = 10.dp)
                    start.linkTo(parent.start, margin = 50.dp)
                })
            Text(
                text = mainViewModel.liveminute,
                fontSize = 11.sp,
                color = Color.Black,
                modifier = Modifier.constrainAs(timeTotal) {
                    top.linkTo(seekBar.bottom, margin = 10.dp)
                    start.linkTo(seekBar.start, margin = 0.dp)
                })
            Text(
                text = mainViewModel.minute,
                fontSize = 11.sp,
                color = Color.Black,
                modifier = Modifier.constrainAs(timeLapsed) {
                    top.linkTo(seekBar.bottom, margin = 10.dp)
                    end.linkTo(seekBar.end, margin = 0.dp)
                })
        }
    }
}