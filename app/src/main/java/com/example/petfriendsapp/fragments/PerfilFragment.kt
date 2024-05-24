package com.example.petfriendsapp.fragments

import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.findNavController
import com.example.petfriendsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.widget.RatingBar
import android.widget.Switch
import android.widget.Toast
import androidx.core.app.NotificationCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.petfriendsapp.LoginActivity
import jp.wasabeef.glide.transformations.RoundedCornersTransformation


class PerfilFragment : Fragment() {
    lateinit var viewPerfil: View
    private lateinit var buttonEditarPerfil: Button
    private lateinit var buttonCambiarEmail: Button
    private lateinit var buttonCambiarPassword: Button
    private lateinit var nombreAvatar: TextView
    private lateinit var urlImageView: ImageView
    private lateinit var btnNotficaciones: Switch
   // private lateinit var ratingBar: RatingBar

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    companion object {
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

      //  ratingBar = viewPerfil.findViewById(R.id.rating_bar_perfil)

        // Obtener y mostrar las valoraciones del usuario
        //fetchUserRatings()

        return viewPerfil
    }

    override fun onStart() {
        super.onStart()
        initListeners()

    }
    private fun initViews() {
        buttonEditarPerfil = viewPerfil.findViewById(BUTTON_EDITAR_PERFIL)
        buttonCambiarEmail = viewPerfil.findViewById(BUTTON_CAMBIAR_EMAIL)
        buttonCambiarPassword = viewPerfil.findViewById(BUTTON_CAMBIAR_PASSWORD)
        nombreAvatar = viewPerfil.findViewById(NOMBRE_AVATAR)
        btnNotficaciones = viewPerfil.findViewById(BUTTON_NOTIFICACIONES)
        urlImageView = viewPerfil.findViewById(R.id.id_avatar)
      //  btnDeleteAccount = viewPerfil.findViewById(DELETE_CUENTA)
    }



    private fun initListeners() {
        buttonEditarPerfil.setOnClickListener { navigateToEditProfile() }
        buttonCambiarEmail.setOnClickListener { navigateToChangeEmail() }
        buttonCambiarPassword.setOnClickListener { navigateToChangePassword() }
        btnNotficaciones.setOnClickListener{showNotification(requireContext())}
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
                        nombreCompleto.also { nombreAvatar.text = it }

                        //  URL de la imagen de perfil
                        val urlImagenPerfil = document.getString("avatarUrl")


                        // Carga imagen
                        Glide.with(requireContext())
                            .load(urlImagenPerfil)
                            .transform(CenterCrop(), RoundedCorners(250))
                            .placeholder(R.drawable.avatar)
                            .error(R.drawable.avatar)
                            .into(urlImageView)

                    } else {
                        Log.d("Perfil", "No existe el documento")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("Perfil", "La obtención de datos falló con ", exception)
                }
        }

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


    /*

    private fun alertDeleteAccount() {
        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Eliminar cuenta")
        builder.setMessage("¿Estás seguro que quieres eliminar tu cuenta? Esta acción no se puede deshacer.")
        builder.setPositiveButton("Aceptar") { _, _ ->
            deleteAccount()
        }
        builder.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.create().show()
    }

    private fun deleteAccount() {
        val user = auth.currentUser
        user?.let {
            val uid = user.uid

            // eliminar el documento del usuario en Firestore
            val userDocRef = db.collection("users").document(uid)

            userDocRef.delete()
                .addOnSuccessListener {
                    // Si la eliminación en Firestore es exitosa, elimina la cuenta de autenticación
                    user.delete()
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Cuenta eliminada exitosamente",
                                    Toast.LENGTH_SHORT
                                ).show()
                                startActivity(Intent(requireContext(), LoginActivity::class.java))
                                requireActivity().finish()
                            } else {
                                Log.w(
                                    "PerfilFragment",
                                    "Error al eliminar la cuenta de autenticación",
                                    task.exception
                                )
                                Toast.makeText(
                                    requireContext(),
                                    "Error al eliminar la cuenta de autenticación",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                }
                .addOnFailureListener { e ->
                    Log.w(
                        "PerfilFragment",
                        "Error al eliminar el documento del usuario en Firestore",
                        e
                    )
                    Toast.makeText(
                        requireContext(),
                        "Error al eliminar el documento del usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }

    }
*/

    /* private fun fetchUserRatings() {
         val userId = auth.currentUser?.uid

         if (userId != null) {
             // Referencia a la subcolección de valoraciones del usuario
             val ratingsRef = db.collection("users").document(userId).collection("valoraciones")

             // Obtener los documentos de la subcolección de valoraciones
             ratingsRef.get()
                 .addOnSuccessListener { documents ->
                     if (!documents.isEmpty) {
                         var sum = 0.0
                         var count = 0

                         // Iterar sobre los documentos y sumar las valoraciones
                         for (document in documents) {
                             val rating = document.getDouble("rating")
                             if (rating != null) {
                                 sum += rating
                                 count++
                             }
                         }

                         // Calcular el promedio de las valoraciones
                         val averageRating = if (count > 0) sum / count else 0.0

                         // Establecer el promedio en el RatingBar
                         ratingBar.rating = averageRating.toFloat()
                     } else {
                         Log.d("PerfilFragment", "No ratings found")
                     }
                 }
                 .addOnFailureListener { exception ->
                     Log.w("PerfilFragment", "Error getting ratings: ", exception)
                 }
         }
     }

 */

}

