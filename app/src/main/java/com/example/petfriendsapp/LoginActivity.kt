package com.example.petfriendsapp


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.google.firebase.firestore.firestore


class LoginActivity : AppCompatActivity() {
    lateinit var txt_registro: TextView
    lateinit var txt_forgot_password: TextView
    lateinit var input_email: EditText
    lateinit var input_password: EditText
    lateinit var loginButton: Button
    private lateinit var auth: FirebaseAuth
    companion object {
        val TXT_REGISTRO = R.id.registro
        val TXT_FORGOT_PASS = R.id.txt_forgot_pass
        val EMAIL_INPUT_ID = R.id.input_email
        val PASSWORD_INPUT_ID = R.id.input_pass
        val LOGIN_BTN_ID = R.id.btn_login
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initViews()
        initFirebase()
        initListeners()
    }


    private fun initViews() {
        txt_registro = findViewById(TXT_REGISTRO)
        txt_forgot_password = findViewById(TXT_FORGOT_PASS)
        input_email = findViewById(EMAIL_INPUT_ID)
        input_password = findViewById(PASSWORD_INPUT_ID)
        loginButton = findViewById(LOGIN_BTN_ID)
    }


    private fun initFirebase() {
        auth = Firebase.auth
    }


    private fun initListeners() {
        txt_registro.setOnClickListener { navigateToRegister() }
        txt_forgot_password.setOnClickListener { navigateToForgotPassword() }
        loginButton.setOnClickListener { loginUser() }
    }

    private fun loginUser() {
        val email = input_email.text.toString()
        val password = input_password.text.toString()

        if (validateInputsLogin(email, password)) {
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Inicio de sesión exitoso
                        Toast.makeText(this, R.string.login_success, Toast.LENGTH_SHORT).show()
                        checkUserDataAndNavigate()
                    } else {
                        // Error en el inicio de sesión
                        Toast.makeText(this, R.string.login_error, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun validateInputsLogin(email: String, password: String): Boolean {
        if (email.isEmpty() || password.isEmpty()) {
            Toast.makeText(this, R.string.login_empty_error, Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun navigateToForgotPassword() {
        val intent = Intent(this, ForgotPasswordActivity::class.java)
        startActivity(intent)
        finish()
    }


    private fun navigateToRegister() {
        val intent = Intent(this, RegistroActivity::class.java)
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