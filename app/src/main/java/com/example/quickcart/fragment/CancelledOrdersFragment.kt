package com.example.quickcart.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.quickcart.R
import com.example.quickcart.adapter.ActiveOrderAdapter
import com.example.quickcart.adapter.CancelledOrderAdapter
import com.example.quickcart.databinding.FragmentCancelledOrdersBinding
import com.example.quickcart.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CancelledOrdersFragment : Fragment() {
    private var _binding: FragmentCancelledOrdersBinding? = null
    private val binding get() = _binding!!
    private lateinit var orderAdapter: CancelledOrderAdapter
    private lateinit var orderList: ArrayList<Order>
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    private lateinit var auth: FirebaseAuth
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentCancelledOrdersBinding.inflate(layoutInflater, container, false)
        initialization()
        fetchUserOrders()
        return binding.root
    }
    private fun initialization() {
        orderList = ArrayList()
        database = FirebaseDatabase.getInstance()
        databaseReference = database.reference
        auth = FirebaseAuth.getInstance()

        // Initialize the adapter with the empty order list
        orderAdapter = CancelledOrderAdapter(orderList, requireContext())
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        // Set the adapter to the RecyclerView
        binding.recycler.adapter = orderAdapter
    }

    private fun fetchUserOrders() {
        binding.progressBar.visibility = View.VISIBLE
        val currentUser = auth.currentUser

        if (currentUser == null) {
            // Handle case when user is not authenticated
            Log.e("test", "fetchUserOrders: User not authenticated.")
            return
        }

        val userId = currentUser.uid

        // Query the database for orders belonging to the current user
        val orderRef =
            databaseReference.child("orders").child(userId) // Use "userId" instead of "user_id"

        orderRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                orderList.clear()
                for (orderSnapshot in snapshot.children) {
                    val order = orderSnapshot.getValue(Order::class.java)
                    if (order != null) {
                        orderList.add(order)
                    } else {
                        Log.w("test", "onDataChange: Received null order from snapshot.")
                    }
                }
                // Update UI based on fetched orders
                if (orderList.isEmpty()) {
                    binding.orderNow.setImageResource(R.drawable.ordernow)
                } else {
                    binding.orderNow.visibility = View.GONE
                }
                orderList.sortByDescending { it.orderDate }
                orderAdapter.notifyDataSetChanged()
                binding.progressBar.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error here
                Log.e("test", "onCancelled: Error fetching orders: ${error.message}")
            }
        })
    }
    override fun onDestroy() {
        super.onDestroy()
        _binding = null
    }
}