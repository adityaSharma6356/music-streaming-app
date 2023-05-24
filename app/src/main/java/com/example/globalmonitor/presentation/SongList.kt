package com.example.globalmonitor.presentation

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import com.example.globalmonitor.R
import com.example.globalmonitor.data.entities.SongModel
import com.example.globalmonitor.data.local.storeFavSong
import com.example.globalmonitor.data.local.storePlaylists
import com.example.globalmonitor.presentation.viewmodels.MainViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SongList(viewModel: MainViewModel, songlist: List<SongModel>, show:Boolean = true) {
    val context = LocalContext.current
    val lifecycleScope = rememberCoroutineScope()
    var tempSong by remember {
        mutableStateOf(SongModel())
    }
    val bottomSheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    BackHandler(bottomSheetState.isVisible) {
        lifecycleScope.launch {
            bottomSheetState.hide()
        }
    }
    ModalBottomSheetLayout(sheetElevation = 0.dp,
        sheetBackgroundColor = Color.Transparent,
        scrimColor = Color(0, 0, 0, 146),
        sheetContent = {
            Column(
                modifier = Modifier
                    .padding(10.dp, 0.dp, 10.dp, 130.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(MaterialTheme.colors.background)
                    .fillMaxWidth()
                    .height(250.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text("Adding : "+tempSong.title, maxLines = 1, style = MaterialTheme.typography.h6, color = MaterialTheme.colors.onPrimary, overflow = TextOverflow.Ellipsis, fontWeight = FontWeight.Bold, modifier = Modifier.padding(30.dp, 10.dp))
                Spacer(modifier = Modifier
                    .height(1.dp)
                    .background(MaterialTheme.colors.onPrimary)
                    .fillMaxWidth(0.9f)
                    .padding(bottom = 20.dp)
                    .align(Alignment.CenterHorizontally))
                viewModel.tempPlaylist.forEachIndexed { it, item ->
                    Row(modifier = Modifier.padding(15.dp)) {
                        var checked by remember {
                             mutableStateOf(viewModel.tempPlaylist[it].list.contains(tempSong))
                        }
                        checked = viewModel.tempPlaylist[it].list.contains(tempSong)
                        Text(text = item.name, fontSize = 18.sp, color = MaterialTheme.colors.onPrimary, modifier = Modifier.weight(1f), maxLines = 1, overflow = TextOverflow.Ellipsis)
                        Checkbox(colors = CheckboxDefaults.colors(uncheckedColor = MaterialTheme.colors.onPrimary, checkedColor = MaterialTheme.colors.onPrimary),
                            checked = checked,
                            onCheckedChange = { checkedOP ->
                                if (checkedOP) {
                                    if(!viewModel.tempPlaylist[it].list.contains(tempSong)){
                                        item.list.add(tempSong)
                                        viewModel.playLists = viewModel.tempPlaylist
                                        storePlaylists(context, viewModel)
                                        viewModel.tempPlaylist = viewModel.playLists
                                    }
                                    checked = true
                                } else {
                                    checked = false
                                    item.list.remove(tempSong)
                                    viewModel.playLists = viewModel.tempPlaylist
                                    storePlaylists(context, viewModel)
                                    viewModel.tempPlaylist = viewModel.playLists
                                }
                            },
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                                .size(20.dp)
                        )
                    }
                }
            }
        },
        sheetState = bottomSheetState,
        content = {
            LazyColumn(Modifier.fillMaxWidth(), contentPadding = PaddingValues(0.dp, 0.dp, 0.dp, 200.dp)) {
                itemsIndexed(songlist){it , song ->
                    var ddm by remember {
                        mutableStateOf(false)
                    }
                    Row(modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            lifecycleScope.launch {
                                viewModel.lazystate.scrollToItem(
                                    viewModel.currentSongIndex
                                )
                            }
                            viewModel.playOrToggleSong(song)
                        }
                        .height(80.dp)
                        .padding(10.dp, 5.dp)) {
                        DropdownMenu( offset = DpOffset((-100).dp, 0.dp),modifier = Modifier.shadow(elevation = 10.dp).background(MaterialTheme.colors.background),expanded = ddm, onDismissRequest = { ddm  = false }) {
                            DropdownMenuItem(onClick = {
                                tempSong = song
                                viewModel.tempPlaylist = viewModel.playLists
                                lifecycleScope.launch {
                                    bottomSheetState.show()
                                }
                                ddm = false
                            }) {
                                Text(text = "Add to playlist", color = MaterialTheme.colors.onPrimary, fontSize = 15.sp)
                            }
                            DropdownMenuItem(onClick = {
                                viewModel.shareLink(context, "http://snowflake-streamer.000webhostapp.com/$it")
                                ddm = false
                            }) {
                                Text(text = "Share song", color = MaterialTheme.colors.onPrimary, fontSize = 15.sp)
                            }
                        }
                        Image(
                            painter = rememberAsyncImagePainter(model = song.imageUri),
                            contentDescription = "song image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .padding(0.dp, 0.dp, 10.dp, 0.dp)
                                .size(60.dp)
                        )
                        Column(Modifier.weight(1f)) {
                            Text(
                                text = song.title,
                                fontSize = 17.sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colors.onPrimary,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(10.dp, 5.dp, 0.dp, 0.dp),
                            )
                            Text(
                                text = song.subtitle,
                                fontSize = 12.sp,
                                overflow = TextOverflow.Ellipsis,
                                color = MaterialTheme.colors.onPrimary,
                                modifier = Modifier.padding(10.dp, 5.dp, 0.dp, 0.dp)
                            )
                        }
                        if(show){
                            var icon by remember { mutableStateOf(R.drawable.not_liked_icon) }
                            icon = if(viewModel.likedSongs.contains(song)) R.drawable.likded_icon else R.drawable.not_liked_icon
                            Icon(painter = painterResource(id = icon),
                                contentDescription = "liked",
                                modifier = Modifier
                                    .padding(5.dp, 0.dp)
                                    .align(Alignment.CenterVertically)
                                    .size(25.dp)
                                    .clickable {
                                        if (viewModel.likedSongs.contains(song)) {
                                            icon = R.drawable.not_liked_icon
                                            viewModel.likedSongs.remove(song)
                                            storeFavSong(context, viewModel)
                                        } else {
                                            icon = R.drawable.likded_icon
                                            viewModel.likedSongs.add(song)
                                            storeFavSong(context, viewModel)
                                        }
                                    }
                                , tint = MaterialTheme.colors.onPrimary)
                            Icon(
                                painter = painterResource(id = R.drawable.menu_icon),
                                contentDescription = "menu",
                                tint = MaterialTheme.colors.onPrimary,
                                modifier = Modifier
                                    .padding(5.dp, 0.dp)
                                    .align(Alignment.CenterVertically)
                                    .size(25.dp)
                                    .clickable {
                                        ddm = true
                                    }
                            )
                        }
                    }
                }
            }
        }
    )
}