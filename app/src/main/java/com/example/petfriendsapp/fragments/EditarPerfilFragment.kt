package com.example.petfriendsapp.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.petfriendsapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage


class EditarPerfilFragment : Fragment() {

    private lateinit var viewEditarPerfil: View
    private lateinit var backButton: ImageView
    private lateinit var editTextNombre: EditText
    private lateinit var editTextApellido: EditText
    private lateinit var cambiarFoto: ImageView
    private lateinit var buttonGuardarPerfil: Button
    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1

    companion object {
        val BACK_BUTTON_ID = R.id.ic_back_editar_perfil
        val EDIT_NOMBRE = R.id.editar_perfil_nombre
        val EDIT_APELLIDO = R.id.editar_perfil_apellido
        val CAMBIAR_FOTO_PERFIL = R.id.editar_foto
        val BUTTON_GUARDAR_PERFIL = R.id.btn_guardar_perfil
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        viewEditarPerfil = inflater.inflate(R.layout.fragment_editar_perfil, container, false)

        initViews()
        fetchUserProfile()

        return viewEditarPerfil
    }

    override fun onStart() {
        super.onStart()
        initListeners()
    }

    private fun initViews() {
        backButton = viewEditarPerfil.findViewById(BACK_BUTTON_ID)
        editTextNombre = viewEditarPerfil.findViewById(EDIT_NOMBRE)
        editTextApellido = viewEditarPerfil.findViewById(EDIT_APELLIDO)
        cambiarFoto = viewEditarPerfil.findViewById(CAMBIAR_FOTO_PERFIL)
        buttonGuardarPerfil = viewEditarPerfil.findViewById(BUTTON_GUARDAR_PERFIL)
    }

    private fun initListeners() {
        backButton.setOnClickListener { navigateToProfile() }
        buttonGuardarPerfil.setOnClickListener { saveUserProfile() }
        cambiarFoto.setOnClickListener{abrirGaleria()}
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
                        editTextNombre.setText(nombre)
                        editTextApellido.setText(apellido)

                        val imageUrl = document.getString("avatarUrl")
                        // Cargar la imagen utilizando Glide
                        if (imageUrl != null) {
                            Glide.with(requireContext())
                                .load(imageUrl)
                                .placeholder(R.drawable.avatar) // Placeholder opcional
                                .error(R.drawable.avatar) // Imagen de error opcional
                                .into(cambiarFoto)

                    }} else {
                        Log.d("EditarPerfilFragment", "No exite documento")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("EditarPerfilFragment", "no se puedo obtener ", exception)
                }
        }
    }

    private fun saveUserProfile() {
        val nombre = editTextNombre.text.toString()
        val apellido = editTextApellido.text.toString()
        val firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        val uid = user?.uid

        if (uid != null) {
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(uid)

            userDocRef.update(mapOf(
                "nombre" to nombre,
                "apellido" to apellido
            ))
                .addOnSuccessListener {
                    // Si la actualización del nombre y apellido fue exitosa, procede a actualizar la foto de perfil
                    if (imageUri != null) {
                        // Subir la imagen a Firebase Storage
                        uploadImageToFirebaseStorage(imageUri!!)
                    } else {
                        // Si no se seleccionó una nueva imagen, simplemente mostrar un mensaje de éxito y volver al perfil
                        Toast.makeText(context, R.string.perfil_changed_successfully, Toast.LENGTH_SHORT).show()
                        navigateToProfile()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("EditarPerfilFragment", "Error al actualizar el documento", exception)
                    Toast.makeText(context, R.string.perfil_changed_failed, Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun abrirGaleria() {
        // val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        //        startActivityForResult(intent, PICK_IMAGE_REQUEST)
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Seleccionar Foto"), PICK_IMAGE_REQUEST)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            if (imageUri != null) {
                Glide.with(this)
                    .load(imageUri)
                    .transform(MultiTransformation(CenterCrop(), RoundedCorners(250)))
                    .into(cambiarFoto) // Cambia imageViewAvatar por cambiarFoto
            } else {
                Log.e("EditarPerfilFragment", "Image URI es nulo")
                Toast.makeText(requireContext(), R.string.photo_changed_failed, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadImageToFirebaseStorage(filePath: Uri) {
        val storageReference = FirebaseStorage.getInstance().reference
        val ref = storageReference.child("profile_images/${FirebaseAuth.getInstance().currentUser?.uid}")

        ref.putFile(filePath)
            .addOnSuccessListener { taskSnapshot ->
                // La imagen se cargó exitosamente a Firebase Storage
                ref.downloadUrl.addOnSuccessListener { uri ->
                    // Guarda la URL de la imagen en Firestore
                    saveImageUrlToFirestore(uri.toString())
                }
            }
            .addOnFailureListener { e ->
                Log.e("EditarPerfilFragment", "Error al cargar la imagen", e)
                Toast.makeText(context, R.string.error_carga_image, Toast.LENGTH_SHORT).show()
            }
    }


    private fun saveImageUrlToFirestore(imageUrl: String) {
        val firebaseAuth = FirebaseAuth.getInstance()
        val user = firebaseAuth.currentUser
        val uid = user?.uid

        if (uid != null) {
            val db = FirebaseFirestore.getInstance()
            val userDocRef = db.collection("users").document(uid)

            // Actualizar la URL de la imagen de perfil en Firestore
            userDocRef.update("avatarUrl", imageUrl)
                .addOnSuccessListener {
                    // La URL de la imagen se guardó exitosamente en Firestore
                    Toast.makeText(context, R.string.photo_changed_successfully, Toast.LENGTH_SHORT).show()
                    navigateToProfile()
                }
                .addOnFailureListener { exception ->
                    Log.e("EditarPerfilFragment", "Error al guardar la URL de la imagen en Firestore", exception)
                    Toast.makeText(context, R.string.url_changed_failed, Toast.LENGTH_SHORT).show()
                }
        }
    }
    private fun navigateToProfile() {
        val action = EditarPerfilFragmentDirections.actionEditarPerfilFragmentToPerfil()
        viewEditarPerfil.findNavController().navigate(action)
    }
}