package com.example.quickcart.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel
import com.example.quickcart.R
import com.example.quickcart.adapter.CategoryBaseAdapter
import com.example.quickcart.adapter.NewProductAdapter
import com.example.quickcart.adapter.TopSellingProductAdapter
import com.example.quickcart.databinding.FragmentHomeBinding
import com.example.quickcart.model.Category
import com.example.quickcart.model.Product
import com.example.quickcart.model.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var topProductList: ArrayList<Product>
    private lateinit var newProductList: ArrayList<Product>
    private lateinit var categoryList: ArrayList<Category>
    private lateinit var categoryBaseAdapter: CategoryBaseAdapter
    private lateinit var topSellingProductAdapter: TopSellingProductAdapter
    private lateinit var newProductAdapter: NewProductAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        // Initialize lists
        topProductList = ArrayList()
        newProductList = ArrayList()
        categoryList = ArrayList()

        // Initialize database reference
        database = FirebaseDatabase.getInstance()
        databaseReference = database.reference

        // Initialize the adapters
        topSellingProductAdapter = TopSellingProductAdapter(requireContext(), topProductList)
        newProductAdapter = NewProductAdapter(requireContext(), newProductList)
        categoryBaseAdapter = CategoryBaseAdapter(requireContext(), categoryList)

        // Set up the RecyclerViews
        setupRecyclerViews()

        // Load data sections
        banner()
        loadData()

        // Fetch and display user data
        displayUserData()

        return binding.root
    }

    private fun displayUserData() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        val userId = currentUser?.uid

        if (userId != null) {
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
                        binding.userName.text = "Hi 👋 $firstName $lastName"
                        profilePhotoUrl?.let {
                            // Use a library like Glide or Picasso to load the image
                            Glide.with(this@HomeFragment)
                                .load(it)
                                .placeholder(R.drawable.landscape_placeholder_svgrepo_com)
                                .into(binding.profileImage)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setupRecyclerViews() {
        binding.topSellingProduct.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.topSellingProduct.adapter = topSellingProductAdapter

        binding.newProducts.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.newProducts.adapter = newProductAdapter

        binding.categoryList.adapter = categoryBaseAdapter
    }

    private fun banner() {
        val imageList = ArrayList<SlideModel>()
        imageList.add(SlideModel("https://firebasestorage.googleapis.com/v0/b/quickcart-2024.appspot.com/o/product_images%2F5658924.jpg?alt=media&token=e740b09a-591c-48b0-9eac-6264115f224b", ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel("https://firebasestorage.googleapis.com/v0/b/quickcart-2024.appspot.com/o/product_images%2F5658924.jpg?alt=media&token=e740b09a-591c-48b0-9eac-6264115f224b", ScaleTypes.CENTER_CROP))
        imageList.add(SlideModel("https://firebasestorage.googleapis.com/v0/b/quickcart-2024.appspot.com/o/product_images%2F5658924.jpg?alt=media&token=e740b09a-591c-48b0-9eac-6264115f224b", ScaleTypes.CENTER_CROP))
        binding.imageSlider.setImageList(imageList)
    }

    private fun loadData() {
        fetchCategories()
        fetchTopSellingProducts()
        fetchNewProducts()
    }

    private fun fetchCategories() {
        val categoryRef = database.reference.child("categories")
        categoryRef.keepSynced(true) // Enable offline capabilities
        binding.progressBar10.visibility = View.VISIBLE
        // Fetch categories from the database
        categoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                categoryList.clear() // Clear existing data
                for (categorySnapshot in snapshot.children) {
                    val category = categorySnapshot.getValue(Category::class.java)
                    if (category != null) {
                        categoryList.add(category)
                    }
                }
                binding.progressBar10.visibility = View.GONE // Hide the progress bar after data loading
                categoryBaseAdapter.notifyDataSetChanged() // Notify the adapter
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Error fetching categories: ${error.message}")
                binding.progressBar10.visibility = View.GONE // Hide the progress bar in case of error
            }
        })
    }

    private fun fetchTopSellingProducts() {
        binding.progressBar2.visibility = View.VISIBLE
        val itemRef = database.reference.child("product")
        itemRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                topProductList.clear()
                for (i in snapshot.children) {
                    val data = i.getValue(Product::class.java)
                    data?.let { topProductList.add(it) }
                }
                topSellingProductAdapter.notifyDataSetChanged()
                binding.progressBar2.visibility = View.GONE // Hide the progress bar after data loading
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Error fetching top selling products: ${error.message}")
                binding.progressBar2.visibility = View.GONE // Hide the progress bar in case of error
            }
        })
    }

    private fun fetchNewProducts() {
        binding.progressBar3.visibility = View.VISIBLE
        val itemRef = database.reference.child("product")
        itemRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                newProductList.clear()
                for (i in snapshot.children) {
                    val data = i.getValue(Product::class.java)
                    data?.let { newProductList.add(it) }
                }
                newProductAdapter.notifyDataSetChanged()
                binding.progressBar3.visibility = View.GONE // Hide the progress bar after data loading
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("HomeFragment", "Error fetching new products: ${error.message}")
                binding.progressBar3.visibility = View.GONE // Hide the progress bar in case of error
            }
        })
    }
}
