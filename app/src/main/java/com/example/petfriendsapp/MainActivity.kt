package com.example.petfriendsapp


import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var bottomNavView: BottomNavigationView

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        initViews()
        configToolbar()

        setupDrawerLayout()
        NavigationUI.setupWithNavController(bottomNavView,navHostFragment.navController)

        //Observador de cambios de destino, se activa cada vez que cambia el destino de navegación
        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            invalidateOptionsMenu() // Cambia de fragment se llama a onPrepareOptionsMenu

            // Oculta la actionBar en los fragmentos requeridos
            if (destination.id == R.id.editarPerfilFragment || destination.id == R.id.cambiarEmail || destination.id == R.id.cambiarPassword) {
                supportActionBar?.hide()
            } else {
                supportActionBar?.show()
            }
        }
        fetchUserProfile()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.drawer_nav)
        navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        bottomNavView = findViewById(R.id.bottom_var)

    }

    private fun configToolbar() {
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //Mostrar icono hamburguesa
        // Oculto el título de la Toolbar
        supportActionBar?.setDisplayShowTitleEnabled(false)
    }

    private fun setupDrawerLayout() {
        val navController = navHostFragment.navController

        // Vinculo la navegación del drawer con la del graph
        navigationView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _, _, _ ->
            supportActionBar?.setHomeAsUpIndicator(R.drawable.hamburguesa)
        }

        // Obtener y mostrar la información del usuario
        fetchUserProfile()

        // cerrar sesión en el menú
        navigationView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.logout_drawer -> {
                    alertCerrarSesion()
                    drawerLayout.closeDrawer(GravityCompat.START) //Cierra el menu despues de seleccionar un elemento
                    true
                }else -> {
                    val handled = NavigationUI.onNavDestinationSelected(menuItem, navController)
                    if (handled) {
                        drawerLayout.closeDrawer(GravityCompat.START)
                    }
                    handled // Indica si fue manejado por NavigationUI
                }
            }
        }
    }

   private fun alertCerrarSesion() {
       val builder = AlertDialog.Builder(this)
       builder.setTitle(R.string.txt_cerrar_sesion)
       builder.setMessage(R.string.txt_pregunta_cerrar_sesion)
       builder.setPositiveButton(R.string.txt_cerrar_sesion) { dialog, _ ->
           // Cerrar sesión
           auth.signOut()
           Toast.makeText(this, R.string.txt_sesion_cerrada, Toast.LENGTH_SHORT).show()
           // sale de la app
           finishAffinity()
           dialog.dismiss()
       }
       builder.setNegativeButton(R.string.cancelar) { dialog, _ ->
           dialog.dismiss()
       }
       builder.create().show()
   }
    private fun fetchUserProfile() {
        val user = auth.currentUser
        val uid = user?.uid

        if (uid != null) {
            val userDocRef = db.collection("users").document(uid)

            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val nombre = document.getString("nombre")
                        val apellido = document.getString("apellido")
                        val nombreCompleto = "$nombre $apellido"
                        val urlImage = document.getString("avatarUrl")

                        if (urlImage != null) {
                            updateHeader(nombreCompleto, urlImage)
                        }
                    } else {
                        Log.d("Perfil", "No existe el documento")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Perfil", "La obtención de datos falló con ", exception)
                }

        }
    }
    public fun updateHeader(nombre: String, urlImagen: String) {
        val headerView = navigationView.getHeaderView(0)
        val userNameTextView: TextView = headerView.findViewById(R.id.username)
        val urlImageView: ImageView = headerView.findViewById(R.id.image_menu_header)

        //carga nombre completo
        userNameTextView.text = nombre

        //Imagen
        if (urlImagen != null.toString()) {
            // Carga la nueva imagen
            Glide.with(this)
                .load(urlImagen)
                .transform(CenterCrop(), RoundedCorners(250))
                .placeholder(R.drawable.avatar)
                .error(R.drawable.avatar)
                .into(urlImageView)
        } else {
            // Recupera la URL de la imagen actual de la base de datos
            val user = auth.currentUser
            val uid = user?.uid
            if (uid != null) {
                db.collection("users").document(uid).get()
                    .addOnSuccessListener { document ->
                        if ( document.exists()) {
                            val urlImagenActual = document.getString("avatarUrl")
                            if (urlImagenActual != null) {
                                // Carga la imagen actual de la base de datos
                                Glide.with(this)
                                    .load(urlImagenActual)
                                    .transform(CenterCrop(), RoundedCorners(250))
                                    .placeholder(R.drawable.avatar)
                                    .error(R.drawable.avatar)
                                    .into(urlImageView)
                            }
                        }
                    }
            }
        }
    }

    //Maneja el evento de la navegacion hacia arriba
    override fun onSupportNavigateUp(): Boolean {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            drawerLayout.openDrawer(GravityCompat.START)
        }

        return false
    }



}
