package com.example.periody.tweet

import com.example.periody.model.Tweet

data class TweetState(
    val list: List<Tweet> = emptyList(),
    val selected: Tweet? = null,
    val loading: Boolean = false,
    val error: String? = null
)
