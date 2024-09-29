package com.example.quickcart.model

data class CartItem (
    val productImage: String? = null,
    val productName: String? = null,
    val productPrice: String? = null,
    val productRating: String? = null,
    val productDescription: String? = null,
    val productModelName: String? = null,
    val quantity: Int? = null
)