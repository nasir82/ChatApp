package com.example.livechat

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.core.text.isDigitsOnly
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.livechat.data.CHATS
import com.example.livechat.data.ChatData
import com.example.livechat.data.ChatUser
import com.example.livechat.data.Event
import com.example.livechat.data.MESSAGES
import com.example.livechat.data.Message
import com.example.livechat.data.USER_NODE
import com.example.livechat.data.UserData
import com.example.livechat.screens.printData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.Filter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.toObject
import com.google.firebase.firestore.toObjects
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.Calendar
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class LCViewModel @Inject constructor(
    val auth: FirebaseAuth,
    val db: FirebaseFirestore,
    val storage: FirebaseStorage
) : ViewModel() {


    var inProcess = mutableStateOf(false)
    var inProcessChat = mutableStateOf(false)
    var inProcessChatMessage = mutableStateOf(false)
    var eventState = mutableStateOf<Event<String>?>(null)
    var singIn = mutableStateOf(false)
    var userData = mutableStateOf<UserData?>(null)
    val chats = mutableStateOf<List<ChatData>>(listOf())
    val messages = mutableStateOf<List<Message>>(listOf())
    var chatMessageListener:ListenerRegistration?=null


    init {
        val current = auth.currentUser
        singIn.value = current != null
        current?.uid?.let {
            getUserData(it)

        }

    }

    fun SingInFunction(
        email: String,
        password: String,
        navController: NavController,
        vm: LCViewModel
    ) {

        if (email.isBlank() or password.isBlank()) {
            handleException(customMessage = "Fill the information ")
            return
        } else {
            inProcess.value = true

            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    singIn.value = true
                    inProcess.value = false
                    auth.currentUser?.uid?.let { it ->
                        getUserData(it)
                        navigateTo(navController, DestinationScreens.profile.route)


                    }
                } else {
                    //navigateTo(navController,DestinationScreens.singleChatlist.route)
                    handleException(it.exception, customMessage = "Login fail ${it.exception}")
                }
            }
        }
    }

    fun uploadProfileImage(uri: Uri, vm: LCViewModel, navController: NavController) {

        uploadImage(uri) {
            CreateorUpdateProfile(imageUrl = it.toString(), navController = navController, vm = vm)
        }
    }

    fun uploadImage(uri: Uri, onSucces: (Uri) -> Unit) {
        inProcess.value = true
        val ref = storage.reference
        val uuid = UUID.randomUUID()
        val imgRef = ref.child("imags${System.currentTimeMillis()}.jpg")
        val uploadtask = imgRef.putFile(uri)
        uploadtask.addOnSuccessListener {
            val result = it.metadata!!.reference!!.downloadUrl
            result?.addOnSuccessListener(onSucces)
            inProcess.value = false
        }
    }

    fun signUp(
        name: String,
        number: String,
        email: String,
        password: String,
        navController: NavController,
        vm: LCViewModel
    ) {

        inProcess.value = true

        db.collection(USER_NODE).whereEqualTo("number", number).get().addOnSuccessListener {
            if (it.isEmpty) {

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                    if (it.isSuccessful) {

                        Log.d("TAG", "signUp: Done ")
                        singIn.value = true
                        CreateorUpdateProfile(name, number, null, navController, vm)

                    } else {
                        handleException(it.exception, customMessage = "Sign Up failed")
                    }
                }
            } else {
                handleException(customMessage = "Number already exist")
            }


        }


    }

    fun CreateorUpdateProfile(
        name: String? = "",
        number: String? = "",
        imageUrl: String? = null,
        navController: NavController,
        vm: LCViewModel
    ) {

        var uid = auth.currentUser?.uid
        var userData = UserData(
            userId = uid,
            name = name ?: userData.value?.name,
            number = number ?: userData.value?.number,
            imageUrl = imageUrl ?: userData.value?.imageUrl
        )

        uid?.let {

            inProcess.value = true
            db.collection(USER_NODE).document(uid).get().addOnSuccessListener {
                if (it.exists()) {
                    inProcess.value = false
                    db.collection(USER_NODE).document(uid).set(userData).addOnSuccessListener {

                        navigateTo(navController, DestinationScreens.profile.route)
                    }

                } else {
                    db.collection(USER_NODE).document(uid).set(userData).addOnSuccessListener {
                        inProcess.value = false
                        getUserData(uid)
                        navigateTo(navController, DestinationScreens.profile.route)

                    }
                }
            }.addOnFailureListener {
                handleException(it, "can not retrieve data ")

            }
        }
    }

    private fun getUserData(uid: String) {
        inProcess.value = true
        db.collection(USER_NODE).document(uid).addSnapshotListener { valu, error ->
            if (error != null) {
                handleException(error, "can not retrieve")
            }
            if (valu != null) {
                val user = valu.toObject<UserData>()
                userData.value = user
                inProcess.value = false
                populateChat()
            }
        }
    }

    fun handleException(exception: Exception? = null, customMessage: String? = null) {
        Log.e("LiveChat", "Exception: ", exception)
        exception?.printStackTrace()
        Log.d("hereEx ", customMessage.toString())
        val errorMes = exception?.localizedMessage ?: ""
        val message = if (customMessage.isNullOrEmpty()) errorMes else customMessage
        eventState.value = Event(message)
        inProcess.value = false

    }

    // Populate chat
    fun addOnChat(number: String) {
        inProcessChat.value = true

        /**
         * first check is the number is empty or containing character with digit
         * if it's correct number then check is it previously added to the chat
         *
         * if not then add the chat to the list
         */

        if (number.isEmpty() or !number.isDigitsOnly()) {
            handleException(customMessage = "Number is not correct enter valid number")
            if (!number.isDigitsOnly()) Log.d("number", number.toString())
            else Log.d("number", "its perfect")
            inProcessChat.value = false
            inProcess.value = false
        } else {

            db.collection(CHATS).where(
                Filter.or(
                    Filter.and(
                        Filter.equalTo("user1.number", number),
                        Filter.equalTo("user2.number", userData.value?.number)
                    ),
                    Filter.and(
                        Filter.equalTo("user1.number", userData.value?.number),
                        Filter.equalTo("user2.number", number)
                    )
                )
            )   // this block of code will gather all the information about chat that you are involve

                .get().addOnSuccessListener {

                    /**
                     * get collect list of object
                     */

                    if (it.isEmpty) {
                        /**
                         * here empty means there are no chat such that where
                         */
                        db.collection(USER_NODE).whereEqualTo("number", number).get()
                            .addOnSuccessListener { it ->
                                if (it.isEmpty) {

                                    /**
                                     * the number have no account
                                     */
                                    handleException(customMessage = "Number not found  $number")
                                    inProcessChat.value = false
                                } else {
                                    //You are ready to collect the data

                                    val chatpartner = it.toObjects<UserData>()[0]
                                    val id = db.collection(CHATS)
                                        .document().id // taking a new documents in the database chats collection

                                    val chat = ChatData(
                                        chatId = id,
                                        ChatUser(
                                            userData.value?.userId,
                                            userData.value?.name,
                                            userData.value?.imageUrl,
                                            userData.value?.number
                                        ),
                                        ChatUser(
                                            chatpartner.userId,
                                            chatpartner.name,
                                            chatpartner.imageUrl,
                                            chatpartner.number
                                        ),


                                        )

                                    db.collection(CHATS).document(id).set(chat)
                                        .addOnSuccessListener {
                                            inProcessChat.value = false
                                            inProcess.value = false

                                        }
                                        .addOnFailureListener {
                                            inProcessChat.value = false
                                            inProcess.value = false
                                        }
                                }

                            }
                            .addOnFailureListener {

                                handleException(it)
                                inProcessChat.value = false
                                inProcess.value = false

                            }
                    } else {


                        handleException(customMessage = "chat already exists")
                        inProcessChat.value = false
                        inProcess.value = false

                    }
                }

        }
    }


    /**
     * Collect chatlist using chatlist method and this will run over the firestore database to collect the list of users whose have a chat with
     * the current user
     * i will check this like is first or second persion of this a chat is equal to the user person using phone number
     *
     * thi method will collect daata while this will collect data at the time of collecting user informations
     *
     */


    fun populateChat() {

        db.collection(CHATS).where(
            Filter.or(
                Filter.equalTo("user1.userId", userData.value?.userId),
                Filter.equalTo("user2.userId", userData.value?.userId),

                )
        )
            .addSnapshotListener { value, err ->
                if (err != null) {
                    handleException(err)
                }
                if (value != null) {
                    chats.value = value.documents.mapNotNull {
                        it.toObject<ChatData>()
                    }
                }
            }
    }

    @Composable
    fun Clct() {
        val datalist =
            remember { mutableStateListOf<UserData>() } // Use mutableStateListOf to make it observable
        db.collection(USER_NODE).get().addOnSuccessListener { querySnapshot ->
            val dm = ArrayList<UserData>()

            for (document in querySnapshot.documents) {
                val userData = document.toObject<UserData>()
                userData?.let { dm.add(it) }
            }
            datalist.addAll(dm)
            // Call your composable function here or pass datalist to it

            // Log.d("size of data", datalist.size.toString() + datalist.toString())
        }
        printData(datalist)
    }


    fun onSendReply(chatId: String, message: String) {
        val time = Calendar.getInstance().time.toString()
        val msg = Message(
            userData.value?.userId,
            message = message,
            time = time
        )

        db.collection(CHATS).document(chatId).collection(MESSAGES).document().set(msg)

    }

    fun populateMessage(id: String) {
        inProcessChatMessage.value = true
        chatMessageListener = db.collection(CHATS).document(id).collection(MESSAGES).addSnapshotListener { valu, err ->

            if (err != null) {
                handleException(customMessage = "Cann't collect messages")
            }
            valu?.let {
                messages.value = valu.documents.mapNotNull {
                    it.toObject<Message>()
                }.sortedBy {
                    it.time

                }
                inProcessChatMessage.value = false
            }

        }
    }

    fun depopulateMessage(){
        messages.value  = listOf()
        chatMessageListener =null
    }

}

/**
 * this is the time to collect all of my chat for this a separate list could be time friendly but memory costly
 *
 */

