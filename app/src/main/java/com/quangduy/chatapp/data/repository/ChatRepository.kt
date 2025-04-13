package com.quangduy.chatapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.quangduy.chatapp.data.model.Users
import com.quangduy.chatapp.datastore.AppSetting
import com.quangduy.chatapp.ultils.Constants.getTime
import com.quangduy.chatapp.ultils.Constants.getUidLoggedIn
import com.quangduy.chatapp.ultils.Logger
import javax.inject.Inject


class ChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val appSetting: AppSetting,
) {
    fun getCurrentUser(callback: (Users?) -> Unit) {
        val uid = getUidLoggedIn()
        firestore.collection("Users").document(uid).addSnapshotListener { value, error ->
                if (error != null) {
                    Logger.logE("ChatRepository: ${error.message}")
                    callback(null)
                    return@addSnapshotListener
                }

                val user = value?.toObject(Users::class.java)
                callback(user)
            }
    }

    suspend fun sendMessage(
        sender: String, receiver: String, message: String, name: String, friendImage: String
    ) {
        val chatroomId = listOf(sender, receiver).sorted().joinToString("")
        val time = getTime()
        val friendNameSplit = name.split("\\s".toRegex())[0]

        val msgMap = mapOf(
            "sender" to sender, "receiver" to receiver, "message" to message, "time" to time
        )

        appSetting.setValue("friendId", receiver)
        appSetting.setValue("chatRoomId", chatroomId)
        appSetting.setValue("friendName", friendNameSplit)
        appSetting.setValue("friendImage", friendImage)

        firestore.collection("Messages").document(chatroomId).collection("chats").document(time)
            .set(msgMap)

        val recentMap = mapOf(
            "friendId" to receiver,
            "time" to time,
            "sender" to sender,
            "message" to message,
            "friendsImage" to friendImage,
            "name" to name,
            "person" to "you"
        )

        firestore.collection("Conversation$sender").document(receiver).set(recentMap)

        firestore.collection("Conversation$receiver").document(sender).update(
                "message", message, "time", time, "person", name
            )
    }
}