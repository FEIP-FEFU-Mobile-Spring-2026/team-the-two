package com.example.myshop.data

import android.content.Context
import com.google.gson.Gson
import com.example.myshop.model.Category
import com.example.myshop.model.Product
import org.json.JSONObject

class ProductsRepository(private val context: Context) {

    private val gson = Gson()

    fun loadCategories(): List<Category> {
        val json = loadJsonFromAssets("products.json")
        val jsonObject = JSONObject(json)
        val categoriesArray = jsonObject.getJSONArray("categories")
        val categories = mutableListOf<Category>()
        for (i in 0 until categoriesArray.length()) {
            val category = gson.fromJson(categoriesArray.getString(i), Category::class.java)
            categories.add(category)
        }
        return categories
    }

    fun loadProducts(): List<Product> {
        val json = loadJsonFromAssets("products.json")
        val jsonObject = JSONObject(json)
        val itemsArray = jsonObject.getJSONArray("items")
        val products = mutableListOf<Product>()
        for (i in 0 until itemsArray.length()) {
            val product = gson.fromJson(itemsArray.getString(i), Product::class.java)
            products.add(product)
        }
        return products
    }

    private fun loadJsonFromAssets(filename: String): String {
        return context.assets.open(filename).bufferedReader().use { it.readText() }
    }
}
