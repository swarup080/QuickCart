package com.example.quickcart.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quickcart.R
import com.example.quickcart.databinding.StatusUiBinding
import com.example.quickcart.databinding.StatusUiChildBinding
import com.example.quickcart.model.Order
import com.example.quickcart.model.CartItem
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ActiveOrderAdapter(private val orderList: ArrayList<Order>, private val context: Context) :
    RecyclerView.Adapter<ActiveOrderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StatusUiBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(orderList[position])
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    inner class ViewHolder(private val binding: StatusUiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.apply {
                // Set up the RecyclerView for cart items
                val cartItemAdapter = CartItemAdapter(order.cartItems,order.deliveryDate) // Create an adapter for the cart items
                cartItemsRecyclerView.adapter = cartItemAdapter
                cartItemsRecyclerView.layoutManager = LinearLayoutManager(context) // Set the layout manager
            }
        }
    }
}

// CartItemAdapter (for displaying cart items)
class CartItemAdapter(private val cartItems: List<CartItem>, private val order: Long) : RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        // Inflate your item layout for cart items here
        val binding = StatusUiChildBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        // Bind cart item data here
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int {
        return cartItems.size
    }

    inner class CartItemViewHolder(private val binding: StatusUiChildBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartItem) {
            // Initialize your views for cart item layout here
            binding.apply {
                // Load the product image using Glide
                Glide.with(itemView.context)
                    .load(cartItem.productImage) // Assuming productImage is a URL or drawable resource
                    .placeholder(R.drawable.landscape_placeholder_svgrepo_com) // Placeholder image while loading
                    .into(productImage)
                productDeliverStatus.setTextColor(Color.parseColor("#009721"))
                // Set delivery status
                if (order > 0) {
                    // Convert Long timestamp stored in 'order' to Date and format it
                    val deliveryDateFormatted = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(
                        Date(order) // 'order' is assumed to be the Long value (timestamp)
                    )
                    productDeliverStatus.text = "Expected Delivery on, $deliveryDateFormatted"
                } else {
                    productDeliverStatus.text = "No Delivery Date Available"
                }
                // Set product name
                productName.text = cartItem.productName
            }
        }
    }
}
