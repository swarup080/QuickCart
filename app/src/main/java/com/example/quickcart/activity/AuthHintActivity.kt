package com.example.quickcart.activity

import GoogleSignInHelper
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.quickcart.R
import com.example.quickcart.databinding.ActivityAuthHintBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class AuthHintActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAuthHintBinding
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
        binding = ActivityAuthHintBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeFirebaseAuth()
        initializeGoogleSignInClient()
        initializeGoogleSignInHelper()

        setupClickListeners()
    }

//    override fun onStart() {
//        super.onStart()
//        val currentUser: FirebaseUser? = auth.currentUser
//        Log.d("AuthHintActivity", "onStart - Current user: ${currentUser?.email}")
//        if (currentUser != null) {
//            navigateToDashboard()
//        }
//    }

    private fun initializeFirebaseAuth() {
        auth = Firebase.auth
        Log.d("AuthHintActivity", "Firebase Auth initialized")
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
        binding.continueWithGoogle.setOnClickListener {
            googleSignInHelper.registerForResult(launcher, { user ->
                updateUi(user)
            }, { error ->
                showToast(error)
            })
        }
        binding.continueWithFacebook.setOnClickListener {
            showToast("Coming Soon")
        }
        binding.loginWithPassword.setOnClickListener {
            startActivity(Intent(this,LoginActivity::class.java))
        }
        binding.SignUp.setOnClickListener {
            startActivity(Intent(this,SignUpActivity::class.java))
        }
    }

    private fun updateUi(user: FirebaseUser?) {
        Log.d("AuthHintActivity", "Updating UI for user: ${user?.email}")
        navigateToDashboard()
    }

    private fun navigateToDashboard() {
        val intent = Intent(this, Dashboard::class.java)
        startActivity(intent)
        finish() // Call finish if you don't want the user to come back to this activity by pressing back
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}