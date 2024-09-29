package com.example.quickcart.fragment

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.denzcoskun.imageslider.constants.ScaleTypes
import com.denzcoskun.imageslider.models.SlideModel

import com.example.quickcart.R
import com.example.quickcart.adapter.CategoryBaseAdapter
import com.example.quickcart.adapter.NewProductAdapter
import com.example.quickcart.adapter.TopSellingProductAdapter
import com.example.quickcart.databinding.FragmentHomeBinding
import com.example.quickcart.model.Category
import com.example.quickcart.model.Product
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class HomeFragment : Fragment() {


    // Declare a private variable for the binding
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var topProductList: ArrayList<Product>
    private lateinit var newProductList: ArrayList<Product>
    private lateinit var topSellingProductAdapter: TopSellingProductAdapter
    private lateinit var newProductAdapter: NewProductAdapter
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using binding
        _binding = FragmentHomeBinding.inflate(inflater, container, false)

        topProductList = ArrayList()
        newProductList = ArrayList()
        database = FirebaseDatabase.getInstance()
        databaseReference = FirebaseDatabase.getInstance().reference
        // Initialize the adapters with empty lists
        topSellingProductAdapter = TopSellingProductAdapter(requireContext(), topProductList)
        newProductAdapter = NewProductAdapter(requireContext(), newProductList)

        // Set up the RecyclerViews
        setupRecyclerViews()
        // Handle banner section
        banner()
        // Handle category section
        category()
        // Handle product section
        topSellingProduct()
        newProduct()
        return binding.root
    }



    override fun onDestroyView() {
        super.onDestroyView()
        // Nullify the binding to avoid memory leaks
        _binding = null
    }
    private fun setupRecyclerViews() {
        binding.topSellingProduct.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.topSellingProduct.adapter = topSellingProductAdapter

        binding.newProducts.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.newProducts.adapter = newProductAdapter
    }
    private fun banner() {
        val imageList = ArrayList<SlideModel>() // Create image list
        imageList.add(
            SlideModel(
                "https://bit.ly/2YoJ77H",
                "The animal population decreased by 58 percent in 42 years.",
                ScaleTypes.CENTER_CROP
            )
        )
        imageList.add(
            SlideModel(
                "https://bit.ly/2BteuF2",
                "Elephants and tigers may become extinct.",
                ScaleTypes.CENTER_CROP
            )
        )
        imageList.add(
            SlideModel(
                "https://bit.ly/3fLJf72",
                "And people do that.",
                ScaleTypes.CENTER_CROP
            )
        )
        binding.imageSlider.setImageList(imageList)
    }

    private fun category() {
        // Initialize the category list
        val categoryArrayList = arrayListOf(
            Category(R.drawable.landscape_placeholder_svgrepo_com, "Men"),
            Category(R.drawable.landscape_placeholder_svgrepo_com, "Women"),
            Category(R.drawable.landscape_placeholder_svgrepo_com, "Kid"),
            Category(R.drawable.landscape_placeholder_svgrepo_com, "Watch"),
            Category(R.drawable.landscape_placeholder_svgrepo_com, "Toy"),
            Category(R.drawable.landscape_placeholder_svgrepo_com, "Belt"),
            Category(R.drawable.landscape_placeholder_svgrepo_com, "Electronics"),
            Category(R.drawable.landscape_placeholder_svgrepo_com, "Home"),
            Category(R.drawable.landscape_placeholder_svgrepo_com, "Cooking"),
            Category(R.drawable.landscape_placeholder_svgrepo_com, "Camping")
        )

        // Initialize the adapter
        val categoryBaseAdapter = CategoryBaseAdapter(requireContext(), categoryArrayList)

        // Set the adapter to the ListView
        binding.categoryList.adapter = categoryBaseAdapter
    }
    private fun topSellingProduct(){
        // Early exit if binding is null
        val binding = _binding ?: return
        binding.progressBar2.visibility = View.VISIBLE
        val itemRef = database.reference.child("product")
        itemRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                topProductList.clear()
                for (i in snapshot.children){
                    val data = i.getValue(Product::class.java)
                    data?.let {
                        topProductList.add(it)
                    }
                    // Safely update UI
                    _binding?.let {
                        topSellingProductAdapter.notifyDataSetChanged()
                        it.progressBar2.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _binding?.progressBar2?.visibility = View.GONE
            }
        })

    }
    private fun newProduct() {
        val binding = _binding ?: return
        binding.progressBar3.visibility = View.VISIBLE
        val itemRef = database.reference.child("product")
        itemRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                newProductList.clear()
                for (i in snapshot.children){
                    val data = i.getValue(Product::class.java)
                    data?.let {
                        newProductList.add(it)
                    }
                    _binding?.let {
                        newProductAdapter.notifyDataSetChanged()
                        it.progressBar3.visibility = View.GONE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _binding?.progressBar3?.visibility = View.GONE
            }
        })
    }

    private fun setAdapterForNewProduct() {
        binding.newProducts.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        val adapter = NewProductAdapter( requireContext(),newProductList)
        binding.newProducts.adapter = adapter
    }

    private fun setAdapterForTopProduct(){
        binding.topSellingProduct.layoutManager = LinearLayoutManager(requireContext(),LinearLayoutManager.HORIZONTAL,false)
        val adapter = TopSellingProductAdapter( requireContext(),topProductList)
        binding.topSellingProduct.adapter = adapter
    }
}