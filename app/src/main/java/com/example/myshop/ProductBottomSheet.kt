package com.example.myshop


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.example.myshop.model.Product


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


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.bottom_sheet_product, container, false)
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val product = arguments?.getSerializable(ARG_PRODUCT) as? Product


        product?.let {
            val imageView = view.findViewById<ImageView>(R.id.productImage)
            val nameView = view.findViewById<TextView>(R.id.productName)
            val priceView = view.findViewById<TextView>(R.id.productPrice)
            val descView = view.findViewById<TextView>(R.id.productDescription)


            nameView.text = it.name
            descView.text = it.longDescription


            val rubles = it.priceInKopecks / 100
            val kopecks = it.priceInKopecks % 100
            priceView.text = "$rubles руб. ${kopecks.toString().padStart(2, '0')} коп."


            Glide.with(requireContext()).load(it.imageUrl).into(imageView)
        }
    }
}
