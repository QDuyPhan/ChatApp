package com.quangduy.chatapp.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.quangduy.chatapp.ultils.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import javax.inject.Inject

class UsersRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    @IODispatcher private val dispatcher: CoroutineDispatcher
) {
    fun getUsers(): Task<QuerySnapshot> {
        return firestore.collection("Users").get()
    }

    suspend fun updateProfile(
        userId: String,
        name: String,
        imageUrl: String,
        friendId: String
    ): Result<Unit> = withContext(dispatcher) {
        return@withContext try {
            val hashMapUser = hashMapOf<String, Any>(
                "username" to name,
                "imageUrl" to imageUrl
            )

            firestore.collection("Users").document(userId).update(hashMapUser).await()

            val hashMapUpdate = hashMapOf<String, Any>(
                "friendsImage" to imageUrl,
                "name" to name,
                "person" to name
            )

            firestore.collection("Conversation$friendId")
                .document(userId)
                .update(hashMapUpdate).await()

            firestore.collection("Conversation$userId")
                .document(friendId)
                .update("person", "you").await()

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}