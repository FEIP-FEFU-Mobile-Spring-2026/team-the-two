package com.example.myshop.data

import android.content.Context
import com.example.myshop.model.Category
import com.example.myshop.model.Product

class ProductsRepository(private val context: Context) {

    companion object {
        private const val TOKEN = "Bearer Cmt7wdwFgDIi1_SRX8hlJIExs0jJKPr4axflLpExAxM"
    }

    // Единый метод для загрузки всего каталога
    suspend fun loadCatalog(): CatalogResponse {
        return try {
            RetrofitClient.apiService.getCatalog(TOKEN)
        } catch (e: Exception) {
            e.printStackTrace()
            CatalogResponse(emptyList(), emptyList())
        }
    }

    // Для обратной совместимости с ViewModel
    suspend fun loadCategories(): List<Category> {
        return loadCatalog().categories
    }

    suspend fun loadProducts(): List<Product> {
        return loadCatalog().items
    }
}