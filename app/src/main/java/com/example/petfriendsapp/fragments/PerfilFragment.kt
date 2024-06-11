package com.example.petfriendsapp.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import com.example.petfriendsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Color
import android.os.Build
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.petfriendsapp.databinding.FragmentPerfilBinding
import java.util.Locale

class PerfilFragment : Fragment() {

    private lateinit var _binding: FragmentPerfilBinding
    private val binding get() = _binding

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPerfilBinding.inflate(inflater, container, false)
        val view = binding.root

        fetchUserProfile()
        fetchUserRatings()

        return view
    }

    override fun onStart() {
        super.onStart()
        initListeners()

    }
    private fun initListeners() {
        binding.btnEditarPerfil.setOnClickListener { navigateToEditProfile() }
        binding.btnCambiarEmail.setOnClickListener { navigateToChangeEmail() }
        binding.btnCambiarPassword.setOnClickListener { navigateToChangePassword() }
        binding.switchNotificaciones.setOnClickListener { showNotification(requireContext()) }
        binding.icBackFragmentPerfil.setOnClickListener { navigateToHome() }
    }

    private fun fetchUserProfile() {
        val user = auth.currentUser
        val uid = user?.uid

        if (uid != null) {
            val userDocRef = db.collection("users").document(uid)

            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        val nombre = document.getString("nombre")
                        val apellido = document.getString("apellido")
                        val nombreCompleto = "$nombre $apellido"
                        binding.idNameAvatar.text = nombreCompleto

                        val urlImagenPerfil = document.getString("avatarUrl")

                        Glide.with(requireContext())
                            .load(urlImagenPerfil)
                            .transform(CenterCrop(), RoundedCorners(250))
                            .placeholder(R.drawable.avatar)
                            .error(R.drawable.avatar)
                            .into(binding.idAvatar)

                    } else {
                        Log.d("Perfil", "No existe el documento")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Perfil", "La obtención de datos falló con ", exception)
                }
        }

    }

    private fun fetchUserRatings() {
        val userId = auth.currentUser?.uid

        if (userId != null) {
            val ratingsRef = db.collection("users").document(userId).collection("ratings")

            ratingsRef.get()
                .addOnSuccessListener { documents ->
                    if (!documents.isEmpty) {
                        var sum = 0.0
                        var count = 0

                        for (document in documents) {
                            val ratingVal = document.getLong("valoracion")?.toInt()
                            val ratingComu = document.getLong("comunicacionRating")?.toInt()
                            val ratingCond = document.getLong("condicionRating")?.toInt()
                            if (ratingVal != null && ratingComu != null && ratingCond != null) {
                                val averageRatingForReview = (ratingVal + ratingComu + ratingCond) / 3 //se calcula el promedio de las tres valoraciones
                                sum += averageRatingForReview
                                count++
                            }
                        }

                        val averageRating = if (count > 0) sum / count else 0

                        // Establece el promedio en el RatingBar
                        binding.ratingBarPerfil.rating = averageRating.toFloat()

                        // Convierte el promedio a cadena de texto antes de mostrarlo
                        val averageRatingText = String.format(Locale.getDefault(), "%.0f", averageRating)
                        val ratingText = getString(R.string.average_rating_text, averageRatingText )
                        binding.tvRatingBarPerfil.text = ratingText


                    } else {
                        Log.d("PerfilFragment", "Valoraciones no encontradas")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.w("PerfilFragment", "Error al obtener las valoraciones: ", exception)
                }
        }
    }

    // Función para mostrar una notificación
    private fun showNotification(context: Context) {
        val title = "¡Adopta una mascota!"
        val message = "Hay muchas mascotas esperando por un hogar. ¡Visita nuestra app y encuentra a tu nuevo compañero peludo!"

        // Se crea un NotificationManager
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        val channelId = "default_channel_id"
        val channelName = "Default Channel"

        // Se crea NotificationCompat.Builder
        val builder = NotificationCompat.Builder(context, channelId)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.logo)
            .setAutoCancel(true)

        // Se comprueban las versiones del celular
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Se crea el canal de notificación para Android Oreo y versiones anteriores
            val channel = NotificationChannel(channelId, channelName, NotificationManager.IMPORTANCE_DEFAULT).apply {
                importance = NotificationManager.IMPORTANCE_DEFAULT
                lightColor = Color.GREEN
            }

            // Registra el canal en el NotificationManager
            notificationManager.createNotificationChannel(channel)
        }

        // Se construye la notificación y la muestra
        notificationManager.notify(1, builder.build())
    }


    private fun navigateToHome() {
        val action1 = PerfilFragmentDirections.actionPerfilToInicio()
        binding.root.findNavController().navigate(action1)
    }
    private fun navigateToChangeEmail() {
        val action1 = PerfilFragmentDirections.actionPerfilToCambiarEmail()
        binding.root.findNavController().navigate(action1)
    }

    private fun navigateToChangePassword() {
        val action2 = PerfilFragmentDirections.actionPerfilToCambiarPassword()
        binding.root.findNavController().navigate(action2)
    }

    private fun navigateToEditProfile() {
        val action = PerfilFragmentDirections.actionPerfilToEditarPerfilFragment()
        binding.root.findNavController().navigate(action)
    }

}

