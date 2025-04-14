package com.quangduy.chatapp.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.quangduy.chatapp.ultils.Constants.getUidLoggedIn
import com.quangduy.chatapp.ultils.IODispatcher
import com.quangduy.chatapp.ultils.Logger
import com.quangduy.chatapp.ultils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val auth: FirebaseAuth,
    private val firestore: FirebaseFirestore,
    @IODispatcher private val dispatcher: CoroutineDispatcher
) : BaseViewModel() {

    private val _authResult = MutableLiveData<Resource<Unit>>()
    val authResult: LiveData<Resource<Unit>> get() = _authResult

    fun statusOnline() {
        if (auth.currentUser != null) {
            firestore.collection("Users").document(getUidLoggedIn()).update("status", "Online")
        }
    }

    fun statusOffline() {
        if (auth.currentUser != null) {
            firestore.collection("Users").document(getUidLoggedIn()).update("status", "Offline")
        }
    }

    fun login(email: String, password: String) {
        _authResult.value = Resource.loading(null)
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Logger.logI("Task result: ${task.result.user?.email}")
                    _authResult.value = Resource.success(Unit)
                } else {
                    _authResult.value =
                        Resource.error(task.exception?.message ?: "Login failed", null)
                }
            }
    }

    fun signUp(name: String, email: String, password: String) {
        _authResult.value = Resource.loading(null)
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val userRef = firestore.collection("Users").document(it.uid)

                        userRef.get().addOnSuccessListener { document ->
                            if (!document.exists()) {
                                val data = hashMapOf(
                                    "userid" to it.uid,
                                    "username" to name,
                                    "email" to email,
                                    "status" to "Offline",
                                    "imageUrl" to "https://img.freepik.com/free-psd/3d-illustration-person-with-sunglasses_23-2149436188.jpg"
                                )

                                userRef.set(data)
                                    .addOnSuccessListener {
                                        _authResult.value = Resource.success(Unit)
                                    }
                                    .addOnFailureListener { e ->
                                        _authResult.value =
                                            Resource.error(e.message ?: "Firestore error", null)
                                    }
                            } else {
                                // User đã tồn tại → không cần tạo mới
                                _authResult.value = Resource.success(Unit)
                            }
                        }.addOnFailureListener { e ->
                            _authResult.value =
                                Resource.error(e.message ?: "Firestore error", null)
                        }
                    } ?: run {
                        _authResult.value = Resource.error("User is null", null)
                    }
                } else {
                    _authResult.value =
                        Resource.error(task.exception?.message ?: "Sign up failed", null)
                }
            }

    }


    fun logout() {
        auth.signOut()
        _authResult.value = Resource.success(Unit)
    }

    fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val user = auth.currentUser
                    user?.let {
                        val userRef = firestore.collection("Users").document(it.uid)

                        userRef.get().addOnSuccessListener { document ->
                            if (!document.exists()) {
                                // ✅ Nếu user chưa tồn tại, tạo mới trong Firestore
                                val userMap = hashMapOf(
                                    "userid" to it.uid,
                                    "username" to (it.displayName ?: "No Name"),
                                    "email" to (it.email ?: ""),
                                    "status" to "default",
                                    "imageUrl" to (it.photoUrl?.toString()
                                        ?: "https://img.freepik.com/free-psd/3d-illustration-person-with-sunglasses_23-2149436188.jpg")
                                )

                                userRef.set(userMap)
                                    .addOnSuccessListener {
                                        _authResult.value = Resource.success(Unit)
                                    }
                                    .addOnFailureListener { e ->
                                        _authResult.value =
                                            Resource.error(e.message ?: "Firestore error", null)
                                    }
                            } else {
                                // ✅ Nếu user đã có → coi như login thành công
                                _authResult.value = Resource.success(Unit)
                            }
                        }.addOnFailureListener { e ->
                            _authResult.value =
                                Resource.error(e.message ?: "Firestore error", null)
                        }
                    }
                } else {
                    _authResult.value =
                        Resource.error(
                            "Google sign-in failed: ${task.exception?.message}",
                            null
                        )
                }
            }
    }

}
