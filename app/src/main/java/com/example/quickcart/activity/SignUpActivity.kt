package com.example.quickcart.activity

import GoogleSignInHelper
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.fragment.findNavController
import com.example.quickcart.R
import com.example.quickcart.databinding.ActivitySignUpBinding
import com.example.quickcart.model.UserModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database

class SignUpActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySignUpBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
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
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initializeFirebaseAuth()
        initializeGoogleSignInClient()
        initializeGoogleSignInHelper()
        initializeDatabase()

        setupClickListeners()
    }

//    override fun onStart() {
//        super.onStart()
//        val currentUser: FirebaseUser? = auth.currentUser
//        Log.d("SignUpActivity", "onStart - Current user: ${currentUser?.email}")
//        if (currentUser != null) {
//            navigateToDashboard()
//        }
//    }

    private fun initializeFirebaseAuth() {
        auth = Firebase.auth
        Log.d("SignUpActivity", "Firebase Auth initialized")
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

    private fun initializeDatabase() {
        database = Firebase.database.reference
    }

    private fun setupClickListeners() {
        binding.signup.setOnClickListener {
            val firstName = binding.firstname.text.toString().trim()
            val lastName = binding.lastname.text.toString().trim()
            val phoneNumber = binding.mobilenumber.text.toString().trim()
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (isInputDataValid(firstName, lastName, phoneNumber, email, password)) {
                createAccount(firstName, lastName, phoneNumber, email, password)
            }
        }

        binding.logInGoto.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.googleAuth.setOnClickListener {
            googleSignInHelper.registerForResult(launcher, { user ->
                updateUi(user)
            }, { error ->
                showToast(error)
            })
        }

        binding.facebookAuth.setOnClickListener {
            showToast("Coming Soon")
        }
    }

    private fun isInputDataValid(firstName: String, lastName: String, phoneNumber: String, email: String, password: String): Boolean {
        return when {
            firstName.isBlank() || lastName.isBlank() || phoneNumber.isBlank() || email.isBlank() || password.isBlank() -> {
                showToast("Please enter all the details")
                false
            }
            password.length <= 5 -> {
                showToast("Password should be six digits")
                false
            }
            else -> true
        }
    }

    private fun createAccount(firstName: String, lastName: String, phoneNumber: String, email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                showToast("Account created successfully")
                saveUserData(firstName, lastName, phoneNumber, email, password)
                navigateToDashboard()
            } else {
                showToast("Account creation failed")
                Log.d("SignUpActivity", "Create Account: Failed", task.exception)
            }
        }
    }

    private fun saveUserData(firstName: String, lastName: String, phoneNumber: String, email: String, password: String) {
        val userId = auth.currentUser?.uid ?: return
        val user = UserModel(firstName, lastName, phoneNumber, email, password)
        database.child("user").child(userId).setValue(user)
    }

    private fun updateUi(user: FirebaseUser?) {
        Log.d("SignUpActivity", "Updating UI for user: ${user?.email}")
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