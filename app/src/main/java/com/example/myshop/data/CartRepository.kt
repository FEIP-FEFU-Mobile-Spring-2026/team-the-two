package com.example.myshop.data

import android.content.Context
import android.content.SharedPreferences
import com.example.myshop.model.CartItem
import com.example.myshop.model.CartSavedItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartRepository(context: Context, private val productsRepository: ProductsRepository) {

    private val prefs: SharedPreferences = context.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private fun getSavedItems(): List<CartSavedItem> {
        val json = prefs.getString("cart_items", "[]")?: "[]"
        val type = object : TypeToken<List<CartSavedItem>>() {}.type
        return gson.fromJson(json, type)
    }

    suspend fun getCartItems(): List<CartItem> {
//        val json = prefs.getString("cart_items", "[]")
//        val type = object : TypeToken<List<CartItem>>() {}.type
//        return gson.fromJson(json, type)

        val savedItems = getSavedItems()
        if (savedItems.isEmpty()) return emptyList()

        val latestProducts = try {
            productsRepository.loadProducts()
        } catch (e: Exception){
            emptyList()
        }

        return savedItems.mapNotNull {saved ->
            val product = latestProducts.find {it.id == saved.productId}
            val size = product?.sizes?.find {it.id == saved.sizeId}

            if (product != null && size != null){
                CartItem(
                    productId = saved.productId,
                    sizeId = saved.sizeId,
                    quantity = saved.quantity,
                    productName = product.name,
                    sizeName = size.name,
                    priceInKopecks = product.priceInKopecks,
                    imageUrl = product.imageUrl
                )
            }else{
                null
            }
        }
    }

    private fun saveCartItems(items: List<CartItem>) {
        val itemsToSave = items.map {
            CartSavedItem(
                productId = it.productId,
                sizeId = it.sizeId,
                quantity = it.quantity
            )
        }
        val json = gson.toJson(itemsToSave)
        prefs.edit().putString("cart_items", json).apply()
    }

    suspend fun addItem(item: CartItem) {
        val currentItems = getCartItems().toMutableList()
        val existingIndex = currentItems.indexOfFirst {
            it.productId == item.productId && it.sizeId == item.sizeId
        }
        if (existingIndex != -1) {
            val existing = currentItems[existingIndex]
            currentItems[existingIndex] = existing.copy(quantity = existing.quantity + 1)
        } else {
            currentItems.add(item)
        }
        saveCartItems(currentItems)
    }

    suspend fun removeItem(productId: String, sizeId: String) {
        val currentItems = getCartItems().toMutableList()
        currentItems.removeAll { it.productId == productId && it.sizeId == sizeId }
        saveCartItems(currentItems)
    }

    suspend fun updateQuantity(productId: String, sizeId: String, newQuantity: Int) {
        if (newQuantity <= 0) {
            removeItem(productId, sizeId)
            return
        }
        val currentItems = getCartItems().toMutableList()
        val index = currentItems.indexOfFirst { it.productId == productId && it.sizeId == sizeId }
        if (index != -1) {
            val item = currentItems[index]
            currentItems[index] = item.copy(quantity = newQuantity)
            saveCartItems(currentItems)
        }
    }

    fun clearCart() {
        saveCartItems(emptyList())
    }

    suspend fun getTotalPrice(): Int {
        return getCartItems().sumOf { it.priceInKopecks * it.quantity }
    }

    suspend fun getItemsCount(): Int {
        return getCartItems().sumOf { it.quantity }
    }
}