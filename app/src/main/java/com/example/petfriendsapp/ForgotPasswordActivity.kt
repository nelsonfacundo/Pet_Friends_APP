package com.example.petfriendsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var backButton: ImageView
    private lateinit var emailEditText: EditText
    private lateinit var resetButton: Button

    companion object {
        val BACK_BUTTON_ID = R.id.ic_back_pass
        val EMAIL_EDIT_TEXT_ID = R.id.text_registro_email_pass
        val RESET_BUTTON_ID = R.id.button_reset_pass
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)

        initViews()
        initFirebase()
        initListeners()
    }

    private fun initViews() {
        backButton = findViewById(BACK_BUTTON_ID)
        emailEditText = findViewById(EMAIL_EDIT_TEXT_ID)
        resetButton = findViewById(RESET_BUTTON_ID)
    }

    private fun initFirebase() {
        auth = FirebaseAuth.getInstance()
    }

    private fun initListeners() {
        backButton.setOnClickListener { navigateToLogin() }
        resetButton.setOnClickListener { sendPasswordResetEmail() }
    }

    private fun sendPasswordResetEmail() {
        val email = emailEditText.text.toString().trim()

        if (validateInputsForgotPassword(email)){
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, R.string.forgot_succes, Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, R.string.forgot_error, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    private fun validateInputsForgotPassword(email: String): Boolean {
        if (email.isEmpty()) {
            Toast.makeText(this, R.string.forgot_empty_error, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}
