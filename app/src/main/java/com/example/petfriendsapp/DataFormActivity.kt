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
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class DataFormActivity : AppCompatActivity() {
    private lateinit var inputNombre: EditText
    private lateinit var inputApellido: EditText
    private lateinit var inputTelefono: EditText
    private lateinit var buttonSeleccionarAvatar: Button
    private lateinit var imageViewAvatar: ImageView
    private lateinit var buttonGuardar: Button
    private var imageUri: Uri? = null


    private val PICK_IMAGE_REQUEST = 1
    private val db = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_data_form)

        initViews()
        initListeners()
    }

    private fun initViews() {
        inputNombre = findViewById(R.id.input_nombre)
        inputApellido = findViewById(R.id.input_apellido)
        inputTelefono = findViewById(R.id.input_telefono)
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
                Toast.makeText(this, "Error al seleccionar imagen", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun guardarDatos() {
        val validationResult = validarDatos()

        if (validationResult.first) {
            val nombre = inputNombre.text.toString().trim()
            val apellido = inputApellido.text.toString().trim()
            val telefono = inputTelefono.text.toString().trim()
            val user = auth.currentUser

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
                                    Toast.makeText(this, R.string.txt_exitoso, Toast.LENGTH_LONG).show()
                                    navigateToHome()
                                }
                                .addOnFailureListener { e ->
                                    Toast.makeText(this, R.string.txt_error_datos , Toast.LENGTH_LONG).show()
                                }
                        }.addOnFailureListener { e ->
                            Toast.makeText(this, R.string.txt_error_url , Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        } else {
            Toast.makeText(this, validationResult.second, Toast.LENGTH_LONG).show()
        }
    }

    private fun validarDatos(): Pair<Boolean, String> {
        val nombre = inputNombre.text.toString().trim()
        val apellido = inputApellido.text.toString().trim()
        val telefono = inputTelefono.text.toString().trim()

        if (nombre.isEmpty()) return Pair(false, getString(R.string.txt_empty_nombre))
        if (nombre.length < 3 || nombre.length > 25) return Pair(false, getString(R.string.txt_cantC_nombre))
        if (apellido.isEmpty()) return Pair(false, getString(R.string.txt_empty_apellido))
        if (apellido.length < 3 || apellido.length > 25) return Pair(false, getString(R.string.txt_cantC_apellido))
        if (telefono.isEmpty()) return Pair(false, getString(R.string.txt_empty_telefono))
        if (telefono.length != 10) return Pair(false, getString(R.string.txt_cantC_telefono))
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

