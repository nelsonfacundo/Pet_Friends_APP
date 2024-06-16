package com.example.petfriendsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.petfriendsapp.components.LoadingDialog
import com.example.petfriendsapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.firebase.firestore.ktx.firestore

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var loadingDialog: LoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splash)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initFirebase()
        initListeners()
        loadingDialog = LoadingDialog(this)
    }

    private fun initFirebase() {
        auth = Firebase.auth
    }

    private fun initListeners() {
        binding.registro.setOnClickListener { navigateToRegister() }
        binding.forgotPass.setOnClickListener { navigateToForgotPassword() }
        binding.btnLogin.setOnClickListener { loginUser() }
    }

    private fun loginUser() {
        val email = binding.inputEmail.text.toString()
        val password = binding.inputPass.text.toString()

        if (validateInputsLogin(email, password)) {
            loadingDialog.show()
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    loadingDialog.dismiss()
                    if (task.isSuccessful) {
                        // Inicio de sesión exitoso
                        Toast.makeText(this, R.string.login_success, Toast.LENGTH_LONG).show()
                        checkUserDataAndNavigate()
                    } else {
                        // Error en el inicio de sesión
                        Toast.makeText(this, R.string.login_error, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun validateInputsLogin(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.login_empty_error, Toast.LENGTH_LONG).show()
            return false
        }
        return true
    }

    private fun checkUserDataAndNavigate() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val uid = currentUser.uid
            val userRef = Firebase.firestore.collection("users").document(uid)
            userRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // Datos del usuario presentes, navegar al fragmento del botón de navegación
                    navigateToHome()
                } else {
                    // Datos del usuario no presentes, navegar al formulario
                    navigateToDataForm()
                }
            }.addOnFailureListener { exception ->
                Toast.makeText(this, "error", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun navigateToForgotPassword() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToHome() {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun navigateToDataForm() {
        val intent = Intent(this, DataFormActivity::class.java)
        startActivity(intent)
        finish()
    }
}
