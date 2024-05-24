package com.example.petfriendsapp

import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.example.petfriendsapp.fragments.PerfilFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initViews()
        configToolbar()

        setupDrawerLayout()
        NavigationUI.setupWithNavController(bottomNavView, navHostFragment.navController)

        // Obtener y mostrar la información del usuario
        fetchUserProfile()

        // Observador de cambios de destino, se activa cada vez que cambia el destino de navegación
        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            invalidateOptionsMenu() // Cambia de fragment se llama a onPrepareOptionsMenu
        }

        // Configurar ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //Mostrar icono hamburguesa

        // Cerrar sesión en el menú
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout_drawer -> {
                    alertCerrarSesion()
                    drawerLayout.closeDrawer(GravityCompat.START) // Cierra el menu despues de seleccionar un elemento
                    true
                }

                R.id.perfil -> {
                    // Navega al fragmento de perfil
                    navController.navigate(R.id.perfil)
                    drawerLayout.closeDrawer(GravityCompat.START)
                    true
                }

                else -> {
                    val handled = NavigationUI.onNavDestinationSelected(menuItem, navController)
                    if (handled) {
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    handled // Indica si fue manejado por NavigationUI
                }
            }
        }
    }

    fun initViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.drawer_nav)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController
        bottomNavView = findViewById(R.id.bottom_var)
    }

    private fun configToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        // Ocultar el título de la Toolbar
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupDrawerLayout() {
        // Vincular la navegación del drawer con la del graph
        navigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, _, _ ->
            supportActionBar?.setHomeAsUpIndicator(R.drawable.hamburguesa)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    // Método que se utiliza para realizar modificaciones del menú antes de que se muestre en la pantalla.
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val deleteItem = menu?.findItem(R.id.delete_account)
        val currentFragment = navHostFragment.childFragmentManager.fragments[0]
        deleteItem?.isVisible = currentFragment is PerfilFragment
        return super.onPrepareOptionsMenu(menu)
    }

    // Maneja los eventos del click en los elementos del menú
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.delete_account -> {
                alertEliminarCuenta()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun alertEliminarCuenta() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eliminar cuenta")
        builder.setMessage("¿Estás seguro que quieres eliminar tu cuenta?")
        builder.setPositiveButton("Eliminar") { dialog, _ ->
            eliminarCuenta()
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun eliminarCuenta() {
        val user = FirebaseAuth.getInstance().currentUser
        user?.let {
            eliminarUsuarioFirebaseAuth(it)
        } ?: run {
            Toast.makeText(this, "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show()
        }
    }

    private fun eliminarUsuarioFirebaseAuth(user: FirebaseUser) {
        user.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                eliminarUsuarioFirestore(user.uid)
            } else {
                Toast.makeText(this, "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun eliminarUsuarioFirestore(uid: String) {
        val db = FirebaseFirestore.getInstance()
        val userDocRef = db.collection("users").document(uid)
        userDocRef.delete()
            .addOnSuccessListener {
                Toast.makeText(this, "Cuenta eliminada", Toast.LENGTH_SHORT).show()
                finishAffinity()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Error al eliminar la cuenta", Toast.LENGTH_SHORT).show()
            }
    }

    private fun alertCerrarSesion() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Cerrar sesión")
        builder.setMessage("¿Estás seguro que quieres cerrar sesión?")
        builder.setPositiveButton("OK") { dialog, _ ->
            // Cerrar sesión
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            // Salir de la app
            finishAffinity()
            dialog.dismiss()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun fetchUserProfile() {
        val firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        val uid = user?.uid

        if (uid != null) {
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(uid)

            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val nombre = document.getString("nombre")
                        val apellido = document.getString("apellido")
                        val nombreCompleto = "$nombre $apellido"
                        updateHeader(nombreCompleto)
                    } else {
                        Log.d("Perfil", "No existe el documento")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Perfil", "La obtención de datos falló con ", exception)
                }
        }
    }

    private fun updateHeader(nombre: String) {
        val headerView = navigationView.getHeaderView(0)
        val userNameTextView: TextView = headerView.findViewById(R.id.username)
        userNameTextView.text = nombre
    }

    // Maneja el evento de la navegación hacia arriba
    override fun onSupportNavigateUp(): Boolean {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        return false
    }
}

