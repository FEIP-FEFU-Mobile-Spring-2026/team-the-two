package com.example.myshop.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myshop.R
import com.example.myshop.model.Product

class CatalogAdapter(
    private var products: List<Product>,
    private val onItemClick: (Product) -> Unit
) : RecyclerView.Adapter<CatalogAdapter.ProductViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = products[position]
        holder.bind(product)
        holder.itemView.setOnClickListener { onItemClick(product) }
    }

    override fun getItemCount() = products.size

    fun updateProducts(newProducts: List<Product>) {
        println("DEBUG: адаптер обновлён, товаров = ${newProducts.size}")
        products = newProducts
        notifyDataSetChanged()
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageView: ImageView = itemView.findViewById(R.id.productImage)
        private val nameView: TextView = itemView.findViewById(R.id.productName)
        private val priceView: TextView = itemView.findViewById(R.id.productPrice)

        fun bind(product: Product) {
            nameView.text = product.name

            // Перевод копеек в рубли
            val rubles = product.priceInKopecks / 100
            priceView.text = "$rubles руб."

            // Загрузка картинки через Glide
            Glide.with(itemView.context)
                .load(product.imageUrl)
                .into(imageView)
        }
    }
}