package com.example.petfriendsapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.MultiTransformation
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class DataFormActivity : AppCompatActivity() {
    private lateinit var inputNombre: EditText
    private lateinit var inputApellido: EditText
    private lateinit var buttonSeleccionarAvatar: Button
    private lateinit var imageViewAvatar: ImageView
    private lateinit var buttonGuardar: Button
    private var imageUri: Uri? = null
    private var avatarUrl: String? = null


    private val PICK_IMAGE_REQUEST = 1
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val firebaseDatabase = FirebaseDatabase.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_form)

        initViews()
        initListeners()
    }

    private fun initViews() {
        inputNombre = findViewById(R.id.input_nombre)
        inputApellido = findViewById(R.id.input_apellido)
        imageViewAvatar = findViewById(R.id.imageViewAvatar)
        buttonSeleccionarAvatar = findViewById(R.id.buttonSeleccionarAvatar)
        buttonGuardar = findViewById(R.id.buttonGuardar)
    }

    private fun initListeners() {
        buttonSeleccionarAvatar.setOnClickListener { abrirGaleria() }
        buttonGuardar.setOnClickListener { guardarDatos() }
    }

    private fun abrirGaleria() {
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
                    .into(imageViewAvatar)
            } else {
                Log.e("DataFormActivity", "Image URI is null")
                Toast.makeText(this, "Error al seleccionar imagen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun guardarDatos() {
        val nombre = inputNombre.text.toString().trim()
        val apellido = inputApellido.text.toString().trim()

        val user = auth.currentUser

        if (user != null) {
            user.getIdToken(true).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val idToken = task.result?.token

                    if (imageUri != null) {
                        val storageRef = FirebaseStorage.getInstance().reference.child("avatars/${user.uid}.jpg")
                        storageRef.putFile(imageUri!!)
                            .addOnSuccessListener {
                                storageRef.downloadUrl.addOnSuccessListener { uri ->
                                    val avatarUrl = uri.toString()
                                    val userMap = hashMapOf(
                                        "nombre" to nombre,
                                        "apellido" to apellido,
                                        "token" to idToken,
                                        "avatarUrl" to avatarUrl
                                    )

                                    db.collection("users").document(user.uid)
                                        .set(userMap)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Datos guardados correctamente", Toast.LENGTH_SHORT).show()
                                            navigateToHome()
                                        }
                                        .addOnFailureListener { e ->
                                            Toast.makeText(this, "Error al guardar datos: ${e.message}", Toast.LENGTH_SHORT).show()
                                        }
                                }.addOnFailureListener { e ->
                                    Toast.makeText(this, "Error al obtener URL de descarga: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                    } else {
                        Toast.makeText(this, "Selecciona una imagen", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Usuario no autenticado", Toast.LENGTH_SHORT).show()
        }
    }


    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }


}


/*
*/
