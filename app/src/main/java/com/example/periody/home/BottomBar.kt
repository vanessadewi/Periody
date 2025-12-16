package com.example.periody.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomBar(navController: NavController) {

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Artikel,
        BottomNavItem.Grafik,
        BottomNavItem.Tweet,
        BottomNavItem.Profile
    )

    NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.route,
                onClick = {
                    navController.navigate(item.route) {
                        popUpTo("home") { inclusive = false }
                        launchSingleTop = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.title) },
                label = { Text(item.title) }
            )
        }
    }
}

sealed class BottomNavItem(
    val route: String,
    val title: String,
    val icon: ImageVector
) {
    object Home : BottomNavItem("home", "Home", Icons.Default.Home)

    // ✅ langsung ke ArtikelListScreen
    object Artikel : BottomNavItem("artikel", "Artikel", Icons.Default.Edit)

    // ✅ langsung ke Grafik Dashboard (bukan grafik/{userId})
    object Grafik : BottomNavItem("grafik", "Grafik", Icons.Default.Menu)

    // ✅ langsung ke TweetListScreen
    object Tweet : BottomNavItem("tweet", "Tweet", Icons.Default.MailOutline)

    // ✅ langsung ke ProfileScreen
    object Profile : BottomNavItem("profile", "Profile", Icons.Default.Person)
}
