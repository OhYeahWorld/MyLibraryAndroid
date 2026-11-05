package com.example.mylibrary.model

data class Book(
    val id: Long = 0L,
    val title: String,
    val author: String,
    val year: Int?,
    val genre: String,
    val isRead: Boolean = false
)
