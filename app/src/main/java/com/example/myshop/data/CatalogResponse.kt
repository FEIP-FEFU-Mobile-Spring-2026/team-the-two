package com.example.myshop.data

import com.example.myshop.model.Category
import com.example.myshop.model.Product

data class CatalogResponse(
    val categories: List<Category>,
    val items: List<Product>
)
