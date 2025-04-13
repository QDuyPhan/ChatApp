package com.quangduy.chatapp.data.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.quangduy.chatapp.data.model.Messages
import com.quangduy.chatapp.ultils.Constants.getUidLoggedIn
import javax.inject.Inject

class MessageRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {
    fun getMessages(friendId: String): LiveData<List<Messages>> {
        val messagesLiveData = MutableLiveData<List<Messages>>()
        val chatRoomId = listOf(getUidLoggedIn(), friendId).sorted().joinToString("")

        firestore.collection("Messages")
            .document(chatRoomId)
            .collection("chats")
            .orderBy("time", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, exception ->
                if (exception != null || snapshot == null) return@addSnapshotListener

                val currentUid = getUidLoggedIn()
                val messages = snapshot.documents.mapNotNull {
                    it.toObject(Messages::class.java)
                }.filter {
                    (it.sender == currentUid && it.receiver == friendId) ||
                            (it.sender == friendId && it.receiver == currentUid)
                }

                messagesLiveData.postValue(messages) // ✅ Safe
            }

        return messagesLiveData
    }
}