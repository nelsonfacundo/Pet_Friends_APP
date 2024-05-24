package com.example.petfriendsapp


import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
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
        }
        // Configurar ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //Mostrar icono hamburguesa

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

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_toolbar, menu)
        return true
    }

    //Metodo que se utiliza para realizar modificaciones del menú antes de que se muestre en la pantalla.
    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val deleteItem = menu?.findItem(R.id.delete_account)
        val currentFragment = navHostFragment.childFragmentManager.fragments[0]
        deleteItem?.isVisible = currentFragment is PerfilFragment
        return super.onPrepareOptionsMenu(menu)
    }


    //Maneja los eventos del click en los elementos del menu
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
        builder.setTitle(R.string.txt_eliminar_cuenta)
        builder.setMessage(R.string.txt_pregunta_eliminar_cuenta)
        builder.setPositiveButton(R.string.eliminar) { dialog, _ ->
            eliminarCuenta()
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.cancelar) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun eliminarCuenta() {
        val user = auth.currentUser
        user?.let {
            eliminarUsuarioFirebaseAuth(it)
        } ?: run {
            Toast.makeText(this, R.string.txt_error_eliminar_cuenta, Toast.LENGTH_SHORT).show()
        }
    }

    //ELIMINA
    private fun eliminarUsuarioFirebaseAuth(user: FirebaseUser) {
        user.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                eliminarUsuarioFirestore(user.uid)
            } else {
                Toast.makeText(this, R.string.txt_error_eliminar_cuenta, Toast.LENGTH_SHORT).show()
            }
        }
    }

    //NO ELIMINA
    private fun eliminarUsuarioFirestore(uid: String) {
        db.collection("users").document(uid)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, R.string.txt_cuenta_eliminada, Toast.LENGTH_SHORT).show()
                finishAffinity()
            }
            .addOnFailureListener {
                Toast.makeText(this, R.string.txt_error_eliminar_cuenta, Toast.LENGTH_SHORT).show()
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
/*
class MainActivity : AppCompatActivity() {

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var bottomNavView: BottomNavigationView
    private lateinit var navController: NavController

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

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
<<<<<<< HEAD
        NavigationUI.setupWithNavController(bottomNavView,navHostFragment.navController)

        //Observador de cambios de destino, se activa cada vez que cambia el destino de navegación
        navHostFragment.navController.addOnDestinationChangedListener { _, destination, _ ->
            invalidateOptionsMenu() // Cambia de fragment se llama a onPrepareOptionsMenu
        }
        // Configurar ActionBar
        supportActionBar?.setDisplayHomeAsUpEnabled(true) //Mostrar icono hamburguesa

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
=======
        NavigationUI.setupWithNavController(bottomNavView, navHostFragment.navController)
>>>>>>> Develop

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
        builder.setTitle(R.string.txt_eliminar_cuenta)
        builder.setMessage(R.string.txt_pregunta_eliminar_cuenta)
        builder.setPositiveButton(R.string.eliminar) { dialog, _ ->
            eliminarCuenta()
            dialog.dismiss()
        }
        builder.setNegativeButton(R.string.cancelar) { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun eliminarCuenta() {
        val user = auth.currentUser
        user?.let {
            eliminarUsuarioFirebaseAuth(it)
        } ?: run {
            Toast.makeText(this, R.string.txt_error_eliminar_cuenta, Toast.LENGTH_SHORT).show()
        }
    }

    private fun eliminarUsuarioFirebaseAuth(user: FirebaseUser) {
        user.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                eliminarUsuarioFirestore(user.uid)
            } else {
                Toast.makeText(this, R.string.txt_error_eliminar_cuenta, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun eliminarUsuarioFirestore(uid: String) {
        db.collection("users").document(uid)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(this, R.string.txt_cuenta_eliminada, Toast.LENGTH_SHORT).show()
                finishAffinity()
            }
            .addOnFailureListener {
                Toast.makeText(this, R.string.txt_error_eliminar_cuenta, Toast.LENGTH_SHORT).show()
            }
    }

    private fun alertCerrarSesion() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(R.string.txt_cerrar_sesion)
        builder.setMessage(R.string.txt_pregunta_cerrar_sesion)
        builder.setPositiveButton(R.string.txt_cerrar_sesion) { dialog, _ ->
            // Cerrar sesión
<<<<<<< HEAD
            auth.signOut()
            Toast.makeText(this, R.string.txt_sesion_cerrada, Toast.LENGTH_SHORT).show()
            // sale de la app
=======
            FirebaseAuth.getInstance().signOut()
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            // Salir de la app
>>>>>>> Develop
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
<<<<<<< HEAD
    public fun updateHeader(nombre: String, urlImagen: String) {
=======

    private fun updateHeader(nombre: String) {
>>>>>>> Develop
        val headerView = navigationView.getHeaderView(0)
        val userNameTextView: TextView = headerView.findViewById(R.id.username)
        val urlImageView: ImageView = headerView.findViewById(R.id.image_menu_header)

        //carga nombre completo
        userNameTextView.text = nombre
<<<<<<< HEAD

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
=======
>>>>>>> Develop
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

<<<<<<< HEAD
}
=======
>>>>>>> Develop
*/
