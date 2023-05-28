package com.example.globalmonitor.presentation

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.pager.PagerState
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
fun BottomNav(viewModel:MainViewModel, lazystate:LazyListState) {
    val csc = rememberCoroutineScope()
    val interactionSource = remember { MutableInteractionSource() }
    var prevPage by remember {
        mutableStateOf(0)
    }

    Column(modifier = Modifier
        .clickable(
            interactionSource = interactionSource,
            indication = null
        ) {
            csc.launch {
                viewModel.iconClick(0, prevPage)
                prevPage = 0
                lazystate.animateScrollToItem(0)
            }
        }) {
        Icon(modifier = Modifier
            .height(40.dp)
            .width(70.dp)
            .padding(0.dp, 5.dp), painter = painterResource(id = R.drawable.home_icon), contentDescription = "home", tint = viewModel.navColorsList[0], )
        Text(text = "Home", fontSize = 8.sp, color = viewModel.navColorsList[0], textAlign = TextAlign.Center, modifier = Modifier.width(70.dp))
    }
    Column(modifier = Modifier
        .clickable(
            interactionSource = interactionSource,
            indication = null
        ) {
            csc.launch {
                viewModel.iconClick(1, prevPage)
                prevPage = 1
                lazystate.animateScrollToItem(1)
            }
        }) {
        Icon(modifier = Modifier
            .height(40.dp)
            .width(70.dp)
            .padding(0.dp, 5.dp), painter = painterResource(id = R.drawable.storage_icon), contentDescription = "playlists", tint = viewModel.navColorsList[1])
        Text(text = "Playlists", fontSize = 8.sp, color = viewModel.navColorsList[1], textAlign = TextAlign.Center, modifier = Modifier.width(70.dp))
    }
    Column(modifier = Modifier
        .clickable(
            interactionSource = interactionSource,
            indication = null
        ) {
            csc.launch {
                viewModel.iconClick(2, prevPage)
                prevPage = 2
                lazystate.animateScrollToItem(2)
            }
        }) {
        Icon(modifier = Modifier
            .height(40.dp)
            .width(70.dp)
            .padding(0.dp, 5.dp), painter = painterResource(id = R.drawable.search_icon), contentDescription = "search", tint = viewModel.navColorsList[2])
        Text(text = "Search", fontSize = 8.sp, color = viewModel.navColorsList[2], textAlign = TextAlign.Center, modifier = Modifier.width(70.dp))
    }
}