package com.example.myshop.data

import android.content.Context
import android.content.SharedPreferences
import com.example.myshop.model.CartItem
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartRepository(context: Context) {

    private val prefs: SharedPreferences = context.getSharedPreferences("cart_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun getCartItems(): List<CartItem> {
        val json = prefs.getString("cart_items", "[]")
        val type = object : TypeToken<List<CartItem>>() {}.type
        return gson.fromJson(json, type)
    }

    private fun saveCartItems(items: List<CartItem>) {
        val json = gson.toJson(items)
        prefs.edit().putString("cart_items", json).apply()
    }

    fun addItem(item: CartItem) {
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

    fun removeItem(productId: String, sizeId: String) {
        val currentItems = getCartItems().toMutableList()
        currentItems.removeAll { it.productId == productId && it.sizeId == sizeId }
        saveCartItems(currentItems)
    }

    fun updateQuantity(productId: String, sizeId: String, newQuantity: Int) {
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

    fun getTotalPrice(): Int {
        return getCartItems().sumOf { it.priceInKopecks * it.quantity }
    }

    fun getItemsCount(): Int {
        return getCartItems().sumOf { it.quantity }
    }
}