package com.example.livechat.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.livechat.CommonDivider
import com.example.livechat.LCViewModel
import com.example.livechat.ReplyBox
import com.example.livechat.commonImage

@Composable
fun SingleChatlist(nav:NavController,vm:LCViewModel,id:String) {
    var reply by rememberSaveable {
        mutableStateOf("")
    }
    val chat = vm.chats.value.first {
        it.chatId==id
    }

    LaunchedEffect(key1 = Unit){
        vm.populateMessage(id)
    }
    BackHandler {
        vm.depopulateMessage()
    }
    val name = if(chat.user1.userId==vm.userData.value?.userId) chat.user2.name else chat.user2.name
    val imag = if(chat.user1.userId==vm.userData.value?.userId) chat.user2.imageUrl else chat.user2.imageUrl
    val onSendReply = {
        vm.onSendReply(chatId = id, message = reply)
        reply = ""
    }
    Column(modifier = Modifier.padding(horizontal = 16.dp)) {
        //Text(text = name?:"Unknown", fontSize = 28.sp, modifier = Modifier.padding(12.dp))
        Header(imag = imag,name){
            nav.popBackStack()
            vm.depopulateMessage()
        }
        CommonDivider()
        val datalist = vm.messages.value
        Box(modifier = Modifier
            .fillMaxWidth()
            .weight(1f), contentAlignment = if(datalist.isEmpty()) Alignment.Center else Alignment.TopStart) {
            if(datalist.isEmpty()){
                Text(text = "You have no chats.Start chatting")
            }else{
                
                LazyColumn(){
                    items(datalist){
                        Text(text = it.message!!, modifier = Modifier.padding(
                           
                           start = 20.dp
                        )
                        
                    }
                }
            }
            
            
        }
       
        ReplyBox(reply = reply, onReplyChange = {
            reply=it;
        }) {
           onSendReply.invoke()
        }


    }

}


@Composable
fun Header(imag:String?="",name:String?="",onBack:()->Unit){

    Row(modifier = Modifier
        .padding(horizontal = 10.dp)
        .height(55.dp),
        verticalAlignment = Alignment.CenterVertically){
        Icon(imageVector = Icons.Default.ArrowBack, contentDescription = "", modifier = Modifier
            .padding(end = 10.dp)
            .clickable {
                onBack.invoke()
            })
        commonImage(data = imag, modifier = Modifier
            .clip(CircleShape)

            .size(50.dp))
        Text(text = name ?: "Unknown", style = TextStyle(
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color.Blue

        ),
            modifier = Modifier
                .padding(horizontal = 10.dp)
                .weight(1f))
    }

}