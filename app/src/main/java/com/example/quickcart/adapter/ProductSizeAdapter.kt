package com.example.quickcart.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.quickcart.R

import com.example.quickcart.databinding.ProductSizeUiBinding

class ProductSizeAdapter(
    private val sizes: List<String>,
    private val onSizeSelected: (String) -> Unit
) : RecyclerView.Adapter<ProductSizeAdapter.SizeViewHolder>() {

    private var selectedPosition = -1 // Default to no selection

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SizeViewHolder {
        val binding = ProductSizeUiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return SizeViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SizeViewHolder, position: Int) {
        holder.bind(sizes[position], position)
    }

    override fun getItemCount(): Int = sizes.size

    inner class SizeViewHolder(private val binding: ProductSizeUiBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(size: String, position: Int) {
            binding.productSize.text = size

            // Update background and text color based on selection
            if (position == selectedPosition) {
                binding.card.setBackgroundResource(R.drawable.size_bg_onselected)
                binding.productSize.setTextColor(ContextCompat.getColor(binding.root.context, R.color.white))
            } else {
                binding.card.setBackgroundResource(R.drawable.size_bg)
                binding.productSize.setTextColor(ContextCompat.getColor(binding.root.context, R.color.gray2))
            }

            // Handle click event
            binding.card.setOnClickListener {
                val previousPosition = selectedPosition
                selectedPosition = position
                notifyItemChanged(previousPosition) // Refresh previous selection
                notifyItemChanged(selectedPosition) // Refresh new selection
                onSizeSelected(size) // Notify selected size
            }
        }
    }
}
