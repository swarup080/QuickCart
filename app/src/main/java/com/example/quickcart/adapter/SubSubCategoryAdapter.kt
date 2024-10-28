package com.example.quickcart.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quickcart.activity.ProductDetailsActivity
import com.example.quickcart.databinding.ProductInfoBinding
import com.example.quickcart.model.SubSubCategory

class SubSubCategoryAdapter(
    private val context: Context,
    private val subSubCategoryList: ArrayList<SubSubCategory>
) : RecyclerView.Adapter<SubSubCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProductInfoBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return subSubCategoryList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        return holder.bind(subSubCategoryList[position])
    }

    class ViewHolder(private val binding: ProductInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(subSubCategory: SubSubCategory) {
            binding.apply {
                productName.text = subSubCategory.productName
                productPrice.text = subSubCategory.productPrice
                productRating.text = subSubCategory.productRating
                Glide.with(binding.root.context)
                    .load(subSubCategory.productImage)
                    .into(productImage)
                itemView.setOnClickListener {
                    val intent = Intent(
                        root.context,
                        ProductDetailsActivity::class.java
                    ) // Replace TargetActivity with the name of the activity you want to navigate to

                    // You can pass data to the next activity using intent extras if needed
                    intent.putExtra("productName", subSubCategory.productName)
                    intent.putExtra("productPrice", subSubCategory.productPrice)
                    intent.putExtra("productRating", subSubCategory.productRating)
                    intent.putExtra("productImage", subSubCategory.productImage)
                    intent.putExtra("productModelName", subSubCategory.productModelName)
                    intent.putExtra("productDescription", subSubCategory.productDescription)


                    root.context.startActivity(intent)
                }
            }
        }
    }
}