package com.example.periody.auth

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController

@Composable
fun SplashScreen(
    navController: NavController,
    viewModel: AuthViewModel
) {
    val state by viewModel.state.collectAsState()
    var sessionChecked by remember { mutableStateOf(false) }

    LaunchedEffect(true) {
        viewModel.checkSession()
        sessionChecked = true
    }

    LaunchedEffect(sessionChecked, state.isAuthenticated, state.isLoading) {

        if (!sessionChecked || state.isLoading) return@LaunchedEffect

        if (state.isAuthenticated) {
            navController.navigate("home") {
                popUpTo(0)
            }
        } else {
            navController.navigate("login") {
                popUpTo(0)
            }
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
