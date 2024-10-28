package com.example.quickcart.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quickcart.R
import com.example.quickcart.activity.ProductDetailsActivity
import com.example.quickcart.databinding.ProductInfoBinding
import com.example.quickcart.model.Product

class TopSellingProductAdapter(
    private val context: Context,
    private val productList: ArrayList<Product>
) :
    RecyclerView.Adapter<TopSellingProductAdapter.ProductViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TopSellingProductAdapter.ProductViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.product_info, parent, false)
        return ProductViewHolder(view)
    }


    override fun onBindViewHolder(
        holder: TopSellingProductAdapter.ProductViewHolder,
        position: Int
    ) {
        val product = productList[position]
        Glide.with(holder.itemView.context).load(product.productImage)
            .into(holder.binding.productImage)
        holder.binding.productName.text = product.productName
        holder.binding.productPrice.text = product.productPrice
        holder.binding.productRating.text = product.productRating
        holder.itemView.setOnClickListener {
            val intent = Intent(
                context,
                ProductDetailsActivity::class.java
            ) // Replace TargetActivity with the name of the activity you want to navigate to

            // You can pass data to the next activity using intent extras if needed
            intent.putExtra("productName", product.productName)
            intent.putExtra("productPrice", product.productPrice)
            intent.putExtra("productRating", product.productRating)
            intent.putExtra("productImage", product.productImage)
            intent.putExtra("productModelName", product.productModelName)
            intent.putExtra("productDescription", product.productDescription)


            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding: ProductInfoBinding = ProductInfoBinding.bind(itemView)
    }
}