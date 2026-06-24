package com.example.myshop.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.myshop.R
import com.example.myshop.model.CartItem

class CartAdapter(
    private var items: List<CartItem>,
    private val onQuantityChange: (productId: String, sizeId: String, newQuantity: Int) -> Unit,
    private val onDelete: (productId: String, sizeId: String) -> Unit
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_cart, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    fun updateItems(newItems: List<CartItem>) {
        items = newItems
        notifyDataSetChanged()
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val productImage: ImageView = itemView.findViewById(R.id.productImage)
        private val productName: TextView = itemView.findViewById(R.id.productName)
        private val productSize: TextView = itemView.findViewById(R.id.productSize)
        private val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        private val quantityText: TextView = itemView.findViewById(R.id.quantityText)
        private val decreaseButton: ImageButton = itemView.findViewById(R.id.decreaseButton)
        private val increaseButton: ImageButton = itemView.findViewById(R.id.increaseButton)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        fun bind(item: CartItem) {
            productName.text = item.productName
            productSize.text = "Размер: ${item.sizeName}"
            val rubles = item.priceInKopecks / 100
            val totalRubles = (item.priceInKopecks * item.quantity) / 100
            productPrice.text = "$rubles ₽ × ${item.quantity} = $totalRubles ₽"
            quantityText.text = item.quantity.toString()
            Glide.with(itemView.context).load(item.imageUrl).into(productImage)

            decreaseButton.setOnClickListener {
                if (item.quantity > 1) {
                    onQuantityChange(item.productId, item.sizeId, item.quantity - 1)
                } else {
                    onDelete(item.productId, item.sizeId)
                }
            }

            increaseButton.setOnClickListener {
                onQuantityChange(item.productId, item.sizeId, item.quantity + 1)
            }

            deleteButton.setOnClickListener {
                onDelete(item.productId, item.sizeId)
            }
        }
    }
}