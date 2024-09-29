package com.example.quickcart.adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import com.example.quickcart.activity.SubCategoryActivity
import com.example.quickcart.databinding.CategoryInfoBinding
import com.example.quickcart.model.Category

class CategoryBaseAdapter(private val context: Context,private val categoryList: ArrayList<Category>) :BaseAdapter(){
    override fun getCount(): Int {
        return categoryList.size
    }

    override fun getItem(position: Int): Any {
        return categoryList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val binding: CategoryInfoBinding
        val view: View

        if (convertView == null) {
            binding = CategoryInfoBinding.inflate(LayoutInflater.from(context), parent, false)
            view = binding.root
            binding.also { view.tag = it }
        } else {
            binding = convertView.tag as CategoryInfoBinding
            view = convertView
        }

        val item = categoryList[position]
        item.categoryImage?.let { binding.categoryImage.setImageResource(it) }
        binding.categoryName.text = item.categoryName
        view.setOnClickListener {
            context.startActivity(Intent(context,SubCategoryActivity::class.java))
        }

        return view
    }
}