package com.example.myshop.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshop.data.ProductsRepository
import com.example.myshop.model.Product
import kotlinx.coroutines.launch

class CatalogViewModel(private val repository: ProductsRepository) : ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products

    private val _categories = MutableLiveData<List<String>>()
    val categories: LiveData<List<String>> = _categories

    private val _selectedCategory = MutableLiveData<String>()
    val selectedCategory: LiveData<String> = _selectedCategory

    private val _filteredProducts = MutableLiveData<List<Product>>()
    val filteredProducts: LiveData<List<Product>> = _filteredProducts

    private var allProducts = listOf<Product>()

    init {
        loadData()
    }

    private fun loadData() {
        _isLoading.value = true
        _errorMessage.value = null
        viewModelScope.launch {
            try {
                val productsList = repository.loadProducts()
                allProducts = productsList

                val categoriesList = repository.loadCategories()
                categoryIdMap = categoriesList.associate { it.name to it.id }
                allCategoryNames = listOf("Новинки") + categoriesList.map { it.name }
                _categories.value = allCategoryNames

                _selectedCategory.value = allCategoryNames[0]
                filterProducts(allCategoryNames[0])
            } catch (e: Exception) {
                e.printStackTrace()
                _errorMessage.value = "Ошибка загрузки данных. Проверьте интернет."
                _filteredProducts.value = emptyList()
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun selectCategory(categoryName: String) {
        _selectedCategory.value = categoryName
        filterProducts(categoryName)
    }

    private fun filterProducts(categoryName: String) {
        val filtered = if (categoryName == "Новинки") {
            allProducts.filter { it.tags.contains("New") }
        } else {
            val categoryId = repository.loadCategories().find { it.name == categoryName }?.id
            allProducts.filter { it.categoryId == categoryId }
        }
        println("DEBUG: отфильтровано товаров = ${filtered.size} для категории $categoryName")
        _filteredProducts.value = filtered
    }
    fun retryLoad() {
        loadData()
    }
}