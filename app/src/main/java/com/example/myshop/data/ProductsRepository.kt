package com.example.myshop.data

import android.content.Context
import com.example.myshop.model.Category
import com.example.myshop.model.Product

class ProductsRepository(private val context: Context) {

    companion object {
        private const val TOKEN = "Bearer Cmt7wdwFgDIi1_SRX8hlJIExs0jJKPr4axflLpExAxM"
    }

    suspend fun loadCategories(): List<Category> {
        return try {
            val response = RetrofitClient.apiService.getCatalog(TOKEN)
            response.categories
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    suspend fun loadProducts(): List<Product> {
        return try {
            val response = RetrofitClient.apiService.getCatalog(TOKEN)
            response.items
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }
}