package com.example.myshop

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myshop.data.ProductsRepository
import com.example.myshop.ui.CatalogAdapter
import com.example.myshop.data.CatalogViewModel
import com.google.android.material.tabs.TabLayout


class CatalogActivity: AppCompatActivity() {
    private lateinit var viewModel: CatalogViewModel
    private lateinit var adapter: CatalogAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tabLayout: TabLayout

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        val repository = ProductsRepository(this)
        viewModel = ViewModelProvider(this, object: ViewModelProvider.Factory {
            override fun <T: androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return CatalogViewModel(repository) as T
            }
        })[CatalogViewModel::class.java]

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CatalogAdapter(emptyList()) { product ->
            // TODO: следующий блок — детали товара
        }
        recyclerView.adapter = adapter

        viewModel.filteredProducts.observe(this) { products ->
            adapter.updateProducts(products)
        }

        tabLayout = findViewById(R.id.tabLayout)
        viewModel.categories.observe(this) {categories ->
            categories.forEach { categoryName ->
                tabLayout.addTab(tabLayout.newTab().setText(categoryName))
            }
        }

        tabLayout.addOnTabSelectedListener(object :
        TabLayout.OnTabSelectedListener {
            override fun onTabListener(tab: TabLayout.Tab) {
                val category = tab.text.toString()
                viewModel.selectCategory(category)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })
    }
}