package com.example.tfg_2025

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tfg_2025.ui.biblioteca.BibliotecaFragment
import com.example.tfg_2025.ui.busqueda.BusquedaFragment
import com.example.tfg_2025.ui.favoritos.FavoritosFragment
import com.example.tfg_2025.ui.perfil.PerfilFragment
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ocultar la barra superior para que no aparezca el menú arriba
        supportActionBar?.hide()

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        bottomNav.setOnItemSelectedListener { item ->
            val fragmentSeleccionado: Fragment = when (item.itemId) {
                R.id.nav_biblioteca -> BibliotecaFragment()
                R.id.nav_busqueda -> BusquedaFragment()
                R.id.nav_favoritos -> FavoritosFragment()
                R.id.nav_perfil -> PerfilFragment()
                else -> BibliotecaFragment()
            }

            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragmentSeleccionado)
                .commit()
            true
        }

        // Cargar por defecto la biblioteca
        if (savedInstanceState == null) {
            bottomNav.selectedItemId = R.id.nav_biblioteca
        }
    }
}