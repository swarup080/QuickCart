package com.example.quickcart.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.quickcart.databinding.ActivityMainBinding
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding:ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.started.setOnClickListener {
            startActivity(Intent(this,AuthHintActivity::class.java))
        }
    }
    override fun onStart() {
        super.onStart()
        auth = Firebase.auth
        val currentUser: FirebaseUser? = auth.currentUser
        Log.d("MainActivity", "onStart - Current user: ${currentUser?.email}")
        if (currentUser != null) {
            val intent = Intent(this, Dashboard::class.java)
            startActivity(intent)
            finish() // Call finish if you don't want the user to come back to this activity by pressing back
        }
    }
}