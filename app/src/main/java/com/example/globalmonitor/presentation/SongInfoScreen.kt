package com.example.globalmonitor.presentation

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.TweenSpec
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.scrollBy
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.Slider
import androidx.compose.material.SliderColors
import androidx.compose.material.SliderDefaults
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
//import androidx.compose.ui.node.CanFocusChecker.end
//import androidx.compose.ui.node.CanFocusChecker.start
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import coil.compose.AsyncImage
import coil.compose.AsyncImagePainter.State.Empty.painter
import coil.request.ImageRequest
import com.example.globalmonitor.R
import com.example.globalmonitor.presentation.main.MainViewModel
import kotlinx.coroutines.NonDisposableHandle.parent
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch


@Composable
fun SongInfoScreen(mainViewModel: MainViewModel, modifier: Modifier,) {

    val configuration = LocalConfiguration.current
    val interactionSource = remember { MutableInteractionSource() }
    val color by animateColorAsState(targetValue = Color(mainViewModel.dominantColors[1].toArgb()), animationSpec = TweenSpec(500))
    val colorD by animateColorAsState(targetValue = Color(mainViewModel.dominantColors[0].toArgb()), animationSpec = TweenSpec(500))
    BackHandler(mainViewModel.expandedSongScreen) {
        mainViewModel.expandedSongScreen = false
        mainViewModel.changeHeight(configuration)
    }
    ConstraintLayout(modifier = modifier
        .fillMaxWidth()
        .zIndex(10f)
        .background(Color.White)
        .height(configuration.screenHeightDp.dp + 90.dp)) {
        val (fullScreenShadow, replayButton, songTitle, songArtist, seekBar, playPauseButton, nextButton, previousButton, backIcon, timeLapsed, timeTotal, seekLine) = createRefs()
        val lazycorscope = rememberCoroutineScope()
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .height(450.dp)
                .constrainAs(fullScreenShadow) {
                    bottom.linkTo(parent.bottom)
                }
                .zIndex(9f)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, colorD)
                    )
                ), color = Color.Transparent
        ){}
            AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(mainViewModel.state.currentPlayingSong.imageUri).crossfade(500).build(), contentDescription = "background", modifier = Modifier
                .fillMaxSize()
                .zIndex(8f)
                .blur(20.dp), contentScale = ContentScale.Crop)
            Box(modifier = Modifier
                .fillMaxWidth()
                .zIndex(10f)
                .background(Color.Transparent)
                .padding(0.dp, 150.dp, 0.dp, 0.dp)) {
                LazyRow(userScrollEnabled = false,modifier = Modifier
                    .background(Color.Transparent)
                    .pointerInput(Unit) {
                        var x = 0f
                        detectHorizontalDragGestures(
                            onHorizontalDrag = { change, dragAmount ->
                                lazycorscope.launch {
                                    x += dragAmount
                                    mainViewModel.lazystate.scrollBy(-dragAmount * 1f)
                                }
                            },
                            onDragEnd = {
                                mainViewModel.currentSongIndex =
                                    mainViewModel.state.currentPlayingSong.mediaid
                                        .toInt()
                                        .minus(1)
                                if (x < 0 && mainViewModel.currentSongIndex != mainViewModel.state.songsList.lastIndex) {
                                    x = 0f
                                    lazycorscope.launch {
                                        mainViewModel.lazystate.animateScrollToItem(mainViewModel.currentSongIndex + 1)
                                        mainViewModel.skipToNextSong()
                                    }
                                } else if (x > 0 && mainViewModel.currentSongIndex != 0) {
                                    x = 0f
                                    lazycorscope.launch {
                                        mainViewModel.lazystate.animateScrollToItem(mainViewModel.currentSongIndex - 1)
                                        mainViewModel.seekTo(0L)
                                        mainViewModel.skipToPreviousSong()
                                    }
                                }
                            },
                        )
                    }
                    .fillMaxWidth(), state = mainViewModel.lazystate){
                    itemsIndexed(mainViewModel.state.songsList){ index, song ->
                        Box(modifier = Modifier
                            .fillParentMaxWidth()
                            .background(Color.Transparent)) {
                            Card(backgroundColor = Color.White,
                                elevation = 15.dp,
                                modifier = Modifier
                                    .align(Alignment.Center)
                                    .size((configuration.screenWidthDp * 0.85).dp)){
                                AsyncImage(model = ImageRequest.Builder(LocalContext.current).data(song.imageUri).crossfade(500).build(),
                                    contentDescription = "song image", contentScale = ContentScale.Crop,  )
                            }
                            LaunchedEffect(key1 =  mainViewModel.state, key2 = mainViewModel.isSongEnding){
                                mainViewModel.scope3?.cancel()
                                mainViewModel.scope3 = launch {
                                    delay(1000)
                                    Log.d("launched", "running")
                                    mainViewModel.lazystate.scrollToItem(mainViewModel.currentSongIndex)
                                }
                            }
                        }
                    }
                }
            }
        Icon(
            painter = painterResource(id = R.drawable.down_arrow),
            contentDescription = "next",
            tint = color,
            modifier = Modifier
                .constrainAs(backIcon) {
                    top.linkTo(parent.top, margin = 35.dp)
                    start.linkTo(parent.start, margin = 15.dp)
                }
                .size(55.dp)
                .zIndex(10f)
                .padding(0.dp, 0.dp, 15.dp, 0.dp)
                .clickable {
                    mainViewModel.expandedSongScreen = false
                    mainViewModel.changeHeight(configuration)
                }
        )
        Icon(
            painter = painterResource(id = mainViewModel.circlePlayIcon),
            contentDescription = "play/pause",
            tint = color,
            modifier = Modifier
                .constrainAs(playPauseButton) {
                    bottom.linkTo(parent.bottom, margin = 70.dp)
                    start.linkTo(parent.start,)
                    end.linkTo(parent.end,)
                }
                .size(70.dp)
                .zIndex(10f)
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
            tint =  color,
            modifier = Modifier
                .constrainAs(previousButton) {
                    top.linkTo(playPauseButton.top)
                    bottom.linkTo(playPauseButton.bottom)
                    end.linkTo(playPauseButton.start, margin = 40.dp)
                }
                .size(40.dp)
                .zIndex(10f)
                .clickable {
                    mainViewModel.currentSongIndex = mainViewModel.state.currentPlayingSong.mediaid
                        .toInt()
                        .minus(1)
                    if (mainViewModel.currentSongIndex > 0) {
                        lazycorscope.launch {
                            mainViewModel.lazystate.animateScrollToItem(
                                mainViewModel.currentSongIndex - 1
                            )
                        }
                    }
                    mainViewModel.skipToPreviousSong()
                }
        )
        Icon(
            painter = painterResource(id = R.drawable.play_next_song_icon),
            contentDescription = "prev",
            tint =  color,
            modifier = Modifier
                .constrainAs(nextButton) {
                    top.linkTo(playPauseButton.top)
                    bottom.linkTo(playPauseButton.bottom)
                    start.linkTo(playPauseButton.end, margin = 40.dp)
                }
                .size(40.dp)
                .zIndex(10f)
                .clickable {
                    mainViewModel.currentSongIndex = mainViewModel.state.currentPlayingSong.mediaid
                        .toInt()
                        .minus(1)
                    if (mainViewModel.currentSongIndex != mainViewModel.state.songsList.lastIndex) {
                        lazycorscope.launch {
                            mainViewModel.lazystate.animateScrollToItem(
                                mainViewModel.currentSongIndex + 1
                            )
                        }
                    }
                    mainViewModel.skipToNextSong()
                }
        )
        var rColor by remember {
            mutableStateOf(Color(182, 182, 182, 159))
        }
        rColor = if(mainViewModel.isReplayEnabled) color else Color(182, 182, 182, 159)
        Icon(
            painter = painterResource(id = R.drawable.replay_icon),
            contentDescription = "prev",
            tint =  rColor,
            modifier = Modifier
                .constrainAs(replayButton) {
                    top.linkTo(playPauseButton.top)
                    bottom.linkTo(playPauseButton.bottom)
                    start.linkTo(playPauseButton.end, margin = 110.dp)
                }
                .size(30.dp)
                .zIndex(10f)
                .clickable {
                    mainViewModel.isReplayEnabled = !mainViewModel.isReplayEnabled
                    rColor = if (mainViewModel.isReplayEnabled) color else Color(182, 182, 182, 159)
                }
        )
        Slider(colors = SliderDefaults.colors(activeTrackColor = color, thumbColor = color, inactiveTrackColor = Color(
            182,
            182,
            182,
            159
        ), ),
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(20.dp)
                .zIndex(11f)
                .constrainAs(seekBar) {
                    bottom.linkTo(playPauseButton.top, margin = 50.dp)
                    start.linkTo(parent.start,)
                    end.linkTo(parent.end,)
                },
            value = mainViewModel.finalposi, onValueChange = {
                mainViewModel.shouldUpdateSeekbar = false
                mainViewModel.finalposi = it
                mainViewModel.setCurPlayerTimeLive((mainViewModel.state.currentPlayingSong.duration * mainViewModel.finalposi).toLong())
        },
        onValueChangeFinished = {
            mainViewModel.seekTo((mainViewModel.state.currentPlayingSong.duration * mainViewModel.finalposi).toLong())
            mainViewModel.shouldUpdateSeekbar = true

        })
        Text(
            text = mainViewModel.state.currentPlayingSong.title,
            fontSize = 24.sp,
            color = color,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .zIndex(10f)
                .constrainAs(songTitle) {
                    bottom.linkTo(songArtist.top, margin = 5.dp)
                    start.linkTo(parent.start, margin = 50.dp)
                })
        Text(
            text = mainViewModel.state.currentPlayingSong.subtitle,
            fontSize = 14.sp,
            color =color,
            modifier = Modifier
                .zIndex(10f)
                .constrainAs(songArtist) {
                    bottom.linkTo(seekBar.top, margin = 20.dp)
                    start.linkTo(parent.start, margin = 50.dp)
                })
        Text(
            text = mainViewModel.liveminute,
            fontSize = 11.sp,
            color = color,
            modifier = Modifier
                .zIndex(10f)
                .constrainAs(timeTotal) {
                    top.linkTo(seekBar.bottom, margin = 10.dp)
                    start.linkTo(seekBar.start, margin = 0.dp)
                })
        Text(
            text = mainViewModel.minute,
            fontSize = 11.sp,
            color = color,
            modifier = Modifier
                .zIndex(10f)
                .constrainAs(timeLapsed) {
                    top.linkTo(seekBar.bottom, margin = 10.dp)
                    end.linkTo(seekBar.end, margin = 0.dp)
                })
    }
}