package com.example.globalmonitor

import android.app.Activity
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.globalmonitor.presentation.HomeScreen
import com.example.globalmonitor.presentation.MainScreen
import com.example.globalmonitor.presentation.SongInfoScreen
import com.example.globalmonitor.presentation.TopMusicControllerScreen
import com.example.globalmonitor.presentation.main.MainViewModel
import com.example.globalmonitor.ui.theme.GlobalMonitorTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContent {
            val systemUiController = rememberSystemUiController()
            SideEffect {
                val window = (this as Activity).window
                window.statusBarColor = Color.Transparent.toArgb()
                window.navigationBarColor = Color.Transparent.toArgb()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                }
                systemUiController.statusBarDarkContentEnabled = true
            }
            val viewModel = viewModel<MainViewModel>()
            viewModel.startPlayBackLiveData(this)
            GlobalMonitorTheme {
                val lazystate = rememberLazyListState()
//                MainScreen(viewModel = viewModel, state = lazystate)
                LazyRow(state = viewModel.lazyMenuState, modifier = Modifier.fillMaxWidth(), userScrollEnabled = false){
                    item {
                        HomeScreen(viewModel, viewModel.state.songsList)
                    }
                    item {
                        HomeScreen(viewModel, emptyList())
                    }
                    item {
                        HomeScreen(viewModel, emptyList())
                    }
                    item {
                        HomeScreen(viewModel, emptyList())
                    }
                }
                ConstraintLayout(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0, 0, 0, 0))) {
                    val (topMusicController, errorSnackBar, songInfoScreen, bottomBlur, tabScreen, songsList, cnt) = createRefs()
                    val ht by animateDpAsState(targetValue = viewModel.heigt, animationSpec = TweenSpec(400))
                    SongInfoScreen(mainViewModel = viewModel, modifier = Modifier
                        .zIndex(10f)
                        .constrainAs(songInfoScreen) {
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            top.linkTo(parent.top, margin = ht)
                        })
                    Surface(color = Color.Transparent,
                        modifier = Modifier
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color(0, 0, 0, 180),
                                        Color(0, 0, 0, 235),
                                        Color.Black,
                                        Color.Black
                                    )
                                )
                            )
                            .fillMaxWidth()
                            .height(150.dp)
                            .constrainAs(bottomBlur) {
                                bottom.linkTo(parent.bottom,)
                            }) {
                    }
                    TopMusicControllerScreen(
                        viewModel = viewModel,
                        Modifier
                            .zIndex(9f)
                            .constrainAs(topMusicController) {
                                bottom.linkTo(bottomBlur.top, margin = (-40).dp)
                                start.linkTo(parent.start)
                                end.linkTo(parent.end)
                            },
                        this@MainActivity,)
                    Row(horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color.Transparent)
                            .fillMaxWidth()
                            .height(55.dp)
                            .constrainAs(tabScreen) {
                                top.linkTo(topMusicController.bottom)
                            }) {
                        val interactionSource = remember { MutableInteractionSource() }
                        Column {
                            Icon(modifier = Modifier
                                .height(40.dp)
                                .width(70.dp)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) { viewModel.iconClick(1)
                                    lifecycleScope.launch {
//                                        viewModel.lazyMenuState.animateScrollToItem(0)
                                    }}
                                .padding(0.dp, 5.dp), painter = painterResource(id = R.drawable.home_icon), contentDescription = "home", tint = viewModel.colorHome, )
                            Text(text = "Home", fontSize = 8.sp, color = viewModel.colorHome, textAlign = TextAlign.Center, modifier = Modifier.width(70.dp))
                        }
                        Column() {
                            Icon(modifier = Modifier
                                .height(40.dp)
                                .width(70.dp)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) { viewModel.iconClick(2)
                                    lifecycleScope.launch {
//                                        viewModel.lazyMenuState.animateScrollToItem(1)
                                    }}
                                .padding(0.dp, 5.dp), painter = painterResource(id = R.drawable.search_icon), contentDescription = "search", tint = viewModel.colorSearch)
                            Text(text = "Search", fontSize = 8.sp, color = viewModel.colorSearch, textAlign = TextAlign.Center, modifier = Modifier.width(70.dp))
                        }
                        Column() {
                            Icon(modifier = Modifier
                                .height(40.dp)
                                .width(70.dp)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) {
                                    viewModel.iconClick(3)
                                    lifecycleScope.launch {
//                                        viewModel.lazyMenuState.animateScrollToItem(2)
                                    }
                                }
                                .padding(0.dp, 5.dp), painter = painterResource(id = R.drawable.storage_icon), contentDescription = "storage", tint = viewModel.colorPlay)
                            Text(text = "Storage", fontSize = 8.sp, color = viewModel.colorPlay, textAlign = TextAlign.Center, modifier = Modifier.width(70.dp))
                        }
                        Column() {
                            Icon(modifier = Modifier
                                .height(40.dp)
                                .width(70.dp)
                                .clickable(
                                    interactionSource = interactionSource,
                                    indication = null
                                ) { viewModel.iconClick(4)
                                    lifecycleScope.launch {
//                                        viewModel.lazyMenuState.animateScrollToItem(3)
                                    }}
                                .padding(0.dp, 5.dp), painter = painterResource(id = R.drawable.profile_icon), contentDescription = "profile", tint = viewModel.colorProf)
                            Text(text = "Profile", fontSize = 8.sp, color = viewModel.colorProf, textAlign = TextAlign.Center, modifier = Modifier.width(70.dp))
                        }
                    }
                    if(viewModel.someKindOfError){
                        Snackbar(shape = RoundedCornerShape(10.dp),
                            elevation = 10.dp,
                            backgroundColor = MaterialTheme.colors.background,
                            modifier = Modifier
                                .height(60.dp)
                                .padding(5.dp, 0.dp)
                                .constrainAs(errorSnackBar) {
                                    bottom.linkTo(topMusicController.top, margin = 10.dp)
                                    end.linkTo(parent.end, margin = 10.dp)
                                    start.linkTo(parent.start, margin = 10.dp)
                                }) {
                            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center){
                                Text(text = viewModel.errorStatement, fontSize = 15.sp, color = MaterialTheme.colors.onBackground)
                            }
                        }
                    }
                }
            }
        }
    }
}