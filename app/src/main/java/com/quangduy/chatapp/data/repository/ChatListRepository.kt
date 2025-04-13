package com.quangduy.chatapp.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.quangduy.chatapp.data.model.RecentChats
import com.quangduy.chatapp.ultils.Constants.getUidLoggedIn
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import javax.inject.Inject

class ChatListRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
) {
    fun getAllChatList(): Flow<List<RecentChats>> = callbackFlow {
        val listener = firestore
            .collection("Conversation${getUidLoggedIn()}")
            .orderBy("time", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val chatList = snapshot?.mapNotNull { doc ->
                    val chat = doc.toObject(RecentChats::class.java)
                    if (chat.sender == getUidLoggedIn()) chat else null
                } ?: emptyList()

                trySend(chatList)
            }

        awaitClose { listener.remove() }
    }
}