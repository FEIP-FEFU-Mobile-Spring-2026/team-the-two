package com.example.myshop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

class CartActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val textView = TextView(this)
        textView.text = "Корзина пока пуста"
        textView.textSize = 24f
        setContentView(textView)
    }
}
