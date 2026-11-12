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
import androidx.compose.material.icons.filled.Person
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
import com.mariogc55.retrowave.tiendabarrioinventory.model.UserEntity
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

private val RegisterDarkBg = Color(0xFF0B0F17)
private val RegisterCardBg = Color(0xFF151C28)
private val RegisterPrimaryBlue = Color(0xFF3B82F6)
private val RegisterGreenEmerald = Color(0xFF10B981)
private val RegisterTextColor = Color(0xFFF1F5F9)
private val RegisterSubtextColor = Color(0xFF94A3B8)

@Composable
fun RegisterScreen(
    userDao: UserDao,
    onRegisterSuccess: () -> Unit,
    onBackToLogin: () -> Unit
) {
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }
    var visible by remember { mutableStateOf(false) }

    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    val shakeOffset = remember { Animatable(0f) }

    val infiniteTransition = rememberInfiniteTransition(label = "logoPulseReg")
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
        containerColor = RegisterDarkBg,
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
                            RegisterDarkBg,
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
                                RegisterGreenEmerald.copy(alpha = 0.15f),
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
                                .background(RegisterCardBg, RoundedCornerShape(24.dp))
                                .border(
                                    width = 1.dp,
                                    brush = Brush.linearGradient(
                                        listOf(RegisterGreenEmerald.copy(alpha = 0.5f), RegisterPrimaryBlue.copy(alpha = 0.4f))
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
                                    .width(160.dp)
                                    .height(48.dp),
                                contentScale = ContentScale.Fit
                            )
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        Text(
                            text = "Crear Cuenta",
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = RegisterTextColor
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Únete y comienza a administrar tu tienda",
                            fontSize = 13.sp,
                            color = RegisterSubtextColor
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

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
                        colors = CardDefaults.cardColors(containerColor = RegisterCardBg),
                        shape = RoundedCornerShape(20.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            OutlinedTextField(
                                value = username,
                                onValueChange = { username = it },
                                label = { Text("Usuario", fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = null,
                                        tint = RegisterGreenEmerald
                                    )
                                },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = RegisterGreenEmerald,
                                    unfocusedBorderColor = Color(0xFF283548),
                                    focusedContainerColor = RegisterDarkBg,
                                    unfocusedContainerColor = RegisterDarkBg,
                                    focusedTextColor = RegisterTextColor,
                                    unfocusedTextColor = RegisterTextColor,
                                    focusedLabelColor = RegisterGreenEmerald,
                                    unfocusedLabelColor = RegisterSubtextColor
                                )
                            )

                            OutlinedTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = { Text("Correo electrónico", fontSize = 13.sp) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.Email,
                                        contentDescription = null,
                                        tint = RegisterGreenEmerald
                                    )
                                },
                                singleLine = true,
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = RegisterGreenEmerald,
                                    unfocusedBorderColor = Color(0xFF283548),
                                    focusedContainerColor = RegisterDarkBg,
                                    unfocusedContainerColor = RegisterDarkBg,
                                    focusedTextColor = RegisterTextColor,
                                    unfocusedTextColor = RegisterTextColor,
                                    focusedLabelColor = RegisterGreenEmerald,
                                    unfocusedLabelColor = RegisterSubtextColor
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
                                        tint = RegisterGreenEmerald
                                    )
                                },
                                trailingIcon = {
                                    TextButton(
                                        onClick = { passwordVisible = !passwordVisible },
                                        contentPadding = PaddingValues(horizontal = 8.dp)
                                    ) {
                                        Text(
                                            text = if (passwordVisible) "Ocultar" else "Ver",
                                            color = RegisterSubtextColor,
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
                                    focusedBorderColor = RegisterGreenEmerald,
                                    unfocusedBorderColor = Color(0xFF283548),
                                    focusedContainerColor = RegisterDarkBg,
                                    unfocusedContainerColor = RegisterDarkBg,
                                    focusedTextColor = RegisterTextColor,
                                    unfocusedTextColor = RegisterTextColor,
                                    focusedLabelColor = RegisterGreenEmerald,
                                    unfocusedLabelColor = RegisterSubtextColor
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Button(
                                onClick = {
                                    if (username.isBlank() || email.isBlank() || password.isBlank()) {
                                        scope.launch {
                                            triggerShake()
                                            snackbarHostState.showSnackbar("Por favor completa todos los campos")
                                        }
                                        return@Button
                                    }

                                    scope.launch {
                                        isLoading = true
                                        val newUser = UserEntity(email = email.trim(), password = password)
                                        userDao.insertUser(newUser)
                                        isLoading = false
                                        onRegisterSuccess()
                                    }
                                },
                                enabled = !isLoading,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .clip(RoundedCornerShape(12.dp)),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = RegisterGreenEmerald,
                                    disabledContainerColor = RegisterGreenEmerald.copy(alpha = 0.5f)
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
                                        text = "Registrarse y Acceder",
                                        color = Color.White,
                                        fontSize = 15.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))

                AnimatedVisibility(
                    visible = visible,
                    enter = fadeIn(tween(800, delayMillis = 300))
                ) {
                    TextButton(onClick = onBackToLogin) {
                        Text(
                            text = "¿Ya tienes una cuenta? ",
                            color = RegisterSubtextColor,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "Inicia sesión",
                            color = RegisterPrimaryBlue,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}