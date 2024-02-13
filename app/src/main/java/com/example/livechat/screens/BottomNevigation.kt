package com.example.livechat.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.livechat.DestinationScreens
import com.example.livechat.R
import com.example.livechat.navigateTo


enum class BottomNevigationItem(val icon:Int,val navDestination:DestinationScreens){
    CHATLIST(R.drawable.chat2,DestinationScreens.chatlist),
    STATUSLIST(R.drawable.status,DestinationScreens.statuslist),
    PROFILE(R.drawable.profile,DestinationScreens.profile)
}

@Composable
fun BottomNevigation(slectedItem: BottomNevigationItem,navController: NavController) {
    Row(modifier = Modifier
        .fillMaxWidth()
        .height(50.dp)
        .background(Color.LightGray)
        .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.Bottom) {

        for(item in BottomNevigationItem.values()){

            Image(painter = painterResource(id = item.icon), contentDescription = null ,Modifier.clickable {
                navigateTo(navController,item.navDestination.route)
            }.size(40.dp))
        }

    }

}