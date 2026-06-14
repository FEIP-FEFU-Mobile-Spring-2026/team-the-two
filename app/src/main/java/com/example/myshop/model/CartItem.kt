package com.example.myshop.model

import java.io.Serializable

data class CartItem(
    val productId: String,
    val productName: String,
    val sizeId: String,
    val sizeName: String,
    val priceInKopecks: Int,
    var quantity: Int,
    val imageUrl: String
) : Serializable