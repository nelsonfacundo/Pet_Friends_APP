package com.example.petfriendsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.petfriendsapp.components.LoadingDialog
import com.example.petfriendsapp.databinding.ActivityForgotPasswordBinding
import com.google.firebase.auth.FirebaseAuth

class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var binding: ActivityForgotPasswordBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityForgotPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initFirebase()
        initListeners()
        loadingDialog = LoadingDialog(this)
    }

    private fun initFirebase() {
        auth = FirebaseAuth.getInstance()
    }

    private fun initListeners() {
        binding.icBackPass.setOnClickListener { navigateToLogin() }
        binding.buttonResetPass.setOnClickListener { sendPasswordResetEmail() }
    }

    private fun sendPasswordResetEmail() {
        val email = binding.textRegistroEmailPass.text.toString().trim()
        if (validateInputsForgotPassword(email)) {
            loadingDialog.show()
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    loadingDialog.dismiss()
                    if (task.isSuccessful) {
                        navigateToLogin()
                        Toast.makeText(this, R.string.forgot_succes, Toast.LENGTH_LONG).show()
                    } else {
                        Toast.makeText(this, R.string.forgot_invalid_email_error, Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun validateInputsForgotPassword(email: String): Boolean {
        if (email.isEmpty()) {
            Toast.makeText(this, R.string.forgot_empty_error, Toast.LENGTH_LONG).show()
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
