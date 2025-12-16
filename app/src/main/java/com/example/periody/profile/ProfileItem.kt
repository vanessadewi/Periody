package com.example.periody.profile

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ProfileItem(
    label: String,
    value: String?,
    onCopy: ((String) -> Unit)? = null
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary
                )
                Text(
                    text = value ?: "-",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            if (value != null && onCopy != null) {
                TextButton(onClick = { onCopy(value) }) {
                    Text("Copy")
                }
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}
