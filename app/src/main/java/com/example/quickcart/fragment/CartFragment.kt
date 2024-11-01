package com.example.quickcart.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickcart.activity.AddressActivity
import com.example.quickcart.adapter.CartAdapter
import com.example.quickcart.databinding.FragmentCartBinding
import com.example.quickcart.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CartFragment : Fragment() {
    // Declare a private variable for the binding
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartItem: ArrayList<CartItem>
    private lateinit var cartAdapter: CartAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var database: FirebaseDatabase
    private lateinit var userId: String
    private var totalPrice: Double = 0.0
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using binding
        _binding = FragmentCartBinding.inflate(inflater, container, false)

        initialization()
        setUpRecyclerView()
        retrieveCartDetails()
        binding.checkOut.setOnClickListener {
            if (totalPrice > 0){
                val intent = Intent(requireContext(), AddressActivity::class.java)
                startActivity(intent)
            }else
                Toast.makeText(requireContext(), "Cart is empty", Toast.LENGTH_SHORT).show()
        }
        // Handle back button click
        binding.back.setOnClickListener {
            requireActivity().onBackPressed()
        }
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Nullify the binding to avoid memory leaks
        _binding = null
    }

    private fun initialization() {
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance()
        cartItem = ArrayList()

        // Initialize the adapter and set up the item deletion callback
        cartAdapter = CartAdapter(requireContext(), cartItem) { deletedItem ->
            // Deduct the deleted item's price from the total price
            totalPrice -= (deletedItem.productPrice?.toDouble() ?: 0.0) * (deletedItem.quantity
                ?: 1)
            // Update the total price in the UI
            binding.productPrice.text = "₹${String.format("%.2f", totalPrice)}"
        }
    }

    private fun setUpRecyclerView() {
        binding.recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        binding.recyclerView.adapter = cartAdapter
        binding.empty.visibility = View.VISIBLE
    }

    private fun retrieveCartDetails() {
        // Check if binding is null
        val binding = _binding ?: return
        // Show the ProgressBar
        binding.progressBar.visibility = View.VISIBLE
        userId = auth.currentUser?.uid ?: ""
        val itemRef = database.reference.child("user").child(userId).child("CartItem")
        itemRef.addListenerForSingleValueEvent(object : ValueEventListener {
            @SuppressLint("NotifyDataSetChanged")
            override fun onDataChange(snapshot: DataSnapshot) {
                cartItem.clear()
                totalPrice = 0.0
                for (i in snapshot.children) {
                    val data = i.getValue(CartItem::class.java)
                    data?.let {
                        cartItem.add(it)
                        Log.d(
                            "CartFragment",
                            "Retrieved item: ${it.productName}, Model: ${it.productModelName ?: "null"}"
                        )
                        // Calculate total price by multiplying productPrice by quantity
                        totalPrice += (it.productPrice?.toDouble() ?: 0.0) * (it.quantity ?: 1)
                    }
                }
                // Notify the adapter of data changes
                cartAdapter.notifyDataSetChanged()
                // Check if binding is still valid
                _binding?.let {
                    it.progressBar.visibility = View.GONE
                    binding.empty.visibility = if (cartItem.isEmpty()) View.VISIBLE else View.GONE
                    binding.productPrice.text = "₹${String.format("%.2f", totalPrice)}"
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Check if binding is still valid
                _binding?.let {
                    it.progressBar.visibility = View.GONE
                    binding.empty.visibility = View.VISIBLE
                }
            }
        })
    }
}