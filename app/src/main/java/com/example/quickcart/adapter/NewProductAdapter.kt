package com.example.quickcart.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quickcart.activity.ProductDetailsActivity
import com.example.quickcart.databinding.ProductInfoBinding
import com.example.quickcart.model.Product

class NewProductAdapter(private val context: Context, private val productList: ArrayList<Product>) :
    RecyclerView.Adapter<NewProductAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProductInfoBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    class ViewHolder(private val binding: ProductInfoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                Glide.with(root.context).load(product.productImage).into(productImage)
                productName.text = product.productName
                productPrice.text = product.productPrice
                productRating.text = product.productRating

                itemView.setOnClickListener {
                    val intent = Intent(
                        root.context,
                        ProductDetailsActivity::class.java
                    ) // Replace TargetActivity with the name of the activity you want to navigate to

                    // You can pass data to the next activity using intent extras if needed
                    intent.putExtra("productName", product.productName)
                    intent.putExtra("productPrice", product.productPrice)
                    intent.putExtra("productRating", product.productRating)
                    intent.putExtra("productImage", product.productImage)
                    intent.putExtra("productModelName", product.productModelName)
                    intent.putExtra("productDescription", product.productDescription)


                    root.context.startActivity(intent)
                }
            }
        }

    }
}