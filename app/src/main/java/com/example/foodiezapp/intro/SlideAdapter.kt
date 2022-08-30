package com.example.foodiezapp.intro

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager.widget.PagerAdapter
import com.example.foodiezapp.R
import com.example.foodiezapp.databinding.ActivityIntroBinding
import com.example.foodiezapp.databinding.SlideBinding

class SlideAdapter(var modelList: List<SlideModel>, var context: Context) :
    RecyclerView.Adapter<SlideAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = SlideBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.binding.slideImg.setImageResource(modelList[position].image)
        holder.binding.textTitle.text = modelList[position].title
        holder.binding.textDescription.text = modelList[position].description
    }

    override fun getItemCount(): Int {
        return modelList.size
    }

   inner class ViewHolder(val binding: SlideBinding) : RecyclerView.ViewHolder(binding.root)
//    lateinit var layoutInflater: LayoutInflater
//
//    override fun getCount(): Int {
//        return modelList.size
//    }
//
//    override fun instantiateItem(container: ViewGroup, position: Int): Any {
//        layoutInflater = LayoutInflater.from(context)
//        var view = layoutInflater.inflate(R.layout.slide, container, false)
//
//        val img = view.findViewById<ImageView>(R.id.slide_imd)
//        val title = view.findViewById<TextView>(R.id.text_title)
//        val description = view.findViewById<TextView>(R.id.text_description)
//
//        img.setImageResource(modelList[position].image)
//        title.text = modelList[position].title
//        description.text = modelList[position].description
//
//        container.addView(view, 0)
//        return view
//    }
//
//
//    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
//        container.removeView(`object` as View)
//    }
//
//    override fun isViewFromObject(view: View, `object`: Any): Boolean {
//        return view == `object`
//    }
}