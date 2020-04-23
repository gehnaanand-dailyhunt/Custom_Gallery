package com.example.mediagallery.flickr.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mediagallery.R
import com.example.mediagallery.flickr.model.Photo
import com.example.mediagallery.model.GalleryPicture
import com.example.mediagallery.model.GalleryPicture.Companion.DiffCallback
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.flickr_photo.view.*

class PhotoAdapter() :  ListAdapter<GalleryPicture, ImageViewHolder3>(GalleryPicture.DiffCallback) {
    private lateinit var onClickImage: (GalleryPicture, Int) -> Unit

    fun setOnClickListenerImage(onClick: (GalleryPicture, Int) -> Unit) {
        this.onClickImage = onClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder3 {
        val holder = ImageViewHolder3(LayoutInflater.from(parent.context).inflate(R.layout.flickr_photo, parent, false))

        return holder
    }

    override fun onBindViewHolder(holder: ImageViewHolder3, position: Int) {

        val flickerPhoto = getItem(position)
        holder.pos = position
        holder.rootView.tag = flickerPhoto

        holder.imageView.setOnClickListener {
            onClickImage(flickerPhoto, holder.adapterPosition)
        }

        Glide.with(holder.imageView)
            .load(flickerPhoto.contentUri)
            .thumbnail(0.33f)
            .centerCrop()
            .into(holder.imageView)
    }
}

class ImageViewHolder3( override val containerView: View) :    RecyclerView.ViewHolder(containerView),
    LayoutContainer {
    val rootView = containerView
    var pos: Int? = null
    val imageView: ImageView = containerView.findViewById(R.id.imageView)

}

