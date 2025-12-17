package com.example.periody.profile

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.periody.auth.AuthViewModel
import com.example.periody.navigation.Routes

@Composable
fun ProfileScreen(
    navController: NavController,
    authViewModel: AuthViewModel,
) {
    val authState by authViewModel.state.collectAsState()
    val user = authState.currentUser
    val scroll = rememberScrollState()

    LaunchedEffect(Unit) {
        val userId = authState.currentUser?.id
        if (userId != null) {
            authViewModel.loadUser(userId)
        }
    }

    Scaffold { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(scroll)
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Box(contentAlignment = Alignment.BottomEnd) {
                Image(
                    painter = rememberAsyncImagePainter(user?.profileImageUrl ?: ""),
                    contentDescription = "Foto Profil",
                    modifier = Modifier
                        .size(130.dp)
                        .clip(CircleShape)
                        .background(Color.LightGray)
                )

                IconButton(
                    onClick = { navController.navigate(Routes.EDIT_PROFILE) },
                    modifier = Modifier
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Edit Foto",
                        tint = Color.White
                    )
                }
            }

            Spacer(Modifier.height(20.dp))

            Text(
                "Informasi Akun",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(Modifier.height(16.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProfileItem("Email", user?.email ?: "-")
                    ProfileItem("Username", user?.username ?: "-")
                    ProfileItem("Full Name", user?.name ?: "-")
                    ProfileItem("Phone", user?.phone ?: "-")
                    ProfileItem("Address", user?.address ?: "-")
                }
            }

            Spacer(Modifier.height(20.dp))

            Button(
                onClick = { navController.navigate(Routes.EDIT_PROFILE) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Edit Profile")
            }

            OutlinedButton(
                onClick = { authViewModel.onEvent(AuthViewModel.AuthEvent.Logout) },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Logout")
            }
        }
    }
}
