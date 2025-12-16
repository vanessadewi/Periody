package com.example.periody.tweet

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.periody.model.Tweet

@Composable
fun TweetCard(
    tweet: Tweet,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(tweet.konten, style = MaterialTheme.typography.bodyLarge)

            Spacer(Modifier.height(8.dp))

            if (!tweet.gambar_url.isNullOrEmpty()) {
                AsyncImage(
                    model = tweet.gambar_url,
                    contentDescription = "Tweet Image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Spacer(Modifier.height(8.dp))

            Text(
                text = tweet.created_at ?: "-",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
