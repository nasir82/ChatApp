package com.example.livechat.screens

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.livechat.CommonRow
import com.example.livechat.DestinationScreens
import com.example.livechat.LCViewModel
import com.example.livechat.comonProgessbar
import com.example.livechat.navigateTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Chatlist(navController: NavController,vm:LCViewModel) {
    val inProcess = vm.inProcessChat.value
    if(inProcess){
        comonProgessbar()
    }else{

        val chats = vm.chats.value
        val userData = vm.userData.value
        val showDialog = remember {
            mutableStateOf(false)
        }
        val onFabClick:()->Unit = {
            showDialog.value = true

        }
        val onDismis:()->Unit = {
            showDialog.value = false
        }
        val onAddChat:(String)->Unit = {
            vm.addOnChat(it)
            showDialog.value = false
        }

        Scaffold(
            floatingActionButton = {FAB(
                showDialog = showDialog.value,
                onFabClick = onFabClick,
                onDismiss = onDismis,
                onAddChat = onAddChat)
            },
            content = {padding ->
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    ) {

                    Text(text = "Chats", style = TextStyle(color = Color.Black,
                        fontSize = 36.sp,
                        fontWeight= FontWeight.Bold

                    ),
                        modifier = Modifier.padding(start = 10.dp,top = 5.dp)
                    )
                    if(chats.isEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {

                            Text(text = "No chat available")

                        }
                    }else{

                        LazyColumn(modifier = Modifier.weight(1f)){
                            items(chats){
                                chat->
                                val chatId = chat.chatId
                                val chatpartner = if(chat.user1.userId==userData?.userId) {
                                    chat.user2
                                }
                                else{
                                    chat.user1
                                }

                                CommonRow(imageUrl = chatpartner.imageUrl, name = chatpartner.name!!){
                                    navigateTo(navController,DestinationScreens.singleChatlist.createRoute(
                                       chatId!!
                                    ))
                                }
                                
                            }
                        }
                    }

                   BottomNevigation(slectedItem = BottomNevigationItem.CHATLIST, navController = navController)
                }
            }
        )

    }



}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FAB(
    showDialog: Boolean,
    onFabClick:()-> Unit,
    onDismiss:()->Unit,
    onAddChat:(String)->Unit
){

    val addChatNumber = remember {
        mutableStateOf(" ")
    }


   if(showDialog){
       Log.d("fab","show dialogue")

       AlertDialog(onDismissRequest = {

              onDismiss.invoke()
              addChatNumber.value=""

       }, confirmButton = {

           Button(onClick = {onAddChat(addChatNumber.value.trim())
           Log.d("clicked","you clicked on add on chat")}) {
                Text(text = "Add on chat")

           }

       },
           title = {
               Text(text = "Add chat")
           },
          text = {

              OutlinedTextField(value = addChatNumber.value, onValueChange = {
                  addChatNumber.value = it
              },
                  keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number,
                      imeAction = ImeAction.Done),
                  keyboardActions = KeyboardActions(
                      onSearch = {

                      }
                  ),
                  maxLines = 1
              )
          }
           )

       FloatingActionButton(onClick = { onFabClick.invoke()},
           containerColor = MaterialTheme.colorScheme.secondary,
           shape = CircleShape,
           modifier = Modifier.padding(bottom = 40.dp)
       ) {
           Icon(imageVector = Icons.Rounded.Add, contentDescription = null, tint = Color.Black)
       }
   }else{

       FloatingActionButton(onClick = { onFabClick.invoke()},
           containerColor = MaterialTheme.colorScheme.secondary,
           shape = CircleShape,
           modifier = Modifier.padding(bottom = 40.dp)
       ) {
           Icon(imageVector = Icons.Rounded.Add, contentDescription = null, tint = Color.Black)
       }
   }
}

