package com.example.quickcart.model

data class Category(
    val categoryImage: String = "",
    val categoryName: String = "",
    val id: String = "",
    val subcategories: Map<String, SubCategory> = mapOf() // Using Map to match Firebase structure
)

data class SubCategory(
    val subCategoryImage: String = "",
    val subCategoryName: String = "",
    val id: String = "",
    val subSubCategories: Map<String, SubSubCategory> = mapOf() // Using Map to match Firebase structure
)

data class SubSubCategory(
    val productDescription: String = "",
    val productImage: String = "",
    val productModelName: String = "",
    val productName: String = "",
    val productPrice: String = "",
    val productRating: String = "",
    val id: String = ""
)
