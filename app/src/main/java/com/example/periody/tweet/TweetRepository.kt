package com.example.periody.tweet

import com.example.periody.model.Tweet
import com.example.periody.supabase.SupabaseProvider
import io.github.jan.supabase.postgrest.from

class TweetRepository {

    private val client = SupabaseProvider.client

    suspend fun getAll(userId: String): List<Tweet> {
        return client.from("tweet")
            .select {
                filter {
                    eq("user_id", userId)
                }
            }
            .decodeList<Tweet>()
    }

    suspend fun getById(id: String): Tweet? {
        val result = client.from("tweet")
            .select {
                filter {
                    eq("id", id)
                }
            }
            .decodeList<Tweet>()

        return result.firstOrNull()
    }

    suspend fun insert(tweet: Tweet) {
        client.from("tweet").insert(tweet)
    }

    suspend fun update(id: String, tweet: Tweet) {
        client.from("tweet")
            .update(tweet) {
                filter {
                    eq("id", id)
                }
            }
    }

    suspend fun delete(id: String) {
        client.from("tweet")
            .delete {
                filter {
                    eq("id", id)
                }
            }
    }
}
