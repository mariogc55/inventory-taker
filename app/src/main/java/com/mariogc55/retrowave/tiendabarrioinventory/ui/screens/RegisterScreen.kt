package com.mariogc55.retrowave.tiendabarrioinventory.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import com.mariogc55.retrowave.tiendabarrioinventory.model.UserDao
import com.mariogc55.retrowave.tiendabarrioinventory.model.UserEntity
import kotlinx.coroutines.launch

@Composable
fun RegisterScreen(
    userDao: UserDao,
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Crear Cuenta", style = MaterialTheme.typography.headlineLarge)

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = username,
                onValueChange = { username = it },
                label = { Text("Usuario") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Correo electrónico") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    if (username.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                        scope.launch {
                            val newUser = UserEntity(email = email, password = password)
                            userDao.insertUser(newUser)
                            onRegisterSuccess()
                        }
                    } else {
                        scope.launch {
                            snackbarHostState.showSnackbar("Por favor completa todos los campos")
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Registrarse y Acceder")
            }

            Spacer(modifier = Modifier.height(8.dp))

            TextButton(onClick = onBackToLogin) {
                Text("¿Ya tienes una cuenta? Inicia sesión")
            }
        }
    }
}