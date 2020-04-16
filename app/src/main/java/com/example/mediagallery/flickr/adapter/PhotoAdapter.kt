package com.example.mediagallery.flickr.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mediagallery.R
import com.example.mediagallery.flickr.model.Photo
import com.example.mediagallery.model.GalleryPicture.Companion.DiffCallback
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.flickr_photo.view.*

class PhotoAdapter() :  ListAdapter<Photo, ImageViewHolder3>(
    Photo.DiffCallback) {
    private lateinit var onClick: (Photo) -> Unit
    fun setOnClickListener(onClick: (Photo) -> Unit) {
        this.onClick = onClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder3 {
        val vh = ImageViewHolder3(LayoutInflater.from(parent.context).inflate(R.layout.flickr_photo, parent, false))

        vh.containerView.setOnClickListener {
            val position = vh.adapterPosition
            val picture = getItem(position)
            onClick(picture!!)
        }

        return vh
    }

    override fun onBindViewHolder(holder: ImageViewHolder3, position: Int) {

        val flickerPhoto = getItem(position)
        holder.pos=position
        holder.rootView.tag = flickerPhoto
        Glide.with(holder.imageView)
            .load(flickerPhoto?.url)
            .thumbnail(0.33f)
            .centerCrop()
            .into(holder.imageView)
    }
}
/**
 * Basic [RecyclerView.ViewHolder] for our gallery.
 */
class ImageViewHolder3( override val containerView: View) :    RecyclerView.ViewHolder(containerView),
    LayoutContainer {
    val rootView = containerView
    var pos: Int? = null
    val imageView: ImageView = containerView.findViewById(R.id.imageView)

}
/*class PhotosAdapter(val photos: MutableList<Photo> = mutableListOf()) : RecyclerView.Adapter<PhotosAdapter.PhotosViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotosViewHolder {
        return PhotosViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.flickr_photo,
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int = photos.size

    override fun onBindViewHolder(holder: PhotosViewHolder, position: Int) {
        holder.bind(photos[position])
    }

    inner class PhotosViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(photo: Photo) {
            Glide.with(itemView.imageView)
                .load(photo.url)
                .thumbnail(0.33f)
                .centerCrop()
                .into(itemView.imageView)
            //Picasso.get().load(photo.url).into(itemView.imageView)
        }
    }
}*/