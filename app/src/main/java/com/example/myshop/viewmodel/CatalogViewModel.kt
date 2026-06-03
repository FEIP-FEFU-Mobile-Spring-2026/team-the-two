package com.example.myshop.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshop.data.ProductsRepository
import com.example.myshop.model.Product
import kotlinx.coroutines.launch

class CatalogViewModel(private val repository: ProductsRepository) : ViewModel() {

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
        viewModelScope.launch {
            val productsList = repository.loadProducts()
            println("DEBUG: Загружено товаров = ${productsList.size}")
            allProducts = productsList
            _products.value = productsList

            val categoriesList = mutableListOf("Новинки")
            repository.loadCategories().forEach { category ->
                categoriesList.add(category.name)
            }
            _categories.value = categoriesList
            println("DEBUG: Загружено категорий = ${categoriesList.size}")

            _selectedCategory.value = categoriesList[0]
            filterProducts(categoriesList[0])
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
}