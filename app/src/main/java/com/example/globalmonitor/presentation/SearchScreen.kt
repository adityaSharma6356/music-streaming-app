package com.example.globalmonitor.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.globalmonitor.R
import com.example.globalmonitor.presentation.viewmodels.MainViewModel
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(title: String, viewModel: MainViewModel){
    val config = LocalConfiguration.current
    val scope = rememberCoroutineScope()
    Surface(color = Color.Transparent,modifier = Modifier
        .padding(0.dp, 50.dp)
        .zIndex(0f)
        .width(config.screenWidthDp.dp)
    ) {
        Column {
            Box(contentAlignment = Alignment.CenterStart,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .background(Color.Black)
            ) {
                Text(text = title, fontSize = 20.sp, color = Color.White, modifier = Modifier.padding(15.dp, 0.dp))
            }
            var textfieldvalue by remember { mutableStateOf("") }
            TextField( trailingIcon = {
                                     Icon(painter = painterResource(id = R.drawable.search_icon), contentDescription = "search", tint = MaterialTheme.colors.onPrimary)
            },
                modifier = Modifier.fillMaxWidth(),
                colors = TextFieldDefaults.textFieldColors(placeholderColor = Color.Gray,textColor = MaterialTheme.colors.onPrimary, backgroundColor = MaterialTheme.colors.background, cursorColor = MaterialTheme.colors.onPrimary  ),
                maxLines = 1,
                value = textfieldvalue, onValueChange = { search ->
                    textfieldvalue = search
                    if(textfieldvalue!=""){
                        scope.launch {
                            viewModel.SearchList = viewModel.state.songsList.filter {
                                it.title.lowercase().contains(textfieldvalue.lowercase()) || it.subtitle.lowercase().contains(textfieldvalue.lowercase())
                            }.toMutableStateList()
                        }
                    } else {
                        viewModel.SearchList.clear()
                    }
            })
            SongList(viewModel = viewModel, songlist = viewModel.SearchList)
        }
    }
}