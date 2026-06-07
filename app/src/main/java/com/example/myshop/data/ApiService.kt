package com.example.myshop.data

import retrofit2.http.GET
import retrofit2.http.Header

interface ApiService {
    @GET("catalog")
    suspend fun getCatalog(
        @Header("Authorization") token: String
    ): CatalogResponse
}