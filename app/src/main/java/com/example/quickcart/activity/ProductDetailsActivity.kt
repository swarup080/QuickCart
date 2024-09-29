package com.example.quickcart.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.quickcart.R
import com.example.quickcart.databinding.ActivityProductDetailsBinding
import com.example.quickcart.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class ProductDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailsBinding
    private lateinit var auth: FirebaseAuth
    private var productName: String? = null
    private var productPrice: String? = null
    private var productRating: String? = null
    private var productImage: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        productName = intent.getStringExtra("productName")
        productPrice = intent.getStringExtra("productPrice")
        productRating = intent.getStringExtra("productRating")
        productImage = intent.getStringExtra("productImage")

        binding.productName.text = productName
        binding.productPrice.text = productPrice
        binding.productRating.text = productRating
        Glide.with(this).load(productImage).into(binding.productImage)

        auth = FirebaseAuth.getInstance()
        setOnClick()
    }

    private fun setOnClick() {
        binding.addToCart.setOnClickListener {
            addProductToCart()
        }
    }

    private fun addProductToCart() {
        val database = FirebaseDatabase.getInstance().reference
        val userId = auth.currentUser?.uid ?: ""
        val cartItem = CartItem(
            productName = productName,
            productPrice = productPrice,
            productRating = productRating,
            productDescription = "",
            productImage = productImage,
            productModelName = "",
            quantity = 1
        )
        // Save cart data
        database.child("user").child(userId).child("CartItem").push().setValue(cartItem)
            .addOnSuccessListener {
                Toast.makeText(this,"Product Added Successfully",Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this,"Product Added Failed",Toast.LENGTH_SHORT).show()
            }
    }
}