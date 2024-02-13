package com.example.livechat

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter

fun navigateTo(navController: NavController,route:String){
    navController.navigate(route){
        popUpTo(route)
        launchSingleTop = true
    }
}

@Composable
fun comonProgessbar(){

    Row(
        Modifier
            .alpha(0.5f)
            .background(Color.LightGray)
            .clickable(enabled = false) {}
            .fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center){
       CircularProgressIndicator()
    }
}
@Composable
fun commonImage(
    data:String?,
    modifier: Modifier=Modifier.wrapContentSize(),
    contentScale: ContentScale = ContentScale.Crop
){

    val paint = rememberAsyncImagePainter(model = data)
    Image(painter = paint, contentDescription =null ,modifier = modifier,contentScale=contentScale)
}

@Composable
fun checkedSignedIn(vm:LCViewModel,navController: NavController){

    var alreadySignedInd = remember {
        mutableStateOf(false)
    }
    val signIn = vm.singIn.value
    if(signIn and !alreadySignedInd.value){
        alreadySignedInd.value = true
        navController.navigate(DestinationScreens.chatlist.route){
            popUpTo(0)
        }
    }

}
@Composable
fun CommonDivider(){
    Divider(color = Color.DarkGray,
        thickness = 1.dp,
        modifier = Modifier
            .padding(top = 8.dp, bottom = 10.dp)
            .alpha(.3f))
}

@Composable
fun CommonRow(imageUrl:String?,name:String,onItemClicked:()->Unit){
    Row(modifier = Modifier
        .fillMaxWidth()
        .padding(horizontal = 20.dp)
        .height(75.dp)
        .clickable {

            onItemClicked.invoke()
        },
        verticalAlignment = Alignment.CenterVertically) {
        
        commonImage(data = imageUrl, modifier = Modifier
            .padding(5.dp)
            .clip(CircleShape)
            .size(50.dp)
            .background(Color.LightGray))
        Text(text = name?:"unnamed")
    }
    
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReplyBox(reply:String,onReplyChange:(String)->Unit,onSendReply:()->Unit){

    Column(modifier = Modifier
        .fillMaxWidth()
        .padding(5.dp)) {
        Row(modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp),
            horizontalArrangement = Arrangement.SpaceBetween) {
            TextField(value = reply, onValueChange = {
                 onReplyChange.invoke(it)
            }, maxLines = 3, modifier = Modifier
                .weight(
                    1f
                )

                .padding(end = 10.dp))
//            TextField(value = reply, onValueChange =onReplyChange, maxLines = 3)
            Button(onClick = { onSendReply.invoke() }, modifier = Modifier.clip(RoundedCornerShape(5.dp))) {
                Text(text = "Send")

            }

        }

    }
}