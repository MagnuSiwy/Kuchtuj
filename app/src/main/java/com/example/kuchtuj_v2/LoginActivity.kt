package com.example.kuchtuj_v2

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth

class LoginActivity: AppCompatActivity() {

    private lateinit var emailLogin: EditText
    private lateinit var passwordLogin: EditText
    private lateinit var loginButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var createAccountButton: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailLogin = findViewById(R.id.email_login)
        passwordLogin = findViewById(R.id.password_login)
        loginButton = findViewById(R.id.login_button)
        progressBar = findViewById(R.id.progress_bar)
        createAccountButton = findViewById(R.id.create_account_button)

        loginButton.setOnClickListener {
            loginUser()
        }
        createAccountButton.setOnClickListener {
            startActivity(Intent(this, CreateAccountActivity::class.java))
        }
    }


    private fun loginUser() {
        val email = emailLogin.text.toString()
        val password = passwordLogin.text.toString()

        if (!validateData(email, password)) {
            return
        }

        loginAccountInFirebase(email, password)
    }


    private fun loginAccountInFirebase(email: String, password: String) {
        val firebaseAuth = FirebaseAuth.getInstance()
        changeInProgress(true)
        firebaseAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            changeInProgress(false)
            if (task.isSuccessful) {
                if (firebaseAuth.currentUser?.isEmailVerified == true) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    val text = "Email has not been verified \n Please verify your email"
                    val duration = Toast.LENGTH_SHORT
                    val toast = Toast.makeText(this, text, duration)
                    toast.show()
                }
            } else {
                val text = "Login failed"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(this, text, duration)
                toast.show()
            }
        }
    }


    private fun changeInProgress(inProgress: Boolean) {
        if (inProgress) {
            progressBar.visibility = View.VISIBLE
            createAccountButton.visibility = View.GONE
        } else {
            progressBar.visibility = View.GONE
            createAccountButton.visibility = View.VISIBLE
        }
    }


    private fun validateData(email: String, password: String): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailLogin.error = "Email is invalid"
            return false
        }
        if (password.length < 6) {
            passwordLogin.error = "Password length is invalid"
            return false
        }

        return true
    }

}