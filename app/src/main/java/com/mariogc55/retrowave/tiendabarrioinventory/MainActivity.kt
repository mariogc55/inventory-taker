package com.mariogc55.retrowave.tiendabarrioinventory

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.mariogc55.retrowave.tiendabarrioinventory.model.AppDatabase
import com.mariogc55.retrowave.tiendabarrioinventory.ui.screens.LoginScreen
import com.mariogc55.retrowave.tiendabarrioinventory.ui.screens.MainDashboardScreen
import com.mariogc55.retrowave.tiendabarrioinventory.ui.screens.RegisterScreen
import com.mariogc55.retrowave.tiendabarrioinventory.ui.theme.TiendaBarrioInventoryTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val database = AppDatabase.getDatabase(applicationContext)
        val userDao = database.userDao()

        setContent {
            TiendaBarrioInventoryTheme {
                MainAppContent(userDao = userDao)
            }
        }
    }
}

@Composable
fun MainAppContent(userDao: com.mariogc55.retrowave.tiendabarrioinventory.model.UserDao) {
    var currentScreen by remember { mutableStateOf("login") }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            when (currentScreen) {
                "login" -> LoginScreen(
                    userDao = userDao,
                    onLoginSuccess = { currentScreen = "dashboard" },
                    onNavigateToRegister = { currentScreen = "register" }
                )
                "register" -> RegisterScreen(
                    userDao = userDao,
                    onRegisterSuccess = { currentScreen = "dashboard" },
                    onBackToLogin = { currentScreen = "login" }
                )
                "dashboard" -> MainDashboardScreen()
            }
        }
    }
}