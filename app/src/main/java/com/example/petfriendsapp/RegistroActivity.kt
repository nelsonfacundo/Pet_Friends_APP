package com.example.petfriendsapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
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
    private lateinit var emailInput: EditText
    private lateinit var passwordInput: EditText
    private lateinit var confirmPasswordInput: EditText
    private lateinit var auth: FirebaseAuth

    private lateinit var registrationEmail: String
    private lateinit var registrationPassword: String


    companion object {
        val REGISTER_BUTTON_ID: Int = R.id.btnRegistrar
        val BACK_BUTTON_ID = R.id.ic_back
        val EMAIL_INPUT_ID = R.id.text_registro_email
        val PASSWORD_INPUT_ID = R.id.text_registro_password
        val CONFIRM_PASSWORD_INPUT_ID = R.id.text_registro_confrim_contraseña
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
        confirmPasswordInput = findViewById(CONFIRM_PASSWORD_INPUT_ID)
    }

    private fun initFirebase() {
        auth = Firebase.auth
    }

    private fun initListeners() {
        registerButton.setOnClickListener { validateAndRegisterUser() }
        backButton.setOnClickListener { navigateToLogin() }
    }

    private fun validateAndRegisterUser() {
        val email = emailInput.text.toString()
        val password = passwordInput.text.toString()
        val confirmPassword = confirmPasswordInput.text.toString()

        // Valido entrada antes de registrar al usuario
        if (validateInputRegister(email, password, confirmPassword)) {
            registrationEmail = email
            registrationPassword = password // Almacena la contraseña para el registro
            emailExists(email)
        }
    }
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
    private fun validateInputRegister(
        email: String,
        password: String,
        confirmPassword: String
    ): Boolean {
        // Verifica si algún campo está vacío
        if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            Toast.makeText(this, "Por favor, complete todos los campos", Toast.LENGTH_SHORT).show()
            return false
        }

        // Verifica si el formato del email es correcto
        if (!isValidEmail(email)) {
            Toast.makeText(this, "Formato de email no válido", Toast.LENGTH_SHORT).show()
            return false
        }

        // Verifica si las contraseñas coinciden
        if (password != confirmPassword) {
            Toast.makeText(this, "Las contraseñas no coinciden", Toast.LENGTH_SHORT).show()
            return false
        }

        // Verifica si las contraseñas tiene al menos 6 caracteres
        if (password.length <= 6 || confirmPassword.length <= 6 ) {
            Toast.makeText(this, "La contraseña debe tener al menos 6 caracteres", Toast.LENGTH_SHORT).show()
            return false
        }

        // todo ok, devuelve true
        return true
    }

    private fun emailExists(email: String) {
        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val signInMethods = task.result?.signInMethods
                    if (signInMethods.isNullOrEmpty()) {
                        // El email no está registrado
                        registerUser()
                    } else {
                        // El email ya está registrado
                        Toast.makeText(this, "Email ya registrado", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    // Error en la consulta de la base de datos
                    Toast.makeText(this, "Error en la consulta de la base de datos", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun registerUser() {
        auth.createUserWithEmailAndPassword(registrationEmail, registrationPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Registro exitoso, redirigir a la pantalla de inicio
                    Toast.makeText(this, "Registro exitoso", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()

                }else {
                    val errorMessage = task.exception?.localizedMessage
                    if (errorMessage == "The email address is already in use by another account.") {
                        // Sobre escribo el mensaje de error de firebase
                        Toast.makeText(this,"Email ya registrado", Toast.LENGTH_SHORT).show()
                    } else {
                        // Error en el registro
                        Toast.makeText(baseContext, "Error al registrar. ${task.exception?.message}",Toast.LENGTH_SHORT).show()
                    }
                }
            }
    }



    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}