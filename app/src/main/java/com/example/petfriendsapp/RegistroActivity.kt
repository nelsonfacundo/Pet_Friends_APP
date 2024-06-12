package com.example.petfriendsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.petfriendsapp.components.LoadingDialog
import com.example.petfriendsapp.databinding.ActivityRegistroBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var registrationEmail: String
    private lateinit var registrationPassword: String
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFirebase()
        initListeners()
        loadingDialog = LoadingDialog(this)
    }

    private fun initFirebase() {
        auth = Firebase.auth
    }

    private fun initListeners() {
        binding.btnRegistrar.setOnClickListener { validateAndRegisterUser() }
        binding.icBack.setOnClickListener { navigateToLogin() }
    }

    private fun validateAndRegisterUser() {
        val email = binding.textRegistroEmail.text.toString()
        val password = binding.textRegistroPassword.text.toString()
        val confirmPassword = binding.textRegistroConfirmPassword.text.toString()

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
            Toast.makeText(this, R.string.msj_campos_vacios, Toast.LENGTH_LONG).show()
            return false
        }

        // Verifica si el formato del email es correcto
        if (!isValidEmail(email)) {
            Toast.makeText(this, R.string.msj_email_formato_bad, Toast.LENGTH_LONG).show()
            return false
        }

        // Verifica si las contraseñas coinciden
        if (password != confirmPassword) {
            Toast.makeText(this, R.string.msj_pass_no_coinciden, Toast.LENGTH_LONG).show()
            return false
        }

        // Verifica si las contraseñas tienen al menos 6 caracteres
        if (password.length <= 6) {
            Toast.makeText(this, R.string.msj_pass_falta_caracteres, Toast.LENGTH_LONG).show()
            return false
        }

        // Todo ok, devuelve true
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
                        Toast.makeText(this, R.string.msj_email_registrado, Toast.LENGTH_LONG).show()
                    }
                } else {
                    // Error en la consulta de la base de datos
                    Toast.makeText(this, R.string.msj_error_consulta_bd, Toast.LENGTH_LONG).show()
                }
            }
    }

    private fun registerUser() {
        loadingDialog.show()
        auth.createUserWithEmailAndPassword(registrationEmail, registrationPassword)
            .addOnCompleteListener(this) { task ->
                loadingDialog.dismiss()
                if (task.isSuccessful) {
                    // Registro exitoso, redirigir a la pantalla de inicio
                    Toast.makeText(this, R.string.register_success, Toast.LENGTH_LONG).show()
                    val intent = Intent(this, DataFormActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    val errorMessage = task.exception?.localizedMessage
                    if (errorMessage == "The email address is already in use by another account.") {
                        // Sobre escribo el mensaje de error de firebase
                        Toast.makeText(this, R.string.msj_email_registrado, Toast.LENGTH_LONG).show()
                    } else {
                        // Error en el registro
                        Toast.makeText(baseContext, "Error al registrar. ${task.exception?.message}", Toast.LENGTH_LONG).show()
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
