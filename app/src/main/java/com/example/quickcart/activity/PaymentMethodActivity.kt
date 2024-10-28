package com.example.quickcart.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.RadioButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.quickcart.R
import com.example.quickcart.databinding.ActivityPaymentMethodBinding
import com.example.quickcart.model.Address
import com.example.quickcart.model.CartItem
import com.example.quickcart.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.razorpay.Checkout
import com.razorpay.PaymentData
import com.razorpay.PaymentResultWithDataListener
import org.json.JSONObject

class PaymentMethodActivity : AppCompatActivity(), PaymentResultWithDataListener {
    private lateinit var binding: ActivityPaymentMethodBinding
    val cartItems = mutableListOf<CartItem>()
    private var totalOrderValue: Double = 0.0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPaymentMethodBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Handle window insets for proper UI adjustments
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setOnClick()
        // Handle back button click
        binding.back.setOnClickListener {
            finish()
        }
    }

    private fun makeOrder() {
        val database = FirebaseDatabase.getInstance().reference
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val cartItemRef = database.child("user").child(userId).child("CartItem")
        val addressRef = database.child("user").child(userId).child("Address")
        //Retrieve CartItem data
        cartItemRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(cartSnapshot: DataSnapshot) {
                for (itemSnapshot in cartSnapshot.children) {
                    val cartItem = itemSnapshot.getValue(CartItem::class.java)
                    if (cartItem != null) {
                        cartItems.add(cartItem)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PaymentMethodActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        })
        //Retrieve Address data after getting CartItem
        addressRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(addressSnapshot: DataSnapshot) {
                val address = addressSnapshot.getValue(Address::class.java)
                if (address != null) {
                    //Save order details in db
                    makeOrderInDatabase(userId, cartItems, address)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PaymentMethodActivity, "Error", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun makeOrderInDatabase(
        userId: String,
        cartItems: MutableList<CartItem>,
        address: Address
    ) {
        val database = FirebaseDatabase.getInstance().reference
        // Save orders under the user's ID
        val orderRef = database.child("orders").child(userId)
        val orderId = orderRef.push().key // Generate a unique order ID
        Log.d("OrderDebug", "Generated order ID: $orderId")

        if (orderId != null) {
            // Calculate total order value, ensuring productPrice and quantity are handled safely
            totalOrderValue = cartItems.sumOf {
                (it.productPrice?.toDouble() ?: 0.0) * (it.quantity ?: 0)
            }

            // Create the Order object
            val order = Order(
                orderId = orderId,
                userId = userId,
                cartItems = cartItems,
                address = address,
                totalOrderValue = totalOrderValue.toString(),
                orderDate = System.currentTimeMillis()
            )

            // First, save the order
            orderRef.child(orderId).setValue(order).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(this, "Order successfully placed!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this, Dashboard::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    finish()
                    // Delete all cart items after the order is successfully placed
                    val cartItemRef = database.child("user").child(userId).child("CartItem")
                    cartItemRef.removeValue().addOnCompleteListener { deleteTask ->
                        if (deleteTask.isSuccessful) {
                            Log.d("OrderDebug", "All cart items deleted successfully.")
                        } else {
                            Log.e("OrderDebug", "Failed to delete cart items.")
                        }
                    }
                } else {
                    Toast.makeText(this, "Failed to place order", Toast.LENGTH_SHORT).show()
                }
            }
        } else {
            Toast.makeText(this, "Failed to generate order ID", Toast.LENGTH_SHORT).show()
        }
    }


    private fun setOnClick() {
        // Radio button click listeners
        binding.radioCashOnDelivery.setOnClickListener {
            setButtonSelected(binding.radioCashOnDelivery)
            setButtonUnselected(binding.radioRazorpay)
        }

        binding.radioRazorpay.setOnClickListener {
            setButtonSelected(binding.radioRazorpay)
            setButtonUnselected(binding.radioCashOnDelivery)
        }
        // Continue button click listener
        binding.continueToPayment.setOnClickListener {
            if (binding.radioCashOnDelivery.isChecked) {
                makeOrder()

            } else if (binding.radioRazorpay.isChecked) {
                payWithRazorPay()
            } else {
                Toast.makeText(this, "Selected Payment Method: Invalid", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setButtonSelected(button: RadioButton) {
        button.isChecked = true
        button.buttonTintList = ContextCompat.getColorStateList(this, R.color.green)
    }

    private fun setButtonUnselected(button: RadioButton) {
        button.isChecked = false
        button.buttonTintList = ContextCompat.getColorStateList(this, R.color.black)
    }

    private fun payWithRazorPay() {
        Checkout.preload(applicationContext)
        val co = Checkout()
        co.setKeyID("rzp_test_1H0Xk3A2gXvcQa")

        try {
            val options = JSONObject()
            options.put("name", "QuickCart")
            options.put("description", "Shopping Application")
            options.put("image", R.drawable.quickcartlogo)
            options.put("theme.color", R.color.green)
            options.put("currency", "INR")
            options.put(
                "amount",
                (totalOrderValue * 100).toInt()
            )  // Convert to smallest currency unit

            val prefill = JSONObject()
            prefill.put("email", "swarupsen080@gmail.com")
            prefill.put("contact", "7431907651")

            options.put("prefill", prefill)
            co.open(this, options)
        } catch (e: Exception) {
            Toast.makeText(this, "Error in payment: ${e.message}", Toast.LENGTH_LONG).show()
            e.printStackTrace()
        }
    }

    override fun onPaymentSuccess(p0: String?, p1: PaymentData?) {
        makeOrder()
    }

    override fun onPaymentError(code: Int, response: String?, data: PaymentData?) {
        Log.e("PaymentError", "Code: $code, Response: $response")
        Toast.makeText(this, "Payment Failed: $response", Toast.LENGTH_SHORT).show()
    }
}