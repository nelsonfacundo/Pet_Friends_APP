package com.example.petfriendsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth

class RegistroActivity : AppCompatActivity() {

    private lateinit var registerButton: Button
    private lateinit var backButton: ImageView
    private lateinit var  emailInput: EditText
    private lateinit var  passwordInput: EditText
    private lateinit var auth: FirebaseAuth

    companion object {
        val REGISTER_BUTTON_ID: Int = R.id.btnRegistrar
        val BACK_BUTTON_ID = R.id.ic_back
        val EMAIL_INPUT_ID = R.id.text_registro_email
        val PASSWORD_INPUT_ID = R.id.text_registro_contraseña
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registro)

        initViews()
        initFirebase()
        initListeners()
    }

    private fun initViews() {
        registerButton = findViewById(REGISTER_BUTTON_ID)
        backButton = findViewById(BACK_BUTTON_ID)
        emailInput = findViewById(EMAIL_INPUT_ID)
        passwordInput = findViewById(PASSWORD_INPUT_ID)
    }

    private fun initFirebase() {
        auth = Firebase.auth
    }
    private fun initListeners() {
        registerButton.setOnClickListener { registerUser() }
        backButton.setOnClickListener { navigateToLogin() }
    }



    private fun registerUser() {
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()

        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registro exitoso, redirigir a la pantalla de inicio
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    // Error en el registro
                    Toast.makeText(baseContext, "Error al registrar. ${task.exception?.message}",
                        Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}