// SubCategoryAdapter.kt
package com.example.quickcart.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat.startActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.quickcart.activity.SubCategoryActivity
import com.example.quickcart.activity.SubSubCategoryActivity
import com.example.quickcart.databinding.SubCategoryUiBinding
import com.example.quickcart.model.SubCategory

class SubCategoryAdapter(
    private val context: Context,
    private val subCategoryList: ArrayList<SubCategory>,
    //private val categoryId: String
) : RecyclerView.Adapter<SubCategoryAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = SubCategoryUiBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount() = subCategoryList.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(subCategoryList[position])
    }

    inner class ViewHolder(private val binding: SubCategoryUiBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(subCategory: SubCategory) {
            binding.subCategoryName.text = subCategory.subCategoryName
            Glide.with(binding.root.context)
                .load(subCategory.subCategoryImage)
                .into(binding.subCategoryImage)
            itemView.setOnClickListener {
                // Handle item click here
                val intent = Intent(itemView.context, SubSubCategoryActivity::class.java)
                intent.also {
                    it.putExtra("subCategoryId", subCategory.id)
                    it.putExtra("subCategoryName", subCategory.subCategoryName)
                    //Direct access category id from SubCategoryActivity
                    it.putExtra("categoryId",(context as SubCategoryActivity).categoryId)
                }
                startActivity(itemView.context, intent, null)
            }
        }
    }
}