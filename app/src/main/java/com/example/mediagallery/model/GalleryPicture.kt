package com.example.mediagallery.model

import android.net.Uri
import androidx.recyclerview.widget.DiffUtil
import java.util.*

data class GalleryPicture(val id: Long,
                          val displayName: String,
                          val dateTaken: Date,
                          val contentUri: Uri) {

    companion object {
        val DiffCallback = object : DiffUtil.ItemCallback<GalleryPicture>() {
            override fun areItemsTheSame(oldItem: GalleryPicture, newItem: GalleryPicture) =
                oldItem.id == newItem.id

            override fun areContentsTheSame(oldItem: GalleryPicture, newItem: GalleryPicture) =
                oldItem == newItem
        }
    }
}