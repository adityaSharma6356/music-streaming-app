package com.example.globalmonitor

import android.app.Activity
import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.*
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.core.view.WindowCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.globalmonitor.data.local.loadFavouriteSongs
import com.example.globalmonitor.data.local.loadPlaylists
import com.example.globalmonitor.presentation.BottomNav
import com.example.globalmonitor.presentation.SongInfoScreen
import com.example.globalmonitor.presentation.TabsScreen
import com.example.globalmonitor.presentation.TopMusicControllerScreen
import com.example.globalmonitor.presentation.viewmodels.MainViewModel
import com.example.globalmonitor.ui.theme.GlobalMonitorTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @RequiresApi(Build.VERSION_CODES.R)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContent {
            val systemUiController = rememberSystemUiController()
            val darkTheme = isSystemInDarkTheme()
            SideEffect {
                val window = (this as Activity).window
                window.statusBarColor = Color.Transparent.toArgb()
                window.navigationBarColor = Color.Transparent.toArgb()
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    window.isNavigationBarContrastEnforced = false
                }
                systemUiController.statusBarDarkContentEnabled = !darkTheme
            }
            val viewModel = viewModel<MainViewModel>()
            viewModel.startPlayBackLiveData(this)
            loadFavouriteSongs(viewModel, this)
            loadPlaylists(viewModel, this)
            viewModel.theseSongs = viewModel.likedSongs
            GlobalMonitorTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    val lazystate = rememberLazyListState()
                    val uri: Uri? = intent.data
                    if(uri != null){
                        val parameters: List<String> = uri.pathSegments
                        val param = parameters[parameters.size - 1]
                        viewModel.deepLinkMedia = param.toInt()
                    }
                    TabsScreen(mainViewModel = viewModel, state = lazystate)
                    ConstraintLayout(
                        Modifier
                            .fillMaxSize()
                            .background(Color(0, 0, 0, 0))) {
                        val (topMusicController, songInfoScreen, bottomBlur, tabScreen) = createRefs()
                        val ht by animateDpAsState(targetValue = viewModel.heigt, animationSpec = TweenSpec(300), finishedListener = { viewModel.openScreenNow = !viewModel.openScreenNow})
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
                                            Color(0, 0, 0, 0),
                                            Color(0, 0, 0, 201),
                                            Color(0, 0, 0, 235),
                                            Color.Black
                                        )
                                    )
                                )
                                .fillMaxWidth()
                                .height(200.dp)
                                .constrainAs(bottomBlur) {
                                    bottom.linkTo(parent.bottom)
                                }) {
                        }
                        TopMusicControllerScreen(
                            viewModel = viewModel,
                            Modifier
                                .zIndex(9f)
                                .constrainAs(topMusicController) {
                                    bottom.linkTo(bottomBlur.top, margin = (-90).dp)
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
                            BottomNav(viewModel = viewModel, lazystate = lazystate)
                        }
                    }
                }
            }
        }
    }
}