package com.example.suitup.main.ui.details.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import suitup.databinding.DetailsPhotosItemBinding

class PhotosAdapter(private val clickListener: PhotosOnClickListener) :
    RecyclerView.Adapter<PhotosAdapter.ViewHolder>() {
    private var storePhotos: List<String>? = emptyList()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = DetailsPhotosItemBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Picasso.get().load(storePhotos?.get(position)).into(holder.binding.detailsPhoto)
        holder.binding.detailsPhoto.setOnClickListener {
            storePhotos?.get(position)?.let { photo -> clickListener.onClick(photo) }
        }
    }

    override fun getItemCount(): Int {
        return storePhotos?.size ?: 0
    }

    fun setItems(storePhotos: List<String>?) {
        this.storePhotos = storePhotos
    }

    inner class ViewHolder(val binding: DetailsPhotosItemBinding) :
        RecyclerView.ViewHolder(binding.root)
}

class PhotosOnClickListener(val clickListener: (String) -> Unit) {
    fun onClick(storePhoto: String) = clickListener(storePhoto)
}