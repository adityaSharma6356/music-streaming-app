package com.example.globalmonitor

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.RequestManager
import com.example.globalmonitor.presentation.MainScreen
import com.example.globalmonitor.presentation.TopMusicControllerScreen
import com.example.globalmonitor.presentation.main.MainViewModel
import com.example.globalmonitor.ui.theme.GlobalMonitorTheme
import com.google.accompanist.systemuicontroller.rememberSystemUiController
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var glide: RequestManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val suc = rememberSystemUiController()
            val dt = isSystemInDarkTheme()
            SideEffect {
                if(dt){
                    suc.setStatusBarColor(Color.White)
                    suc.statusBarDarkContentEnabled = true
                } else {
                    suc.setStatusBarColor(Color.Black)
                    suc.statusBarDarkContentEnabled = false
                }
            }

            val viewModel = viewModel<MainViewModel>()
            GlobalMonitorTheme {
                ConstraintLayout(Modifier.fillMaxWidth()) {
                    val (topMusicController, songsList) = createRefs()
                    if(viewModel.state.songsList.isNotEmpty()) {
                        TopMusicControllerScreen(
                            viewModel = viewModel,
                            Modifier.constrainAs(topMusicController) {
                                top.linkTo(parent.top, )
                                start.linkTo(parent.start, )
                                end.linkTo(parent.end, )
                            })
                        MainScreen(viewModel = viewModel, Modifier.constrainAs(songsList){
                            top.linkTo(parent.top, margin = 80.dp)
                            end.linkTo(parent.end)
                            start.linkTo(parent.start)
                        })
                    }
                }
            }
        }
    }
}