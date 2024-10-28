package com.example.quickcart.activity

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.example.quickcart.R
import com.example.quickcart.databinding.ActivityEditProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage

class EditProfileActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private var photo: Uri? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityEditProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        database = FirebaseDatabase.getInstance()
        databaseReference = database.reference
        displayUserData()
        binding.photo.setOnClickListener {
            pickImage.launch("image/*")
        }
        binding.save.setOnClickListener {
            val firstName = binding.firstName.text.toString()
            val lastName = binding.lastName.text.toString()
            saveUserData(firstName, lastName)
        }
    }

    private fun saveUserData(firstName: String, lastName: String) {
        val itemKey = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef = databaseReference.child("user").child(itemKey)
        if (photo != null) {
            val storageRef = FirebaseStorage.getInstance().reference
            val imageRef = storageRef.child("user_images/$itemKey.jpg")
            val uploadTask = imageRef.putFile(photo!!)
            uploadTask.addOnSuccessListener {
                imageRef.downloadUrl.addOnSuccessListener { uri ->
                    val photoUrl = uri.toString()
                    val userData = mapOf(
                        "firstName" to firstName,
                        "lastName" to lastName,
                        "photo" to photoUrl
                    )
                    userRef.updateChildren(userData).addOnSuccessListener {
                        finish()
                    }.addOnFailureListener {
                        Log.e("EditProfileActivity", "Error updating user data: ${it.message}")
                    }
                }
            }
        }
    }

    fun back(view: View) {
        finish()
    }

    private val pickImage =
        registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
            uri?.let {
                binding.photo.setImageURI(it)
                photo = it
            }
        }

    private fun displayUserData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            val userRef = databaseReference.child("user").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val userDataMap = snapshot.value as? Map<String, Any>
                        val firstName = userDataMap?.get("firstName") as? String ?: ""
                        val lastName = userDataMap?.get("lastName") as? String ?: ""
                        val email = userDataMap?.get("email") as? String ?: ""
                        val profilePhotoUrl = userDataMap?.get("photo") as? String

                        binding.firstName.setText(firstName)
                        binding.lastName.setText(lastName)
                        binding.email.setText(email)
                        // Load profile photo
                        profilePhotoUrl?.let {
                            // Use a library like Glide or Picasso to load the image
                            Glide.with(this@EditProfileActivity)
                                .load(it)
                                .placeholder(R.drawable.landscape_placeholder_svgrepo_com)
                                .into(binding.photo)
                        }
                    } else {
                        Log.e("EditProfileActivity", "User data is empty")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("EditProfileActivity", "Error fetching user data: ${error.message}")
                }
            })
        } else {
            Log.e("EditProfileActivity", "User is not logged in.")
        }
    }
}