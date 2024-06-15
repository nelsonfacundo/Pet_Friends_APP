package com.example.petfriendsapp.fragments

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.petfriendsapp.R
import com.example.petfriendsapp.components.LoadingDialog
import com.example.petfriendsapp.entities.Mascota
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.util.Locale

class DetailsAdoptar : Fragment() {

    private lateinit var buttonBackDetails: ImageView
    private val args: DetailsAdoptarArgs by navArgs()
    private lateinit var buttonNumero: ImageButton
    private lateinit var buttonAdoptar: Button
    private lateinit var mascota: Mascota
    private lateinit var idMascota: String
    private lateinit var promStar: TextView


    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private var isButtonEnabled = true
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_details_adoptar, container, false)

        buttonBackDetails = view.findViewById(R.id.ic_back_fragment_detail)
        buttonAdoptar = view.findViewById(R.id.buttonAdoptar)
        buttonNumero = view.findViewById(R.id.botonWpp)
        loadingDialog = LoadingDialog(requireContext())
        promStar = view.findViewById(R.id.numberText)

        buttonBackDetails.setOnClickListener {
            navigateToHome()
        }

//        val txtRaza: TextView = view.findViewById(R.id.razaMascota)
        val txtEdad: TextView = view.findViewById(R.id.edadMascota)
        val txtNombre: TextView = view.findViewById(R.id.nombreMascota)
        val txtSexo: TextView = view.findViewById(R.id.sexoMascota)
        val txtUbicacion: TextView = view.findViewById(R.id.ubicacionMascotaDetalle)
        val txtDescripcion: TextView = view.findViewById(R.id.descripcionMascota)
        val imagenPerro: ImageView = view.findViewById(R.id.imagenPerro)

        val txtNombreDueño: TextView = view.findViewById(R.id.nombreDueño)
        val imagenDueño: ImageView = view.findViewById(R.id.imagenDueño)

        mascota = args.Mascota
        idMascota = args.mascotaId

       // txtRaza.text = mascota.especie
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
        promRating(userIdDueño)

        txtNombreDueño.setOnClickListener {
            navigateToPetOwner(userIdDueño, mascota, idMascota)
        }
        imagenDueño.setOnClickListener {
            navigateToPetOwner(userIdDueño, mascota, idMascota)
        }

        val userIdAdopta = auth.currentUser?.uid
        if (userIdAdopta != null) {
            val sharedPreferences =
                requireContext().getSharedPreferences("PetFriendsPrefs", Context.MODE_PRIVATE)
            isButtonEnabled =
                sharedPreferences.getBoolean("isButtonEnabled_${userIdAdopta}_$idMascota", true)
            buttonAdoptar.isEnabled = isButtonEnabled
        }

        buttonAdoptar.setOnClickListener {
            if (isButtonEnabled) {
                crearPeticionAdopcion(mascota, idMascota)
                // Deshabilitar el botón después de que se presione y guardar el estado
                isButtonEnabled = false
                buttonAdoptar.isEnabled = false
                if (userIdAdopta != null) {
                    val sharedPreferences = requireContext().getSharedPreferences(
                        "PetFriendsPrefs",
                        Context.MODE_PRIVATE
                    )
                    with(sharedPreferences.edit()) {
                        putBoolean("isButtonEnabled_${userIdAdopta}_$idMascota", false)
                        apply()
                    }
                }
            } else {
                Toast.makeText(
                    requireContext(),
                    "Ya has realizado una solicitud de adopción",
                    Toast.LENGTH_LONG
                ).show()
            }
        }

        return view
    }

    private fun fetchUserDetails(
        userId: String,
        txtNombreDueño: TextView,
        imagenDueño: ImageView,
        buttonNumero: ImageButton
    ) {
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
                Toast.makeText(
                    requireContext(),
                    "Error al obtener los detalles del dueño: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
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

    private fun crearPeticionAdopcion(mascota: Mascota, idMascota: String) {
        val userIdAdopta = auth.currentUser?.uid
        val userIdDueño = mascota.userId
        if (userIdAdopta == userIdDueño) {
            Toast.makeText(
                requireContext(),
                "No podes adoptar a tu propia mascota",
                Toast.LENGTH_LONG
            ).show()

        } else if (userIdAdopta != null && userIdDueño != null) {
            loadingDialog.show()
            if (userIdAdopta != null && userIdDueño != null) {
                val peticion = hashMapOf(
                    "estado" to "pendiente",
                    "Review" to false,
                    "idMascota" to idMascota,
                    "idUsuarioAdopta" to userIdAdopta,
                    "idUsuarioDueño" to userIdDueño
                )
                db.collection("peticiones")
                    .add(peticion)
                    .addOnSuccessListener {
                        loadingDialog.dismiss()
                        Toast.makeText(
                            requireContext(),
                            "Petición de adopción creada!",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .addOnFailureListener { e ->
                        loadingDialog.dismiss()
                        Toast.makeText(
                            requireContext(),
                            "Error al crear petición: ${e.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            } else {
                loadingDialog.dismiss()
                Toast.makeText(
                    requireContext(),
                    "Error: Usuario no autenticado o ID de dueño no disponible",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun navigateToPetOwner(ownerId: String, mascota: Mascota, idMascota: String) {
        val action = DetailsAdoptarDirections.actionDetailsAdoptarToPerfilPetOwner(ownerId, mascota, idMascota)
        findNavController().navigate(action)
    }
    private fun navigateToHome() {
        val action = DetailsAdoptarDirections.actionDetailsAdoptarToInicio()
        findNavController().navigate(action)
    }

    private fun promRatingT(idOwner: String) {
        val promStar = view?.findViewById<TextView>(R.id.numberText)

        val ratingsRef = db.collection("users").document(idOwner).collection("ratings")

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
                            val averageRatingForReview =
                                (ratingVal + ratingComu + ratingCond) / 3
                            sum += averageRatingForReview
                            count++
                        }
                    }

                    val averageRating = if (count > 0) sum / count else 0

                    val averageRatingText = String.format(Locale.getDefault(), "%.0f", averageRating)
                    promStar?.text = averageRatingText


                } else {
                    Log.d("DetailsAdoptar", "Valoraciones no encontradas")
                }
            }
            .addOnFailureListener { exception ->
                Log.w("DetailsAdoptar", "Error al obtener las valoraciones: ", exception)
            }
    }

    private fun promRating(idOwner: String) {
        val ratingsRef = db.collection("users").document(idOwner).collection("ratings")

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
                            val averageRatingForReview =
                                (ratingVal + ratingComu + ratingCond) / 3
                            sum += averageRatingForReview
                            count++
                        }
                    }

                    if (count != 0) {
                        val average = sum / count
                        val averageRatingText = String.format(Locale.getDefault(), "%.0f", average)
                        promStar.text = averageRatingText
                    } else {
                        promStar.text = "0"
                    }
                } else {
                    promStar.text = "0"
                }
            }
            .addOnFailureListener { exception ->
                promStar.text = "0"
                Toast.makeText(
                    requireContext(),
                    "Error al obtener las valoraciones: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
