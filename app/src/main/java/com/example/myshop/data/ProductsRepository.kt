package com.example.myshop.data

import android.content.Context

class ProductsRepository(private val context: Context) {

    companion object {
        private const val TOKEN = "Bearer Cmt7wdwFgDIi1_SRX8hlJIExs0jJKPr4axflLpExAxM"
    }

    // Исключения НЕ перехватываются — они идут в ViewModel
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