package com.example.tfg_2025

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Ocultamos la barra superior gris del sistema
        supportActionBar?.hide()

        bottomNav = findViewById(R.id.bottom_navigation)

        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.nav_host_fragment) as? NavHostFragment

        if (navHostFragment != null) {
            navController = navHostFragment.navController

            // Enlace nativo automático con la barra inferior
            bottomNav.setupWithNavController(navController)

            // Control de flujo inicial seguro con Firebase
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser != null) {
                val opcionesNavegacion = androidx.navigation.NavOptions.Builder()
                    .setPopUpTo(R.id.loginFragment, true)
                    .build()
                navController.navigate(R.id.nav_biblioteca, null, opcionesNavegacion)
            }
        }
    }
}