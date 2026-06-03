package com.example.myshop.model

data class Product(
    val id: String,
    val name: String,
    val shortDescription: String,
    val longDescription: String,
    val priceInKopecks: Int,
    val imageUrl: String,
    val tags: List<String>,
    val sizes: List<Size>,
    val categoryId: String,
    val material: String,
    val weight: String,
    val season: String,
    val countryOfOrigin: String
) : java.io.Serializable

