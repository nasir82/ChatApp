package com.example.livechat.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.example.livechat.LCViewModel

@Composable
fun StatusScreen(navController: NavController,vm:LCViewModel) {
    Column {
        Text(text = "This is statusList")
        BottomNevigation(slectedItem = BottomNevigationItem.STATUSLIST, navController =navController ) 
    }
    
}