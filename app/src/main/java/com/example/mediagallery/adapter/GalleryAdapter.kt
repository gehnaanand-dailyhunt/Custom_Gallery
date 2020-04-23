package com.example.mediagallery.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mediagallery.R
import com.example.mediagallery.model.GalleryPicture
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.multi_gallery_listitem.view.*

class GalleryAdapter() : ListAdapter<GalleryPicture, ImageViewHolder>(GalleryPicture.DiffCallback) {

    private lateinit var onClick: (GalleryPicture, Int) -> Unit

    fun setOnClickListener(onClick: (GalleryPicture, Int) -> Unit) {
        this.onClick = onClick
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.multi_gallery_listitem, parent, false)
        val holder = ImageViewHolder(view)

        holder.containerView.setOnClickListener {
            val position = holder.adapterPosition
            val picture = getItem(position)
            onClick(picture,position)
        }
        return holder
    }


    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val galleryPicture = getItem(position)
        holder.rootView.tag = galleryPicture
        holder.pos = position


        Glide.with(holder.imageView)
            .load(galleryPicture.contentUri)
            .thumbnail(0.33f)
            .centerCrop()
            .into(holder.imageView)


    }
}

class ImageViewHolder (override  val containerView: View) : RecyclerView.ViewHolder(containerView), LayoutContainer {
    val rootView = containerView
    val imageView: ImageView = containerView.findViewById(R.id.image)
    var pos: Int? = null
}


/*class ItemClickListener(val clickListener: (id : Long)-> Unit){
    fun onClick(galleryPicture: GalleryPicture) = clickListener(galleryPicture.id)
}*/
