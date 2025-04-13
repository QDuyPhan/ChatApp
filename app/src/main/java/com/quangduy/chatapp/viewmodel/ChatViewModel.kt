package com.quangduy.chatapp.viewmodel

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.quangduy.chatapp.data.model.Messages
import com.quangduy.chatapp.data.model.RecentChats
import com.quangduy.chatapp.data.model.Users
import com.quangduy.chatapp.data.repository.ChatListRepository
import com.quangduy.chatapp.data.repository.ChatRepository
import com.quangduy.chatapp.data.repository.MessageRepository
import com.quangduy.chatapp.data.repository.UsersRepository
import com.quangduy.chatapp.datastore.AppSetting
import com.quangduy.chatapp.ultils.Constants.getUidLoggedIn
import com.quangduy.chatapp.ultils.IODispatcher
import com.quangduy.chatapp.ultils.Logger
import com.quangduy.chatapp.ultils.MainDispatcher
import com.quangduy.chatapp.ultils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val usersRepository: UsersRepository,
    private val chatRepository: ChatRepository,
    private val messageRepository: MessageRepository,
    private val chatListRepository: ChatListRepository,
    @IODispatcher private val ioDispatcher: CoroutineDispatcher,
    @MainDispatcher private val mainDispatcher: CoroutineDispatcher,
    @ApplicationContext private val context: Context,
    private val appSetting: AppSetting,
    private val firestore: FirebaseFirestore,
) : BaseViewModel() {
    val name = MutableLiveData<String>()

    val imageUrl = MutableLiveData<String>()

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> get() = _message

    private val _users = MutableLiveData<Resource<List<Users>>>()
    val users: LiveData<Resource<List<Users>>> get() = _users

//    val chatList: LiveData<List<RecentChats>> = chatListRepository.getAllChatList()

    private val _chatList = MutableLiveData<List<RecentChats>>()
    val chatList: LiveData<List<RecentChats>> = _chatList

    init {
        getAllUsers()
        getCurrentUser()
        getRecentChats()
    }

    private fun getRecentChats() {
        viewModelScope.launch {
            chatListRepository.getAllChatList()
                .catch { e -> Logger.logE("ChatListVM Error: ${e.message}") }
                .collect { chats ->
                    _chatList.value = chats
                }
        }
    }

    private fun getAllUsers() {
        viewModelScope.launch(ioDispatcher + exceptionHandler) {
            safeFirebaseCall(
                liveData = _users, task = {
                    usersRepository.getUsers().continueWith { task ->
                        val result = task.result?.documents?.mapNotNull { document ->
                            val user = document.toObject(Users::class.java)
                            if (user?.userid != getUidLoggedIn()) user else null
                        } ?: emptyList()
                        result
                    }
                })
        }
    }

    fun getCurrentUser() {
        chatRepository.getCurrentUser { user ->
            user?.let {
                name.value = it.username ?: ""
                imageUrl.value = it.imageUrl ?: ""

                viewModelScope.launch {
                    it.username?.let { uname ->
                        appSetting.setValue("username", uname)
                    }
                }
            }
        }
    }

    fun setMessage(msg: String) {
        _message.value = msg
    }

    fun sendMessage(receiver: String, friendName: String, friendImage: String) {
        viewModelScope.launch(ioDispatcher) {
            val sender = getUidLoggedIn()
            val msg = _message.value?.trim()
            if (!msg.isNullOrEmpty()) {
                chatRepository.sendMessage(sender, receiver, msg, friendName, friendImage)
                withContext(mainDispatcher) {
                    _message.value = ""
                }
            }
        }
    }

    fun getMessages(friend: String): LiveData<List<Messages>> {
        return messageRepository.getMessages(friend)
    }
//    private fun sendPushNotificationIfNeeded(receiverId: String, message: String, prefs: SharedPrefs) {
//        firestore.collection("Tokens").document(receiverId).get()
//            .addOnSuccessListener { snapshot ->
//                val tokenObject = snapshot.toObject(Token::class.java)
//                val token = tokenObject?.token ?: return@addOnSuccessListener
//
//                val senderName = prefs.getValue("username")?.split("\\s".toRegex())?.firstOrNull() ?: "Someone"
//
//                if (message.isNotEmpty()) {
//                    val notification = PushNotification(
//                        NotificationData(senderName, message),
//                        token
//                    )
//                    sendNotification(notification)
//                }
//            }
//            .addOnFailureListener {
//                Log.e("ChatViewModel", "Không lấy được token: ${it.message}")
//            }
//    }

}