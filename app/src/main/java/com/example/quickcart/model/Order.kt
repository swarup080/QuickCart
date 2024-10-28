package com.example.quickcart.model

data class Order(
    val orderId: String = "",
    val userId: String = "",
    val cartItems: List<CartItem> = listOf(),
    val address: Address = Address(),
    val totalOrderValue: String? = null,
    val orderDate: Long = 0,
    val paymentMethod: String = "",
    val orderAccepted: Boolean = false,
    val paymentReceived: Boolean = false,
    val deliveryDate: Long = 0,
    val canceledDate: Long = 0,
    val status: String = ""
)
