package org.example.project.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import minio_multiplatform.composeapp.generated.resources.Res
import minio_multiplatform.composeapp.generated.resources.folder
import minio_multiplatform.composeapp.generated.resources.visibility_24dp
import minio_multiplatform.composeapp.generated.resources.visibility_off_24dp
import org.example.project.data.AuthViewModel
import org.example.project.data.Error
import org.example.project.data.Route
import org.jetbrains.compose.resources.painterResource


@Composable
fun LoginScreen(
    authViewModel: AuthViewModel = AuthViewModel(),
    navigateTo: (Route) -> Unit,
) {
    val invalidFields = remember { mutableStateListOf<String>() }

    LaunchedEffect(Unit) {
        authViewModel.errors.collectLatest { error ->
            when (error) {
                is Error.BadResponse -> {
                    TODO("Not yet implemented")
                }

                is Error.ValidationException -> {
                    TODO("Not yet implemented")
                }
            }
        }
    }

    Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 48.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(40.dp))

            Image(
                painter = painterResource(Res.drawable.folder),
                contentDescription = null,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.height(32.dp))

            OutlinedTextField(
                value = authViewModel.email,
                onValueChange = {
                    invalidFields.remove("email")
                    authViewModel.email = it
                },
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Email
                ),
                isError = invalidFields.contains("email")
            )

            Spacer(modifier = Modifier.height(12.dp))

            var passwordVisible by remember { mutableStateOf(false) }

            OutlinedTextField(
                value = authViewModel.password,
                onValueChange = {
                    invalidFields.remove("password")
                    authViewModel.password = it
                },
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val icon =
                        if (passwordVisible) Res.drawable.visibility_off_24dp else Res.drawable.visibility_24dp

                    IconButton(
                        onClick = { passwordVisible = !passwordVisible }
                    ) {
                        Icon(
                            painter = painterResource(icon),
                            contentDescription = "Toggle Password Visibility"
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.NumberPassword
                ),
                isError = invalidFields.contains("password")
            )

            TextButton(
                onClick = {
                    navigateTo(Route.ForgotPasswordScreen)
                }
            ) {
                Text(
                    text = "Forgot Password?",
                    modifier = Modifier.padding(8.dp),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary,
                    textDecoration = TextDecoration.Underline,
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            val scope = rememberCoroutineScope()

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Button(
                    onClick = {
                        scope.launch {
                            val loginSuccess = authViewModel.loginUser()
                            if (loginSuccess) {
//                                sharedPreferences.edit {
//                                    putString(MainActivity.USERNAME, authViewModel.username)
//                                    putString(MainActivity.EMAIL, authViewModel.email)
//                                    putString(MainActivity.PIN, authViewModel.password)
//                                }

                                navigateTo(Route.HomeScreen)
                                return@launch
                            }
                        }
                    },
                    shape = RoundedCornerShape(50), // Makes it oval
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors().copy(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary,
                    )
                ) {
                    Text(
                        text = "Login",
                        style = MaterialTheme.typography.titleLarge,
                        modifier = Modifier.padding(vertical = 12.dp),
                    )
                }

                TextButton(
                    onClick = {
                        navigateTo(Route.SignUpScreen)
                    }
                ) {
                    Text(
                        "Don't have an account? Create One",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}
