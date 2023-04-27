package com.example.globalmonitor

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.lifecycle.viewmodel.compose.viewModel
import com.bumptech.glide.RequestManager
import com.example.globalmonitor.presentation.MainScreen
import com.example.globalmonitor.presentation.SongInfoScreen
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

    @OptIn(ExperimentalMaterialApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            val suc = rememberSystemUiController()
            val dt = isSystemInDarkTheme()
            SideEffect {
                if(!dt){
                    suc.setStatusBarColor(Color.White)
                    suc.statusBarDarkContentEnabled = true
                } else {
                    suc.setStatusBarColor(Color.Black)
                    suc.statusBarDarkContentEnabled = false
                }
            }
            val viewModel = viewModel<MainViewModel>()
            viewModel.startPlayBackLiveData(this)
            GlobalMonitorTheme {

                ConstraintLayout(Modifier.fillMaxSize()) {
                    val (topMusicController, errorSnackBar, songInfoScreen) = createRefs()
                    TopMusicControllerScreen(
                        viewModel = viewModel,
                        Modifier.constrainAs(topMusicController) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                        this@MainActivity)
                    SongInfoScreen(mainViewModel = viewModel, modifier = Modifier.constrainAs(songInfoScreen){
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    })
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