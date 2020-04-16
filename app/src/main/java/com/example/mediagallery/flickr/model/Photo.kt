package com.example.mediagallery.flickr.model

import androidx.recyclerview.widget.DiffUtil

data class Photo constructor(
    val id: String,
    val url: String,
    val title: String) {

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<Photo>() {
            override fun areItemsTheSame(oldItem: Photo, newItem: Photo) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: Photo, newItem: Photo) =
                oldItem == newItem
        }
    }
}

