package com.example.suitup.intro

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import suitup.databinding.IntroSlideBinding

class SlideAdapter(var modelList: List<SlideModel>, var context: Context) :
    RecyclerView.Adapter<SlideAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = IntroSlideBinding.inflate(inflater, parent, false)
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

    inner class ViewHolder(val binding: IntroSlideBinding) : RecyclerView.ViewHolder(binding.root)
}