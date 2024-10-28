package com.example.quickcart.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.bumptech.glide.Glide
import com.example.quickcart.R
import com.example.quickcart.activity.SubCategoryActivity
import com.example.quickcart.databinding.CategoryInfoBinding
import com.example.quickcart.model.Category

class CategoryBaseAdapter(private val context: Context, private val categoryList: ArrayList<Category>) : BaseAdapter() {

    override fun getCount() = categoryList.size

    override fun getItem(position: Int) = categoryList[position]

    override fun getItemId(position: Int) = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: CategoryInfoBinding
        val view: View

        if (convertView == null) {
            binding = CategoryInfoBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            view.tag = binding
        } else {
            binding = convertView.tag as CategoryInfoBinding
            view = convertView
        }

        val item = categoryList[position]
        Glide.with(context)
            .load(item.categoryImage) // Assuming you have a subCategoryImage for display
            .error(R.drawable.landscape_placeholder_svgrepo_com) // Handle image load error
            .into(binding.categoryImage)

        binding.categoryName.text = item.categoryName // Adjust based on your data

        view.setOnClickListener {
            // Handle click to show subcategories or navigate to a new activity
            val intent = Intent(context, SubCategoryActivity::class.java).apply {
                putExtra("categoryId", item.id) // Pass the ID of the selected category
                putExtra("categoryName", item.categoryName) // Pass the name of the selected category")
            }
            context.startActivity(intent)
        }

        return view
    }
}