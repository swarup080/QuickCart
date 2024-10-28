package com.example.quickcart.model

data class UserModel(
    val firstName: String = "",
    val lastName: String = "",
    val phoneNumber: String = "",
    val email: String = "",
    val password: String = "",
    val photo: String = "",
    val address: Address = Address() // Nested Address class with default empty Address
)
