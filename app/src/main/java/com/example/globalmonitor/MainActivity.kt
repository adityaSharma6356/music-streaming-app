package com.example.globalmonitor

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.RequestManager
import com.example.globalmonitor.presentation.MainScreen
import com.example.globalmonitor.presentation.SongInfoScreen
import com.example.globalmonitor.presentation.SongViewModel
import com.example.globalmonitor.presentation.TopMusicControllerScreen
import com.example.globalmonitor.presentation.main.MainViewModel
import com.example.globalmonitor.ui.theme.GlobalMonitorTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                }
                systemUiController.statusBarDarkContentEnabled = true
            }
            val viewModel = viewModel<MainViewModel>()
            val songViewModel = viewModel<SongViewModel>()
            viewModel.startPlayBackLiveData(this)
            GlobalMonitorTheme {
                val configuration = LocalConfiguration.current
                if(viewModel.expandedSongScreen){
                    viewModel.heigt = (-configuration.screenHeightDp-85).dp
                } else {
                    viewModel.heigt = 0.dp
                }
                val ht by animateDpAsState(targetValue = viewModel.heigt, animationSpec = TweenSpec(300))

                ConstraintLayout(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0, 0, 0, 140))) {
                    val (topMusicController, errorSnackBar, songInfoScreen, bottomBlur, tabScreen) = createRefs()
                    Surface(color = Color.Transparent,
                        modifier = Modifier
                            .background(
                                brush = Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent, Color(
                                            0,
                                            0,
                                            0,
                                            139
                                        ), Color(0, 0, 0, 201), Color.Black
                                    )
                                )
                            )
                            .fillMaxWidth()
                            .height(100.dp)
                            .constrainAs(bottomBlur) {
                                bottom.linkTo(parent.bottom, margin = 40.dp)
                            }) {
                    }
                    TopMusicControllerScreen(
                        viewModel = viewModel,
                        Modifier.constrainAs(topMusicController) {
                            bottom.linkTo(bottomBlur.top, margin = (-30).dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                        this@MainActivity,
                    songViewModel)
                    Row(horizontalArrangement = Arrangement.SpaceAround,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(Color.Transparent)
                            .fillMaxWidth()
                            .height(50.dp)
                            .constrainAs(tabScreen) {
                                top.linkTo(topMusicController.bottom)
                            }) {
                        Column {
                            Icon(modifier = Modifier
                                .size(40.dp)
                                .clickable { viewModel.iconClick(1) }
                                .padding(0.dp, 5.dp), painter = painterResource(id = R.drawable.home_icon), contentDescription = "home", tint = viewModel.colorHome, )
                            Text(text = "Home", fontSize = 8.sp, color = viewModel.colorHome, textAlign = TextAlign.Center, modifier = Modifier.width(40.dp))
                        }
                        Column() {
                            Icon(modifier = Modifier
                                .size(40.dp)
                                .clickable { viewModel.iconClick(2) }
                                .padding(0.dp, 5.dp), painter = painterResource(id = R.drawable.search_icon), contentDescription = "search", tint = viewModel.colorSearch)
                            Text(text = "Search", fontSize = 8.sp, color = viewModel.colorSearch, textAlign = TextAlign.Center, modifier = Modifier.width(40.dp))
                        }
                        Column() {
                            Icon(modifier = Modifier
                                .size(40.dp)
                                .clickable { viewModel.iconClick(3) }
                                .padding(0.dp, 5.dp), painter = painterResource(id = R.drawable.playlist_icon), contentDescription = "playlist", tint = viewModel.colorPlay)
                            Text(text = "Library", fontSize = 8.sp, color = viewModel.colorPlay, textAlign = TextAlign.Center, modifier = Modifier.width(40.dp))
                        }
                        Column() {
                            Icon(modifier = Modifier
                                .size(40.dp)
                                .clickable { viewModel.iconClick(4) }
                                .padding(0.dp, 5.dp), painter = painterResource(id = R.drawable.profile_icon), contentDescription = "profile", tint = viewModel.colorProf)
                            Text(text = "Profile", fontSize = 8.sp, color = viewModel.colorProf, textAlign = TextAlign.Center, modifier = Modifier.width(40.dp))
                        }
                    }
                    SongInfoScreen(mainViewModel = viewModel, modifier = Modifier.constrainAs(songInfoScreen){
                        top.linkTo(parent.bottom, margin = ht)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }, songViewModel)
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