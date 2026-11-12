package com.mariogc55.retrowave.tiendabarrioinventory.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mariogc55.retrowave.tiendabarrioinventory.R
import com.mariogc55.retrowave.tiendabarrioinventory.model.UserDao
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val LoginDarkBg = Color(0xFF0B0F17)
private val LoginCardBg = Color(0xFF151C28)
private val LoginPrimaryBlue = Color(0xFF3B82F6)
private val LoginGreenEmerald = Color(0xFF10B981)
private val LoginTextColor = Color(0xFFF1F5F9)
private val LoginSubtextColor = Color(0xFF94A3B8)

@Composable
fun LoginScreen(
    userDao: UserDao,
    onLoginSuccess: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val shakeOffset = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "logoPulse")
    val logoScale by infiniteTransition.animateFloat(
        initialValue = 0.97f,
        targetValue = 1.03f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "scale"
    )

    LaunchedEffect(Unit) {
        delay(100)
        visible = true
    }

    suspend fun triggerShake() {
        for (i in 0..3) {
            shakeOffset.animateTo(14f, tween(45))
            shakeOffset.animateTo(-14f, tween(45))
        }
        shakeOffset.animateTo(0f, tween(45))
    }

    Scaffold(
        containerColor = LoginDarkBg,
        snackbarHost = {
            SnackbarHost(snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = Color(0xFF7F1D1D),
                    contentColor = Color.White,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0F172A),
                            LoginDarkBg,
                            Color(0xFF070A0F)
                        )
                    )
                )
                .padding(padding)
        ) {
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .align(Alignment.TopCenter)
                    .offset(y = (-60).dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                LoginPrimaryBlue.copy(alpha = 0.18f),
                                Color.Transparent
                            )
                        ),
                        shape = CircleShape
                    )
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(600)) + slideInVertically(
                        initialOffsetY = { -40 },
                        animationSpec = tween(600, easing = FastOutSlowInEasing)
                    )
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .scale(logoScale)
                                .background(LoginCardBg, RoundedCornerShape(24.dp))
                                .border(
                                    width = 1.dp,
                                    brush = Brush.linearGradient(
                                        listOf(LoginPrimaryBlue.copy(alpha = 0.6f), LoginGreenEmerald.copy(alpha = 0.4f))
                                    ),
                                    shape = RoundedCornerShape(24.dp)
                                )
                                .padding(horizontal = 24.dp, vertical = 16.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.inventory_taker_logo),
                                contentDescription = "Logo Inventory Taker",
                                modifier = Modifier
                                    .width(180.dp)
                                    .height(54.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Bienvenido de nuevo",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = LoginTextColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Gestiona tu inventario y ventas al instante",
                            fontSize = 13.sp,
                            color = LoginSubtextColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(750, delayMillis = 150)) + slideInVertically(
                        initialOffsetY = { 60 },
                        animationSpec = tween(750, delayMillis = 150, easing = FastOutSlowInEasing)
                    )
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .offset { IntOffset(shakeOffset.value.roundToInt(), 0) }
                            .border(1.dp, Color(0xFF1E293B), RoundedCornerShape(20.dp)),
                        colors = CardDefaults.cardColors(containerColor = LoginCardBg),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Correo electrónico", fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = LoginPrimaryBlue
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LoginPrimaryBlue,
                                    unfocusedBorderColor = Color(0xFF283548),
                                    focusedContainerColor = LoginDarkBg,
                                    unfocusedContainerColor = LoginDarkBg,
                                    focusedTextColor = LoginTextColor,
                                    unfocusedTextColor = LoginTextColor,
                                    focusedLabelColor = LoginPrimaryBlue,
                                    unfocusedLabelColor = LoginSubtextColor
                                )
                            )

                            OutlinedTextField(
                                value = password,
                                onValueChange = { password = it },
                                label = { Text("Contraseña", fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Lock,
                                        contentDescription = null,
                                        tint = LoginPrimaryBlue
                                    )
                                },
                                trailingIcon = {
                                    TextButton(
                                        onClick = { passwordVisible = !passwordVisible },
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Text(
                                            text = if (passwordVisible) "Ocultar" else "Ver",
                                            color = LoginSubtextColor,
                                            fontSize = 12.sp,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                    }
                                },
                                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = LoginPrimaryBlue,
                                    unfocusedBorderColor = Color(0xFF283548),
                                    focusedContainerColor = LoginDarkBg,
                                    unfocusedContainerColor = LoginDarkBg,
                                    focusedTextColor = LoginTextColor,
                                    unfocusedTextColor = LoginTextColor,
                                    focusedLabelColor = LoginPrimaryBlue,
                                    unfocusedLabelColor = LoginSubtextColor
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = {
                                    if (email.isBlank() || password.isBlank()) {
                                        scope.launch {
                                            triggerShake()
                                            snackbarHostState.showSnackbar("Completa correo y contraseña")
                                        }
                                        return@Button
                                    }

                                    scope.launch {
                                        isLoading = true
                                        val user = userDao.login(email.trim(), password)
                                        isLoading = false
                                        if (user != null) {
                                            onLoginSuccess()
                                        } else {
                                            triggerShake()
                                            snackbarHostState.showSnackbar("Credenciales incorrectas")
                                        }
                                    }
                                },
                                enabled = !isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = LoginPrimaryBlue,
                                    disabledContainerColor = LoginPrimaryBlue.copy(alpha = 0.5f)
                                )
                            ) {
                                if (isLoading) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(22.dp),
                                        color = Color.White,
                                        strokeWidth = 2.dp
                                    )
                                } else {
                                    Text(
                                        text = "Ingresar",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(800, delayMillis = 300))
                ) {
                    TextButton(onClick = onNavigateToRegister) {
                        Text(
                            text = "¿No tienes cuenta? ",
                            color = LoginSubtextColor,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Regístrate aquí",
                            color = LoginGreenEmerald,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}