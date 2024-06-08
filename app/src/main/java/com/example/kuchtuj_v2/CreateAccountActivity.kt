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

class CreateAccountActivity: AppCompatActivity() {
    private lateinit var emailRegistration: EditText
    private lateinit var passwordRegistration: EditText
    private lateinit var repeatPasswordRegistration: EditText
    private lateinit var createAccountButton: Button
    private lateinit var progressBar: ProgressBar
    private lateinit var loginInsteadButton: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_account)

        emailRegistration = findViewById(R.id.email_registration)
        passwordRegistration = findViewById(R.id.password_registration)
        repeatPasswordRegistration = findViewById(R.id.password_repeat_registration)
        createAccountButton = findViewById(R.id.create_account_button)
        progressBar = findViewById(R.id.progress_bar)
        loginInsteadButton = findViewById(R.id.login_instead_button)

        createAccountButton.setOnClickListener {
            createAccount()
        }

        loginInsteadButton.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun createAccount() {
        val email = emailRegistration.text.toString()
        val password = passwordRegistration.text.toString()
        val confirmPassword = repeatPasswordRegistration.text.toString()

        val isValidated = validateData(email, password, confirmPassword)
        if (!isValidated) {
            return
        }

        createAccountInFirebase(email, password)
    }

    private fun createAccountInFirebase(email: String, password: String) {
        changeInProgress(true)

        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this) { task ->
            changeInProgress(false)
            if (task.isSuccessful) {
                val text = "Successfully created an account!"
                val duration = Toast.LENGTH_SHORT
                val toast = Toast.makeText(this, text, duration)
                toast.show()

                firebaseAuth.currentUser?.sendEmailVerification()
                firebaseAuth.signOut()
                finish()
            } else {
                val text = "Failed to create an account"
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

    private fun validateData(email: String, password: String, confirmPassword: String): Boolean {
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailRegistration.error = "Email is invalid"
            return false
        }
        if (password.length < 6) {
            passwordRegistration.error = "Password length is invalid"
            return false
        }
        if (password != confirmPassword) {
            repeatPasswordRegistration.error = "Password not matched"
            return false
        }

        return true
    }


}