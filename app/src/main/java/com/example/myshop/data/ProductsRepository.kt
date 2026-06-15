package com.example.myshop.data

import android.content.Context
import com.example.myshop.model.Category   // ← добавить
import com.example.myshop.model.Product   // ← добавить

class ProductsRepository(private val context: Context) {

    companion object {
        private const val TOKEN = "Bearer Cmt7wdwFgDIi1_SRX8hlJIExs0jJKPr4axflLpExAxM"
    }

    suspend fun loadCatalog(): CatalogResponse {
        return RetrofitClient.apiService.getCatalog(TOKEN)
    }

    suspend fun loadCategories(): List<Category> {
        return loadCatalog().categories
    }

    suspend fun loadProducts(): List<Product> {
        return loadCatalog().items
    }
}