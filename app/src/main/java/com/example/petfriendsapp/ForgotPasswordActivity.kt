package com.example.petfriendsapp

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity


class ForgotPasswordActivity : AppCompatActivity() {
    private lateinit var backButton: ImageView

    companion object {
        val BACK_BUTTON_ID = R.id.ic_back_pass

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_forgot_password)

        initViews()
        initListeners()
    }



    private fun initViews() {
        backButton = findViewById(ForgotPasswordActivity.BACK_BUTTON_ID)
    }

    private fun initListeners() {
        backButton.setOnClickListener { navigateToLogin() }
    }

    private fun navigateToLogin() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}