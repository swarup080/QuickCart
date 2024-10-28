package com.example.quickcart.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickcart.adapter.SubCategoryAdapter
import com.example.quickcart.databinding.ActivitySubCategoryBinding
import com.example.quickcart.model.SubCategory
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class SubCategoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySubCategoryBinding
    private lateinit var database: FirebaseDatabase
    lateinit var categoryId: String
    private lateinit var subCategoryList: ArrayList<SubCategory>
    private lateinit var subCategoryAdapter: SubCategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySubCategoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initialization()
        retrieveFromIntent()
        fetchDataFromSubCategories()
    }

    private fun fetchDataFromSubCategories() {
        binding.progressBar5.visibility = android.view.View.VISIBLE
        val categoryRef = database.reference.child("categories").child(categoryId).child("subcategories")
        categoryRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (subCategorySnapshot in snapshot.children) {
                        val subCategory = subCategorySnapshot.getValue(SubCategory::class.java)
                        subCategory?.let { subCategoryList.add(it) }
                        binding.progressBar5.visibility = android.view.View.GONE
                    }
                    // Notify the adapter after all items are added
                    subCategoryAdapter.notifyDataSetChanged()
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
        binding.textView12.text = intent.getStringExtra("categoryName")
    }

    private fun initialization() {
        database = FirebaseDatabase.getInstance()
        subCategoryList = ArrayList()
        subCategoryAdapter = SubCategoryAdapter(this, subCategoryList)
        binding.recyclerView2.apply {
            layoutManager = LinearLayoutManager(this@SubCategoryActivity)
            adapter = subCategoryAdapter
        }
    }

    fun back(view: View) {
        finish()
    }
}
