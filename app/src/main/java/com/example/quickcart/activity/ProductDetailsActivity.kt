package com.example.quickcart.activity

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.quickcart.adapter.ProductColors
import com.example.quickcart.adapter.ProductSizeAdapter
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
    private var productQuantity: Int = 1
    private var productModelName: String? = null
    private var productDescription: String? = null
    private var currentPrice: Double = 0.0
    private var priceInstance: Double = 0.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        productName = intent.getStringExtra("productName")
        productPrice = intent.getStringExtra("productPrice")
        productRating = intent.getStringExtra("productRating")
        productImage = intent.getStringExtra("productImage")
        productModelName = intent.getStringExtra("productModelName")
        productDescription = intent.getStringExtra("productDescription")

        if (productPrice != null) {
            currentPrice = productPrice!!.toDouble()
            priceInstance = productPrice!!.toDouble()
        } else {
            Toast.makeText(this, "Price is not available", Toast.LENGTH_SHORT).show()
            return
        }

        quantityPlus()
        quantityMinus()

        binding.productName.text = productName
        binding.productPrice.text = "₹" + String.format("%.2f", currentPrice) // Formatted to 2 decimal places
        binding.productRating.text = productRating
        binding.modelName.text = productModelName
        binding.descroption.text = productDescription
        Glide.with(this).load(productImage).into(binding.productImage)

        auth = FirebaseAuth.getInstance()
        setOnClick()
        setUpColor()
        setUpSize()
    }

    private fun updatePriceAndQuantity() {
        currentPrice = priceInstance * productQuantity
        binding.quantity.text = productQuantity.toString()
        binding.productPrice.text = "₹" + String.format("%.2f", currentPrice) // Format price to 2 decimal places
    }

    private fun quantityMinus() {
        binding.quantityMinus.setOnClickListener {
            if (productQuantity > 1) {
                productQuantity--
                updatePriceAndQuantity()
            } else {
                Toast.makeText(this, "Minimum Limit Reached", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun quantityPlus() {
        binding.quantityPlus.setOnClickListener {
            if (productQuantity < 10) {
                productQuantity++
                updatePriceAndQuantity()
            } else {
                Toast.makeText(this, "Maximum Limit Exceeded", Toast.LENGTH_SHORT).show()
            }
        }
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
            productPrice = String.format("%.2f", currentPrice), // Format price to 2 decimal places
            productRating = productRating,
            productDescription = productDescription,
            productImage = productImage,
            productModelName = productModelName,
            quantity = productQuantity
        )
        // Save cart data
        database.child("user").child(userId).child("CartItem").push().setValue(cartItem)
            .addOnSuccessListener {
                Toast.makeText(this, "Product Added Successfully", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Product Added Failed", Toast.LENGTH_SHORT).show()
            }
    }
    private fun setUpColor(){
        val colors = listOf("#FFFFFF", "#000000", "#CADCA7","#F79F1F") // Example colors from backend
        val adapter = ProductColors(colors) { selectedColor ->
            // Handle the selected color, e.g., update the product preview
            //Toast.makeText(this, "Selected Color: $selectedColor", Toast.LENGTH_SHORT).show()
        }

        binding.colorRecyclerView.adapter = adapter
        binding.colorRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
    }
    private fun setUpSize(){
        val sizes = listOf("S", "M", "L", "XL",) // Example sizes

        val sizeAdapter = ProductSizeAdapter(sizes) { selectedSize ->
            // Handle the selected size
            //Toast.makeText(this, "Selected size: $selectedSize", Toast.LENGTH_SHORT).show()
        }

        binding.recyclerSize.adapter = sizeAdapter
        binding.recyclerSize.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
    }

    fun back(view: View) {
        finish()
    }
}
