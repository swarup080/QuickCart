package com.example.quickcart.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.quickcart.databinding.ProductColorsUiBinding

class ProductColors(
    private val colors: List<String>,
    private val onColorSelected: (String) -> Unit
) : RecyclerView.Adapter<ProductColors.ViewHolder>() {

    private var selectedPosition = -1  // Tracks selected color position

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ProductColorsUiBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int = colors.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(private val binding: ProductColorsUiBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(position: Int) {
            val colorHex = colors[position]

            // Set CardView color from the hex string
            binding.card.setCardBackgroundColor(Color.parseColor(colorHex))

            // Toggle visibility of selection indicator (e.g., an image inside the CardView)
            binding.setImage.visibility = if (position == selectedPosition) View.VISIBLE else View.GONE

            // Handle click event to update the selected color
            binding.root.setOnClickListener {
                if (selectedPosition != position) {
                    // Update old and new selection
                    notifyItemChanged(selectedPosition)
                    selectedPosition = position
                    notifyItemChanged(selectedPosition)

                    // Trigger the callback with the selected color
                    onColorSelected(colorHex)
                }
            }
        }
    }
}
