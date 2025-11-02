package com.example.resoursv.controller

import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth

class AuthController(private val auth: FirebaseAuth) {

    suspend fun isEmailValid(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun register(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onResult(true, null)
                else onResult(false, task.exception?.message)
            }
    }

    fun login(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) onResult(true, null)
                else onResult(false, task.exception?.message)
            }
    }

    fun logout() {
        auth.signOut()
    }
}