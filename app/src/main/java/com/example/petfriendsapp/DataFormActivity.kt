package com.example.petfriendsapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.example.petfriendsapp.components.LoadingDialog
import com.example.petfriendsapp.databinding.ActivityDataFormBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class DataFormActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDataFormBinding
    private var imageUri: Uri? = null

    private val PICK_IMAGE_REQUEST = 1
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDataFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initListeners()

        // Inicializa el ProgressDialog
        loadingDialog = LoadingDialog(this)
    }

    private fun initListeners() {
        binding.buttonSeleccionarAvatar.setOnClickListener { openGallery() }
        binding.buttonGuardar.setOnClickListener { saveData() }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            imageUri = data.data
            if (imageUri != null) {
                Glide.with(this)
                    .load(imageUri)
                    .transform(MultiTransformation(CenterCrop(), RoundedCorners(250)))
                    .into(binding.imageViewAvatar)
            } else {
                Log.e("DataFormActivity", "Image URI is null")
                Toast.makeText(this, "Error al seleccionar imagen", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun saveData() {
        val validationResult = validateData()

        if (validationResult.first) {
            val nombre = binding.inputNombre.text.toString().trim()
            val apellido = binding.inputApellido.text.toString().trim()
            val telefono = binding.inputTelefono.text.toString().trim()
            val user = auth.currentUser

            // Muestra el ProgressDialog
            loadingDialog.show()

            user?.getIdToken(true)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result?.token
                    val storageRef = FirebaseStorage.getInstance().reference.child("avatars/${user.uid}.jpg")

                    storageRef.putFile(imageUri!!).addOnSuccessListener {
                        storageRef.downloadUrl.addOnSuccessListener { uri ->
                            val avatarUrl = uri.toString()
                            val userMap = hashMapOf(
                                "nombre" to nombre,
                                "apellido" to apellido,
                                "telefono" to telefono,
                                "token" to idToken,
                                "avatarUrl" to avatarUrl
                            )

                            db.collection("users").document(user.uid)
                                .set(userMap)
                                .addOnSuccessListener {
                                    // Oculta el ProgressDialog
                                    loadingDialog.dismiss()
                                    Toast.makeText(this, R.string.txt_exitoso, Toast.LENGTH_LONG).show()
                                    navigateToHome()
                                }
                                .addOnFailureListener { e ->
                                    // Oculta el ProgressDialog
                                    loadingDialog.dismiss()
                                    Toast.makeText(this, R.string.txt_error_datos, Toast.LENGTH_LONG).show()
                                }
                        }.addOnFailureListener { e ->
                            // Oculta el ProgressDialog
                            loadingDialog.dismiss()
                            Toast.makeText(this, R.string.txt_error_url, Toast.LENGTH_LONG).show()
                        }
                    }.addOnFailureListener { e ->
                        // Oculta el ProgressDialog
                        loadingDialog.dismiss()
                        Toast.makeText(this, R.string.txt_error_upload, Toast.LENGTH_LONG).show()
                    }
                } else {
                    // Oculta el ProgressDialog
                    loadingDialog.dismiss()
                    Toast.makeText(this, R.string.txt_error_token, Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(this, validationResult.second, Toast.LENGTH_LONG).show()
        }
    }

    private fun validateData(): Pair<Boolean, String> {
        val nombre = binding.inputNombre.text.toString().trim()
        val apellido = binding.inputApellido.text.toString().trim()
        val telefono = binding.inputTelefono.text.toString().trim()

        val telefonoPattern = "\\d+".toRegex() // Expresión regular para aceptar solo dígitos

        if (nombre.isEmpty()) return Pair(false, getString(R.string.txt_empty_nombre))
        if (nombre.length < 2 || nombre.length >= 25) return Pair(false, getString(R.string.txt_cantC_nombre))
        if (apellido.isEmpty()) return Pair(false, getString(R.string.txt_empty_apellido))
        if (apellido.length < 2 || apellido.length >= 25) return Pair(false, getString(R.string.txt_cantC_apellido))
        if (telefono.isEmpty()) return Pair(false, getString(R.string.txt_empty_telefono))
        if (telefono.length != 10 || !telefono.matches(telefonoPattern)) return Pair(false, getString(R.string.txt_formato_telefono))
        if (imageUri == null) return Pair(false, getString(R.string.txt_empty_imagen))
        if (auth.currentUser == null) return Pair(false, getString(R.string.txt_validate_auth))

        return Pair(true, "")
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
