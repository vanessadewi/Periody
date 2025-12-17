package com.example.periody.catatan

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CatatanListScreen(
    navController: NavController,
    viewModel: CatatanViewModel,
    userId: String
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(userId) {
        if (userId.isNotEmpty()) {
            viewModel.loadAll(userId)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Catatan") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    navController.navigate("catatan_form")
                }
            ) {
                Text("+")
            }
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            if (state.loading) {
                CircularProgressIndicator()
                return@Column
            }

            state.error?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error
                )
            }

            if (state.list.isEmpty()) {
                Text("Belum ada catatan")
                return@Column
            }

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.list) { catatan ->
                    CatatanCard(
                        catatan = catatan,
                        onClick = {
                            navController.navigate("catatan_detail/${catatan.id}")
                        }
                    )
                }
            }
        }
    }
}