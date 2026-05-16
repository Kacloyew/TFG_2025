package com.example.tfg_2025

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.tfg_2025.ui.autenticacion.LoginFragment
import com.example.tfg_2025.ui.biblioteca.BibliotecaFragment
import com.example.tfg_2025.ui.busqueda.BusquedaFragment
import com.example.tfg_2025.ui.favoritos.FavoritosFragment
import com.example.tfg_2025.ui.perfil.PerfilFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        supportActionBar?.hide()
        bottomNav = findViewById(R.id.bottom_navigation)

        // 🔥 LÍNEA PARA LA ENTREGA/PRUEBAS: Forzamos el cierre de sesión aquí mismo
        FirebaseAuth.getInstance().signOut()

        val currentUser = FirebaseAuth.getInstance().currentUser

        try {
            if (currentUser == null) {
                bottomNav.visibility = View.GONE

                // Vaciamos a la fuerza cualquier historial viejo retenido por Android
                supportFragmentManager.popBackStackImmediate(null, androidx.fragment.app.FragmentManager.POP_BACK_STACK_INCLUSIVE)

                // Metemos el Login limpio
                remplazarFragmento(LoginFragment())
            } else {
                bottomNav.visibility = View.VISIBLE
                if (savedInstanceState == null) {
                    bottomNav.selectedItemId = R.id.nav_biblioteca
                    remplazarFragmento(BibliotecaFragment())
                }
            }
        } catch (e: Exception) {
            android.widget.Toast.makeText(this, "Error al cargar fragmento inicial: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
        }

        bottomNav.setOnItemSelectedListener { item ->
            val fragmentSeleccionado: Fragment = when (item.itemId) {
                R.id.nav_biblioteca -> BibliotecaFragment()
                R.id.nav_busqueda -> BusquedaFragment()
                R.id.nav_favoritos -> FavoritosFragment()
                R.id.nav_perfil -> PerfilFragment()
                else -> BibliotecaFragment()
            }
            remplazarFragmento(fragmentSeleccionado)
            true
        }
    }

    private fun remplazarFragmento(fragment: Fragment) {
        try {
            supportFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment, fragment)
                .commitAllowingStateLoss()
        } catch (e: Exception) {
            android.widget.Toast.makeText(this, "Error en transacción: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
        }
    }

    fun loginExitoso() {
        bottomNav.visibility = View.VISIBLE
        bottomNav.selectedItemId = R.id.nav_biblioteca
        remplazarFragmento(BibliotecaFragment())
    }
}