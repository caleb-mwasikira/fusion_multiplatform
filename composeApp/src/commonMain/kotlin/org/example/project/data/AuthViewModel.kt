package org.example.project.data

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.withContext

sealed class Error : Exception() {
    data class ValidationException(val field: String, override val message: String) : Error()
    data class BadResponse(override val message: String) : Error()
}


class AuthViewModel : ViewModel() {
    companion object {
        const val MIN_USERNAME_LEN: Int = 4
        const val MIN_PASSWORD_LEN: Int = 6
        const val MIN_OTP_LEN: Int = 6
    }

    var username by mutableStateOf<String>("")
    var email by mutableStateOf<String>("")
    var password by mutableStateOf<String>("")
    var confirmPassword by mutableStateOf<String>("")
    var otp by mutableStateOf<String>("")

    val errors = MutableSharedFlow<Error>()

    suspend fun createAccount(): Boolean = withContext((Dispatchers.IO)) {
        try {
            validateUsername()
            validateEmail()
            validatePassword(password)
            matchPasswordAndConfirmPassword()

            println("createAccount()")

            TODO("Not yet implemented")

        } catch (e: Error.ValidationException) {
            errors.emit(e)
            return@withContext false
        } catch (e: Exception) {
            println("createAccount() => ${e.message}")
            return@withContext false
        }
    }

    suspend fun loginUser(): Boolean = withContext((Dispatchers.IO)) {
        try {
            validateEmail()
            validatePassword(password)

            println("loginUser()")

            TODO("Not yet implemented")

        } catch (e: Error.ValidationException) {
            errors.emit(e)
            return@withContext false
        } catch (e: Exception) {
            println("loginUser() => ${e.message}")
            return@withContext false
        }
    }

    suspend fun forgotPassword(): Boolean = withContext((Dispatchers.IO)) {
        try {
            validateEmail()

            println("forgotPassword()")

            TODO("Not yet implemented")

        } catch (e: Error.ValidationException) {
            errors.emit(e)
            return@withContext false
        } catch (e: Exception) {
            println("forgotPassword() => ${e.message}")
            return@withContext false
        }
    }

    suspend fun resetPassword(): Boolean = withContext((Dispatchers.IO)) {
        try {
            validateEmail()
            validatePassword(password)
            matchPasswordAndConfirmPassword()
            validateOTP()

            println("resetPassword()")

            TODO("Not yet implemented")

        } catch (e: Error.ValidationException) {
            errors.emit(e)
            return@withContext false
        } catch (e: Exception) {
            println("forgotPassword() => ${e.message}")
            return@withContext false
        }
    }

    fun logout(): Boolean {
        TODO("Not yet implemented")
    }

    private fun validateUsername() {
        if (username.length < MIN_USERNAME_LEN) {
            throw Error.ValidationException(field = "username", message = "Username too short")
        }
    }

    private fun validateEmail() {
        if (email.isEmpty()) {
            throw Error.ValidationException(field = "email", message = "Email cannot be empty")
        }
        if (!email.contains("@") || !email.contains(".com")) {
            throw Error.ValidationException(field = "email", message = "Invalid email")
        }
    }

    private fun validatePassword(password: String) {
        if (password.length < MIN_PASSWORD_LEN) {
            throw Error.ValidationException(field = "password", message = "Password too short")
        }
    }

    private fun validateOTP() {
        if (otp.length < MIN_OTP_LEN) {
            throw Error.ValidationException(field = "otp", message = "OTP too short")
        }
    }

    private fun matchPasswordAndConfirmPassword() {
        if (password != confirmPassword) {
            throw Error.ValidationException(
                field = "confirmPassword",
                message = "Password and confirmPassword does NOT match"
            )
        }
    }
}