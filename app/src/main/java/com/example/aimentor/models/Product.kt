package com.example.aimentor.models

// Khớp với JSON của https://fakestoreapi.com/products (tên field phải trùng key JSON)
data class Product(
    val id: Int,
    val title: String,
    val price: Double,
    val description: String,
    val category: String,
    val image: String,
    val rating: Rating
)

data class Rating(
    val rate: Double,
    val count: Int
)
