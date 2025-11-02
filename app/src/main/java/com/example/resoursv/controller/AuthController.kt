package com.example.resoursv.controller

import android.util.Patterns
import com.google.firebase.auth.FirebaseAuth

data class PasswordValidation(
    val isValid: Boolean,
    val errors: List<String>
)

class AuthController(private val auth: FirebaseAuth) {

    fun isEmailValid(email: String) = Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun validatePassword(password: String): PasswordValidation {
        val errors = mutableListOf<String>()

        if (password.length < 8) {
            errors.add("Debe tener al menos 8 caracteres")
        }
        if (!password.any { it.isUpperCase() }) {
            errors.add("Debe tener al menos una letra mayúscula")
        }
        if (!password.any { it.isLowerCase() }) {
            errors.add("Debe tener al menos una letra minúscula")
        }
        if (!password.any { it.isDigit() }) {
            errors.add("Debe tener al menos un número")
        }
        if (!password.any { it in "!@#\$%^&*" }) {
            errors.add("Debe tener al menos un carácter especial (!@#\$%^&*)")
        }

        return PasswordValidation(errors.isEmpty(), errors)
    }

    fun register(email: String, password: String, onResult: (Boolean, String?) -> Unit) {
        if (!isEmailValid(email)) {
            onResult(false, "Correo electrónico inválido")
            return
        }

        val validation = validatePassword(password)
        if (!validation.isValid) {
            onResult(false, validation.errors.joinToString("\n"))
            return
        }

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

    fun getCurrentUser() = auth.currentUser
}