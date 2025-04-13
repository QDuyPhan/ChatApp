package com.quangduy.chatapp.data.repository

import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.quangduy.chatapp.ultils.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class UsersRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    @IODispatcher private val dispatcher: CoroutineDispatcher
) {
    fun getUsers(): Task<QuerySnapshot> {
        return firestore.collection("Users").get()
    }
}