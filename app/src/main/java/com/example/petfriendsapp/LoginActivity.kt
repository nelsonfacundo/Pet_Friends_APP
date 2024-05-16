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

class LoginActivity : AppCompatActivity() {
    lateinit var txt_registro: TextView
    private lateinit var bntLogin: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.login_activity)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val loginButton: Button = findViewById(R.id.btn_login)
        loginButton.setOnClickListener {
            val nombreUsuario = findViewById<EditText>(R.id.input_user)
            val contraseña: EditText = findViewById(R.id.input_pass)
            var credencialesValidas: Boolean = true

            if (credencialesValidas) {
                // Si las credenciales son válidas, navega a la siguiente actividad
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            } else {
                // Si las credenciales son inválidas, muestra un mensaje de error
                Toast.makeText(this, "Credenciales inválidas", Toast.LENGTH_SHORT).show()
            }
        }

        txt_registro = findViewById(R.id.registro)
        bntLogin = findViewById(R.id.btn_login)

        txt_registro.setOnClickListener {
            val intent = Intent(this, RegistroActivity::class.java)
            startActivity(intent)
        }

        bntLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

}