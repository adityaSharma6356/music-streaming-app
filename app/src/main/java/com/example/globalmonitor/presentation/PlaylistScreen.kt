package com.example.globalmonitor.presentation

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.DropdownMenu
import androidx.compose.material.DropdownMenuItem
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
import androidx.compose.runtime.setValue
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import com.example.globalmonitor.R
import com.example.globalmonitor.data.entities.PlayListItem
import com.example.globalmonitor.data.local.storePlaylists
import com.example.globalmonitor.presentation.viewmodels.MainViewModel


@Composable
fun PlaylistsScreen(viewModel: MainViewModel, title: String ) {
    val configuration = LocalConfiguration.current
    var openDialogue by remember { mutableStateOf(false) }
    var openDialogueRename by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var opened by remember {
        mutableStateOf(false)
    }
    var setRenameIndex by remember {
        mutableStateOf(0)
    }
    BackHandler(opened) {
        opened = false
        viewModel.expandedSongScreen = false
        viewModel.changeHeight(configuration)
    }

    val config = LocalConfiguration.current


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
                if(!opened){
                    Icon(painter = painterResource(id = R.drawable.add_icon),
                        tint = Color.White, contentDescription = "add",
                        modifier = Modifier
                            .padding(0.dp, 0.dp, 10.dp, 0.dp)
                            .size(30.dp)
                            .clickable {
                                openDialogue = true
                            }
                            .align(alignment = Alignment.CenterEnd)
                    )
                }
            }
            if(!opened){
                Column {
                    Card(shape = RoundedCornerShape(20.dp),
                        backgroundColor = Color.Black,
                        modifier = Modifier
                            .width(205.dp)
                            .clickable {
                                viewModel.theseSongs = viewModel.likedSongs
                                opened = true
                            }
                            .height(220.dp)
                            .padding(20.dp)) {
                        Row(horizontalArrangement = Arrangement.SpaceBetween ) {
                            Text(text = "Liked\nSongs", fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.White, modifier = Modifier.padding(20.dp, 20.dp))
                            Icon(painter = painterResource(id = R.drawable.likded_icon), contentDescription = "liked", modifier = Modifier
                                .padding(20.dp, 26.dp)
                                .size(30.dp), tint = Color.White)
                        }
                    }
                    LazyVerticalGrid(modifier = Modifier.weight(1f),columns = GridCells.Fixed(2), contentPadding = PaddingValues(0.dp, 0.dp,0.dp, 120.dp)){
                        viewModel.tempPlaylist = viewModel.playLists.toMutableStateList()
                        itemsIndexed(viewModel.tempPlaylist){ it, _ ->
                            var dropDownMenu by remember { mutableStateOf(false) }
                            if(viewModel.tempPlaylist[it].name!="" && viewModel.tempPlaylist.isNotEmpty()){
                                Card(shape = RoundedCornerShape(20.dp),
                                    backgroundColor = Color.Black,
                                    modifier = Modifier
                                        .width(220.dp)
                                        .clickable {
                                            viewModel.theseSongs = viewModel.tempPlaylist[it].list
                                            opened = true
                                        }
                                        .height(220.dp)
                                        .padding(20.dp)) {
                                    Row(horizontalArrangement = Arrangement.SpaceBetween ) {
                                        Text(text = viewModel.tempPlaylist[it].name, fontWeight = FontWeight.ExtraBold, fontSize = 20.sp, color = Color.White, modifier = Modifier.padding(10.dp,20.dp, 0.dp, 20.dp).weight(1f))
                                        Icon(painter = painterResource(id = R.drawable.menu_icon), contentDescription = "liked", modifier = Modifier
                                            .padding(0.dp, 20.dp, 10.dp, 0.dp)
                                            .size(30.dp)
                                            .clickable {
                                                dropDownMenu = true
                                            }, tint = Color.White)
                                    }
                                    DropdownMenu(modifier = Modifier.background(MaterialTheme.colors.background),expanded = dropDownMenu, onDismissRequest = { dropDownMenu = false }, offset = DpOffset(0.dp , (-100).dp,
                                    )) {
                                        DropdownMenuItem(onClick = {
                                            setRenameIndex = it
                                            openDialogueRename = true
                                            dropDownMenu = false
                                        }) {
                                            Text(
                                                text = "Rename",
                                                fontSize = 15.sp,
                                                color = MaterialTheme.colors.onPrimary
                                            )
                                        }
                                        DropdownMenuItem(onClick = {
                                            viewModel.playLists.removeAt(it)
                                            viewModel.tempPlaylist.removeAt(it)
                                            storePlaylists(context, viewModel)
                                            dropDownMenu = false
                                                                   } ) {
                                            Text(
                                                text = "Delete",
                                                fontSize = 15.sp,
                                                color = MaterialTheme.colors.onPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                if(openDialogue){
                    var plName by remember {
                        mutableStateOf("")
                    }
                    AlertDialog(
                        onDismissRequest = {
                            openDialogue = false
                        },
                        text = {
                            Column { TextField(colors = TextFieldDefaults.textFieldColors(focusedLabelColor = Color.White, focusedIndicatorColor = Color.White, cursorColor = Color.White),
                                    label = { Text(text = "New Playlist Name") }, value = plName, onValueChange = { plName = it }) } },
                        buttons = { Column(
                                modifier = Modifier.padding(all = 8.dp),
                                verticalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Button(colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = { openDialogue = false }
                                ) {
                                    Text("Dismiss", color = Color.White)
                                }
                                Button(colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                                    modifier = Modifier.fillMaxWidth(),
                                    onClick = {
                                        if(plName==""){
                                            Toast.makeText(context, "Name cannot be empty!!", Toast.LENGTH_SHORT).show()
                                        } else {
                                            viewModel.playLists.add(PlayListItem(name = plName))
                                            storePlaylists(context = context, viewModel)
                                            openDialogue = false
                                        }
                                    }
                                ) {
                                    Text("SAVE", color = Color.White)
                                }
                            }
                        }
                    )
                } else if (openDialogueRename) {
                    var plName by remember {
                        mutableStateOf("")
                    }
                    AlertDialog(
                        onDismissRequest = {
                            openDialogueRename = false
                        },
                        text = {
                            Column { TextField(colors = TextFieldDefaults.textFieldColors(focusedLabelColor = Color.White, focusedIndicatorColor = Color.White, cursorColor = Color.White),
                                label = { Text(text = "Set new name for "+viewModel.playLists[setRenameIndex].name) }, value = plName, onValueChange = { plName = it }) } },
                        buttons = { Column(
                            modifier = Modifier.padding(all = 8.dp),
                            verticalArrangement = Arrangement.SpaceEvenly
                        ) {
                            Button(colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                                modifier = Modifier.fillMaxWidth(),
                                onClick = { openDialogueRename = false }
                            ) {
                                Text("Dismiss", color = Color.White)
                            }
                            Button(colors = ButtonDefaults.buttonColors(backgroundColor = Color.Black),
                                modifier = Modifier.fillMaxWidth(),
                                onClick = {
                                    if(plName==""){
                                        Toast.makeText(context, "Name cannot be empty!!", Toast.LENGTH_SHORT).show()
                                    } else {
                                        viewModel.playLists[setRenameIndex].name = plName
                                        storePlaylists(context = context, viewModel)
                                        openDialogueRename = false
                                    }
                                }
                            ) {
                                Text("SAVE", color = Color.White)
                            }
                        }
                        }
                    )
                }
            }
            else {
                if(viewModel.theseSongs.size<1){
                    Text(
                        text = "Empty Playlist",
                        fontSize = 20.sp,
                        color = Color.Black,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
                SongList(viewModel = viewModel, songlist = viewModel.theseSongs )
            }
        }
    }
}