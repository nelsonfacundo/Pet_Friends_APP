package com.example.petfriendsapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_splash)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.splash)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        GlobalScope.launch { // launch new coroutine in background and continue
            delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
            if( FirebaseAuth.getInstance().currentUser != null ){
                val intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this@SplashActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}




//override fun onCreate(savedInstanceState: Bundle?) {
//    super.onCreate(savedInstanceState)
//    enableEdgeToEdge()
//
//
//    ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//        val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//        v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//        insets
//    }
//
//    GlobalScope.launch { // launch new coroutine in background and continue
//        delay(1000L) // non-blocking delay for 1 second (default time unit is ms)
//        if( FirebaseAuth.getInstance().currentUser != null ){
//            val intent = Intent(this@SplashActivity, MainActivity::class.java)
//            startActivity(intent)
//            finish()
//        } else {
//            val intent = Intent(this@SplashActivity, LoginActivity::class.java)
//            startActivity(intent)
//            finish()
//        }
//    }
//}