package com.example.petfriendsapp.fragments

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

        // Bind the details to the views
        val txtRaza: TextView = view.findViewById(R.id.razaMascota)
        val txtEdad: TextView = view.findViewById(R.id.edadMascota)
        val txtNombre: TextView = view.findViewById(R.id.nombreMascota)
        val txtSexo: TextView = view.findViewById(R.id.sexoMascota)
        val txtUbicacion: TextView = view.findViewById(R.id.ubicacionMascotaDetalle)
        val imagenPerro : ImageView = view.findViewById(R.id.imagenPerro)

        val txtNombreDueño: TextView = view.findViewById(R.id.nombreDueño)
        val imagenDueño : ImageView = view.findViewById(R.id.imagenDueño)



        val mascota: Mascota = args.Mascota


        txtRaza.text = mascota.raza
        txtEdad.text = mascota.edad.toString()
        txtNombre.text = mascota.nombre
        txtSexo.text = mascota.sexo
        txtUbicacion.text = mascota.ubicacion

        /*buttonAdoptar.setOnClickListener {
            crearPeticionAdopcion(mascota)
        }*/
        /*val userIdDueño = mascota.userId
        db.collection("usuarios").document(userIdDueño).get()
            .addOnSuccessListener { userDocument ->
                val nombreDueño = userDocument.getString("nombre") ?: "Nombre no disponible"
                val avatarUrl = userDocument.getString("avatarUrl") ?: ""
                val telefonoDueño = userDocument.getString("telefono") ?: "Teléfono no disponible"

                txtNombreDueño.text = nombreDueño
                if (avatarUrl.isNotEmpty()) {
                    Glide.with(this)
                        .load(avatarUrl)
                        .into(imagenDueño)
                }

                // Configurar el botón para abrir WhatsApp
                buttonNumero.setOnClickListener {
                    val url = "https://wa.me/$telefonoDueño"
                    val intent = Intent(Intent.ACTION_VIEW)
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error al obtener los detalles del dueño: ${e.message}", Toast.LENGTH_SHORT).show()
            }*/

        return view
    }

 /*   private fun crearPeticionAdopcion(mascota: Mascota) {
        val userIdAdopta = auth.currentUser?.uid
//        val userIdDueño = mascota.userId

        if (userIdAdopta != null && userIdDueño != null) {
            val peticion = hashMapOf(
                "estado" to "pendiente",
                "idMascota" to mascota.id,
                "idUsuarioAdopta" to userIdAdopta,
                "idUsuarioDueño" to userIdDueño
            )
            db.collection("peticiones")
                .add(peticion)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Petición de adopción creada!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Error al crear petición: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(requireContext(), "Error: Usuario no autenticado o ID de dueño no disponible", Toast.LENGTH_SHORT).show()
        }
    }*/
    }


