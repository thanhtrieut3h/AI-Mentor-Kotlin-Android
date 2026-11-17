package com.example.aimentor.api

import com.example.aimentor.models.Product
import retrofit2.http.GET

interface ApiService {

    // GET https://fakestoreapi.com/products
    @GET("products")
    suspend fun getAllProducts(): List<Product>
}
