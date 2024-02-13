package com.example.livechat.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.livechat.CommonDivider
import com.example.livechat.DestinationScreens
import com.example.livechat.LCViewModel
import com.example.livechat.commonImage
import com.example.livechat.comonProgessbar
import com.example.livechat.navigateTo

@Composable
fun Profile(navController: NavController, vm: LCViewModel) {
    val inProcess = vm.inProcess.value

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        if (inProcess) {
            comonProgessbar()
        } else {
            myprofile(navController, vm, onBack = { navigateTo(navController, DestinationScreens.chatlist.route) }, onSave = {})
        }
        BottomNevigation(slectedItem = BottomNevigationItem.PROFILE, navController = navController)
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun myprofile(
    navController: NavController,
    vm: LCViewModel,
    onBack: () -> Unit,
    onSave: () -> Unit
) {
    var email by remember {
        mutableStateOf(TextFieldValue())
    }
    var password by remember {
        mutableStateOf(TextFieldValue())
    }
    Column(modifier = Modifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {

        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Back", modifier = Modifier
                .padding(start = 5.dp)
                .clickable {
                    onBack.invoke()
                })
            Text(text = "Save", modifier = Modifier
                .padding(end = 5.dp)
                .clickable {
                    onSave.invoke()
                })

        }
        CommonDivider()
        profileImage(vm.userData.value?.imageUrl, vm, navController)

        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Email", modifier = Modifier.padding(end = 5.dp))
            TextField(
                value = email, onValueChange = {

                    email = it

                }, modifier = Modifier.wrapContentHeight(),

                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent
                )
            )

        }
        Row(
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "Password", modifier = Modifier.padding(end = 5.dp))
            TextField(
                value = password, onValueChange = {
                    password = it
                }, modifier = Modifier.wrapContentHeight(),
                colors = TextFieldDefaults.textFieldColors(
                    containerColor = Color.Transparent
                )
            )

        }

    }


}


@Composable
fun profileImage(imagaUrl: String?, vm: LCViewModel, navController: NavController) {

    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri ->
            uri?.let {

                vm.uploadProfileImage(uri, navController = navController, vm = vm)
            }

        }
    Box(
        modifier = Modifier
            .padding(8.dp)
            .height(intrinsicSize = IntrinsicSize.Min)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
                .clickable {
                    launcher.launch("image/*")
                },
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Card(
                shape = CircleShape,
                modifier = Modifier
                    .padding(8.dp)
                    .size(100.dp)
            ) {

                commonImage(data = imagaUrl)


            }

            Text(text = "Change Profile Picture")

        }

        if (vm.inProcess.value) {
            comonProgessbar()
        }

    }

}
