package com.example.petfriendsapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.petfriendsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.widget.Switch
import androidx.core.app.NotificationCompat


class PerfilFragment : Fragment() {
    lateinit var viewPerfil: View
    private lateinit var backButton: ImageView
    private lateinit var buttonEditarPerfil: Button
    private lateinit var buttonCambiarEmail: Button
    private lateinit var buttonCambiarPassword: Button
    private lateinit var nombreAvatar: TextView


    private lateinit var btn_notficaciones: Switch

    companion object {
        val BACK_BUTTON_ID = R.id.ic_back_perfil
        val BUTTON_CAMBIAR_EMAIL = R.id.btn_cambiar_email
        val BUTTON_CAMBIAR_PASSWORD = R.id.btn_cambiar_password
        val BUTTON_EDITAR_PERFIL = R.id.btn_editar_perfil
        val NOMBRE_AVATAR = R.id.id_name_avatar
        val BUTTON_NOTIFICACIONES = R.id.switch_notificaciones
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewPerfil=inflater.inflate(R.layout.fragment_perfil, container, false)

        initViews()
        fetchUserProfile()

        return viewPerfil
    }

    override fun onStart() {
        super.onStart()
        initListeners()

    }
    private fun initViews() {
        backButton = viewPerfil.findViewById(BACK_BUTTON_ID)
        buttonEditarPerfil = viewPerfil.findViewById(BUTTON_EDITAR_PERFIL)
        buttonCambiarEmail = viewPerfil.findViewById(BUTTON_CAMBIAR_EMAIL)
        buttonCambiarPassword = viewPerfil.findViewById(BUTTON_CAMBIAR_PASSWORD)
        nombreAvatar = viewPerfil.findViewById(NOMBRE_AVATAR)
        btn_notficaciones = viewPerfil.findViewById(BUTTON_NOTIFICACIONES)
    }



    private fun initListeners() {
        backButton.setOnClickListener { navigateToHome() }
        buttonEditarPerfil.setOnClickListener { navigateToEditProfile() }
        buttonCambiarEmail.setOnClickListener { navigateToChangeEmail() }
        buttonCambiarPassword.setOnClickListener { navigateToChangePassword() }
        btn_notficaciones.setOnClickListener{showNotification(requireContext())}
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
                        nombreAvatar.setText(nombreCompleto)
                    } else {
                        Log.d("Perfil", "No existe el documento")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Perfil", "La obtención de datos falló con ", exception)
                }
        }
    }
    private fun navigateToHome() {
        val navController = findNavController()
        navController.navigate(R.id.main)
    }

    private fun navigateToChangeEmail() {
        val action1 = PerfilFragmentDirections.actionPerfilToCambiarEmail()
        viewPerfil.findNavController().navigate(action1)
    }

    private fun navigateToChangePassword() {
        val action2 = PerfilFragmentDirections.actionPerfilToCambiarPassword()
        viewPerfil.findNavController().navigate(action2)
    }

    private fun navigateToEditProfile() {
        val action = PerfilFragmentDirections.actionPerfilToEditarPerfilFragment()
        viewPerfil.findNavController().navigate(action)
    }


    // Función para mostrar una notificación
    fun showNotification(context: Context) {
        val title = "¡Adopta una mascota!"
        val message = "Hay muchas mascotas esperando por un hogar. ¡Visita nuestra app y encuentra a tu nuevo compañero peludo!"

        // Crear un NotificationManager
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // ID de canal para Android Oreo y versiones posteriores
        val channelId = "default_channel_id"
        val channelName = "Default Channel"

        // Crear un NotificationCompat.Builder
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.logo)
            .setAutoCancel(true)

        // Comprobar si la versión del dispositivo es mayor o igual a Oreo (API 26)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Crear el canal de notificación para Android Oreo y versiones posteriores
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
                // Configurar la importancia y el color de las luces del canal
                importance = NotificationManager.IMPORTANCE_DEFAULT
                lightColor = Color.GREEN
            }

            // Registrar el canal en el NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Construir la notificación y mostrarla
        notificationManager.notify(1, builder.build())
    }
}