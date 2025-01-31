package com.example.petfriendsapp.fragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.petfriendsapp.MainActivity
import com.example.petfriendsapp.R
import com.example.petfriendsapp.components.LoadingDialog
import com.example.petfriendsapp.databinding.FragmentEditarPerfilBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class EditarProfileFragment : Fragment() {

    private lateinit var bindingFragment: FragmentEditarPerfilBinding
    private val binding get() = bindingFragment

    private var imageUri: Uri? = null
    private val PICK_IMAGE_REQUEST = 1
    private lateinit var loadingDialog: LoadingDialog

    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        bindingFragment = FragmentEditarPerfilBinding.inflate(inflater, container, false)
        fetchUserProfile()
        loadingDialog = LoadingDialog(requireContext())
        return binding.root
    }
    override fun onStart() {
        super.onStart()
        initListeners()
    }


    private fun initListeners() {
        binding.icBackEditarPerfil.setOnClickListener { navigateToProfile() }
        binding.btnGuardarPerfil.setOnClickListener { saveUserProfile() }
        binding.editarFoto.setOnClickListener { abrirGaleria() }
        binding.icDeleteEditarPerfil.setOnClickListener { alertEliminarCuenta() }
    }

    private fun fetchUserProfile() {
        val user = auth.currentUser
        val uid = user?.uid

        if (uid != null) {
            val userDocRef = db.collection("users").document(uid)
            userDocRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        binding.editarPerfilNombre.setText(document.getString("nombre"))
                        binding.editarPerfilApellido.setText(document.getString("apellido"))
                        binding.editarPerfilTelefono.setText(document.getString("telefono"))


                        val imageUrl = document.getString("avatarUrl")
                        if (imageUrl != null) {
                            Glide.with(requireContext())
                                .load(imageUrl)
                                .transform(MultiTransformation(CenterCrop(), RoundedCorners(250)))
                                .placeholder(R.drawable.avatar)
                                .error(R.drawable.avatar)
                                .into(binding.editarFoto)
                        }
                    } else {
                        Log.d("EditarPerfilFragment", "No existe el documento")
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("EditarPerfilFragment", "No se pudo obtener el documento", exception)
                }
        }
    }

    private fun abrirGaleria() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(
            Intent.createChooser(intent, ""),
            PICK_IMAGE_REQUEST
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            if (imageUri != null) {
                Glide.with(this)
                    .load(imageUri)
                    .transform(MultiTransformation(CenterCrop(), RoundedCorners(250)))
                    .into(binding.editarFoto)
            } else {
                Log.e("EditarPerfilFragment", "La imagen URI es nulo")
                Toast.makeText(requireContext(), R.string.photo_changed_failed, Toast.LENGTH_LONG)
                    .show()
            }
        }
    }
    private fun saveUserProfile() {
        val nombre = binding.editarPerfilNombre.text.toString()
        val apellido = binding.editarPerfilApellido.text.toString()
        val telefono = binding.editarPerfilTelefono.text.toString()

       if( validateInputs(nombre, apellido,telefono )){
           loadingDialog.show()
           val nombreCompleto = "$nombre $apellido"
           val user = auth.currentUser
           val uid = user?.uid

           if (uid != null) {
               val userDocRef = db.collection("users").document(uid)

               userDocRef.update(mapOf(
                   "nombre" to nombre,
                   "apellido" to apellido,
                   "telefono" to telefono

               ))
                   .addOnSuccessListener {
                       loadingDialog.dismiss()
                       // Si la actualización del nombre y apellido fue exitosa, procede a actualizar la foto de perfil
                       if (imageUri != null) {
                           // Subir la imagen a Firebase Storage
                           uploadImageToFirebaseStorage(imageUri!!)
                           (activity as MainActivity).updateHeader(nombreCompleto, imageUri.toString())

                       } else {
                           // Actualiza el header del menu si no hay imagen nueva, la url se carga en null pq no se actualizo la foto
                           (activity as MainActivity).updateHeader(nombreCompleto, null.toString()) //casting de activity al MainActivity
                           // No se seleccionó una nueva imagen, mostrar un mensaje de éxito y volver al perfil
                           Toast.makeText(context, R.string.perfil_changed_successfully, Toast.LENGTH_LONG).show()
                           navigateToProfile()
                       }
                   }
                   .addOnFailureListener { exception ->
                       loadingDialog.dismiss()
                       Log.d("EditarPerfilFragment", "Error al actualizar el documento", exception)
                       Toast.makeText(context, R.string.perfil_changed_failed, Toast.LENGTH_LONG).show()
                   }
           }
       }


    }

    private fun uploadImageToFirebaseStorage(filePath: Uri) {
        val storageReference = FirebaseStorage.getInstance().reference
        val ref = storageReference.child("avatars/${auth.currentUser?.uid}.jpg")
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
                Toast.makeText(context, R.string.error_carga_image, Toast.LENGTH_LONG).show()
            }
    }


 private fun saveImageUrlToFirestore(imageUrl: String) {
         val user = auth.currentUser
         val uid = user?.uid

         if (uid != null) {
             val db = FirebaseFirestore.getInstance()
             val userDocRef = db.collection("users").document(uid)

             // Actualizar la URL de la imagen de perfil en Firestore
             userDocRef.update("avatarUrl", imageUrl)
                 .addOnSuccessListener {
                     // La URL de la imagen se guardó exitosamente en Firestore
                     Toast.makeText(context, R.string.photo_changed_successfully, Toast.LENGTH_LONG).show()
                     navigateToProfile()
                 }
                 .addOnFailureListener { exception ->
                     Log.e("EditarPerfilFragment", "Error al guardar la URL de la imagen en Firestore", exception)
                     Toast.makeText(context, R.string.url_changed_failed, Toast.LENGTH_LONG).show()
                 }
         }
     }

    private fun alertEliminarCuenta() {
        val builder = AlertDialog.Builder(requireContext())
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
            Toast.makeText(requireContext(), R.string.txt_error_eliminar_cuenta, Toast.LENGTH_LONG).show()
        }
    }

    //ELIMINA
    private fun eliminarUsuarioFirebaseAuth(user: FirebaseUser) {
        user.delete().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), R.string.txt_cuenta_eliminada, Toast.LENGTH_LONG).show()
                requireActivity().finish()
            } else {
                Toast.makeText(requireContext(), R.string.txt_error_eliminar_cuenta, Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun navigateToProfile() {
        val action = EditarProfileFragmentDirections.actionEditarPerfilFragmentToPerfil()
        binding.root.findNavController().navigate(action)
    }
    //NO ELIMINA
    /*private fun eliminarUsuarioFirestore(uid: String) {
        db.collection("users").document(uid)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), R.string.txt_cuenta_eliminada, Toast.LENGTH_SHORT).show()
                startActivity(Intent(requireContext(), LoginActivity::class.java))
                requireActivity().finish()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), R.string.txt_error_eliminar_cuenta, Toast.LENGTH_SHORT).show()
            }
    }*/
    private fun validateInputs(nombre: String, apellido: String, telefono: String): Boolean {
        // Verifica si algún campo está vacío
        if (nombre.isEmpty() || apellido.isEmpty() || telefono.isEmpty()) {
            Toast.makeText(context, R.string.msj_campos_vacios, Toast.LENGTH_LONG).show()
            return false
        }
        if (nombre.length < 3 || nombre.length > 25 ){
            Toast.makeText(context, R.string.txt_cantC_nombre, Toast.LENGTH_LONG).show()
            return false
        }
        if (apellido.length < 3 || apellido.length > 25){
            Toast.makeText(context, R.string.txt_cantC_apellido, Toast.LENGTH_LONG).show()
            return false
        }
        if (telefono.length != 10){
            Toast.makeText(context, R.string.txt_cantC_telefono, Toast.LENGTH_LONG).show()
            return false
        }
        // todo ok, devuelve true
        return true
    }
}
