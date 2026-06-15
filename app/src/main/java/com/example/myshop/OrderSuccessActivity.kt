package com.example.myshop

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class OrderSuccessActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_success)

        findViewById<Button>(R.id.backToHomeButton).setOnClickListener {
            startActivity(Intent(this, CatalogActivity::class.java))
            finish()
        }
    }
}