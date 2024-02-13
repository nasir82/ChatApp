package com.example.livechat.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.modifier.modifierLocalConsumer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.livechat.DestinationScreens
import com.example.livechat.LCViewModel
import com.example.livechat.R
import com.example.livechat.checkedSignedIn
import com.example.livechat.comonProgessbar
import com.example.livechat.navigateTo

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUp(navControler:NavController,vm:LCViewModel) {
    val context = LocalContext.current
   // checkedSignedIn(vm = vm, navController = navControler)

    Box(modifier = Modifier.fillMaxSize()) {

        Column(modifier = Modifier
            .fillMaxWidth()
            .wrapContentHeight()
            .verticalScroll(rememberScrollState(),),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var nameState by remember {
                mutableStateOf(TextFieldValue())
            }
            var nnumberState by remember {
                mutableStateOf(TextFieldValue())
            }
             var emailState by remember {
                mutableStateOf(TextFieldValue())
            }
             var passwordState by remember {
                mutableStateOf(TextFieldValue())
            }

            val focus = LocalFocusManager.current

            Image(painter = painterResource(id = R.drawable.chat ), contentDescription ="",
                modifier = Modifier
                    .height(150.dp)
                    .padding(top = 32.dp)
                    .padding(8.dp)
            )
            Text(text = "Sign Up", Modifier.padding(top = 8.dp),
                style = TextStyle(fontSize = 20.sp,
                    color = Color.Cyan,
                    )
            )
            OutlinedTextField(value = nameState, onValueChange = {
                nameState = it
            },
                label = { Text(text = "Name")},
                placeholder = { Text(text = "Enter your name")},
                modifier = Modifier.padding(8.dp)
                )

            OutlinedTextField(value = nnumberState, onValueChange = {
                nnumberState = it
            },
                label = { Text(text = "Number")},
                placeholder = { Text(text = "Enter your number")},
                modifier = Modifier.padding(8.dp)
            )

            OutlinedTextField(value = emailState, onValueChange = {
                emailState = it
            },
                label = { Text(text = "Email")},
                placeholder = { Text(text = "Enter your email")},
                modifier = Modifier.padding(8.dp)
            )
            OutlinedTextField(value = passwordState, onValueChange = {
                passwordState = it
            },
                label = { Text(text = "Password")},
                placeholder = { Text(text = "Enter your password")},
                modifier = Modifier.padding(8.dp)
            )
            Button(onClick = {
                if(nameState.text.isBlank() || nnumberState.text.isBlank() || emailState.text.isBlank() || passwordState.text.isBlank()){
                    Toast.makeText(context, "Fill the information", Toast.LENGTH_SHORT).show()
                }else {
                    vm.signUp(
                        nameState.text,
                        nnumberState.text,
                        emailState.text,
                        passwordState.text,
                        navControler,
                        vm
                    )

                }

            }) {
                Modifier.padding(8.dp)
                Text(text = "Sign Up")
            }
            Text(text = "Already Have an account? Go to login ->",
                color= Color.Blue,
               modifier = Modifier
                   .padding(8.dp)
                   .clickable {
                       navigateTo(navControler, DestinationScreens.login.route)

                   })
        }

    }
    if(vm.inProcess.value){
        comonProgessbar()
    }

}