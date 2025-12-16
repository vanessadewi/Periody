package com.example.periody.tweet

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.periody.model.Tweet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TweetListScreen(
    navController: NavController,
    viewModel: TweetViewModel
) {
    val state by viewModel.state.collectAsState()
    val list = state.list

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Tweet", style = MaterialTheme.typography.titleLarge) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { navController.navigate("tweet_form") }
            ) {
                Text("+", style = MaterialTheme.typography.titleMedium)
            }
        }
    ) { padding ->

        if (list.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Text("Belum ada tweet", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(list) { tweet ->
                    TweetCard(
                        tweet = tweet,
                        onClick = { navController.navigate("tweet_edit/${tweet.id}") }
                    )
                }
            }
        }
    }
}
