package com.example.foodiezapp.main.ui.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.foodiezapp.databinding.DetailsPhotosItemBinding
import com.squareup.picasso.Picasso

class PhotosAdapter(private val clickListener: PhotosOnClickListener) :
    RecyclerView.Adapter<PhotosAdapter.ViewHolder>() {
    private var restaurantPhotos: List<String>? = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DetailsPhotosItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get().load(restaurantPhotos?.get(position)).into(holder.binding.detailsPhoto)
        holder.binding.detailsPhoto.setOnClickListener {
            restaurantPhotos?.get(position)?.let { photo -> clickListener.onClick(photo) }
        }
    }

    override fun getItemCount(): Int {
        return restaurantPhotos?.size ?: 0
    }

    fun setItems(restaurantPhotos: List<String>?) {
        this.restaurantPhotos = restaurantPhotos
    }

    inner class ViewHolder(val binding: DetailsPhotosItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}

class PhotosOnClickListener(val clickListener: (String) -> Unit) {
    fun onClick(restaurantPhoto: String) = clickListener(restaurantPhoto)
}