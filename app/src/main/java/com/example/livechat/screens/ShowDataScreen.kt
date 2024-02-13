package com.example.livechat.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.livechat.LCViewModel
import com.example.livechat.data.UserData

@Composable
fun Mainshow(nav:NavController,vm:LCViewModel){
    Column(modifier = Modifier.padding(15.dp)) {
        Text(text = "showing data")
        vm.Clct()
    }

}


@Composable
fun printData(datalist: SnapshotStateList<UserData>){
    LazyColumn{

        items(datalist){
            Column {
                Text(text = it.name!!)
                Text(text = if(it.number!! =="0173200") "match" else it.number!!)
                Text(text = it.userId!!)
            }
        }
    }
}