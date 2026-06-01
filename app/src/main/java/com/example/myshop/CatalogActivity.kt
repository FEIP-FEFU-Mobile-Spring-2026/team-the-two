package com.example.myshop

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myshop.data.ProductsRepository
import com.example.myshop.ui.CatalogAdapter
import com.example.myshop.viewmodel.CatalogViewModel
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout

class CatalogActivity : AppCompatActivity() {

    private lateinit var viewModel: CatalogViewModel
    private lateinit var adapter: CatalogAdapter
    private lateinit var recyclerView: RecyclerView
    private lateinit var tabLayout: TabLayout
    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_catalog)

        // Инициализация репозитория и ViewModel
        val repository = ProductsRepository(this)
        viewModel = ViewModelProvider(this, object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return CatalogViewModel(repository) as T
            }
        })[CatalogViewModel::class.java]

        // Находим элементы на экране
        recyclerView = findViewById(R.id.recyclerView)
        tabLayout = findViewById(R.id.tabLayout)
        bottomNavigation = findViewById(R.id.bottomNavigation)

        // Настройка списка (RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CatalogAdapter(emptyList()) { product ->
            // TODO: следующий блок — детали товара (Блок 3)
        }
        recyclerView.adapter = adapter

        // ==================== ТАБЫ (категории) ====================
        viewModel.categories.observe(this) { categories ->
            tabLayout.removeAllTabs()  // ← добавить эту строку
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
                R.id.nav_catalog -> {
                    // Уже в каталоге, ничего не делаем
                    true
                }
                R.id.nav_cart -> {
                    // Открываем корзину
                    val intent = Intent(this, CartActivity::class.java)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }

        // ==================== ПОДПИСКА НА ТОВАРЫ ====================
        viewModel.filteredProducts.observe(this) { products ->
            adapter.updateProducts(products)
        }
    }
}