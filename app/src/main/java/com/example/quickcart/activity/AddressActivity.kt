package com.example.quickcart.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quickcart.R
import com.example.quickcart.databinding.ActivityAdressBinding
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddressActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAdressBinding
    private lateinit var database: FirebaseDatabase
    private lateinit var databaseReference: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityAdressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        // Initialize Firebase
        database = FirebaseDatabase.getInstance()

        setUpAddress()
        setOnClick()
        enabled()
        binding.floatingActionButton.setOnClickListener {
            showBottomSheetDialog()
        }
        // Handle back button click
        binding.back.setOnClickListener {
            finish()
        }
    }

    private fun setUpAddress() {
        // Show the progress bar before starting to load the data
        binding.progressBar4.visibility = View.VISIBLE
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        databaseReference = database.reference.child("user").child(userId).child("Address")
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val name = snapshot.child("name").value.toString()
                    val mobileNumber = snapshot.child("mobileNumber").value.toString()
                    val house = snapshot.child("house").value.toString()
                    val road = snapshot.child("road").value.toString()
                    val pincode = snapshot.child("pincode").value.toString()
                    val state = snapshot.child("state").value.toString()
                    val city = snapshot.child("city").value.toString()
                    binding.name.setText(name)
                    binding.mobileNumber.setText(mobileNumber)
                    binding.house.setText(house)
                    binding.road.setText(road)
                    binding.pincode.setText(pincode)
                    binding.state.setText(state)
                    binding.city.setText(city)
                }
                // Hide the progress bar once data is loaded
                binding.progressBar4.visibility = View.GONE
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@AddressActivity, "Error", Toast.LENGTH_SHORT).show()
                // Hide the progress bar once data is loaded
                binding.progressBar4.visibility = View.GONE
            }
        })
    }

    private fun enabled() {
        binding.apply {
            name.isEnabled = false
            mobileNumber.isEnabled = false
            pincode.isEnabled = false
            state.isEnabled = false
            city.isEnabled = false
            house.isEnabled = false
            road.isEnabled = false
        }
    }

    private fun setOnClick() {
        binding.continueToPayment.setOnClickListener {
            if ((binding.name.text.toString().isEmpty()) || (binding.mobileNumber.text.toString()
                    .isEmpty()) || (binding.house.text.toString()
                    .isEmpty()) || (binding.road.text.toString()
                    .isEmpty()) || (binding.pincode.text.toString().isEmpty())
            ) {
                Toast.makeText(this, "All the fields are required", Toast.LENGTH_SHORT).show()
            } else {
                startActivity(Intent(this@AddressActivity, PaymentMethodActivity::class.java))
            }
        }
    }

    private fun showBottomSheetDialog() {
        // Inflate the bottom sheet layout
        val view = layoutInflater.inflate(R.layout.input_address, null)

        // Create the BottomSheetDialog
        val dialog = BottomSheetDialog(this)
        dialog.setContentView(view)

        // Initialize dialog elements (if needed)
        val nameEditText: EditText = view.findViewById(R.id.name)
        val mobileNumberEditText: EditText = view.findViewById(R.id.mobileNumber)
        val houseEditText: EditText = view.findViewById(R.id.house)
        val roadEditText: EditText = view.findViewById(R.id.road)
        val pincodeEditText: EditText = view.findViewById(R.id.pincode)
        val stateEditText: EditText = view.findViewById(R.id.state)
        val cityEditText: EditText = view.findViewById(R.id.city)

        val save: Button = view.findViewById(R.id.save)
        save.setOnClickListener {
            // Perform your action (e.g., form submission)
            val name = nameEditText.text.toString()
            val mobileNumber = mobileNumberEditText.text.toString()
            val house = houseEditText.text.toString()
            val road = roadEditText.text.toString()
            val pincode = pincodeEditText.text.toString()
            val state = stateEditText.text.toString()
            val city = cityEditText.text.toString()
            if (name.isEmpty() || mobileNumber.isEmpty() || house.isEmpty() || road.isEmpty() || pincode.isEmpty() || state.isEmpty() || city.isEmpty()) {
                Toast.makeText(this, "All the fields are required", Toast.LENGTH_SHORT).show()
            } else {
                addAddress(name, mobileNumber, house, road, pincode, state, city)
                dialog.dismiss()
            }
        }

        // Show the bottom sheet dialog
        dialog.show()
    }

    private fun addAddress(
        name: String,
        mobileNumber: String,
        house: String,
        road: String,
        pincode: String,
        state: String,
        city: String
    ) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

        // Reference to the "address" node for the current user
        databaseReference = database.reference.child("user").child(userId).child("Address")

        // Create a map or data class to store the address details
        val addressMap = mapOf<String, Any>(
            "name" to name,
            "mobileNumber" to mobileNumber,
            "house" to house,
            "road" to road,
            "pincode" to pincode,
            "state" to state,
            "city" to city
        )

        // Save the address to the "Address" node in the database
        databaseReference.setValue(addressMap).addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(this, "Address saved successfully", Toast.LENGTH_SHORT).show()
                setUpAddress()
            } else {
                Toast.makeText(this, "Failed to save address", Toast.LENGTH_SHORT).show()
            }
        }
    }
}

















//// Assume that you have initialized Firebase Database
//val database = FirebaseDatabase.getInstance().reference
//
//fun placeOrder(userId: String) {
//    // Create references to CartItem and Address
//    val cartItemRef = database.child("user").child(userId).child("CartItem")
//    val addressRef = database.child("user").child(userId).child("Address")
//
//    // Retrieve CartItem data
//    cartItemRef.addListenerForSingleValueEvent(object : ValueEventListener {
//        override fun onDataChange(cartSnapshot: DataSnapshot) {
//            val cartItems = mutableListOf<CartItem>() // Assuming you have a CartItem data model
//
//            // Loop through all cart items
//            for (itemSnapshot in cartSnapshot.children) {
//                val cartItem = itemSnapshot.getValue(CartItem::class.java)
//                if (cartItem != null) {
//                    cartItems.add(cartItem)
//                }
//            }
//
//            // Retrieve Address data after getting CartItem
//            addressRef.addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(addressSnapshot: DataSnapshot) {
//                    val address = addressSnapshot.getValue(Address::class.java) // Assuming you have an Address data model
//
//                    if (address != null) {
//                        // Combine the cartItems and address data to place the order
//                        placeOrderInDatabase(userId, cartItems, address)
//                    }
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    // Handle error if needed
//                    Log.e("FirebaseError", "Failed to retrieve address: ${error.message}")
//                }
//            })
//        }
//
//        override fun onCancelled(error: DatabaseError) {
//            // Handle error if needed
//            Log.e("FirebaseError", "Failed to retrieve cart items: ${error.message}")
//        }
//    })
//}
//
//fun placeOrderInDatabase(userId: String, cartItems: List<CartItem>, address: Address) {
//    // Create an Order object containing the cart items and address
//    val order = Order(
//        cartItems = cartItems,
//        address = address,
//        orderDate = System.currentTimeMillis()
//    )
//
//    // Push the order data to the "Order" table under the userId
//    database.child("user").child(userId).child("Order").push().setValue(order)
//        .addOnCompleteListener { task ->
//            if (task.isSuccessful) {
//                Log.d("OrderPlacement", "Order successfully placed!")
//            } else {
//                Log.e("OrderPlacement", "Failed to place order: ${task.exception?.message}")
//            }
//        }
//}
