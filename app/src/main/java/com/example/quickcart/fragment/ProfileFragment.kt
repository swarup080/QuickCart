package com.example.quickcart.fragment

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.quickcart.R
import com.example.quickcart.activity.EditProfileActivity
import com.example.quickcart.activity.MainActivity
import com.example.quickcart.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ProfileFragment : Fragment() {
    // Declare a private variable for the binding
    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using binding
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        signOut()
        setOnClick()
        displayUserData()
        return binding.root
    }

    private fun displayUserData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
            val database = FirebaseDatabase.getInstance()
            val databaseReference = database.reference
            val userRef = databaseReference.child("user").child(userId)
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Check if the snapshot has data
                    if (snapshot.exists()) {
                        // Convert the DataSnapshot to a Map
                        val userDataMap = snapshot.value as? Map<String, Any>

                        // Extract firstName and lastName
                        val firstName = userDataMap?.get("firstName") as? String ?: ""
                        val lastName = userDataMap?.get("lastName") as? String ?: ""
                        val profilePhotoUrl = userDataMap?.get("photo") as? String

                        // Update UI with user data
                        binding.textView7.text = "Hi 👋 $firstName $lastName"
                        profilePhotoUrl?.let {
                            // Use a library like Glide or Picasso to load the image
                            Glide.with(this@ProfileFragment)
                                .load(it)
                                .placeholder(R.drawable.landscape_placeholder_svgrepo_com)
                                .into(binding.imageView9)
                        }
                    } else {
                        Log.e("HomeFragment", "User data is empty")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("HomeFragment", "Error fetching user data: ${error.message}")
                }
            })
        } else {
            Log.e("HomeFragment", "User is not logged in.")
        }
    }

    private fun setOnClick() {
        binding.developer.setOnClickListener {
            
        }
        binding.editProfile.setOnClickListener {
            startActivity(Intent(requireContext(), EditProfileActivity::class.java))
        }
    }


    private fun signOut() {
        binding.logOut.setOnClickListener {
            // Sign out the user
            val firebaseAuth = FirebaseAuth.getInstance()
            firebaseAuth.signOut()

            // Show a toast message
            Toast.makeText(requireContext(), "Logged out successfully", Toast.LENGTH_SHORT).show()

            // Navigate to the login screen (replace LoginActivity::class.java with your actual login activity)
            val intent = Intent(requireContext(), MainActivity::class.java)
            startActivity(intent)
            requireActivity().finish() // Close the current activity
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Nullify the binding to avoid memory leaks
        _binding = null
    }

}
