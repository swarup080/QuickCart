package com.example.quickcart.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quickcart.databinding.CartUiBinding
import com.example.quickcart.model.CartItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartAdapter(
    private val context: Context,
    private val cartList: ArrayList<CartItem>,
    private val onItemDeleted: (CartItem) -> Unit // Callback for item deletion
) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    // Instance of Firebase
    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()
    private var cartItemReference: DatabaseReference

    init {
        // Initialize firebase
        val userId = auth.currentUser?.uid ?: ""
        cartItemReference = database.reference.child("user").child(userId).child("CartItem")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = CartUiBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(cartList[position])
    }

    override fun getItemCount(): Int {
        return cartList.size
    }

    inner class ViewHolder(private val binding: CartUiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartItem) {
            binding.apply {
                Glide.with(root.context).load(cartItem.productImage).into(productImage)
                productName.text = cartItem.productName
                productPrice.text = "₹" + cartItem.productPrice
                productModelName.text = "Model: ${cartItem.productModelName}"
                Log.e("check", "Model: " + cartItem.productModelName.toString())
                unitPrice.text = "Unit Price: ${cartItem.productPrice}"
                delete.setOnClickListener {
                    deleteItem(adapterPosition)
                }
            }
        }
    }

    private fun deleteItem(position: Int) {
        getUniqueKeyAtPosition(position) { uniqueKey ->
            if (uniqueKey != null) {
                removeData(uniqueKey, position)
            }
        }
    }

    private fun removeData(uniqueKey: String, position: Int) {
        cartItemReference.child(uniqueKey).removeValue().addOnSuccessListener {
            // Get the item before removing it to pass it in the callback
            val deletedItem = cartList[position]

            // Remove item from the list
            cartList.removeAt(position)
            notifyItemRemoved(position)
            notifyItemRangeChanged(position, cartList.size)

            // Trigger the callback to update the total price
            onItemDeleted(deletedItem)
        }.addOnFailureListener {
            // Handle failure if needed
        }
    }

    private fun getUniqueKeyAtPosition(position: Int, onComplete: ((String) -> Unit)?) {
        cartItemReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                var uniqueKey: String? = null
                // Loop for snapshot Children
                snapshot.children.forEachIndexed { index, dataSnapshot ->
                    if (index == position) {
                        uniqueKey = dataSnapshot.key
                        return@forEachIndexed
                    }
                }
                uniqueKey?.let {
                    onComplete?.invoke(it)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle cancellation error if needed
            }
        })
    }
}
