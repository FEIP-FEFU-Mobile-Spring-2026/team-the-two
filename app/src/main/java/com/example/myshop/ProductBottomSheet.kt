package com.example.myshop

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.HorizontalScrollView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.myshop.data.CartRepository
import com.example.myshop.data.ProductsRepository
import com.example.myshop.model.CartItem
import com.example.myshop.model.Product
import com.example.myshop.model.Size
import com.example.myshop.viewmodel.CartViewModel

class ProductBottomSheet : BottomSheetDialogFragment() {

    companion object {
        private const val ARG_PRODUCT = "product"

        fun newInstance(product: Product): ProductBottomSheet {
            val fragment = ProductBottomSheet()
            val args = Bundle()
            args.putSerializable(ARG_PRODUCT, product)
            fragment.arguments = args
            return fragment
        }
    }

    private var selectedSize: Size? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_product, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val product = arguments?.getSerializable(ARG_PRODUCT) as? Product ?: return

        // Инициализация ViewModel корзины
        val productsRepository = ProductsRepository(requireContext())
        val cartRepository = CartRepository(requireContext(), productsRepository)
        val cartViewModel = ViewModelProvider(requireActivity()).get(CartViewModel::class.java)

        // Фото
        val imageView = view.findViewById<ImageView>(R.id.productImage)
        Glide.with(requireContext()).load(product.imageUrl).into(imageView)

        // Название
        val nameView = view.findViewById<TextView>(R.id.productName)
        nameView.text = product.name

        // Цена
        val priceView = view.findViewById<TextView>(R.id.productPrice)
        val rubles = product.priceInKopecks / 100
        priceView.text = "$rubles ₽"

        // Описание
        val descView = view.findViewById<TextView>(R.id.productDescription)
        descView.text = product.longDescription

        // Теги (чипы)
        setupTags(view, product.tags)

        // Размеры
        setupSizes(view, product.sizes)

        // Кнопка "В корзину"
        val addToCartButton = view.findViewById<Button>(R.id.addToCartButton)
        addToCartButton.text = "В корзину: $rubles ₽"

        addToCartButton.setOnClickListener {
            if (selectedSize == null) {
                Toast.makeText(requireContext(), "Выберите размер", Toast.LENGTH_SHORT).show()
            } else {
                val cartItem = CartItem(
                    productId = product.id,
                    productName = product.name,
                    sizeId = selectedSize!!.id,
                    sizeName = selectedSize!!.name,
                    priceInKopecks = product.priceInKopecks,
                    quantity = 1,
                    imageUrl = product.imageUrl
                )
                cartViewModel.addToCart(cartItem)
                Toast.makeText(requireContext(), "Товар добавлен в корзину", Toast.LENGTH_SHORT).show()
                dismiss()
            }
        }

        // Кнопка информации (i)
        val infoButton = view.findViewById<ImageButton>(R.id.infoButton)
        infoButton.setOnClickListener {
            showInfoDialog(product)
        }
    }

    private fun setupTags(view: View, tags: List<String>) {
        val tagsContainer = view.findViewById<HorizontalScrollView>(R.id.tagsContainer)
        if (tags.isEmpty()) {
            tagsContainer.visibility = View.GONE
            return
        }

        val chipGroup = LinearLayout(requireContext()).apply {
            orientation = LinearLayout.HORIZONTAL
            layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
        }

        tags.forEach { tag ->
            val chip = layoutInflater.inflate(R.layout.chip_tag, null) as com.google.android.material.chip.Chip
            chip.text = tag
            chipGroup.addView(chip)
        }

        tagsContainer.removeAllViews()
        tagsContainer.addView(chipGroup)
        tagsContainer.visibility = View.VISIBLE
    }

    private fun setupSizes(view: View, sizes: List<Size>) {
        val sizesContainer = view.findViewById<LinearLayout>(R.id.sizesContainer)
        sizesContainer.removeAllViews()

        sizes.forEach { size ->
            val button = Button(requireContext()).apply {
                text = size.name
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    marginEnd = 8
                }
                setOnClickListener {
                    selectedSize = size
                    // Сброс выделения всех кнопок
                    for (i in 0 until sizesContainer.childCount) {
                        sizesContainer.getChildAt(i).isSelected = false
                    }
                    isSelected = true
                }
            }
            sizesContainer.addView(button)
        }
    }

    private fun showInfoDialog(product: Product) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_product_details, null)

        dialogView.findViewById<TextView>(R.id.materialText).text = "Материал: ${product.material}"
        dialogView.findViewById<TextView>(R.id.weightText).text = "Вес: ${product.weight}"
        dialogView.findViewById<TextView>(R.id.seasonText).text = "Сезон: ${product.season}"
        dialogView.findViewById<TextView>(R.id.countryText).text = "Страна: ${product.countryOfOrigin}"

        AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setPositiveButton("Закрыть", null)
            .show()
    }
}