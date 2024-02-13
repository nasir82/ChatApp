package com.example.livechat

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.livechat.screens.Chatlist
import com.example.livechat.screens.Login
import com.example.livechat.screens.Mainshow
import com.example.livechat.screens.Profile
import com.example.livechat.screens.SignUp
import com.example.livechat.screens.SingleChatlist
import com.example.livechat.screens.StatusScreen
import com.example.livechat.ui.theme.LiveChatTheme
import dagger.hilt.android.AndroidEntryPoint


sealed class DestinationScreens(var route: String) {
    object singUp : DestinationScreens("signUp")
    object login : DestinationScreens("login")
    object profile : DestinationScreens("Profile")
    object chatlist : DestinationScreens("Chatlist")
    object singleChatlist : DestinationScreens("singlechat/{chatId}") {
        fun createRoute(id: String): String {
            return "singlechat/$id"
        }
    }

    object statuslist : DestinationScreens("StatusScreen")
    object singlestatus : DestinationScreens("singlestatus/{userid}") {

        fun createRoute(userid: String) = "siglestatus/$userid"
    }

    object showScreen:DestinationScreens("show")


}

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LiveChatTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    ChatAppNav()
                }
            }
        }
    }

    @Composable
    fun ChatAppNav() {
        val nav = rememberNavController()
        val vm = hiltViewModel<LCViewModel>()

        NavHost(navController = nav, startDestination = DestinationScreens.login.route){
            composable(DestinationScreens.singUp.route){
                SignUp(nav,vm)
            }
            composable(DestinationScreens.login.route){
                Login(nav,vm)

            }
            composable(DestinationScreens.chatlist.route){
                Chatlist(nav,vm)
            }
            composable(DestinationScreens.singleChatlist.route,
                arguments = listOf(navArgument("chatId"){
                    type = NavType.StringType
                })){
                val id = it.arguments!!.getString("chatId")
                id?.let {

                    SingleChatlist(nav=nav,vm=vm,id=id)
                }
            }
            composable(DestinationScreens.statuslist.route){
                StatusScreen(nav,vm)

            }
            composable(DestinationScreens.profile.route){
                Profile(nav,vm)
            }

            composable(DestinationScreens.showScreen.route){
                Mainshow(nav,vm)
            }


        }

    }
}


