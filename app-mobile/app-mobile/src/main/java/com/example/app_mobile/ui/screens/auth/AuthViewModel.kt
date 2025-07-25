package com.example.app_mobile.ui.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> = _errorMessage

    private val _user = MutableStateFlow<FirebaseUser?>(auth.currentUser)
    val user: StateFlow<FirebaseUser?> = _user

    fun login(email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    _isLoading.value = false
                    if (task.isSuccessful) {
                        _user.value = auth.currentUser
                    } else {
                        _errorMessage.value = task.exception?.message ?: "Error desconocido"
                    }
                }
        }
    }

    fun register(name: String, email: String, password: String) {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        _user.value = user
                        // Guarda el usuario en Firestore
                        user?.let {
                            val userData = hashMapOf(
                                "uid" to it.uid,
                                "name" to name,
                                "email" to email
                            )
                            firestore.collection("users").document(it.uid).set(userData)
                        }
                        _isLoading.value = false
                    } else {
                        _isLoading.value = false
                        _errorMessage.value = task.exception?.message ?: "Error desconocido"
                    }
                }
        }
    }

    fun logout() {
        auth.signOut()
        _user.value = null
    }

    fun clearError() {
        _errorMessage.value = null
    }
} 