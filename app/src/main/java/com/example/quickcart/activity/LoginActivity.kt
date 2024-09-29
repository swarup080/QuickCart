package com.example.quickcart.activity

import GoogleSignInHelper
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.example.quickcart.R
import com.example.quickcart.databinding.ActivityLoginBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var googleSignInHelper: GoogleSignInHelper

    private val launcher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            googleSignInHelper.handleActivityResult(result, { user ->
                updateUi(user)
                showToast("Google Sign-In successful")
            }, { error ->
                showToast(error)
            })
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeFirebaseAuth()
        initializeGoogleSignInClient()
        initializeGoogleSignInHelper()

        setupClickListeners()
    }

//    override fun onStart() {
//        super.onStart()
//        val currentUser: FirebaseUser? = auth.currentUser
//        Log.d("LoginActivity", "onStart - Current user: ${currentUser?.email}")
//        if (currentUser != null) {
//            navigateToDashboard()
//        }
//    }

    private fun initializeFirebaseAuth() {
        auth = Firebase.auth
        Log.d("LoginActivity", "Firebase Auth initialized")
    }

    private fun initializeGoogleSignInClient() {
        googleSignInClient = GoogleSignIn.getClient(
            this, GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build()
        )
    }

    private fun initializeGoogleSignInHelper() {
        googleSignInHelper = GoogleSignInHelper(this, auth, googleSignInClient)
    }

    private fun setupClickListeners() {
        binding.googleAuth.setOnClickListener {
            googleSignInHelper.registerForResult(launcher, { user ->
                updateUi(user)
            }, { error ->
                showToast(error)
            })
        }
        binding.loginbtn.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            Log.d("LoginActivity", "Login button clicked with email: $email")

            when {
                email.isBlank() || password.isBlank() -> showToast("Please enter all the details")
                password.length <= 5 -> showToast("Password should be six digits")
                else -> checkUser(email, password)
            }
        }
        binding.signUp.setOnClickListener { startActivity(Intent(this,SignUpActivity::class.java)) }
        binding.facebookAuth.setOnClickListener { Toast.makeText(this,"Coming Soon",Toast.LENGTH_SHORT).show() }

    }

    private fun checkUser(email: String, password: String) {
        Log.d("LoginActivity", "Checking user with email: $email")
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d("LoginActivity", "User authentication successful")
                val user: FirebaseUser? = auth.currentUser
                updateUi(user)
            } else {
                Log.d("LoginActivity", "User authentication failed: ${task.exception?.message}")
                showToast("User doesn't exist")
            }
        }
    }

    private fun updateUi(user: FirebaseUser?) {
        Log.d("LoginActivity", "Updating UI for user: ${user?.email}")
        navigateToDashboard()
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, Dashboard::class.java)
        startActivity(intent)
        finish()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}