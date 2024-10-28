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
import com.example.quickcart.model.CartItem
import com.example.quickcart.model.Order
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CompleteOrderAdapter(private val orderList: ArrayList<Order>, private val context: Context) :
    RecyclerView.Adapter<CompleteOrderAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StatusUiBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return orderList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(orderList[position])
    }

    inner class ViewHolder(private val binding: StatusUiBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            binding.apply {
                // Set up the RecyclerView for cart items
                val cartItemAdapter = CartItemAdapterForComplete(order.cartItems, order.deliveryDate)
                cartItemsRecyclerView.layoutManager = LinearLayoutManager(context) // Set layout manager once
                cartItemsRecyclerView.adapter = cartItemAdapter
            }
        }
    }
}

// CartItemAdapter (for displaying cart items)
class CartItemAdapterForComplete(private val cartItems: List<CartItem>, private val deliveryDate: Long) :
    RecyclerView.Adapter<CartItemAdapterForComplete.CartItemViewHolder>() {

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
            binding.apply {
                // Load the product image using Glide
                Glide.with(itemView.context)
                    .load(cartItem.productImage)
                    .placeholder(R.drawable.landscape_placeholder_svgrepo_com)
                    .into(productImage)

                // Set delivery status
                productDeliverStatus.setTextColor(Color.parseColor("#009721"))
                if (deliveryDate > 0) {
                    // Convert Long timestamp stored in 'deliveryDate' to Date and format it
                    val deliveryDateFormatted = SimpleDateFormat("dd MMM yyyy", Locale.getDefault()).format(
                        Date(deliveryDate)
                    )
                    productDeliverStatus.text = "Delivered on, $deliveryDateFormatted"
                } else {
                    productDeliverStatus.text = "No Delivery Date Available"
                }
                // Set product name
                productName.text = cartItem.productName
            }
        }
    }
}
