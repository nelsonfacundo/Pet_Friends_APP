package com.example.petfriendsapp.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.petfriendsapp.R
import com.example.petfriendsapp.entities.Mascota
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DetailsAdoptar : Fragment() {

    private lateinit var buttonBackDetails: ImageView
    private val args: DetailsAdoptarArgs by navArgs()
    private lateinit var buttonNumero: ImageButton
    private lateinit var buttonAdoptar: Button

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var isButtonEnabled = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_details_adoptar, container, false)

        buttonBackDetails = view.findViewById(R.id.ic_back_fragment_detail)
        buttonAdoptar = view.findViewById(R.id.buttonAdoptar)
        buttonNumero = view.findViewById(R.id.botonWpp)

        buttonBackDetails.setOnClickListener {
            findNavController().navigateUp()
        }

        val txtRaza: TextView = view.findViewById(R.id.razaMascota)
        val txtEdad: TextView = view.findViewById(R.id.edadMascota)
        val txtNombre: TextView = view.findViewById(R.id.nombreMascota)
        val txtSexo: TextView = view.findViewById(R.id.sexoMascota)
        val txtUbicacion: TextView = view.findViewById(R.id.ubicacionMascotaDetalle)
        val txtDescripcion: TextView = view.findViewById(R.id.descripcionMascota)
        val imagenPerro : ImageView = view.findViewById(R.id.imagenPerro)

        val txtNombreDueño: TextView = view.findViewById(R.id.nombreDueño)
        val imagenDueño : ImageView = view.findViewById(R.id.imagenDueño)

        val mascota: Mascota = args.Mascota
        val idMascota: String = args.mascotaId

        txtRaza.text = mascota.especie
        txtEdad.text = mascota.edad.toString()
        txtNombre.text = mascota.nombre
        txtUbicacion.text = mascota.ubicacion
        txtSexo.text = mascota.sexo
        txtDescripcion.text = mascota.descripcion

        if (mascota.imageUrl.isNotEmpty()) {
            Glide.with(this)
                .load(mascota.imageUrl)
                .transform(MultiTransformation(CenterCrop()))
                .into(imagenPerro)
        }


        val userIdDueño = mascota.userId
        fetchUserDetails(userIdDueño, txtNombreDueño, imagenDueño, buttonNumero)


        val userIdAdopta = auth.currentUser?.uid
        if (userIdAdopta != null) {
            val sharedPreferences = requireContext().getSharedPreferences("PetFriendsPrefs", Context.MODE_PRIVATE)
            isButtonEnabled = sharedPreferences.getBoolean("isButtonEnabled_${userIdAdopta}_$idMascota", true)
            buttonAdoptar.isEnabled = isButtonEnabled
        }

        buttonAdoptar.setOnClickListener {
            if (isButtonEnabled) {
                crearPeticionAdopcion(mascota, idMascota)
                // Deshabilitar el botón después de que se presione y guardar el estado
                isButtonEnabled = false
                buttonAdoptar.isEnabled = false
                if (userIdAdopta != null) {
                    val sharedPreferences = requireContext().getSharedPreferences("PetFriendsPrefs", Context.MODE_PRIVATE)
                    with(sharedPreferences.edit()) {
                        putBoolean("isButtonEnabled_${userIdAdopta}_$idMascota", false)
                        apply()
                    }
                }
            } else {
                Toast.makeText(requireContext(), "Ya has realizado una solicitud de adopción", Toast.LENGTH_LONG).show()
            }
        }

        return view
    }

    private fun fetchUserDetails(userId: String, txtNombreDueño: TextView, imagenDueño: ImageView, buttonNumero: ImageButton) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { userDocument ->
                val nombreDueño = userDocument.getString("nombre") ?: "Nombre no disponible"
                val avatarUrl = userDocument.getString("avatarUrl") ?: ""
                val telefonoDueño = userDocument.getString("telefono") ?: "Teléfono no disponible"

                txtNombreDueño.text = nombreDueño

                // Cargar la imagen del dueño si la URL no está vacía
                Glide.with(this)
                    .load(avatarUrl)
                    .transform(CenterCrop(), RoundedCorners(250))
                    .placeholder(R.drawable.avatar)
                    .into(imagenDueño)

                // Configurar el botón para abrir WhatsApp con el prefijo del país (+54)
                buttonNumero.setOnClickListener {
                    val formattedPhoneNumber = formatPhoneNumber(telefonoDueño)
                    val url = "https://wa.me/$formattedPhoneNumber"
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al obtener los detalles del dueño: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    // Función para formatear el número de teléfono con el prefijo del país (+54)
    private fun formatPhoneNumber(phoneNumber: String): String {
        // Elimina espacios en blanco y caracteres no numéricos
        val cleanedPhoneNumber = phoneNumber.replace(Regex("[^\\d]"), "")

        // Añadir prefijo +54 si no está presente
        return if (!cleanedPhoneNumber.startsWith("54")) {
            "+54$cleanedPhoneNumber"
        } else {
            "+$cleanedPhoneNumber"
        }
    }

    private fun crearPeticionAdopcion(mascota: Mascota, idMascota : String) {
        val userIdAdopta = auth.currentUser?.uid
        val userIdDueño = mascota.userId

        if ( userIdAdopta ==  userIdDueño){
            Toast.makeText(requireContext(), "No podes adoptar a tu propia mascota", Toast.LENGTH_LONG).show()

        }else if (userIdAdopta != null && userIdDueño != null) {
            val peticion = hashMapOf(
                "estado" to "pendiente",
                "idMascota" to idMascota,
                "idUsuarioAdopta" to userIdAdopta,
                "idUsuarioDueño" to userIdDueño
            )
            db.collection("peticiones")
                .add(peticion)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Petición de adopción creada!", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error al crear petición: ${e.message}", Toast.LENGTH_LONG).show()
                }
        } else {
            Toast.makeText(requireContext(), "Error: Usuario no autenticado o ID de dueño no disponible", Toast.LENGTH_LONG).show()
        }
    }
}
