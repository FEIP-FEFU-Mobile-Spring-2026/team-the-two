package com.example.myshop

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myshop.data.CartRepository
import com.example.myshop.data.ProductsRepository
import com.example.myshop.ui.CatalogAdapter
import com.example.myshop.viewmodel.CartViewModel
import com.example.myshop.viewmodel.CatalogViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

class CatalogActivity : AppCompatActivity() {

    private lateinit var viewModel: CatalogViewModel
    private lateinit var cartViewModel: CartViewModel
    private lateinit var adapter: CatalogAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tabLayout: TabLayout
    private lateinit var bottomNavigation: BottomNavigationView
    private lateinit var errorContainer: LinearLayout
    private lateinit var errorText: TextView
    private lateinit var retryButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var mainContent: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        // Инициализация элементов
        recyclerView = findViewById(R.id.recyclerView)
        tabLayout = findViewById(R.id.tabLayout)
        bottomNavigation = findViewById(R.id.bottomNavigation)
        errorContainer = findViewById(R.id.errorContainer)
        errorText = findViewById(R.id.errorText)
        retryButton = findViewById(R.id.retryButton)
        progressBar = findViewById(R.id.progressBar)
        mainContent = findViewById(R.id.mainContent)

        // Инициализация репозиториев и ViewModel
        val repository = ProductsRepository(this)
        viewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                    return CatalogViewModel(repository) as T
                }
            }
        ).get(CatalogViewModel::class.java)

        val productsRepository = ProductsRepository(this)
        val cartRepository = CartRepository(this, productsRepository)
        cartViewModel = ViewModelProvider(
            this,
            object : ViewModelProvider.Factory{
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return CartViewModel(cartRepository) as T
                }
            }
        ).get(CartViewModel::class.java)

        // Настройка списка (RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CatalogAdapter(emptyList()) { product ->
            val bottomSheet = ProductBottomSheet.newInstance(product)
            bottomSheet.show(supportFragmentManager, "product_bottom_sheet")
        }
        recyclerView.adapter = adapter

        // ==================== ТАБЫ (категории) ====================
        viewModel.categories.observe(this) { categories ->
            tabLayout.removeAllTabs()
            categories.forEach { categoryName ->
                tabLayout.addTab(tabLayout.newTab().setText(categoryName))
            }
        }

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {
                val category = tab.text.toString()
                viewModel.selectCategory(category)
            }
            override fun onTabUnselected(tab: TabLayout.Tab) {}
            override fun onTabReselected(tab: TabLayout.Tab) {}
        })

        // ==================== НИЖНЕЕ МЕНЮ ====================
        bottomNavigation.setOnItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_catalog -> true
                R.id.nav_cart -> {
                    startActivity(Intent(this, CartActivity::class.java))
                    true
                }
                else -> false
            }
        }

        // ==================== ПОДПИСКА НА ТОВАРЫ ====================
        viewModel.filteredProducts.observe(this) { products ->
            adapter.updateProducts(products)
        }

        // ==================== ИНДИКАТОР ЗАГРУЗКИ ====================
        viewModel.isLoading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // ==================== ОБРАБОТКА ОШИБОК ====================
        viewModel.errorMessage.observe(this) { error ->
            if (error != null) {
                errorText.text = error
                errorContainer.visibility = View.VISIBLE
                mainContent.visibility = View.GONE
                progressBar.visibility = View.GONE
            } else {
                errorContainer.visibility = View.GONE
                mainContent.visibility = View.VISIBLE
            }
        }

        // Кнопка "Повторить"
        retryButton.setOnClickListener {
            errorContainer.visibility = View.GONE
            mainContent.visibility = View.VISIBLE
            progressBar.visibility = View.VISIBLE
            viewModel.retryLoad()
        }

        // ==================== БЕЙДЖ НА КОРЗИНЕ ====================
        val menuItem = bottomNavigation.menu.findItem(R.id.nav_cart)
        val badge = bottomNavigation.getOrCreateBadge(menuItem.itemId)
        badge.isVisible = false

        cartViewModel.itemsCount.observe(this) { count ->
            if (count > 0) {
                badge.isVisible = true
                badge.number = count
            } else {
                badge.isVisible = false
            }
        }
    }

    override fun onResume() {
        super.onResume()
        cartViewModel.loadCart()
    }
}