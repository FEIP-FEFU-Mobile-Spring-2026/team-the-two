package com.example.myshop

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myshop.data.CartRepository
import com.example.myshop.ui.CartAdapter
import com.example.myshop.viewmodel.CartViewModel

class CartActivity : AppCompatActivity() {

    private lateinit var viewModel: CartViewModel
    private lateinit var adapter: CartAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var totalPriceText: TextView
    private lateinit var clearCartButton: Button
    private lateinit var checkoutButton: Button
    private lateinit var emptyCartContainer: LinearLayout
    private lateinit var orderComment: EditText
    private lateinit var userEmail: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        supportActionBar?.title = "Корзина"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val repository = CartRepository(this)
        viewModel = ViewModelProvider(this).get(CartViewModel::class.java)
        viewModel.loadCart()

        recyclerView = findViewById(R.id.cartRecyclerView)
        totalPriceText = findViewById(R.id.totalPriceText)
        clearCartButton = findViewById(R.id.clearCartButton)
        checkoutButton = findViewById(R.id.checkoutButton)
        emptyCartContainer = findViewById(R.id.emptyCartContainer)
        orderComment = findViewById(R.id.orderComment)
        userEmail = findViewById(R.id.userEmail)

        // TODO: Получить реальный email пользователя
        userEmail.text = "anton@gmail.com"

        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CartAdapter(
            emptyList(),
            onQuantityChange = { productId, sizeId, newQuantity ->
                viewModel.updateQuantity(productId, sizeId, newQuantity)
            },
            onDelete = { productId, sizeId ->
                viewModel.removeItem(productId, sizeId)
            }
        )
        recyclerView.adapter = adapter

        viewModel.cartItems.observe(this) { items ->
            adapter.updateItems(items)
            if (items.isEmpty()) {
                recyclerView.visibility = View.GONE
                emptyCartContainer.visibility = View.VISIBLE
                checkoutButton.isEnabled = false
            } else {
                recyclerView.visibility = View.VISIBLE
                emptyCartContainer.visibility = View.GONE
                checkoutButton.isEnabled = true
            }
        }

        viewModel.totalPrice.observe(this) { total ->
            val rubles = total / 100
            totalPriceText.text = "$rubles ₽"
        }

        clearCartButton.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Очистка корзины")
                .setMessage("Вы уверены, что хотите удалить все товары из корзины?")
                .setPositiveButton("Да") { _, _ ->
                    viewModel.clearCart()
                    Toast.makeText(this, "Корзина очищена", Toast.LENGTH_SHORT).show()
                }
                .setNegativeButton("Нет", null)
                .show()
        }

        checkoutButton.setOnClickListener {
            val comment = orderComment.text.toString()
            viewModel.setOrderComment(comment)
            val intent = Intent(this, OrderSuccessActivity::class.java)
            startActivity(intent)
            viewModel.clearCart()
            finish()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}