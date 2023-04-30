package com.example.globalmonitor.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.globalmonitor.presentation.main.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel, modifier: Modifier = Modifier) {
    val state = rememberLazyListState()
    LazyRow(state = state, modifier = modifier){
        item {
            HomeScreen()
        }
        item {

        }
        item {

        }
        item {

        }
    }
}

@Composable
fun HomeScreen(){

}


















