package com.example.globalmonitor.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.globalmonitor.R
import com.example.globalmonitor.presentation.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomNav(viewModel:MainViewModel, lazystate:PagerState) {
    val csc = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    Column(modifier = Modifier
        .clickable(
            interactionSource = interactionSource,
            indication = null
        ) {
//            viewModel.iconClick(1)
            csc.launch {
                lazystate.animateScrollToPage(0)
            }
        }) {
        Icon(modifier = Modifier
            .height(40.dp)
            .width(70.dp)
            .padding(0.dp, 5.dp), painter = painterResource(id = R.drawable.home_icon), contentDescription = "home", tint = viewModel.colorHome, )
        Text(text = "Home", fontSize = 8.sp, color = viewModel.colorHome, textAlign = TextAlign.Center, modifier = Modifier.width(70.dp))
    }
    Column(modifier = Modifier
        .clickable(
            interactionSource = interactionSource,
            indication = null
        ) {
//            viewModel.iconClick(2)
            csc.launch {
                lazystate.animateScrollToPage(1)
            }
        }) {
        Icon(modifier = Modifier
            .height(40.dp)
            .width(70.dp)
            .padding(0.dp, 5.dp), painter = painterResource(id = R.drawable.storage_icon), contentDescription = "playlists", tint = viewModel.colorPlay)
        Text(text = "Playlists", fontSize = 8.sp, color = viewModel.colorPlay, textAlign = TextAlign.Center, modifier = Modifier.width(70.dp))
    }
    Column(modifier = Modifier
        .clickable(
            interactionSource = interactionSource,
            indication = null
        ) {
//            viewModel.iconClick(3)
            csc.launch {
                lazystate.animateScrollToPage(2)
            }
        }) {
        Icon(modifier = Modifier
            .height(40.dp)
            .width(70.dp)
            .padding(0.dp, 5.dp), painter = painterResource(id = R.drawable.profile_icon), contentDescription = "profile", tint = viewModel.colorProf)
        Text(text = "Profile", fontSize = 8.sp, color = viewModel.colorProf, textAlign = TextAlign.Center, modifier = Modifier.width(70.dp))
    }
}