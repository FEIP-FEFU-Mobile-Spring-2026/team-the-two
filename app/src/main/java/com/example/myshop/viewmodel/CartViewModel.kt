package com.example.myshop.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myshop.data.CartRepository
import com.example.myshop.model.CartItem
import kotlinx.coroutines.launch

class CartViewModel(private val repository: CartRepository) : ViewModel() {

    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _totalPrice = MutableLiveData<Int>()
    val totalPrice: LiveData<Int> = _totalPrice

    private val _itemsCount = MutableLiveData<Int>()
    val itemsCount: LiveData<Int> = _itemsCount

    private var orderComment: String = ""

    init {
        loadCart()
    }

    fun loadCart() {
        viewModelScope.launch {
            _cartItems.value = repository.getCartItems()
            _totalPrice.value = repository.getTotalPrice()
            _itemsCount.value = repository.getItemsCount()
        }
    }

    fun addToCart(item: CartItem) {
        viewModelScope.launch {
            repository.addItem(item)
            loadCart()
        }
    }

    fun removeItem(productId: String, sizeId: String) {
        viewModelScope.launch {
            repository.removeItem(productId, sizeId)
            loadCart()
        }
    }

    fun updateQuantity(productId: String, sizeId: String, quantity: Int) {
        viewModelScope.launch {
            repository.updateQuantity(productId, sizeId, quantity)
            loadCart()
        }
    }

    fun clearCart() {
        viewModelScope.launch {
            repository.clearCart()
            loadCart()
        }
    }

    fun setOrderComment(comment: String) {
        orderComment = comment
    }

    fun getOrderComment(): String = orderComment
}