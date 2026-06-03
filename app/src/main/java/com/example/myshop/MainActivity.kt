package com.example.myshop

import android.os.Bundle
import android.widget.Button /// добавление Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.content.Intent /// добавление Intent

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        /// переход к активности просмотра товара
        val buttonToProductDetails: Button = findViewById<Button>(R.id.buttonToProductDetails)

        buttonToProductDetails.setOnClickListener {
            val intent = Intent(this, ProductDetailsActivity::class.java)
            startActivity(intent)
        }
        ///
    }
}