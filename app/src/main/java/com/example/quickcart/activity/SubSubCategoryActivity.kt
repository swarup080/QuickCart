package com.example.quickcart.activity

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.example.quickcart.R
import com.example.quickcart.adapter.SubSubCategoryAdapter
import com.example.quickcart.databinding.ActivitySubSubCategoryBinding
import com.example.quickcart.model.SubCategory
import com.example.quickcart.model.SubSubCategory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SubSubCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubSubCategoryBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var categoryId: String
    private lateinit var subCategoryId: String
    private lateinit var subSubCategoryList: ArrayList<SubSubCategory>
    private lateinit var subSubCategoryAdapter: SubSubCategoryAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySubSubCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initialization()
        retrieveFromIntent()
        fetchDataFromSubCategories()
    }

    private fun fetchDataFromSubCategories() {
        binding.progressBar5.visibility = android.view.View.VISIBLE
        val categoryRef = database.reference.child("categories").child(categoryId).child("subcategories").child(subCategoryId).child("sub subcategories")
        categoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (subCategorySnapshot in snapshot.children) {
                        val subCategory = subCategorySnapshot.getValue(SubSubCategory::class.java)
                        subCategory?.let { subSubCategoryList.add(it) }
                        binding.progressBar5.visibility = android.view.View.GONE
                    }
                    // Notify the adapter after all items are added
                    subSubCategoryAdapter.notifyDataSetChanged()
                }
                binding.progressBar5.visibility = android.view.View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle possible errors
                binding.progressBar5.visibility = android.view.View.GONE
            }
        })
    }

    private fun retrieveFromIntent() {
        categoryId = intent.getStringExtra("categoryId") ?: ""
        subCategoryId = intent.getStringExtra("subCategoryId") ?: ""
        binding.textView12.text = intent.getStringExtra("subCategoryName")
    }

    private fun initialization() {
        database = FirebaseDatabase.getInstance()
        subSubCategoryList = ArrayList()
        subSubCategoryAdapter = SubSubCategoryAdapter(this, subSubCategoryList)
        binding.recyclerView2.apply {
            layoutManager = GridLayoutManager(this@SubSubCategoryActivity,2)
            adapter = subSubCategoryAdapter
        }
    }

    fun back(view: View) {
        finish()
    }
}