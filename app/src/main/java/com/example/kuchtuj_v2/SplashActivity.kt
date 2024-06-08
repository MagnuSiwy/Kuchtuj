package com.example.kuchtuj_v2

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity

class SplashActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

//        Handler(Looper.getMainLooper()).postDelayed({
//            val currentUser = FirebaseAuth.getInstance().currentUser
//            if (currentUser == null) {
//                startActivity(Intent(this@SplashActivity, LoginActivity::class.java))
//            } else {
//                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
//            }
//            finish()
//        }, 1000)

        Handler(Looper.getMainLooper()).postDelayed(object : Runnable {
            override fun run() {
                startActivity(Intent(this@SplashActivity, MainActivity::class.java))
                finish()
            }
        }, 1000)


    }
}