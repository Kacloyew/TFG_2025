package com.example.tfg_2025

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tfg_2025.ui.theme.adapter.HomeFragment
import com.example.tfg_2025.ui.theme.adapter.BibliotecaFragment
import com.example.tfg_2025.ui.theme.adapter.SearchFragment
import com.example.tfg_2025.ui.theme.adapter.ProfileFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigation)

        // Cargar el fragmento inicial (Home)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, HomeFragment())
                .commit()
        }

        // Configurar la navegación del bottom navigation
        bottomNav.setOnItemSelectedListener { item ->
            var selectedFragment: Fragment? = null

            when(item.itemId) {
                R.id.nav_home -> {
                    selectedFragment = HomeFragment()
                }
                R.id.nav_search -> {
                    selectedFragment = SearchFragment()
                }
                R.id.nav_favorites -> {
                    selectedFragment = BibliotecaFragment()
                }
                R.id.nav_profile -> {
                    selectedFragment = ProfileFragment()
                }
            }

            selectedFragment?.let {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.nav_host_fragment, it)
                    .commit()
            }

            true
        }
    }
}