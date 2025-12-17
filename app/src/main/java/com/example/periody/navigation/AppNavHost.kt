package com.example.periody.navigation

import android.annotation.SuppressLint
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.periody.artikel.*
import com.example.periody.auth.*
import com.example.periody.catatan.*
import com.example.periody.grafik.GrafikScreen
import com.example.periody.grafik.GrafikViewModel
import com.example.periody.home.HomeScreen
import com.example.periody.profile.EditProfileScreen
import com.example.periody.profile.ProfileScreen
import com.example.periody.reminder.*
import com.example.periody.tweet.TweetListScreen
import com.example.periody.tweet.TweetFormScreen
import com.example.periody.tweet.TweetViewModel

@RequiresApi(Build.VERSION_CODES.O)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AppNavHost() {

    val navController = rememberNavController()

    val authViewModel: AuthViewModel = viewModel()
    val grafikViewModel: GrafikViewModel = viewModel()
    val artikelViewModel: ArtikelViewModel = viewModel()
    val tweetViewModel: TweetViewModel = viewModel()

    val catatanRepository = remember { CatatanRepository() }
    val catatanViewModel: CatatanViewModel = viewModel(
        factory = CatatanViewModelFactory(catatanRepository)
    )

    val reminderRepository = remember { ReminderRepository() }
    val reminderViewModel: ReminderViewModel = viewModel(
        factory = ReminderViewModelFactory(reminderRepository)
    )

    val authState by authViewModel.state.collectAsState()

    LaunchedEffect(authState.isAuthenticated) {
        if (!authState.isAuthenticated) {
            navController.navigate(Routes.LOGIN) {
                popUpTo(0)
                launchSingleTop = true
            }
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            SplashScreen(navController, authViewModel)
        }

        composable(Routes.LOGIN) {
            LoginScreen(navController, authViewModel)
        }

        composable(Routes.REGISTER) {
            RegisterScreen(navController, authViewModel)
        }

        composable(Routes.HOME) {
            if (authState.isAuthenticated) {

                val userId = authState.currentUser?.id ?: ""

                HomeScreen(
                    navController = navController,
                    catatanViewModel = catatanViewModel,
                    grafikViewModel = grafikViewModel,
                    reminderViewModel = reminderViewModel,
                    artikelViewModel = artikelViewModel,
                    tweetViewModel = tweetViewModel
                )

                LaunchedEffect(userId) {
                    if (userId.isNotEmpty()) {
                        catatanViewModel.loadAll(userId)
                        grafikViewModel.loadData(userId)
                        reminderViewModel.load(userId)
                        tweetViewModel.loadTweets(userId)
                    }
                }
            }
        }

        composable(Routes.PROFILE) {
            ProfileScreen(navController, authViewModel)
        }

        composable(Routes.EDIT_PROFILE) {
            EditProfileScreen(navController, authViewModel)
        }

        composable("artikel") {
            ArtikelListScreen(navController, artikelViewModel)
        }

        composable("artikel_form") {
            val userId = authState.currentUser?.id ?: ""
            ArtikelFormScreen(navController, artikelViewModel, userId)
        }

        composable("artikel_detail/{id}") { backStack ->
            val id = backStack.arguments?.getString("id") ?: return@composable
            ArtikelDetailScreen(navController, artikelViewModel, id)
        }

        composable("artikel_edit/{id}") { backStack ->
            val id = backStack.arguments?.getString("id") ?: return@composable
            val userId = authState.currentUser?.id ?: ""
            ArtikelFormScreen(navController, artikelViewModel, userId, existingId = id)
        }

        composable("grafik") {
            GrafikScreen(
                authViewModel = authViewModel,
                viewModel = grafikViewModel
            )
        }

        composable("catatan") {
            val userId = authState.currentUser?.id ?: ""
            CatatanListScreen(navController, catatanViewModel, userId)
        }

        composable("catatan_form") {
            val userId = authState.currentUser?.id ?: ""
            CatatanFormScreen(navController, catatanViewModel, "tambah", null, userId)
        }

        composable("catatan_detail/{id}") { backStack ->
            val id = backStack.arguments?.getString("id") ?: return@composable
            CatatanDetailScreen(navController, catatanViewModel, id)
        }

        composable("catatan_edit/{id}") { backStack ->
            val id = backStack.arguments?.getString("id") ?: return@composable
            val userId = authState.currentUser?.id ?: ""
            CatatanFormScreen(navController, catatanViewModel, "edit", id, userId)
        }

        composable(Routes.REMINDER) {
            ReminderListScreen(navController, reminderViewModel)
        }

        composable("reminder_form") {
            val userId = authState.currentUser?.id ?: ""
            ReminderFormScreen(navController, reminderViewModel, "tambah", null, userId)
        }

        composable("reminder_detail/{id}") { backStack ->
            val id = backStack.arguments?.getString("id") ?: return@composable
            ReminderDetailScreen(navController, reminderViewModel, id)
        }

        composable("reminder_edit/{id}") { backStack ->
            val id = backStack.arguments?.getString("id") ?: return@composable
            val userId = authState.currentUser?.id ?: ""
            ReminderFormScreen(navController, reminderViewModel, "edit", id, userId)
        }

        composable(Routes.TWEET) {
            TweetListScreen(navController, tweetViewModel)
        }

        composable("tweet_form") {
            val userId = authState.currentUser?.id ?: ""
            TweetFormScreen(navController, tweetViewModel, "tambah", null, userId)
        }

        composable("tweet_edit/{id}") { backStack ->
            val id = backStack.arguments?.getString("id") ?: return@composable
            val userId = authState.currentUser?.id ?: ""
            TweetFormScreen(navController, tweetViewModel, "edit", id, userId)
        }
    }
}
