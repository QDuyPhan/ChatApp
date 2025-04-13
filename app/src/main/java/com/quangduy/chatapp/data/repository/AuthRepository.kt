package com.quangduy.chatapp.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.quangduy.chatapp.ultils.IODispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class AuthRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    @IODispatcher private val dispatcher: CoroutineDispatcher
) {
    suspend fun login(email: String, password: String): FirebaseUser? =
        withContext(dispatcher) {
            suspendCoroutine { continuation ->
                firebaseAuth.signInWithEmailAndPassword(email, password)
                    .addOnSuccessListener { result ->
                        continuation.resume(result.user)
                    }
                    .addOnFailureListener { e ->
                        continuation.resume(null)
                    }
            }
        }

    suspend fun signup(email: String, password: String): FirebaseUser? =
        withContext(dispatcher) {
            suspendCoroutine { continuation ->
                firebaseAuth.createUserWithEmailAndPassword(email, password)
                    .addOnSuccessListener { result ->
                        continuation.resume(result.user)
                    }
                    .addOnFailureListener {
                        continuation.resume(null)
                    }
            }
        }

    suspend fun logout() = withContext(dispatcher) {
        firebaseAuth.signOut()
    }
}